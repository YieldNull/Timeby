package com.nectar.timeby.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.nectar.timeby.gui.NotifyActivity;
import com.nectar.timeby.util.PrefsUtil;

/**
 * 接收屏幕开关的广播信息，进而开启NotifyActivity
 */
public class ScreenStateReceiver extends BroadcastReceiver {
    private static final String TAG = "ScreenStateReceiver";


    public ScreenStateReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //将控制逻辑转给TimeCountService
        Intent i = new Intent(context, TimeCountService.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.i(TAG, "Screen on");
            i.putExtra(TimeCountService.INTENT_TYPE, TimeCountService.TYPE_SCREEN_ON);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.i(TAG, "Screen off");
            i.putExtra(TimeCountService.INTENT_TYPE, TimeCountService.TYPE_SCREEN_OFF);
        }
        context.startService(i);
    }
}
