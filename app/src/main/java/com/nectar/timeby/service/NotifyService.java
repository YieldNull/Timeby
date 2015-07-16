package com.nectar.timeby.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

/**
 * 后台运行的Service，用于与接收好友的请求等工作，开机自起
 * <p>
 * 创建后注册ScreenOnReceiver,销毁时撤销ScreenReceiver
 * 因为屏幕与电源有关，不能在Mainifest中注册refer:
 * http://stackoverflow.com/questions/12681884/android-broadcast-receiver-doesnt-receive-action-screen-on
 */
public class NotifyService extends Service {
    private static final String TAG = "NotifyService";

    private ScreenOnReceiver mScreenOnReceiver;

    public NotifyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "notify service start");
        Log.i(TAG, "fragment_register screenOn broadcast receiver");

        mScreenOnReceiver = new ScreenOnReceiver();
        registerReceiver(mScreenOnReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "notify service destroy");
        Log.i(TAG, "unregister screenOn broadcast receiver");

        unregisterReceiver(mScreenOnReceiver);
    }
}
