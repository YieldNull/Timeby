package com.nectar.timeby.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.nectar.timeby.util.PrefsUtil;

/**
 * Created by finalize on 7/25/15.
 */
public class AlarmListener implements WakefulIntentService.AlarmListener {
    @Override
    public void scheduleAlarms(AlarmManager alarmManager,
                               PendingIntent pendingIntent, Context context) {

        //4秒后启动轮训，每隔30秒
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 1000,
                1000 * 30, pendingIntent);
    }

    @Override
    public void sendWakefulWork(Context context) {
        String phoneNum = PrefsUtil.getUserPhone(context);
        Intent intent = new Intent(context, PollingService.class);
        intent.putExtra(PollingService.INTENT_PHONE_NUM, phoneNum);
        WakefulIntentService.sendWakefulWork(context, intent);
    }

    @Override
    public long getMaxAge() {
        return 1000 * 60;
    }
}
