package com.nectar.timeby.gui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nectar.timeby.R;
import com.nectar.timeby.util.HttpProcess;
import com.nectar.timeby.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class ResetPasswordPhoneActivity extends Activity {
    private static final String TAG = "ResetPasswordPhone";
    private static final String SEND_BUTTON_TEXT = "点击获取验证码";

    //有强迫症的小子发这么多消息干嘛啊啊啊啊啊啊啊
    private static final int MSG_RESEND_BUTTON_CHANGE = 0x0001;
    private static final int MSG_RESEND_BUTTON_RESET = 0x0002;
    private static final int MSG_SMSSDK_RECALL = 0x0003;
    private static final int MSG_PHONE_VALID = 0x0004;
    private static final int MSG_PHONE_INVALID = 0x0005;
    private static final int MSG_SERVER_ERROR = 0x0008;
    private static final int MSG_NET_INACTIVE = 0x0009;

    private static final int RESENT_TIME_INTERVAL = 60;
    private int mTimeRemain = RESENT_TIME_INTERVAL;
    private static final String mPhoneReg = "^(145|147|176)\\d{8}$|^(1700|1705|1709)\\d{7}$" +
            "|^1[38]\\d{9}$|^15[012356789]\\d{8}$";


    //记录发送短信时输入框内的手机号，防止申请验证码后改变手机号
    private String mPhoneStr = TAG;
    private String mUserStr;
    private String mPasswordStr;
    private boolean isCaptchaOnSending;

    private ImageView mSubmitText;
    private ImageView mBackText;
    private TextView mSendButton;

    private EditText mPhoneText;
    private EditText mCaptchaText;

    private Handler mHandler;
    private Timer mTimer;
    private TimerTask mTask;
    private EventHandler mSMSHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        Intent intent = getIntent();
        mUserStr = intent.getStringExtra(RegisterActivity.INTENT_EXTRA_USER);
        mPasswordStr = intent.getStringExtra(RegisterActivity.INTENT_EXTRA_PASSWORD);

        mSubmitText = (ImageView) findViewById(R.id.imageView_verify_phone_next);
        mSendButton = (TextView) findViewById(R.id.textView_verify_phone_send);
        mPhoneText = (EditText) findViewById(R.id.editText_verify_phone_phone);
        mCaptchaText = (EditText) findViewById(R.id.editText_verify_phone_captcha);
        mBackText = (ImageView) findViewById(R.id.imageView_verify_phone_return);

        initSMSSDK();
        initHandler();
        initSendButton();
        initSubmitButton();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Destroying...Unregister  all SMSSDK event handler");
        SMSSDK.unregisterAllEventHandler();
    }

    /**
     * 初始化SMSSDK,设置回调handler
     */
    private void initSMSSDK() {
        //初始化SMSSDK
        SMSSDK.initSDK(this, getResources().getString(R.string.smssdk_app_key),
                getResources().getString(R.string.smssdk_app_secret));

        //收到SDK的回调之后将消息发送给系统的Handler，用以更新UI
        mSMSHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int resultCode, Object data) {
                super.afterEvent(event, resultCode, data);
                Message msg = Message.obtain();
                msg.what = MSG_SMSSDK_RECALL;
                msg.arg1 = event;
                msg.arg2 = resultCode;
                msg.obj = data;
                mHandler.sendMessage(msg);
            }
        };

        //注册回调handler
        SMSSDK.registerEventHandler(mSMSHandler);
    }

    /**
     * 初始化提交按钮
     */
    private void initSubmitButton() {
        mSubmitText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先判断是否填写了验证码
                if (mCaptchaText.getText().length() != 0) {
                    if (mPhoneStr.equals(TAG)) {
                        Toast.makeText(ResetPasswordPhoneActivity.this,
                                "请先填发送验证短信", Toast.LENGTH_SHORT).show();
                    } else {
                        //发送验证信息
                        Log.i(TAG, mPhoneStr);
                        SMSSDK.submitVerificationCode("86", mPhoneStr,
                                mCaptchaText.getText().toString());
                    }
                } else {
                    if (mPhoneStr.equals(TAG)) {
                        Toast.makeText(ResetPasswordPhoneActivity.this,
                                "请先填发送验证短信", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ResetPasswordPhoneActivity.this,
                                "请先填写短信验证码", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mBackText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPasswordPhoneActivity.this.onBackPressed();
            }
        });
    }

    /**
     * 初始化重新发送功能
     */
    private void initSendButton() {

        //设置重发倒计时
        mTimer = new Timer();
        mTask = new MyTimerTask();
        mSendButton.setText(SEND_BUTTON_TEXT);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimeRemain == RESENT_TIME_INTERVAL) {
                    //电话号码合法则发送验证码
                    Pattern pattern = Pattern.compile(mPhoneReg);
                    Matcher matcher = pattern.matcher(mPhoneText.getText().toString());

                    if (matcher.matches()) {
                        //记录发送短信时的手机号
                        mPhoneStr = mPhoneText.getText().toString();
                        ResetPasswordPhoneActivity.this.verifyOrSave(true);//判断是否注册过
                    } else {
                        Toast.makeText(ResetPasswordPhoneActivity.this,
                                "请填写正确的电话号码", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * handler,用于更新UI
     */
    private void initHandler() {
        //初始化handler
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case MSG_RESEND_BUTTON_CHANGE:
                        //改变倒计时
                        mSendButton.setText("重新发送（"
                                + Integer.toString(msg.arg1)
                                + "秒)");
                        break;
                    case MSG_RESEND_BUTTON_RESET:
                        //重设发送倒计时
                        resetSendButton();
                        break;
                    case MSG_PHONE_VALID:
                        //手机号没被注册，发送验证码,开启倒计时，输入框右侧打钩
                        Log.i(TAG, "Valid phone number");
                        isCaptchaOnSending = true;
                        mTask = new MyTimerTask();
                        mTimer.scheduleAtFixedRate(mTask, 0, 1 * 1000);
                        Drawable rightImg = ResetPasswordPhoneActivity.this.getResources()
                                .getDrawable(R.drawable.icn_login_valid);
                        mPhoneText.setCompoundDrawablesWithIntrinsicBounds(null, null, rightImg, null);
                        Log.i(TAG, "Request captcha from server phone:" + mPhoneStr);
                        SMSSDK.getVerificationCode("86", mPhoneStr);
                        break;
                    case MSG_PHONE_INVALID:
                        //手机号已被注册，不发送验证码
                        Log.i(TAG, "Same phone has been registered");
                        Toast.makeText(ResetPasswordPhoneActivity.this, "手机号并未绑定，请重新输入",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_SERVER_ERROR:
                        //服务器错误
                        Log.i(TAG, "Server error");
                        Toast.makeText(ResetPasswordPhoneActivity.this, "服务器错误，请稍后再试",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_NET_INACTIVE:
                        //没有连接互联网
                        Log.i(TAG, "Network inactive");
                        Toast.makeText(ResetPasswordPhoneActivity.this, "无网络连接，请打开数据网络",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_SMSSDK_RECALL:
                        //接收SMSSDK的回调
                        int event = msg.arg1;
                        int result = msg.arg2;
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            switch (event) {
                                case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                                    //已发送验证码
                                    isCaptchaOnSending = false;
                                    Log.i(TAG, "Captcha has been sent");
                                    break;
                                case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
                                    //验证码正确
                                    isCaptchaOnSending = false;
                                    Log.i(TAG, "Valid Captcha");
                                    Log.i(TAG, "storing user info to server");
                                    resetSendButton();
//                                    verifyOrSave(false);
                                    startResetPasswdActivity();
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            if (isCaptchaOnSending) {
                                Log.i(TAG, "Captcha server error");
                                Toast.makeText(ResetPasswordPhoneActivity.this,
                                        "短信服务器异常，请重新申请验证码", Toast.LENGTH_SHORT).show();
                                resetSendButton();
                            } else {
                                Log.i(TAG, "Invalid Captcha");
                                Toast.makeText(ResetPasswordPhoneActivity.this,
                                        "验证码错误，请重新申请验证码", Toast.LENGTH_SHORT).show();
                                resetSendButton();
                            }
                            isCaptchaOnSending = false;
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    /**
     * 重新发送验证码，初始化参数
     */
    private void resetSendButton() {
        Log.i(TAG, "Send captcha button was reset");

        mTask.cancel();
        mSendButton.setText(SEND_BUTTON_TEXT);
        mTimeRemain = RESENT_TIME_INTERVAL;
        mCaptchaText.setText("");
    }

    /**
     * Timer要执行的任务，也就是每隔1秒向handler发送消息更新UI
     */
    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            if (mTimeRemain == 1) {
                cancel();
                mHandler.sendEmptyMessage(MSG_RESEND_BUTTON_RESET);
            } else {
                Message msg = Message.obtain();
                msg.what = MSG_RESEND_BUTTON_CHANGE;
                msg.arg1 = --mTimeRemain;

                mHandler.sendMessage(msg);
            }
        }
    }

    /**
     * 把用户信息存到本地，进入主界面
     */
    private void startResetPasswdActivity() {
        Intent intent = new Intent(ResetPasswordPhoneActivity.this,
                ResetPasswordActivity.class);
        intent.putExtra(ResetPasswordActivity.INTENT_PHONENUM, mPhoneStr);
        ResetPasswordPhoneActivity.this.startActivity(intent);
    }

    /**
     * 验证手机号是否已经被注册或将信息存到服务器
     *
     * @param isCheck 是否是验证手机号
     */
    private void verifyOrSave(final boolean isCheck) {
        if (!HttpUtil.isNetAvailable(this)) {
            mHandler.sendEmptyMessage(MSG_NET_INACTIVE);
            return;
        }

        if (isCheck) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = HttpProcess.checkPhoneNum(mPhoneStr);
                    try {
                        if (data.get("status").equals(-1)) {
                            //str = data.getString("errorStr");
                        } else if (data.get("status").equals(0)) {
                            mHandler.sendEmptyMessage(MSG_SERVER_ERROR);
                        } else if (data.get("status").equals(1)) {
                            if (data.getString("result").equals("true")) {
                                mHandler.sendEmptyMessage(MSG_PHONE_INVALID);
                            } else if (data.getString("result").equals("false")) {
                                mHandler.sendEmptyMessage(MSG_PHONE_VALID);
                            }
                        }
                    } catch (JSONException e) {
                        Log.i(TAG, e.getMessage());
                    }
                }
            }).start();
        }
    }
}
