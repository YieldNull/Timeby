package com.nectar.timeby.gui.fragment;

import android.app.Activity;
import android.content.Context;
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

import com.nectar.timeby.R;

public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";

    private Activity mActivity;
    private OnToggleClickListener mListener;
    private ClockWidget mLeftClockWidget;
    private ClockWidget mRightClockWidget;

    public MainFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View rootView = inflater
                .inflate(R.layout.fragment_main, container, false);
        mLeftClockWidget = new ClockWidget((ViewGroup) rootView
                .findViewById(R.id.layout_dial_left));
        mRightClockWidget = new ClockWidget((ViewGroup) rootView
                .findViewById(R.id.layout_dial_right));
        return rootView;
    }


    public interface OnToggleClickListener {
        public void onToggleClick(Fragment fragment, boolean addToBackStack);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;

        try {
            mListener = (OnToggleClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnToggleClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private class ClockWidget {
        private MinuteHandView minuteHandView = null;
        private HourHandView hourHandView = null;
        private double currMinuHandDegr = 0.0;
        private double currHourHandDegr = 0.0;

        private ViewGroup mContainer;

        /**
         * 传递时钟所在ViewGroup,时针、秒针将会画在其中，同时会设置拨动监听
         * by finalize 2015.7.14
         */
        public ClockWidget(ViewGroup container) {
            mContainer = container;
            minuteHandView = new MinuteHandView(mActivity, 0);
            hourHandView = new HourHandView(mActivity, 0);
            container.addView(minuteHandView);
            container.addView(hourHandView);
            container.setOnTouchListener(new DialTouchListener());
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
                        getResources(), R.drawable.clock_minute_hand);
                Matrix matrix = new Matrix();
                float percent = (float) ((double) getHeight() / 2 / (double) minuteHandBitmap.getHeight());
                matrix.postScale(percent, percent);
                matrix.postTranslate(getWidth() / 2 - minuteHandBitmap.getWidth() * percent / 2,
                        getHeight() / 2 - minuteHandBitmap.getHeight() * percent);
                matrix.postRotate(degree, getWidth() / 2, getHeight() / 2);
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
                Bitmap hourHandBitmap = BitmapFactory.decodeResource(getResources(),
                        R.drawable.clock_minute_hand);
                Matrix matrix = new Matrix();
                float percent = 0.7f * (float) ((double) getHeight() / 2 / (double) hourHandBitmap.getHeight());
                matrix.postScale(percent * 1.5f, percent);
                matrix.postTranslate(getWidth() / 2 - hourHandBitmap.getWidth() * percent * 1.5f / 2,
                        getHeight() / 2 - hourHandBitmap.getHeight() * percent);
                matrix.postRotate(degree, getWidth() / 2, getHeight() / 2);
                canvas.drawBitmap(hourHandBitmap, matrix, new Paint());
            }
        }

        class DialTouchListener implements View.OnTouchListener {
            private boolean isMinuHandMove = false;
            private boolean isHourHandMove = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //TODO 计算旋转后所得时间

                double x, y;
                x = (double) event.getX();
                y = (double) event.getY();
                double dial_layout_width = (double) mContainer.getWidth();
                double radius = Math.sqrt(Math.pow((x - dial_layout_width / 2), 2)
                        + Math.pow((y - dial_layout_width / 2), 2));
                int enventType = event.getAction();
                if (enventType == MotionEvent.ACTION_DOWN && radius > 135)
                    return false;
                double degree = 90 - 180 / 3.1415926 * (Math.asin((dial_layout_width / 2 - y) / radius));
                if ((x - dial_layout_width / 2) < 0)
                    degree = 360 - degree;

                //当鼠标刚刚点击的时候（Down）
                if (enventType == MotionEvent.ACTION_DOWN) {
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
                } else if (enventType == MotionEvent.ACTION_MOVE) {
                    if (isMinuHandMove == true) {
                        mContainer.removeView(minuteHandView);
                        minuteHandView = new MinuteHandView(mActivity, (float) degree);
                        mContainer.addView(minuteHandView);
                    } else if (isHourHandMove == true) {
                        mContainer.removeView(hourHandView);
                        hourHandView = new HourHandView(mActivity, (float) degree);
                        mContainer.addView(hourHandView);
                    }
                } else if (enventType == MotionEvent.ACTION_UP) {
                    if (isMinuHandMove == true) {
                        mContainer.removeView(minuteHandView);
                        currMinuHandDegr = degree;
                        minuteHandView = new MinuteHandView(mActivity, (float) degree);
                        mContainer.addView(minuteHandView);
                        isMinuHandMove = false;
                    } else if (isHourHandMove == true) {
                        mContainer.removeView(hourHandView);
                        currHourHandDegr = degree;
                        hourHandView = new HourHandView(mActivity, (float) degree);
                        mContainer.addView(hourHandView);
                        isHourHandMove = false;
                    }
                }
                return true;
            }
        }
    }
}
