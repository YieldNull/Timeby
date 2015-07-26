package com.nectar.timeby.service.countdown;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

/**
 * 用于动态注册ScreenStateReceiver
 * <p/>
 * 创建后注册ScreenStateReceiver,销毁时撤销ScreenStateReceiver
 * 因为屏幕与电源有关，不能在Mainifest中注册refer:
 * http://stackoverflow.com/questions/12681884/android-broadcast-receiver-doesnt-receive-action-screen-on
 */
public class ScreenService extends Service {

    private static final String TAG = "ScreenService";
    private ScreenStateReceiver mScreenStateReceiver;


    public ScreenService() {
        Log.i(TAG, "ScreenService Create");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Registering ScreenStateBroadcast receiver");

        mScreenStateReceiver = new ScreenStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStateReceiver, filter);

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "Screen service destroy");
        Log.i(TAG, "unregister screenOn broadcast receiver");
        unregisterReceiver(mScreenStateReceiver);
    }
}
