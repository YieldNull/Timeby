package com.nectar.timeby.gui.widget;

/**
 * Created by Dean on 15/7/19.
 */


import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nectar.timeby.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Custom digital clock
 */
public class TextClock {

    private int mCurrHour;
    private int mCurrMin;
    private int mCurrSec;
    private int mSumSec;

    private TextView mHourText;
    private TextView mMinText;
    private TextView mSecText;

    private Handler mHandler;
    private Timer mTimer;

    private static final int MSG_CLOCK_TICK = 0x0001;
    private static final int MSG_CLOCK_STOP = 0x0002;

    public TextClock(int startHour, int startMin, ViewGroup container) {
        mCurrHour = startHour;
        mCurrMin = startMin;
        mCurrSec = 0;
        mSumSec = mCurrHour * 3600 + mCurrMin * 60;


        mHourText = (TextView) container.findViewById(R.id.textView_main_countdown_hour);
        mMinText = (TextView) container.findViewById(R.id.textView_main_countdown_min);
        mSecText = (TextView) container.findViewById(R.id.textView_main_countdown_sec);
        mTimer = new Timer();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

            }
        };
    }

    public void startTick() {
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mSumSec -= 1;
                mCurrSec = mSumSec % 60;
                mCurrMin = (mSumSec % 3600) / 60;
                mCurrHour = mSumSec / 3600;

                if (mSumSec == 0) {
                   mTimer.cancel();
                }
                mHandler.sendEmptyMessage(MSG_CLOCK_TICK);
            }
        }, 0, 1 * 1000);
    }

}
