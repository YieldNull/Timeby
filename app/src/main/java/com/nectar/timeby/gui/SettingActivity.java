package com.nectar.timeby.gui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nectar.timeby.R;
import com.nectar.timeby.util.PrefsUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import static com.nectar.timeby.R.id.setting_page_logout_arrow;

/**
 * Created by 上白泽丶稻叶 on 2015/7/18.
 */
public class SettingActivity extends Activity {
    public static final int BG_NOTIFIC_SWTICH = 0;
    public static final int MUSIC_EFFECT_SWTICH = 1;
    private static final String SHARE_ICON_NAME = "com.nectar.timeby.gui.SettingActivity.img.png";
    private static final String SHARE_URL = "http://finalize.sinaapp.com";

    private boolean currBgNotificState = false;
    private boolean currMusicEffectState = false;
    private boolean isButtonMoving = false;

    private ImageView switchBgBgNotific;
    private ImageView switchBgCoverBgNotific;
    private ImageView switchButtonBgNotific;
    private ImageView switchBgMusicEffect;
    private ImageView switchBgCoverMusicEffect;
    private ImageView switchButtonMusicEffect;

    private float slideDistance;
    private float switchBgWidth;
    private float switchBgHeight;
    private float stripWidth;
    private float stripHeight;
    private float returnViewWidth;
    private float returnViewHeight;

    private TextView returnView;
    private TextView chanPwdStripArrow;
    private TextView timeNotiStripArrow;
    private TextView feedbackStripArrow;
    private TextView shareStripArrorw;
    private TextView logoutStripArrow;

    private RelativeLayout chanPwdStrip;
    private RelativeLayout timeNotiStrip;
    private RelativeLayout feedbackStrip;
    private RelativeLayout shareStrip;
    private RelativeLayout logoutStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        switchBgBgNotific = (ImageView) findViewById(R.id.switch_bg_bg_noti);
        switchBgCoverBgNotific = (ImageView) findViewById(R.id.switch_bg_cover_bg_noti);
        switchButtonBgNotific = (ImageView) findViewById(R.id.switch_button_bg_noti);
        switchBgMusicEffect = (ImageView) findViewById(R.id.switch_bg_music_effect);
        switchBgCoverMusicEffect = (ImageView) findViewById(R.id.switch_bg_cover_music_effect);
        switchButtonMusicEffect = (ImageView) findViewById(R.id.switch_button_music_effect);
        returnView = (TextView) findViewById(R.id.setting_page_return);
        chanPwdStrip = (RelativeLayout) findViewById(R.id.setting_page_chan_pwd);
        chanPwdStripArrow = (TextView) findViewById(R.id.setting_page_chan_pwd_arrow);
        timeNotiStrip = (RelativeLayout) findViewById(R.id.setting_page_notify_time);
        timeNotiStripArrow = (TextView) findViewById(R.id.setting_page_notify_time_arrow);
        feedbackStrip = (RelativeLayout) findViewById(R.id.setting_page_feedback);
        feedbackStripArrow = (TextView) findViewById(R.id.setting_page_feedback_arrow);
        shareStrip = (RelativeLayout) findViewById(R.id.setting_page_share);
        shareStripArrorw = (TextView) findViewById(R.id.setting_page_share_arrow);
        logoutStrip = (RelativeLayout) findViewById(R.id.setting_page_logout);
        logoutStripArrow = (TextView) findViewById(setting_page_logout_arrow);

