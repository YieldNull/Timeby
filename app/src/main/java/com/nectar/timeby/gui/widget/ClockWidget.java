package com.nectar.timeby.gui.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nectar.timeby.R;

import java.util.Calendar;

/**
 * Created by 上白泽、稻叶.
 * Refactor by finalize.
 */
public class ClockWidget {
    private MinuteHandView minuteHandView = null;
    private HourHandView hourHandView = null;
    private double currMinuHandDegr = 0.0;
    private double currHourHandDegr = 0.0;
    private int currMinu;
    private int currHour;
    private int dialWidth = 0;
    private ViewGroup mContainer;
    private ImageView mClockImg;

    private Activity mActivity;
    private OnPointerMoveListener mPointerMoveListener;


    //时钟使用者需要设置OnPointerMoveListener才能在指针移动时实时获取时间
    public interface OnPointerMoveListener {
        void onPointerMove(int currentHour, int currentMin);
    }

    public void setOnPointerMoveListener(OnPointerMoveListener listener) {
        mPointerMoveListener = listener;
    }

    public ClockWidget(Activity activity, ViewGroup rootView) {
        mActivity = activity;
        mContainer = (ViewGroup) rootView.findViewById(R.id.layout_main_dial);
        mClockImg = (ImageView) rootView
                .findViewById(R.id.imageView_main_dial);

        initClockWidget();
    }


    /**
     * 初始化
     */
    private void initClockWidget() {
        currHour = Calendar.getInstance().get(Calendar.HOUR);
        currMinu = Calendar.getInstance().get(Calendar.MINUTE);
        currHourHandDegr = currHour * 30 + currMinu * 0.5;
        currMinuHandDegr = currMinu * 6;

        dialWidth = (int) (mActivity.getWindowManager().getDefaultDisplay().getWidth() / 2 * 1.2);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dialWidth, dialWidth);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        mContainer.setLayoutParams(layoutParams);
        int padding = (int) (0.15 * dialWidth);
        mClockImg.setPadding(padding, padding, padding, padding);
        minuteHandView = new MinuteHandView(mActivity, (float) currMinuHandDegr);
        hourHandView = new HourHandView(mActivity, (float) currHourHandDegr);
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
                    mActivity.getResources(), R.drawable.img_clock_minute_hand);
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
                    mActivity.getResources(), R.drawable.img_clock_hour_hand);
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
                    if (!isMinuHandMove) {
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
                if (isMinuHandMove) {
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

                    minuteHandView = new MinuteHandView(mActivity, (float) degree);
                    mContainer.addView(minuteHandView);
                    //重置时针位置
                    if (isToIncreHour)
                        currHour = (currHour + 1) % 12;
                    else if (isToDecreHour)
                        currHour = (currHour + 11) % 12;
                    currHourHandDegr = currHour * 30 + currMinu * 0.5;
                    mContainer.removeView(hourHandView);
                    hourHandView = new HourHandView(mActivity, (float) currHourHandDegr);
                    mContainer.addView(hourHandView);
                } else if (isHourHandMove) {
                    mContainer.removeView(hourHandView);
                    currHourHandDegr = degree;
                    setHourByDegree();

                    hourHandView = new HourHandView(mActivity, (float) degree);
                    mContainer.addView(hourHandView);
                }
            } else if (eventType == MotionEvent.ACTION_UP) {
                if (isMinuHandMove) {
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

                    minuteHandView = new MinuteHandView(mActivity, (float) degree);
                    mContainer.addView(minuteHandView);
                    //重置时针位置
                    if (isToIncreHour)
                        currHour = (currHour + 1) % 12;
                    else if (isToDecreHour)
                        currHour = (currHour + 11) % 12;
                    currHourHandDegr = currHour * 30 + currMinu * 0.5;
                    mContainer.removeView(hourHandView);
                    hourHandView = new HourHandView(mActivity, (float) currHourHandDegr);
                    mContainer.addView(hourHandView);
                    isMinuHandMove = false;
                } else if (isHourHandMove) {
                    mContainer.removeView(hourHandView);
                    currHourHandDegr = degree;
                    setHourByDegree();

                    hourHandView = new HourHandView(mActivity, (float) degree);
                    mContainer.addView(hourHandView);
                    isHourHandMove = false;
                }
            }
            return true;
        }
    }

    /**
     * 更改时间
     */
    private void setMinuByDegree() {
        currMinu = (int) (currMinuHandDegr / 6);

        //返回时间给widget使用者
        if (mPointerMoveListener != null)
            mPointerMoveListener.onPointerMove(currHour, currMinu);
    }

    /**
     * 更改时间
     */
    private void setHourByDegree() {
        currHour = (int) ((currHourHandDegr + 5) / 30);
        if (currHour == 12)
            currHour = 0;
        //返回时间给widget使用者
        if (mPointerMoveListener != null)
            mPointerMoveListener.onPointerMove(currHour, currMinu);
    }
}
