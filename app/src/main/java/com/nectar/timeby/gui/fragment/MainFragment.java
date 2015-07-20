package com.nectar.timeby.gui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nectar.timeby.R;
import com.nectar.timeby.gui.MainSingleActivity;
import com.nectar.timeby.gui.util.OnDrawerStatusChangedListener;
import com.nectar.timeby.gui.util.OnDrawerToggleClickListener;


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

    private OnDrawerToggleClickListener mListener;
    private ImageView mDrawerToggle;

    private TextView mStartAPMText;
    private TextView mStartHourText;
    private TextView mStartMinText;
    private TextView mEndAPMText;
    private TextView mEndHourText;
    private TextView mEndMinText;
    private TextView mTimeIntervalText;
    private Button mSubmitButton;

    private boolean isEndSet;//用于切换起始时间的更改，当点击结束时间时为true
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
        Log.i(TAG, "onCreateView");
        View rootView = inflater
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

        mDrawerToggle = (ImageView) rootView.findViewById(R.id.imageView_drawer_toggle);
        mDrawerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDrawerToggleClick();
            }
        });
        initTimeWidget();

        //时钟
        mContainer = (RelativeLayout) rootView
                .findViewById(R.id.layout_main_dial);
        mClockImg = (ImageView) rootView
                .findViewById(R.id.imageView_main_dial);
        initClockWidget();

        //提交按钮
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskTypeSelectDialog taskTypeSelectDialog=new TaskTypeSelectDialog(getActivity());
                taskTypeSelectDialog.showDialog();

