package com.nectar.timeby.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 接收开机消息，启动Service
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";


    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "received boot complete broadcast");
        Log.i(TAG, "trying to start notify service");

        Intent theIntent = new Intent(context, NotifyService.class);
        theIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(theIntent);
    }
}
