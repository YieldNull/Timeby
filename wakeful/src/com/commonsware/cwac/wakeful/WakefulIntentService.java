/**
 * Copyright (c) 2009-14 CommonsWare, LLC
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.commonsware.cwac.wakeful;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;

public abstract class WakefulIntentService extends IntentService {
    abstract protected void doWakefulWork(Intent intent);

    static final String NAME =
            "com.commonsware.cwac.wakeful.WakefulIntentService";
    static final String LAST_ALARM = "lastAlarm";

    private static volatile PowerManager.WakeLock lockStatic = null;

    public interface AlarmListener {
        void scheduleAlarms(AlarmManager mgr, PendingIntent pi, Context context);

        void sendWakefulWork(Context context);

        long getMaxAge();
    }

    public WakefulIntentService(String name) {
        super(name);

        //被系统切断后可以重新发送Intent
        setIntentRedelivery(true);
    }

    /**
     * 获取系统WakeLock
     *
     * @param context
     * @return
     */
    private static synchronized PowerManager.WakeLock getLock(Context context) {
        if (lockStatic == null) {
            PowerManager mgr =
                    (PowerManager) context.getSystemService(Context.POWER_SERVICE);

            //保证关屏之后CPU还在运行
            lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, NAME);
            lockStatic.setReferenceCounted(true);
        }

        return (lockStatic);
    }

    public static void sendWakefulWork(Context context, Intent intent) {
        getLock(context.getApplicationContext()).acquire();
        context.startService(intent);
    }

    public static void sendWakefulWork(Context context, Class<?> clsService) {
        sendWakefulWork(context, new Intent(context, clsService));
    }

    public static void scheduleAlarms(AlarmListener listener, Context context) {
        scheduleAlarms(listener, context, true);
    }

    public static void scheduleAlarms(AlarmListener listener,
                                      Context context, boolean force) {

        SharedPreferences prefs = context.getSharedPreferences(NAME, 0);
        long lastAlarm = prefs.getLong(LAST_ALARM, 0);

        if (lastAlarm == 0
                || force
                || (System.currentTimeMillis() > lastAlarm && System.currentTimeMillis()
                - lastAlarm > listener.getMaxAge())) {
            AlarmManager mgr =
                    (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

            listener.scheduleAlarms(mgr, pi, context);
        }
    }

    public static void cancelAlarms(Context context) {
        AlarmManager mgr =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

        mgr.cancel(pi);

        context.getSharedPreferences(NAME, 0).edit().remove(LAST_ALARM)
                .commit();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PowerManager.WakeLock lock = getLock(this.getApplicationContext());

        if (!lock.isHeld() || (flags & START_FLAG_REDELIVERY) != 0) {
            lock.acquire();
        }

        super.onStartCommand(intent, flags, startId);

        return (START_REDELIVER_INTENT);
    }

    /**
     * 处理完之后释放WakeLock锁
     *
     * @param intent
     */
    @Override
    final protected void onHandleIntent(Intent intent) {
        try {
            doWakefulWork(intent);
        } finally {
            PowerManager.WakeLock lock = getLock(this.getApplicationContext());

            if (lock.isHeld()) {
                try {
                    lock.release();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(),
                            "Exception when releasing wakelock", e);
                }
            }
        }
    }
}
