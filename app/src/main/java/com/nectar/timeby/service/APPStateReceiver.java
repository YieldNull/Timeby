package com.nectar.timeby.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 监听APP退出，开启TimeCounterService
 */
public class APPStateReceiver extends BroadcastReceiver {
    public static final String ACTION_NAME_QUIT = "com.nectar.timeby.service.QUIT_APP";
    public static final String ACTION_NAME_ENTER = "com.nectar.timeby.service.ENTER_APP";

    private static final String TAG = "APPStateReceiver";


    public APPStateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO show Notification


        if (intent.getAction().equals(ACTION_NAME_ENTER)) {
            Log.i(TAG, "Received app Enter broadcast");
            Log.i(TAG, "Stopping TimeCountService");

            context.startService(TimeCountService.genIntent(
                    context, TimeCountService.TYPE_ENTER_APP));
        } else {
            Log.i(TAG, "Received app Quit broadcast");
            Log.i(TAG, "Starting TimeCountService");

            context.startService(TimeCountService.genIntent(
                    context, TimeCountService.TYPE_LEAVE_APP));
        }
    }
}
