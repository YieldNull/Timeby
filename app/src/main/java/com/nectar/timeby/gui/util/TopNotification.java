package com.nectar.timeby.gui.util;


import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nectar.timeby.R;

/**
 * Created by 上白泽稻叶 on 2015/7/17.
 */
public class TopNotification {
    private Activity activity;
    private String promptMess;
    private long duration;
    private int windowWidth;
    private int notificationHeight;

    public TopNotification(Activity activity, String promptMess, long duration) {
        this.activity = activity;
        this.promptMess = promptMess;
        this.duration = duration;
        windowWidth = activity.getWindowManager().getDefaultDisplay().getWidth();
        notificationHeight = (int) (0.16 * activity.getWindowManager().getDefaultDisplay().getHeight());
    }

    public void show() {
        RelativeLayout notificLayout = new RelativeLayout(activity);
        RelativeLayout.LayoutParams notificLayoutParams = new RelativeLayout.LayoutParams(windowWidth, notificationHeight);
        notificLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        ImageView bgImage = new ImageView(activity);
        bgImage.setImageResource(R.drawable.top_notific_bg);
        bgImage.setScaleType(ImageView.ScaleType.FIT_XY);
        RelativeLayout.LayoutParams bgImageParams = new RelativeLayout.LayoutParams((int) (windowWidth * 0.6), notificationHeight);
        bgImageParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        bgImageParams.addRule(RelativeLayout.CENTER_VERTICAL);

        notificLayout.addView(bgImage, bgImageParams);

        TextView notificMess = new TextView(activity);
        notificMess.setText(promptMess);
        RelativeLayout.LayoutParams notificMessParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        notificMessParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        notificMessParams.addRule(RelativeLayout.CENTER_VERTICAL);

        notificLayout.addView(notificMess, notificMessParams);

        activity.addContentView(notificLayout, notificLayoutParams);

        Animation action1 = new TranslateAnimation(0, 0, -notificationHeight, 0);
        action1.setDuration(1000);
        action1.setInterpolator(new DecelerateInterpolator());
        Animation action2 = new AlphaAnimation(1.0f, 1.0f);
        action2.setDuration(duration);
        Animation action3 = new AlphaAnimation(1.0f, 0);
        action3.setDuration(1200);
        action1.setAnimationListener(new AnimationJointListener(notificLayout, action2));
        action2.setAnimationListener(new AnimationJointListener(notificLayout, action3));
        action3.setAnimationListener(new AnimaRemoveNotificListener(notificLayout));
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringer = RingtoneManager.getRingtone(activity.getApplicationContext(), notification);
        ringer.play();
        notificLayout.startAnimation(action1);

        //((ViewGroup)notificLayout.getParent()).removeView(notificLayout);
        /*
        TextView notificView=new TextView(activity);
        notificView.setBackgroundColor(Color.rgb(142,142,149));
        notificView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        notificView.setText(promptMess);
        notificView.setTextSize((float)(0.4*notificationHeight));
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(windowWidth,notificationHeight);
        params.topMargin=0;
        params.gravity= Gravity.TOP|Gravity.CENTER_HORIZONTAL;
        activity.addContentView(notificView, params);
        */

        /*
        Animation action1=new TranslateAnimation(0,0,-notificationHeight,0);
        action1.setDuration(1000);
        action1.setInterpolator(new DecelerateInterpolator());
        Animation action2=new AlphaAnimation(1.0f,1.0f);
        action2.setDuration(duration);
        Animation action3=new AlphaAnimation(1.0f,0);
        action3.setDuration(1200);
        action1.setAnimationListener(new AnimationJointListener(notificView, action2));
        action2.setAnimationListener(new AnimationJointListener(notificView, action3));
        action3.setAnimationListener(new AnimaRemoveTextViewListener(notificView));
        notificView.startAnimation(action1);
        */
    }

    public class AnimationJointListener implements Animation.AnimationListener {

        private RelativeLayout notificView;
        private Animation nextAction;

        public AnimationJointListener(RelativeLayout notificView, Animation nextAction) {
            this.notificView = notificView;
            this.nextAction = nextAction;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            notificView.startAnimation(nextAction);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    public class AnimaRemoveNotificListener implements Animation.AnimationListener {

        private RelativeLayout notificView;

        public AnimaRemoveNotificListener(RelativeLayout notificView) {
            this.notificView = notificView;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            ((ViewGroup) notificView.getParent()).removeView(notificView);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}