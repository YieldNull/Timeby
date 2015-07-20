package com.nectar.timeby.gui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nectar.timeby.R;
import com.nectar.timeby.gui.MainCooperActivity;
import com.nectar.timeby.gui.MainPKActivity;
import com.nectar.timeby.gui.MainSingleActivity;
import com.nectar.timeby.gui.interfaces.OnDrawerStatusChangedListener;
import com.nectar.timeby.gui.interfaces.OnDrawerToggleClickListener;
import com.nectar.timeby.gui.widget.ClockWidget;
import com.nectar.timeby.gui.widget.TaskTypeSelectDialog;

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

    //by Dean
    int sumMin, startHour, startMin, endHour, endMin;

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
                setTimeText();
            }
        });

        //提交按钮
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTaskTypeSelectDialog.showDialog();
            }
        });

        //底部模式选择弹框
        mTaskTypeSelectDialog = new TaskTypeSelectDialog(getActivity());
        mTaskTypeSelectDialog.setSelectDialogListener(new TaskTypeSelectDialog.DialogListener() {
            @Override
            public void onSelectSolo() {
                Intent intToSingle = new Intent(getActivity(), MainSingleActivity.class);
                intToSingle.putExtras(getDeliverBunlder());
                startActivity(intToSingle);
            }

            @Override
            public void onSelectCooper() {
                Intent intToSingle = new Intent(getActivity(), MainCooperActivity.class);
                intToSingle.putExtras(getDeliverBunlder());
                startActivity(intToSingle);
            }

            @Override
            public void onSelectPK() {
                Intent intToSingle = new Intent(getActivity(), MainPKActivity.class);
                intToSingle.putExtras(getDeliverBunlder());
                startActivity(intToSingle);
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
        //设置初始时间
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
            setTimeText();
        }
    }

    /**
     * 根据时钟指针变化，将时间反馈到顶部显示框以及底部时差框
     * 在DialTouchListener中调用
     */
    private void setTimeText() {
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

        startHour = Integer.parseInt(mStartHourText.getText().toString());
        startMin = Integer.parseInt(mStartMinText.getText().toString());
        endHour = Integer.parseInt(mEndHourText.getText().toString());
        endMin = Integer.parseInt(mEndMinText.getText().toString());

        //总时差的分钟形式
        sumMin = (endHour - startHour) * 60 + (endMin - startMin);

        //上下AM PM不同则要加上12小时
        if (!mEndAPMText.getText().toString()
                .equals(mStartAPMText.getText().toString())) {
            sumMin += 12 * 60;
        }

        //下面比上面小则归零
        if (sumMin <= 0) {
            mTimeIntervalText.setText("00时00分");
            return;
        }
        String intervalHour = sumMin / 60 > 9 ? "" + sumMin / 60 : "0" + sumMin / 60;
        String intervalMin = sumMin % 60 > 9 ? "" + sumMin % 60 : "0" + sumMin % 60;
        mTimeIntervalText.setText(intervalHour + "时" + intervalMin + "分");

    }

    private Bundle getDeliverBunlder() {
        Bundle bundle = new Bundle();
        bundle.putInt("CountDownTime", sumMin);
        bundle.putInt("startHour", startHour);
        bundle.putInt("startMin", startMin);
        bundle.putInt("endHour", endHour);
        bundle.putInt("endMin", endMin);
        bundle.putString("startAPM", mStartAPMText.getText().toString());
        bundle.putString("endAPM", mEndAPMText.getText().toString());
        return bundle;
    }
}