package com.nectar.timeby.service;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.nectar.timeby.util.PrefsUtil;

/**
 * 接收屏幕开关的广播信息，进而开启NotifyActivity
 */
public class ScreenStateReceiver extends BroadcastReceiver {
    private static final String TAG = "ScreenStateReceiver";


    public ScreenStateReceiver() {
        Log.i(TAG, "Created ScreenStateReceiver");
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //将控制逻辑转给TimeCountService

        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            if (PrefsUtil.isOnTask(context)) {
                Log.i(TAG, "Screen on");
                context.startService(TimeCountService
                        .genIntent(context, TimeCountService.TYPE_SCREEN_ON));
            }
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            if (PrefsUtil.isOnTask(context)) {
                Log.i(TAG, "Screen off");
                context.startService(TimeCountService
                        .genIntent(context, TimeCountService.TYPE_SCREEN_OFF));
            }
        }
    }

    /**
     * 判断是否处于锁屏状态
     *
     * @param context
     * @return
     */
    public final static boolean isScreenOff(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return !pm.isScreenOn();
    }
}
