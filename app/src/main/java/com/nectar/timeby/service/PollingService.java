package com.nectar.timeby.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;


/**
 * 后台运行的Service，用于与接收好友的请求等工作，开机自起
 * 定时向服务器请求数据。
 * <p>
 * 创建后注册ScreenStateReceiver,销毁时撤销ScreenStateReceiver
 * 因为屏幕与电源有关，不能在Mainifest中注册refer:
 * http://stackoverflow.com/questions/12681884/android-broadcast-receiver-doesnt-receive-action-screen-on
 */
public class PollingService extends Service {

    private static final String TAG = "PollingService";


    private ScreenStateReceiver mScreenStateReceiver;

    public PollingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Polling service create");
        Log.i(TAG, "Registering ScreenStateBroadcast receiver");

        mScreenStateReceiver = new ScreenStateReceiver();
        registerReceiver(mScreenStateReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(mScreenStateReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Polling service destroy");
        Log.i(TAG, "unregister screenOn broadcast receiver");

        unregisterReceiver(mScreenStateReceiver);
    }
}
