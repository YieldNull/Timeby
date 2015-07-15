package com.nectar.timeby.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nectar.timeby.R;

import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


public class RegisterPhoneActivity extends Activity {

    private Button mRegisterButton;
    private Button mResendButton;
    private EditText mPhoneText;
    private EditText mCaptchaText;
    private Handler mHandler;

    private static final int MSG_CHANGE_RESEND_BUTTON = 0x0001;
    private static final int MSG_RESET_RESEND_BUTTON = 0x0002;

    private static final String RESEND_BUTTON_TEXT = "点击获取验证码";

    private Timer mTimer;
    private int mTimeRemain = 60;
    private EventHandler mSMSHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);

        initSMSSDK();

        mRegisterButton = (Button) findViewById(R.id.button_register_phone_login);
        mResendButton = (Button) findViewById(R.id.button_register_phone_send);
        mPhoneText = (EditText) findViewById(R.id.editText_register_phone_phone);
        mCaptchaText = (EditText) findViewById(R.id.editText_register_phone_captcha);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (msg.what == MSG_CHANGE_RESEND_BUTTON) {
                    mResendButton.setText("重新发送（"
                            + Integer.toString(msg.arg1)
                            + "秒)");
                } else if (msg.what == MSG_RESET_RESEND_BUTTON) {
                    mResendButton.setText(RESEND_BUTTON_TEXT);
                }
            }
        };

        mTimer = new Timer();
        mResendButton.setText(RESEND_BUTTON_TEXT);
        mResendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimeRemain == 60) {
                    mTimer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            if (mTimeRemain == 1) {
                                mTimer.cancel();
                                mHandler.sendEmptyMessage(MSG_RESET_RESEND_BUTTON);
                            }else {
                                Message msg = Message.obtain();
                                msg.what = MSG_CHANGE_RESEND_BUTTON;
                                msg.arg1 = --mTimeRemain;

                                mHandler.sendMessage(msg);
                            }
                        }
                    }, 0, 1 * 1000);
                }
            }
        });


        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeUserInfo();
            }
        });
    }


    private void initSMSSDK() {
        //初始化SMSSDK
        SMSSDK.initSDK(this, getResources().getString(R.string.smssdk_app_key),
                getResources().getString(R.string.smssdk_app_secret));

        mSMSHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int resultCode, Object data) {
                super.afterEvent(event, resultCode, data);
                if (resultCode == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                    }
                } else {
                    ((Throwable) data).printStackTrace();
                }
            }
        };

        SMSSDK.registerEventHandler(mSMSHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //给上层注册页面发送消息，让其finish
        Intent intent = new Intent();
        intent.putExtra(RegisterActivity.INTENT_EXTRA_SUCCESS_REGISTER, true);
        RegisterPhoneActivity.this.setResult(1, intent);
        RegisterPhoneActivity.this.finish();
        SMSSDK.unregisterAllEventHandler();
    }

    private void storeUserInfo() {
        //TODO 将用户信息存入ContentProvider以及本地数据库
    }

}
