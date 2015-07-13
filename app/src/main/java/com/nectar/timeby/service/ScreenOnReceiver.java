package com.nectar.timeby.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.nectar.timeby.ui.NotifyActivity;

/**
 * 接收屏幕开启的广播信息，进而开启NotifyActivity
 */
public class ScreenOnReceiver extends BroadcastReceiver {
    private static final String TAG = "ScreenOnReceiver";

    public static final String ENABLE_ACTION = "com.nectar.timeby.action.ENABLE_NOTIFY";

    private static boolean enable = false;


    public ScreenOnReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "received screen on broadcast");
        Log.i(TAG, "trying to start NotifyActivity before lock screen on");


        Intent mIntent = new Intent(context, NotifyActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(mIntent);
    }
}