        ViewTreeObserver vto = chanPwdStrip.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                chanPwdStrip.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                slideDistance = switchBgBgNotific.getWidth() - switchButtonBgNotific.getWidth();
                switchBgWidth = switchBgBgNotific.getWidth();
                switchBgHeight = switchBgBgNotific.getHeight();
                returnViewHeight = returnView.getHeight();
                returnViewWidth = returnView.getWidth();
                stripHeight = chanPwdStrip.getHeight();
                stripWidth = chanPwdStrip.getWidth();
            }
        });

        initSwitchByState();

        returnView.setOnTouchListener(new ReturnListener());

        switchBgBgNotific.setOnTouchListener(new SwitchButtonListener(SettingActivity.BG_NOTIFIC_SWTICH));
        switchBgMusicEffect.setOnTouchListener(new SwitchButtonListener(SettingActivity.MUSIC_EFFECT_SWTICH));
        chanPwdStrip.setOnTouchListener(new SettingStripListener(chanPwdStrip, chanPwdStripArrow));
        timeNotiStrip.setOnTouchListener(new SettingStripListener(timeNotiStrip, timeNotiStripArrow));
        feedbackStrip.setOnTouchListener(new SettingStripListener(feedbackStrip, feedbackStripArrow));
        shareStrip.setOnTouchListener(new SettingStripListener(shareStrip, shareStripArrorw));
        logoutStrip.setOnTouchListener(new SettingStripListener(logoutStrip, logoutStripArrow));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    private class ReturnListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            TextPaint textPaint = returnView.getPaint();
            int eventAction = event.getAction();
            if (eventAction == MotionEvent.ACTION_DOWN) {
                textPaint.setFakeBoldText(true);
                returnView.setTextColor(Color.rgb(130, 180, 130));
            }
            if (eventAction == MotionEvent.ACTION_UP) {
                textPaint.setFakeBoldText(false);
                returnView.setTextColor(Color.rgb(173, 204, 173));
                int pointX = (int) event.getX();
                int pointY = (int) event.getY();
                if (pointX > 0 && pointX < returnViewWidth && pointY > 0 && pointY < returnViewHeight) {
                    onBackPressed();
                }
                return false;
            }
            return true;
        }
    }

    private class SettingStripListener implements View.OnTouchListener {
        private RelativeLayout relativeLayout;
        private TextView arrow;

        SettingStripListener(RelativeLayout relativeLayout, TextView arrow) {
            this.relativeLayout = relativeLayout;
            this.arrow = arrow;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            TextPaint textPaint = arrow.getPaint();
            int eventAction = event.getAction();
            if (eventAction == MotionEvent.ACTION_DOWN) {
                textPaint.setFakeBoldText(true);
                arrow.setTextColor(Color.rgb(130, 180, 130));
                relativeLayout.setBackgroundColor(Color.rgb(227, 225, 223));
            }
            if (eventAction == MotionEvent.ACTION_UP) {
                relativeLayout.setBackgroundColor(Color.argb(0, 225, 225, 225));
                textPaint.setFakeBoldText(false);
                arrow.setTextColor(Color.rgb(173, 204, 173));
                int pointX = (int) event.getX();
                int pointY = (int) event.getY();
                if (pointX > 0 && pointX < stripWidth && pointY > 0 && pointY < stripHeight) {
                    Intent intent = new Intent();
                    if (relativeLayout == chanPwdStrip)
                        intent.setClass(SettingActivity.this, ResetPasswordPhoneActivity.class);
                    else if (relativeLayout == timeNotiStrip)
                        intent.setClass(SettingActivity.this, SettingNotifyActivity.class);
                    else if (relativeLayout == shareStrip)
                        showShare();
                    else if (relativeLayout == feedbackStrip)
                        intent.setClass(SettingActivity.this, SettingFeedbackActivity.class);
                    else {
                        intent.setClass(SettingActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PrefsUtil.logout(SettingActivity.this);
                    }

                    if (relativeLayout != shareStrip) {
                        startActivity(intent);
                    }
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
                return false;
            }
            return true;
        }
    }

    private class SwitchButtonListener implements View.OnTouchListener {
        private int switchID;

        SwitchButtonListener(int switchID) {
            this.switchID = switchID;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (isButtonMoving) {
                return false;
            }
            int eventAction = event.getAction();
            int pointX = (int) event.getX();
            int pointY = (int) event.getY();
            if (eventAction == MotionEvent.ACTION_UP) {
                if (pointX < 0 || pointX > switchBgWidth || pointY < 0 || pointY > switchBgHeight)
                    return false;
                if (switchID == SettingActivity.BG_NOTIFIC_SWTICH) {
                    if (currBgNotificState) {
                        Animation action1 = new TranslateAnimation(0, -slideDistance, 0, 0);
                        action1.setFillEnabled(true);
                        action1.setFillBefore(true);
                        action1.setInterpolator(new LinearInterpolator());
                        action1.setDuration(150);
                        Animation action2 = new ScaleAnimation(0, 1.0f, 0.5f, 1.0f,
                                Animation.RELATIVE_TO_SELF, 0.97f,
                                Animation.RELATIVE_TO_SELF, 0.5f);
                        action2.setInterpolator(new LinearInterpolator());
                        action2.setDuration(150);
                        switchBgCoverBgNotific.setVisibility(View.VISIBLE);
                        switchButtonBgNotific.startAnimation(action1);
                        switchBgCoverBgNotific.startAnimation(action2);
                        currBgNotificState = false;
                        action1.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                isButtonMoving = true;
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) switchButtonBgNotific.getLayoutParams();
                                rlp.addRule(RelativeLayout.ALIGN_RIGHT, 0);
                                rlp.addRule(RelativeLayout.ALIGN_LEFT, R.id.switch_bg_bg_noti);
                                switchButtonBgNotific.setLayoutParams(rlp);
                                isButtonMoving = false;
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    } else {
                        Animation action1 = new TranslateAnimation(0, slideDistance, 0, 0);
                        action1.setFillEnabled(true);
                        action1.setFillBefore(true);
                        action1.setInterpolator(new LinearInterpolator());
                        action1.setDuration(150);
                        Animation action2 = new ScaleAnimation(1.0f, 0f, 1.0f, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.97f,
                                Animation.RELATIVE_TO_SELF, 0.5f);
                        action2.setInterpolator(new LinearInterpolator());
                        action2.setDuration(150);
                        switchBgCoverBgNotific.setVisibility(View.VISIBLE);
                        switchButtonBgNotific.startAnimation(action1);
                        switchBgCoverBgNotific.startAnimation(action2);
                        currBgNotificState = true;
                        action1.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                isButtonMoving = true;
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                switchBgCoverBgNotific.setVisibility(View.GONE);
                                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) switchButtonBgNotific.getLayoutParams();
                                rlp.addRule(RelativeLayout.ALIGN_LEFT, 0);
                                rlp.addRule(RelativeLayout.ALIGN_RIGHT, R.id.switch_bg_bg_noti);
                                switchButtonBgNotific.setLayoutParams(rlp);
                                isButtonMoving = false;
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                }
                if (switchID == SettingActivity.MUSIC_EFFECT_SWTICH) {
                    if (currMusicEffectState) {
                        Animation action1 = new TranslateAnimation(0, -slideDistance, 0, 0);
                        action1.setFillEnabled(true);
                        action1.setFillBefore(true);
                        action1.setInterpolator(new LinearInterpolator());
                        action1.setDuration(150);
                        Animation action2 = new ScaleAnimation(0, 1.0f, 0.5f, 1.0f,
                                Animation.RELATIVE_TO_SELF, 0.97f,
                                Animation.RELATIVE_TO_SELF, 0.5f);
                        action2.setInterpolator(new LinearInterpolator());
                        action2.setDuration(150);
                        switchBgCoverMusicEffect.setVisibility(View.VISIBLE);
                        switchButtonMusicEffect.startAnimation(action1);
                        switchBgCoverMusicEffect.startAnimation(action2);
                        currMusicEffectState = false;
                        action1.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                isButtonMoving = true;
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) switchButtonMusicEffect.getLayoutParams();
                                rlp.addRule(RelativeLayout.ALIGN_RIGHT, 0);
                                rlp.addRule(RelativeLayout.ALIGN_LEFT, R.id.switch_bg_music_effect);
                                switchButtonMusicEffect.setLayoutParams(rlp);
                                isButtonMoving = false;
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    } else {
                        Animation action1 = new TranslateAnimation(0, slideDistance, 0, 0);
                        action1.setFillEnabled(true);
                        action1.setFillBefore(true);
                        action1.setInterpolator(new LinearInterpolator());
                        action1.setDuration(150);
                        Animation action2 = new ScaleAnimation(1.0f, 0f, 1.0f, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.97f,
                                Animation.RELATIVE_TO_SELF, 0.5f);
                        action2.setInterpolator(new LinearInterpolator());
                        action2.setDuration(150);
                        switchBgCoverMusicEffect.setVisibility(View.VISIBLE);
                        switchButtonMusicEffect.startAnimation(action1);
                        switchBgCoverMusicEffect.startAnimation(action2);
                        currMusicEffectState = true;
                        action1.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                isButtonMoving = true;
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                switchBgCoverMusicEffect.setVisibility(View.GONE);
                                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) switchButtonMusicEffect.getLayoutParams();
                                rlp.addRule(RelativeLayout.ALIGN_LEFT, 0);
                                rlp.addRule(RelativeLayout.ALIGN_RIGHT, R.id.switch_bg_music_effect);
                                switchButtonMusicEffect.setLayoutParams(rlp);
                                isButtonMoving = false;
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                }
            }
            return true;
        }
    }

    private void initSwitchByState() {
        if (currBgNotificState) {
            switchBgCoverBgNotific.setVisibility(View.GONE);
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) switchButtonBgNotific.getLayoutParams();
            rlp.addRule(RelativeLayout.ALIGN_LEFT, 0);
            rlp.addRule(RelativeLayout.ALIGN_RIGHT, R.id.switch_bg_bg_noti);
            switchButtonBgNotific.setLayoutParams(rlp);
        }
        if (currMusicEffectState) {
            switchBgCoverMusicEffect.setVisibility(View.GONE);
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) switchButtonMusicEffect.getLayoutParams();
            rlp.addRule(RelativeLayout.ALIGN_LEFT, 0);
            rlp.addRule(RelativeLayout.ALIGN_RIGHT, R.id.switch_bg_music_effect);
            switchButtonMusicEffect.setLayoutParams(rlp);
        }
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("https://www.baidu.com");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是Timeby分享测试文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        Bitmap bitmap = BitmapFactory.decodeResource(SettingActivity.this.getResources(), R.drawable.logo_facebook);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = this.openFileOutput("shared_picture.png", Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        try {
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        oks.setImagePath(getFilesDir() + "/shared_picture.png");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("https://www.baidu.com");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("https://www.baidu.com");

        // 启动分享GUI
        oks.show(this);
    }
}
