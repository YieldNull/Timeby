package com.nectar.timeby.gui.widget;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nectar.timeby.R;

/**
 * Created by finalize on 7/20/15.
 */
//时钟 by 上白泽稻叶
//以下是时钟的控件与属性

class TaskTypeSelectDialog {
    public static final int SOLO = 0;
    public static final int COOP = 1;
    public static final int PK = 2;
    private Activity activity;

    private int windowWidth;
    private int windowHeight;
    private int dialogHeight;
    private RelativeLayout windowLayout;
    private RelativeLayout dialogLayout;
    private TextView solo;
    private TextView coop;
    private TextView pk;

    private void startSolo() {
        //
        //在此填上点击单人按钮后的动作
        //
    }

    private void startCoop() {
        //
        //在此填上点击合作按钮后的动作
        //
    }

    private void startPK() {
        //
        //在此填上点击PK按钮后的动作
        //
    }

    private void removeDialog() {
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
                ((ViewGroup) windowLayout.getParent()).removeView(windowLayout);
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

        SelectTaskTypeListener(int taskType) {
            this.taskType = taskType;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int eventType = event.getAction();
            if (taskType == TaskTypeSelectDialog.SOLO) {
                if (eventType == MotionEvent.ACTION_DOWN) {
                    solo.setBackgroundColor(Color.rgb(227, 225, 223));
                    solo.getPaint().setFakeBoldText(true);
                    solo.setTextColor(Color.rgb(130, 180, 130));
                    return true;
                } else if (eventType == MotionEvent.ACTION_MOVE) {
                    return true;
                } else if (eventType == MotionEvent.ACTION_UP) {
                    solo.setBackgroundColor(Color.argb(0, 255, 255, 255));
                    solo.getPaint().setFakeBoldText(false);
                    solo.setTextColor(Color.rgb(255, 255, 227));
                    int pointX = (int) event.getX();
                    int pointY = (int) event.getY();
                    if (pointX > 0 && pointX < windowWidth / 3 && pointY > 0 && pointY < (int) (dialogHeight * 0.67)) {
                        startSolo();
                        removeDialog();
                    }
                    return false;
                }
            } else if (taskType == TaskTypeSelectDialog.COOP) {
                if (eventType == MotionEvent.ACTION_DOWN) {
                    coop.setBackgroundColor(Color.rgb(227, 225, 223));
                    coop.getPaint().setFakeBoldText(true);
                    coop.setTextColor(Color.rgb(130, 180, 130));
                    return true;
                } else if (eventType == MotionEvent.ACTION_MOVE) {
                    return true;
                } else if (eventType == MotionEvent.ACTION_UP) {
                    coop.setBackgroundColor(Color.argb(0, 255, 255, 255));
                    coop.getPaint().setFakeBoldText(false);
                    coop.setTextColor(Color.rgb(255, 255, 227));
                    int pointX = (int) event.getX();
                    int pointY = (int) event.getY();
                    if (pointX > 0 && pointX < windowWidth / 3 && pointY > 0 && pointY < (int) (dialogHeight * 0.67)) {
                        startCoop();
                        removeDialog();
                    }
                    return false;
                }
            } else if (taskType == TaskTypeSelectDialog.PK) {
                if (eventType == MotionEvent.ACTION_DOWN) {
                    pk.setBackgroundColor(Color.rgb(227, 225, 223));
                    pk.getPaint().setFakeBoldText(true);
                    pk.setTextColor(Color.rgb(130, 180, 130));
                    return true;
                } else if (eventType == MotionEvent.ACTION_MOVE) {
                    return true;
                } else if (eventType == MotionEvent.ACTION_UP) {
                    pk.setBackgroundColor(Color.argb(0, 255, 255, 255));
                    pk.getPaint().setFakeBoldText(false);
                    pk.setTextColor(Color.rgb(255, 255, 227));
                    int pointX = (int) event.getX();
                    int pointY = (int) event.getY();
                    if (pointX > 0 && pointX < windowWidth / 3 && pointY > 0 && pointY < (int) (dialogHeight * 0.67)) {
                        startPK();
                        removeDialog();
                    }
                    return false;
                }
            } else {
                System.out.println("The task type in SelectTaskTypeListener wasn't set correctly!");
                return false;
            }
            return false;
        }
    }

    private class dialogCancelListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int pointX = (int) event.getX();
            int pointY = (int) event.getY();
            if (pointX > 0 && pointX < windowWidth && pointY > 0 && pointY < windowHeight - dialogHeight) {
                removeDialog();
            }
            return false;
        }
    }
}