//                Bundle bundle = new Bundle();
//                bundle.putInt("CountDownTime", sumMin);
//                bundle.putInt("startHour", startHour);
//                bundle.putInt("startMin", startMin);
//                bundle.putInt("endHour", endHour);
//                bundle.putInt("endMin", endMin);
//                bundle.putString("startAPM", mStartAPMText.getText().toString());
//                bundle.putString("endAPM", mEndAPMText.getText().toString());
//                Intent intToSingle = new Intent(getActivity(), MainSingleActivity.class);
//                intToSingle.putExtras(bundle);
//                startActivity(intToSingle);
            }
        });


        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void initTimeWidget() {
        //设置初始时间
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int min = Calendar.getInstance().get(Calendar.MINUTE);
        boolean isAm = hour >= 0 && hour < 12 ? true : false;
        if (isAm) {
            mStartAPMText.setText("AM");
            mEndAPMText.setText("AM");
        } else {
            mStartAPMText.setText("PM");
            mEndAPMText.setText("PM");
        }
        hour = hour % 12;
        mStartHourText.setText(hour > 9 ? "" + hour : "0" + hour);
        mStartMinText.setText(min > 9 ? "" + min : "0" + min);
        mEndHourText.setText(hour > 9 ? "" + hour : "0" + hour);
        mEndMinText.setText(min > 9 ? "" + min : "0" + min);

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
            setTimeText(currHour, currMinu);
        }


    }

    /**
     * 根据时钟指针变化，将时间反馈到顶部显示框以及底部时差框
     * 在DialTouchListener中调用
     *
     * @param hour
     * @param min
     */
    private void setTimeText(int hour, int min) {
        String mHourStr = hour > 9 ? "" + hour : "0" + hour;
        String mMinStr = min > 9 ? "" + min : "0" + min;
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


    //时钟 by 上白泽稻叶
    //以下是时钟的控件与属性

    class TaskTypeSelectDialog {
        public static final int SOLO=0;
        public static final int COOP=1;
        public static final int PK=2;
        private Activity activity;

        private int windowWidth;
        private int windowHeight;
        private int dialogHeight;
        private RelativeLayout windowLayout;
        private RelativeLayout dialogLayout;
        private TextView solo;
        private TextView coop;
        private TextView pk;

        private void startSolo(){
            //
            //在此填上点击单人按钮后的动作
            //
        }
        private void startCoop(){
            //
            //在此填上点击合作按钮后的动作
            //
        }
        private void startPK(){
            //
            //在此填上点击PK按钮后的动作
            //
        }

        private void removeDialog(){
            mSubmitButton.setEnabled(true);
            Animation dialogDisappear = new TranslateAnimation(0, 0, 0, dialogHeight);
            dialogDisappear.setDuration(500);
            dialogDisappear.setFillEnabled(true);
            dialogDisappear.setFillAfter(true);
            dialogDisappear.setInterpolator(new LinearInterpolator());
            dialogDisappear.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ((ViewGroup)windowLayout.getParent()).removeView(windowLayout);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            dialogLayout.startAnimation(dialogDisappear);
        }

        public TaskTypeSelectDialog(Activity activity) {
            this.activity = activity;

            windowWidth = activity.getWindowManager().getDefaultDisplay().getWidth();
            dialogHeight = (int) (0.18 * activity.getWindowManager().getDefaultDisplay().getHeight());
        }

        public void showDialog() {
            mSubmitButton.setEnabled(false);

            windowLayout = new RelativeLayout(activity);
            RelativeLayout.LayoutParams windowLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            dialogLayout = new RelativeLayout(activity);
            dialogLayout.setBackgroundColor(Color.rgb(173, 204, 173));
            RelativeLayout.LayoutParams dialogLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight);
            dialogLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            TextView selectTaskType = new TextView(activity);
            selectTaskType.setText(R.string.select_task_type);
            selectTaskType.setTextColor(Color.rgb(255, 255, 255));
            selectTaskType.setGravity(Gravity.BOTTOM);
            selectTaskType.setTextSize(dialogHeight / 10);
            RelativeLayout.LayoutParams selectTaskTypeParams = new RelativeLayout.LayoutParams((int) (windowWidth * 0.9), (int) (dialogHeight * 0.33));
            selectTaskTypeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            selectTaskTypeParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

            solo = new TextView(activity);
            solo.setText(R.string.task_type_solo);
            solo.setTextColor(Color.rgb(255, 255, 227));
            solo.setTextSize(dialogHeight / 6);
            solo.setGravity(Gravity.CENTER);
            RelativeLayout.LayoutParams soloParams = new RelativeLayout.LayoutParams(windowWidth / 3, (int) (dialogHeight * 0.67));
            soloParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            soloParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            coop = new TextView(activity);
            coop.setText(R.string.task_type_coop);
            coop.setTextSize(dialogHeight / 6);
            coop.setTextColor(Color.rgb(255, 255, 227));
            coop.setGravity(Gravity.CENTER);
            RelativeLayout.LayoutParams coopParams = new RelativeLayout.LayoutParams(windowWidth / 3, (int) (dialogHeight * 0.67));
            coopParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            coopParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            pk = new TextView(activity);
            pk.setText(R.string.task_type_pk);
            pk.setTextSize(dialogHeight / 6);
            pk.setTextColor(Color.rgb(255, 255, 227));
            pk.setGravity(Gravity.CENTER);
            RelativeLayout.LayoutParams pkParams = new RelativeLayout.LayoutParams(windowWidth / 3, (int) (dialogHeight * 0.67));
            pkParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            pkParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            dialogLayout.addView(selectTaskType, selectTaskTypeParams);
            dialogLayout.addView(solo, soloParams);
            dialogLayout.addView(coop, coopParams);
            dialogLayout.addView(pk, pkParams);
            windowLayout.addView(dialogLayout, dialogLayoutParams);
            activity.addContentView(windowLayout, windowLayoutParams);

            ViewTreeObserver vto = windowLayout.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    windowLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    windowHeight = windowLayout.getHeight();
                }
            });

            Animation popUp = new TranslateAnimation(0, 0, dialogHeight, 0);
            popUp.setDuration(500);
            popUp.setInterpolator(new DecelerateInterpolator());
            popUp.setAnimationListener(new PopUpEndListener());
            dialogLayout.startAnimation(popUp);
        }

        private class PopUpEndListener implements Animation.AnimationListener {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                solo.setOnTouchListener(new SelectTaskTypeListener(TaskTypeSelectDialog.SOLO));
                coop.setOnTouchListener(new SelectTaskTypeListener(TaskTypeSelectDialog.COOP));
                pk.setOnTouchListener(new SelectTaskTypeListener(TaskTypeSelectDialog.PK));
                windowLayout.setOnTouchListener(new dialogCancelListener());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        }

        private class SelectTaskTypeListener implements View.OnTouchListener {
            private int taskType;

            SelectTaskTypeListener(int taskType){
                this.taskType=taskType;
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int eventType=event.getAction();
                if(taskType==TaskTypeSelectDialog.SOLO){
                    if(eventType==MotionEvent.ACTION_DOWN){
                        solo.setBackgroundColor(Color.rgb(227, 225, 223));
                        solo.getPaint().setFakeBoldText(true);
                        solo.setTextColor(Color.rgb(130, 180, 130));
                        return true;
                    }
                    else if(eventType==MotionEvent.ACTION_MOVE){
                        return true;
                    }
                    else if(eventType==MotionEvent.ACTION_UP){
                        solo.setBackgroundColor(Color.argb(0, 255, 255, 255));
                        solo.getPaint().setFakeBoldText(false);
                        solo.setTextColor(Color.rgb(255, 255, 227));
                        int pointX=(int)event.getX();
                        int pointY=(int)event.getY();
                        if(pointX>0&&pointX<windowWidth/3&&pointY>0&&pointY<(int)(dialogHeight*0.67)){
                            startSolo();
                            removeDialog();
                        }
                        return false;
                    }
                }
                else if(taskType==TaskTypeSelectDialog.COOP){
                    if(eventType==MotionEvent.ACTION_DOWN){
                        coop.setBackgroundColor(Color.rgb(227, 225, 223));
                        coop.getPaint().setFakeBoldText(true);
                        coop.setTextColor(Color.rgb(130, 180, 130));
                        return true;
                    }
                    else if(eventType==MotionEvent.ACTION_MOVE){
                        return true;
                    }
                    else if(eventType==MotionEvent.ACTION_UP){
                        coop.setBackgroundColor(Color.argb(0, 255, 255, 255));
                        coop.getPaint().setFakeBoldText(false);
                        coop.setTextColor(Color.rgb(255, 255, 227));
                        int pointX=(int)event.getX();
                        int pointY=(int)event.getY();
                        if(pointX>0&&pointX<windowWidth/3&&pointY>0&&pointY<(int)(dialogHeight*0.67)){
                            startCoop();
                            removeDialog();
                        }
                        return false;
                    }
                }
                else if(taskType==TaskTypeSelectDialog.PK){
                    if(eventType==MotionEvent.ACTION_DOWN){
                        pk.setBackgroundColor(Color.rgb(227, 225, 223));
                        pk.getPaint().setFakeBoldText(true);
                        pk.setTextColor(Color.rgb(130, 180, 130));
                        return true;
                    }
                    else if(eventType==MotionEvent.ACTION_MOVE){
                        return true;
                    }
                    else if(eventType==MotionEvent.ACTION_UP){
                        pk.setBackgroundColor(Color.argb(0, 255, 255, 255));
                        pk.getPaint().setFakeBoldText(false);
                        pk.setTextColor(Color.rgb(255, 255, 227));
                        int pointX=(int)event.getX();
                        int pointY=(int)event.getY();
                        if(pointX>0&&pointX<windowWidth/3&&pointY>0&&pointY<(int)(dialogHeight*0.67)){
                            startPK();
                            removeDialog();
                        }
                        return false;
                    }
                }
                else{
                    System.out.println("The task type in SelectTaskTypeListener wasn't set correctly!");
                    return false;
                }
                return false;
            }
        }

        private class dialogCancelListener implements View.OnTouchListener{

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int pointX=(int)event.getX();
                int pointY=(int)event.getY();
                if(pointX>0&&pointX<windowWidth&&pointY>0&&pointY<windowHeight-dialogHeight){
                    removeDialog();
                }
                return false;
            }
        }
    }

    private MinuteHandView minuteHandView = null;
    private HourHandView hourHandView = null;
    private double currMinuHandDegr = 0.0;
    private double currHourHandDegr = 0.0;
    private int currMinu;
    private int currHour;
    private int dialWidth = 0;
    private RelativeLayout mContainer;
    private ImageView mClockImg;

    private void initClockWidget() {
        currHour = Calendar.getInstance().get(Calendar.HOUR);
        currMinu = Calendar.getInstance().get(Calendar.MINUTE);
        currHourHandDegr = currHour * 30 + currMinu * 0.5;
        currMinuHandDegr = currMinu * 6;

        dialWidth = (int) (getActivity().getWindowManager().getDefaultDisplay().getWidth() / 2 * 1.2);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dialWidth, dialWidth);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        mContainer.setLayoutParams(layoutParams);
        int padding = (int) (0.15 * dialWidth);
        mClockImg.setPadding(padding, padding, padding, padding);
        minuteHandView = new MinuteHandView(getActivity(), (float) currMinuHandDegr);
        hourHandView = new HourHandView(getActivity(), (float) currHourHandDegr);
        mContainer.addView(minuteHandView);
        mContainer.addView(hourHandView);
        mContainer.setOnTouchListener(new DialTouchListener());
    }

    class MinuteHandView extends View {
        private float degree;

        public MinuteHandView(Context context, float degree) {
            super(context);
            this.degree = degree;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Bitmap minuteHandBitmap = BitmapFactory.decodeResource(
                    getActivity().getResources(), R.drawable.img_clock_minute_hand);
            Matrix matrix = new Matrix();
            float percent = (float) ((double) dialWidth / 2
                    / (double) minuteHandBitmap.getHeight());
            matrix.postScale(percent, percent);
            matrix.postTranslate(dialWidth / 2 - minuteHandBitmap.getWidth() * percent / 2,
                    dialWidth / 2 - minuteHandBitmap.getHeight() * percent);
            matrix.postRotate(degree, dialWidth / 2, dialWidth / 2);
            canvas.drawBitmap(minuteHandBitmap, matrix, new Paint());
        }
    }

    class HourHandView extends View {
        private float degree;

        public HourHandView(Context context, float degree) {
            super(context);
            this.degree = degree;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Bitmap hourHandBitmap = BitmapFactory.decodeResource(
                    getActivity().getResources(), R.drawable.img_clock_hour_hand);
            Matrix matrix = new Matrix();
            float percent = 0.7f * (float) ((double) dialWidth / 2 /
                    (double) hourHandBitmap.getHeight());
            matrix.postScale(percent * 1.5f, percent);
            matrix.postTranslate(dialWidth / 2 - hourHandBitmap.getWidth() * percent
                    * 1.5f / 2, dialWidth / 2 - hourHandBitmap.getHeight() * percent);
            matrix.postRotate(degree, dialWidth / 2, dialWidth / 2);
            canvas.drawBitmap(hourHandBitmap, matrix, new Paint());
        }
    }

    class DialTouchListener implements View.OnTouchListener {
        private boolean isMinuHandMove = false;
        private boolean isHourHandMove = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            double x, y;
            x = (double) event.getX();
            y = (double) event.getY();
            double dial_layout_width = (double) (mContainer.findViewById(R.id.layout_main_dial))
                    .getWidth();
            double radius = Math.sqrt(Math.pow((x - dial_layout_width / 2), 2) +
                    Math.pow((y - dial_layout_width / 2), 2));
            int eventType = event.getAction();
            if (eventType == MotionEvent.ACTION_DOWN && radius > dialWidth / 2)
                return false;
            double degree = 90 - 180 / 3.1415926 *
                    (Math.asin((dial_layout_width / 2 - y) / radius));
            if ((x - dial_layout_width / 2) < 0)
                degree = 360 - degree;

            //当鼠标刚刚点击的时候（Down）
            if (eventType == MotionEvent.ACTION_DOWN) {
                double handsAngle = Math.abs(currMinuHandDegr - currHourHandDegr);
                if (handsAngle > 180.0)
                    handsAngle = 360.0 - handsAngle;
                if (handsAngle <= 10.0) {
                    if (currMinuHandDegr >= 0.0 && currMinuHandDegr < 15.0) {
                        if ((degree < (currMinuHandDegr + 15.0) && degree >= 0.0)
                                || (degree > (345.0 + currMinuHandDegr) && degree <= 360.0)) {
                            isMinuHandMove = true;
                        } else
                            return false;
                    } else if (currMinuHandDegr <= 360.0 && currMinuHandDegr > 345.0) {
                        if ((degree > (currMinuHandDegr - 15.0) && degree <= 360.0)
                                || (degree < (currMinuHandDegr - 345.0) && degree >= 0.0)) {
                            isMinuHandMove = true;
                        } else
                            return false;
                    } else {
                        if (degree < (currMinuHandDegr + 15.0)
                                && (degree > currMinuHandDegr - 15.0)) {
                            isMinuHandMove = true;
                        } else
                            return false;
                    }
                } else {
                    if (currMinuHandDegr >= 0.0 && currMinuHandDegr < 15.0) {
                        if ((degree < (currMinuHandDegr + 15.0) && degree >= 0.0)
                                || (degree > (345.0 + currMinuHandDegr) && degree <= 360.0)) {
                            isMinuHandMove = true;
                        }
                    } else if (currMinuHandDegr <= 360.0 && currMinuHandDegr > 345.0) {
                        if ((degree > (currMinuHandDegr - 15.0) && degree <= 360.0)
                                || (degree < (currMinuHandDegr - 345.0) && degree >= 0.0)) {
                            isMinuHandMove = true;
                        }
                    } else {
                        if (degree < (currMinuHandDegr + 15.0)
                                && (degree > currMinuHandDegr - 15.0)) {
                            isMinuHandMove = true;
                        }
                    }

                    //时针检测
                    if (isMinuHandMove == false) {
                        if (currHourHandDegr >= 0.0 && currHourHandDegr < 15.0) {
                            if ((degree < (currHourHandDegr + 15.0) && degree >= 0.0)
                                    || (degree > (345.0 + currHourHandDegr) && degree <= 360.0)) {
                                isHourHandMove = true;
                            } else
                                return false;
                        } else if (currHourHandDegr <= 360.0 && currHourHandDegr > 345.0) {
                            if ((degree > (currHourHandDegr - 15.0) && degree <= 360.0)
                                    || (degree < (currHourHandDegr - 345.0) && degree >= 0.0)) {
                                isHourHandMove = true;
                            } else
                                return false;
                        } else {
                            if (degree < (currHourHandDegr + 15.0)
                                    && (degree > currHourHandDegr - 15.0)) {
                                isHourHandMove = true;
                            } else
                                return false;
                        }
                    }
                }
            } else if (eventType == MotionEvent.ACTION_MOVE) {
                if (isMinuHandMove == true) {
                    //isToIncreHour用来判断分针是否从59分转到0分从而将时针值加1
                    boolean isToIncreHour = false;
                    //isToDecreHour用来判断分针是否从0分转到59分从而将时针值减1
                    boolean isToDecreHour = false;
                    //判断分针的角度是增加了还是减小了
                    if (currMinuHandDegr > 330.0 && degree < 30.0) {
                        isToIncreHour = true;
                    } else if (currMinuHandDegr < 30.0 && degree > 330.0) {
                        isToDecreHour = true;
                    }
                    mContainer.removeView(minuteHandView);
                    currMinuHandDegr = degree;
                    setMinuByDegree();

                    //修改界面时间
                    setTimeText(currHour, currMinu);

                    minuteHandView = new MinuteHandView(getActivity(), (float) degree);
                    mContainer.addView(minuteHandView);
                    //重置时针位置
                    if (isToIncreHour)
                        currHour = (currHour + 1) % 12;
                    else if (isToDecreHour)
                        currHour = (currHour + 11) % 12;
                    currHourHandDegr = currHour * 30 + currMinu * 0.5;
                    mContainer.removeView(hourHandView);
                    hourHandView = new HourHandView(getActivity(), (float) currHourHandDegr);
                    mContainer.addView(hourHandView);
                } else if (isHourHandMove == true) {
                    mContainer.removeView(hourHandView);
                    currHourHandDegr = degree;
                    setHourByDegree();
                    //修改界面时间
                    setTimeText(currHour, currMinu);

                    hourHandView = new HourHandView(getActivity(), (float) degree);
                    mContainer.addView(hourHandView);
                }
            } else if (eventType == MotionEvent.ACTION_UP) {
                if (isMinuHandMove == true) {
                    //isToIncreHour用来判断分针是否从59分转到0分从而将时针值加1
                    boolean isToIncreHour = false;
                    //isToDecreHour用来判断分针是否从0分转到59分从而将时针值减1
                    boolean isToDecreHour = false;
                    //判断分针的角度是增加了还是减小了
                    if (currMinuHandDegr > 330.0 && degree < 30.0) {
                        isToIncreHour = true;
                    } else if (currMinuHandDegr < 30.0 && degree > 330.0) {
                        isToDecreHour = true;
                    }
                    mContainer.removeView(minuteHandView);
                    currMinuHandDegr = degree;
                    setMinuByDegree();

                    //修改界面时间
                    setTimeText(currHour, currMinu);

                    minuteHandView = new MinuteHandView(getActivity(), (float) degree);
                    mContainer.addView(minuteHandView);
                    //重置时针位置
                    if (isToIncreHour)
                        currHour = (currHour + 1) % 12;
                    else if (isToDecreHour)
                        currHour = (currHour + 11) % 12;
                    currHourHandDegr = currHour * 30 + currMinu * 0.5;
                    mContainer.removeView(hourHandView);
                    hourHandView = new HourHandView(getActivity(), (float) currHourHandDegr);
                    mContainer.addView(hourHandView);
                    isMinuHandMove = false;
                } else if (isHourHandMove == true) {
                    mContainer.removeView(hourHandView);
                    currHourHandDegr = degree;
                    setHourByDegree();
                    //修改界面时间
                    setTimeText(currHour, currMinu);

                    hourHandView = new HourHandView(getActivity(), (float) degree);
                    mContainer.addView(hourHandView);
                    isHourHandMove = false;
                }
            }
            return true;
        }
    }

    private void setMinuByDegree() {
        currMinu = (int) (currMinuHandDegr / 6);
    }

    private void setHourByDegree() {
        currHour = (int) ((currHourHandDegr + 5) / 30);
        if (currHour == 12)
            currHour = 0;
    }
}