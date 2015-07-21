package com.nectar.timeby.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import com.nectar.timeby.gui.widget.ClockWidget;


import java.util.Timer;
import java.util.TimerTask;

/**
 * 后台运行的Service，用于与接收好友的请求等工作，开机自起
 * <p/>
 * 创建后注册ScreenOnReceiver,销毁时撤销ScreenReceiver
 * 因为屏幕与电源有关，不能在Mainifest中注册refer:
 * http://stackoverflow.com/questions/12681884/android-broadcast-receiver-doesnt-receive-action-screen-on
 */
public class BackgroundService extends Service {
    private static final String TAG = "BackgroundService";
    private static final String TIMER_CLOCK = "clock_timer";
    private static final String TIMER_NET = "net_timer";

    private int mTaskSumSecond;
    private int mLeavingSecond;
    private int mPresupposeLeavingSecond;

    private CountDownTimer mClockTimer;
    private TimerTask mClockTimerTask;

    private Timer mNetTimer;
    private TimerTask mNetTimerTask;

    private ScreenOnReceiver mScreenOnReceiver;

    public BackgroundService() {

    }

    /*
* 时钟自开启后一直走
* 有以下情况可以导致时钟停止
* 1.时钟走完
* 2.用户离开倒计时页面超过预设时间，只有打入电话不算离开时间
*
* 有以下情况会导致结束时间改变
* */
    public class ClockTickBinder extends Binder {
        public BackgroundService getService() {
            return BackgroundService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "notify service create");
        Log.i(TAG, "trying to register screenOn broadcast receiver");

        mScreenOnReceiver = new ScreenOnReceiver();
        registerReceiver(mScreenOnReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));

        mClockTimer = new CountDownTimer(0, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTaskSumSecond--;
            }

            @Override
            public void onFinish() {

            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "notify service destroy");
        Log.i(TAG, "unregister screenOn broadcast receiver");

        unregisterReceiver(mScreenOnReceiver);
    }

    public void startClockTick(int sumSecond) {

    }
}
