package com.nectar.timeby.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.nectar.timeby.util.PrefsUtil;

/**
 * 接收开机消息，启动PollingService
 */
public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";


    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received boot complete broadcast");
        Log.i(TAG, "Starting polling service");
        WakefulIntentService.scheduleAlarms(new AlarmListener(), context, false);

        Log.i(TAG, "Starting ScreenService");
        context.startService(new Intent(context, ScreenService.class));
    }

}
