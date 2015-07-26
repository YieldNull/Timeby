package com.nectar.timeby.service.countdown;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.nectar.timeby.service.MessageReceiver;
import com.nectar.timeby.util.PrefsUtil;


/**
 * 包内主控制器
 * <p/>
 * 监听退出APP，计算退出时间，若退出时间达到预定值，则任务失败
 * 屏幕关闭后停止计时，屏幕开启且未进入APP时计时，打进电话时停止计时
 * <p/>
 * 时钟自开启后一直走
 * 有以下情况可以导致时钟停止
 * 1.时钟走完
 * 2.用户离开倒计时页面超过预设时间，只有打入电话不算离开时间
 * <p/>
 * 开始时间、结束时间不会改变
 */
public class TimeCountService extends Service {
    private static final String TAG = "TimeCountService";

    //传递过来的数据标识符:结束时间
    public static final String INTENT_END_MILLIS = "task_end_millis";
    //预设的可离开APP的最长时间
    public static final String INTENT_SUPPOSED_ESCAPE_TIME = "task_supposed_escape_time";
    public static final String INTENT_TYPE = "intent_type";

    public static final int TYPE_ENTER_APP = 0x0001;
    public static final int TYPE_LEAVE_APP = 0x0002;
    public static final int TYPE_SCREEN_ON = 0x0003;
    public static final int TYPE_SCREEN_OFF = 0x0004;
    public static final int TYPE_RING_COME = 0x0005;
    public static final int TYPE_RING_OFF = 0x0006;

    private long mCurrMillis;//当前时间
    private long mStopMillis;//结束时间
    private int mSupposedTime;//预设可离开时间

    private CountDownTimer mCountDownTimer;


    public TimeCountService() {

    }

    public class ClockTickBinder extends Binder {
        public TimeCountService getService() {
            return TimeCountService.this;
        }
    }


    public static Intent genIntent(Context context, int type) {
        //启动TimeCountService
        Intent intent = new Intent(context, TimeCountService.class);
        intent.putExtra(TimeCountService.INTENT_TYPE, type);
        intent.putExtra(TimeCountService.INTENT_END_MILLIS, PrefsUtil.getTaskEndTime(context));
        intent.putExtra(TimeCountService.INTENT_SUPPOSED_ESCAPE_TIME,
                PrefsUtil.getSupposedEscapeTime(context));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ClockTickBinder();
    }

    /**
     * 根据消息类型选择处理逻辑
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "start command");

        int type = intent.getIntExtra(INTENT_TYPE, -1);
        switch (type) {
            case TYPE_LEAVE_APP:
                initCountDown(intent);
                break;

            case TYPE_ENTER_APP:
                if (mCountDownTimer != null) {
                    Log.i(TAG, "Cancel CountDown");
                    mCountDownTimer.cancel();
                }
                break;

            case TYPE_SCREEN_ON:
                initCountDown(intent);
                break;

            case TYPE_SCREEN_OFF:
                if (mCountDownTimer != null) {
                    Log.i(TAG, "Cancel CountDown");
                    mCountDownTimer.cancel();
                }
                break;

            case TYPE_RING_COME:
                if (mCountDownTimer != null) {
                    Log.i(TAG, "Cancel CountDown");
                    mCountDownTimer.cancel();
                }
                break;

            case TYPE_RING_OFF:
                initCountDown(intent);
                break;

            default:
                break;
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "TimeCountService destroy");
    }

    /**
     * 离开app时初始化计时器
     *
     * @param intent
     */
    private void initCountDown(Intent intent) {
        Log.i(TAG, "InitCountDown");

        mCurrMillis = System.currentTimeMillis();
        mStopMillis = intent.getLongExtra(INTENT_END_MILLIS, 0);
        mSupposedTime = intent.getIntExtra(INTENT_SUPPOSED_ESCAPE_TIME, 0);

        mCountDownTimer = new CountDownTimer(mSupposedTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mCurrMillis += 1000 * 1;

                Log.d(TAG, "tick");
                if (mCurrMillis > mStopMillis) {
                    Log.i(TAG, "Arrived end time,counter quit!");
                    mCountDownTimer.cancel();

                    stopSelf();
                }
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "Leave APP over time! Your task failed!");

                //提示任务失败
                sendFailBroadcast();

                stopSelf();
            }
        };
        mCountDownTimer.start();
    }

    private void sendFailBroadcast() {
        Intent intent = new Intent();
        intent.setAction(MessageReceiver.INTENT_ACTION);
        intent.putExtra(MessageReceiver.INTENT_EXTRA_TITLE, "任务失败");
        intent.putExtra(MessageReceiver.INTENT_EXTRA_CONTENT, "离开APP过长时间，您的任务失败");
        intent.putExtra(MessageReceiver.INTENT_FLAG,MessageReceiver.FLAG_TASK_FAIL);
        sendBroadcast(intent);
    }
}
