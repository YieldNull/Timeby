package com.nectar.timeby.gui.fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mob.tools.MobUIShell;
import com.nectar.timeby.R;
import com.nectar.timeby.gui.CountDownActivity;
import com.nectar.timeby.gui.interfaces.OnDrawerStatusChangedListener;
import com.nectar.timeby.gui.interfaces.OnDrawerToggleClickListener;
import com.nectar.timeby.gui.widget.ClockWidget;
import com.nectar.timeby.gui.widget.TaskTypeSelectDialog;
import com.nectar.timeby.gui.widget.TopNotification;
import com.nectar.timeby.util.PrefsUtil;

import java.util.Calendar;

/**
 * by finalize
 * <p/>
 * 时钟by上百泽稻叶
 * <p/>
 * 点击顶部TextView，获取时钟控制权
 */
public class MainFragment extends Fragment
        implements OnDrawerStatusChangedListener {

    private static final String TAG = "MainFragment";

    private ImageView mDrawerToggle;
    private TaskTypeSelectDialog mTaskTypeSelectDialog;
    private ClockWidget mClockWidget;
    private OnDrawerToggleClickListener mListener;

    private TextView mStartAPMText;
    private TextView mStartHourText;
    private TextView mStartMinText;
    private TextView mEndAPMText;
    private TextView mEndHourText;
    private TextView mEndMinText;
    private TextView mTimeIntervalText;
    private Button mSubmitButton;

    private int mCurrHour;
    private int mCurrMinu;

    //用于切换起始时间的更改，当点击结束时间时为true
    private boolean isEndSet;
    private boolean isEndOnSetting;
    private static int TYPE_START = 0x0001;
    private static int TYPE_END = 0x0002;

    private int mSumMin, mStartHour, mStartMin, mEndHour, mEndMin;

    public MainFragment() {

    }

    @Override
    public void onDrawerOpening() {
        mDrawerToggle.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDrawerClosed() {

        mDrawerToggle.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnDrawerToggleClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnToggleClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_main, container, false);

        //获取一堆引用
        mStartAPMText = (TextView) rootView.findViewById(R.id.textView_main_start_apm);
        mStartHourText = (TextView) rootView.findViewById(R.id.textView_main_start_hour);
        mStartMinText = (TextView) rootView.findViewById(R.id.textView_main_start_min);
        mEndAPMText = (TextView) rootView.findViewById(R.id.textView_main_end_apm);
        mEndHourText = (TextView) rootView.findViewById(R.id.textView_main_end_hour);
        mEndMinText = (TextView) rootView.findViewById(R.id.textView_main_end_min);
        mEndAPMText = (TextView) rootView.findViewById(R.id.textView_main_end_apm);
        mTimeIntervalText = (TextView) rootView.findViewById(R.id.textView_main_hint_time);
        mSubmitButton = (Button) rootView.findViewById(R.id.button_main_submit);

        //抽屉开关
        mDrawerToggle = (ImageView) rootView.findViewById(R.id.imageView_drawer_toggle);
        mDrawerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDrawerToggleClick();
            }
        });

        //时间
        initTimeWidget();

        //时钟
        mClockWidget = new ClockWidget(getActivity(), rootView);
        mClockWidget.setOnPointerMoveListener(new ClockWidget.OnPointerMoveListener() {
            @Override
            public void onPointerMove(int currentHour, int currentMin) {
                mCurrHour = currentHour;
                mCurrMinu = currentMin;
                refreshTimeText();
            }
        });

        //提交按钮
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSumMin == 0) {
                    new TopNotification(getActivity(), "请先设置任务时间", 4 * 1000).show();
                } else {
                    mTaskTypeSelectDialog.showDialog();
                }
            }
        });

        //底部模式选择弹框
        mTaskTypeSelectDialog = new TaskTypeSelectDialog(getActivity());
        mTaskTypeSelectDialog.setSelectDialogListener(new TaskTypeSelectDialog.DialogListener() {
            @Override
            public void onSelectSolo() {
                setAlarm(CountDownActivity.TASK_TYPE_SOLO);
            }

            @Override
            public void onSelectCooper() {
                setAlarm(CountDownActivity.TASK_TYPE_COOPER);
            }

            @Override
            public void onSelectPK() {
                setAlarm(CountDownActivity.TASK_TYPE_PK);
            }

            @Override
            public void onDialogOpen() {
                mSubmitButton.setEnabled(false);
            }

            @Override
            public void onDialogClose() {
                mSubmitButton.setEnabled(true);
            }
        });

        return rootView;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * 初始化时间，在界面上显示当前的时间，并设置点击监听器
     */
    private void initTimeWidget() {
        mCurrHour = Calendar.getInstance().get(Calendar.HOUR);
        mCurrMinu = Calendar.getInstance().get(Calendar.MINUTE);

        int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        boolean isAm = hourOfDay >= 0 && hourOfDay < 12;

        if (isAm) {
            mStartAPMText.setText("AM");
            mEndAPMText.setText("AM");
        } else {
            mStartAPMText.setText("PM");
            mEndAPMText.setText("PM");
        }
        mStartHourText.setText(mCurrHour > 9 ? "" + mCurrHour : "0" + mCurrHour);
        mStartMinText.setText(mCurrMinu > 9 ? "" + mCurrMinu : "0" + mCurrMinu);
        mEndHourText.setText(mCurrHour > 9 ? "" + mCurrHour : "0" + mCurrHour);
        mEndMinText.setText(mCurrMinu > 9 ? "" + mCurrMinu : "0" + mCurrMinu);

        //设置监听器，用于获取时钟控制权
        mStartAPMText.setOnClickListener(new TimeWidgetOnClickListener(mStartAPMText, TYPE_START));
        mStartHourText.setOnClickListener(new TimeWidgetOnClickListener(null, TYPE_START));
        mStartMinText.setOnClickListener(new TimeWidgetOnClickListener(null, TYPE_START));
        mEndAPMText.setOnClickListener(new TimeWidgetOnClickListener(mEndAPMText, TYPE_END));
        mEndHourText.setOnClickListener(new TimeWidgetOnClickListener(null, TYPE_END));
        mEndMinText.setOnClickListener(new TimeWidgetOnClickListener(null, TYPE_END));

    }


    /**
     * 顶部六个TextView的点击监听器，点击之后获取时钟控制权，或切换APM
     */
    class TimeWidgetOnClickListener implements View.OnClickListener {
        private TextView mTextView;
        private int mType;

        public TimeWidgetOnClickListener(TextView textView, int type) {
            mTextView = textView;
            mType = type;
        }

        @Override
        public void onClick(View v) {
            if (mType == TYPE_START) {
                isEndOnSetting = false;
            } else {
                isEndSet = true;
                isEndOnSetting = true;
            }

            if (mTextView != null) {
                if (mTextView.getText().equals("AM"))
                    mTextView.setText("PM");
                else
                    mTextView.setText("AM");
            }
            //更改时差
            refreshTimeText();
        }
    }

    /**
     * 根据时钟指针变化，将时间反馈到顶部显示框以及底部时差框
     * 在DialTouchListener中调用
     */
    private void refreshTimeText() {
        String mHourStr = mCurrHour > 9 ? "" + mCurrHour : "0" + mCurrHour;
        String mMinStr = mCurrMinu > 9 ? "" + mCurrMinu : "0" + mCurrMinu;

        if (isEndOnSetting) {
            mEndHourText.setText(mHourStr);
            mEndMinText.setText(mMinStr);
        } else {
            if (!isEndSet) {
                mEndHourText.setText(mHourStr);
                mEndMinText.setText(mMinStr);
            }
            mStartHourText.setText(mHourStr);
            mStartMinText.setText(mMinStr);
        }

        //当结束时间设置之后，显示底部时间差
        if (!isEndSet)
            return;

        mStartHour = Integer.parseInt(mStartHourText.getText().toString());
        mStartMin = Integer.parseInt(mStartMinText.getText().toString());
        mEndHour = Integer.parseInt(mEndHourText.getText().toString());
        mEndMin = Integer.parseInt(mEndMinText.getText().toString());

        //总时差的分钟形式
        mSumMin = (mEndHour - mStartHour) * 60 + (mEndMin - mStartMin);

        //上下AM PM不同则要加上12小时
        if (!mEndAPMText.getText().toString()
                .equals(mStartAPMText.getText().toString())) {
            mSumMin += 12 * 60;
        }

        //下面比上面小则归零
        if (mSumMin <= 0) {
            mTimeIntervalText.setText("00时00分");
            return;
        }
        String intervalHour = mSumMin / 60 > 9 ? "" + mSumMin / 60 : "0" + mSumMin / 60;
        String intervalMin = mSumMin % 60 > 9 ? "" + mSumMin % 60 : "0" + mSumMin % 60;
        mTimeIntervalText.setText(intervalHour + "时" + intervalMin + "分");

    }

    /**
     * 利用AlarmManager设置Alarm用以启动倒计时页面
     * <p/>
     * 设置之前要将任务保存到SharedPreference
     *
     * @param type 任务类型
     */
    private void setAlarm(int type) {
        long triggerTime = calcAlertTriggerTime();

        //将任务信息存到本地
        long startMillis = triggerTime;
        long endMillis = triggerTime + mSumMin * 60 * 1000;
        PrefsUtil.storeTask(getActivity(), startMillis, endMillis, type);

        Log.i(TAG, (triggerTime - System.currentTimeMillis()) / 1000 + "");

        //设置Alarm，定时当达到任务开启时间时打开倒计时页面
        AlarmManager manager = (AlarmManager) getActivity()
                .getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getActivity(), CountDownActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        manager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);


        new TopNotification(getActivity(), getStartTimeHint(triggerTime), 3 * 1000).show();

        mSubmitButton.setEnabled(false);
    }

    private String getStartTimeHint(long triggerTime) {
        int DValue = (int) ((triggerTime - System.currentTimeMillis()) / 1000);
        int hour = DValue / 3600;
        int min = DValue % 60;

        return getTimeStr(hour) + "时" + getTimeStr(min) + "分进入倒计时页面";
    }

    private CharSequence getTimeStr(int time) {
        return time > 9 ? "" + time : "0" + time;
    }

    /**
     * 计算Alarm触发时间的RTC值
     * <p/>
     * 先将启动时间转为二十四小时制，然后计算出与当前时间的差值(毫秒)
     */
    private long calcAlertTriggerTime() {
        //当前时间
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);

        //启动时间
        int startHour = Integer.parseInt(mStartHourText.getText().toString());
        int startMin = Integer.parseInt(mStartMinText.getText().toString());
        if (mStartAPMText.getText().toString().equalsIgnoreCase("PM")) {
            startHour += 12;
        }

        //计算差值,精确到分
        int startSumSec = startHour * 3600 + startMin * 60 + sec;
        int currSumSec = hourOfDay * 3600 + min * 60 + sec;
        int dValue = startSumSec - currSumSec;
        dValue = dValue > 0 ? dValue : dValue + 3600 * 24;

        return dValue * 1000 + System.currentTimeMillis();
    }
}