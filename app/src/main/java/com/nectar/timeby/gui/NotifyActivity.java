package com.nectar.timeby.gui;

import android.app.Activity;
import android.os.PersistableBundle;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.nectar.timeby.R;


/**
 * 2015.7.13 by finalize
 * 点亮屏幕时显示提示信息。
 * 启动过程：
 * 先正常启动，然后立即被锁屏界面干掉，之后锁屏界面又被它干掉。
 * 在Receiver开启此Activity时使用FLAG_ACTIVITY_REORDER_TO_FRONT，可以避免重复开启多个Activity
 * 但是当APP可见时，重复开关屏会出现重复开启多个Activity而不是reorder to front,待解决
 *
 * 2015.7.13 晚 by finalize
 * 在ScreenOnReceiver中使用FLAG_ACTIVITY_NO_HISTORY 而不是FLAG_ACTIVITY_REORDER_TO_FRONT
 * 解决了上述问题
 */

public class NotifyActivity extends Activity {
    private static final String TAG = "NotifyActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "create");

        setContentView(R.layout.activity_notify);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i(TAG, "success show notification before lock screen on");
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Log.i(TAG, "create");
    }

}
