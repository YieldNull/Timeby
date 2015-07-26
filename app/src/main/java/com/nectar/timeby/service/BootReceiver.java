package com.nectar.timeby.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.nectar.timeby.service.countdown.ScreenService;
import com.nectar.timeby.service.wakeful.AlarmListener;

/**
 * 接收开机消息，启动PollingService,
 * 第一次使用APP时，在MainActivity OnCreate中启动Polling
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
