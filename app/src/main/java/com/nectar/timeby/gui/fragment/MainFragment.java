package com.nectar.timeby.gui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nectar.timeby.R;
import com.nectar.timeby.gui.MainPK;
import com.nectar.timeby.gui.util.OnDrawerStatusChangedListener;
import com.nectar.timeby.gui.util.OnDrawerToggleClickListener;

import java.util.Calendar;

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

    private boolean isEndSet;
    private boolean isEndOnSetting;
    private static int TYPE_START = 0x0001;
    private static int TYPE_END = 0x0002;

    int sumMin,startHour,startMin,endHour,endMin;

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

        //设置监听器
        mStartAPMText.setOnClickListener(new TimeWidgetOnClickListener(mStartAPMText, TYPE_START));
        mStartHourText.setOnClickListener(new TimeWidgetOnClickListener(null, TYPE_START));
        mStartMinText.setOnClickListener(new TimeWidgetOnClickListener(null, TYPE_START));
        mEndAPMText.setOnClickListener(new TimeWidgetOnClickListener(mEndAPMText, TYPE_END));
        mEndHourText.setOnClickListener(new TimeWidgetOnClickListener(null, TYPE_END));
        mEndMinText.setOnClickListener(new TimeWidgetOnClickListener(null, TYPE_END));
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("CountDownTime",sumMin);
                bundle.putInt("startHour",startHour);
                bundle.putInt("startMin",startMin);
                bundle.putInt("endHour",endHour);
                bundle.putInt("endMin",endMin);
                bundle.putString("startAPM",mStartAPMText.getText().toString());
                bundle.putString("endAPM",mEndAPMText.getText().toString());
                Intent intToSingle = new Intent(getActivity(), MainPK.class);
                intToSingle.putExtras(bundle);
                startActivity(intToSingle);
            }
        });
    }


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