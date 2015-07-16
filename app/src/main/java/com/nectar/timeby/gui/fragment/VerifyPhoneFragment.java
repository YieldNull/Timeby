package com.nectar.timeby.gui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.nectar.timeby.R;
import com.nectar.timeby.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class VerifyPhoneFragment extends Fragment {
    private static final int MSG_RESEND_BUTTON_CHANGE = 0x0001;
    private static final int MSG_RESEND_BUTTON_RESET = 0x0002;
    private static final String SEND_BUTTON_TEXT = "点击获取验证码";
    private static final String TAG = "VerifyPhoneFragment";

    private static final int RESENT_TIME_INTERVAL = 60;
    private int mTimeRemain = RESENT_TIME_INTERVAL;

    //记录发送短信时输入框内的手机号，防止申请验证码后改变手机号
    private String mPhoneStr = "VerifyPhoneFragment";
    private String mUser;
    private String mPassword;

    private ImageButton mSubmitButton;
    private Button mSendButton;
    private ImageButton mBackButton;

    private EditText mPhoneText;
    private EditText mCaptchaText;

    private Handler mHandler;
    private Timer mTimer;
    private TimerTask mTask;
    private EventHandler mSMSHandler;


    private MainFragment.OnToggleClickListener mToggleClickListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_register_phone, container, false);

        mSubmitButton = (ImageButton) rootView.findViewById(R.id.button_register_phone_login);
        mSendButton = (Button) rootView.findViewById(R.id.button_register_phone_send);
        mPhoneText = (EditText) rootView.findViewById(R.id.editText_register_phone_phone);
        mCaptchaText = (EditText) rootView.findViewById(R.id.editText_register_phone_captcha);
        mBackButton = (ImageButton) rootView.findViewById(R.id.button_register_phone_back);

        initSMSSDK();
        initHandler();
        initTimer();
        initButton();
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mUser = getArguments().getString(RegisterFragment.INTENT_EXTRA_USER);
        mPassword = getArguments().getString(RegisterFragment.INTENT_EXTRA_PASSWORD);
    }

    /**
     * 初始化SMSSDK,设置回调handler
     */
    private void initSMSSDK() {
        //初始化SMSSDK
        SMSSDK.initSDK(getActivity(), getResources().getString(R.string.smssdk_app_key),
                getResources().getString(R.string.smssdk_app_secret));

        //收到SDK的回调之后将消息发送给系统的Handler，用以更新UI
        mSMSHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int resultCode, Object data) {
                super.afterEvent(event, resultCode, data);
                Message msg = Message.obtain();
                msg.arg1 = event;
                msg.arg2 = resultCode;
                msg.obj = data;
                mHandler.sendMessage(msg);
            }
        };

        SMSSDK.registerEventHandler(mSMSHandler);
    }

    private void initButton() {
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先判断是否填写了验证码
                if (mCaptchaText.getText().length() != 0) {
                    //发送验证信息
                    SMSSDK.submitVerificationCode("86", mPhoneStr,
                            mCaptchaText.getText().toString());
                } else {
                    if (mPhoneStr.equals("VerifyPhoneFragment")) {
                        Toast.makeText(getActivity(),
                                "请先填发送验证短信", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(),
                                "请先填写短信验证码", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    /**
     * 初始化重新发送功能
     */
    private void initTimer() {

        //设置重发倒计时
        mTimer = new Timer();
        mTask = new MyTimerTask();
        mSendButton.setText(SEND_BUTTON_TEXT);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimeRemain == RESENT_TIME_INTERVAL) {
                    //获取短信验证码
                    if (!TextUtils.isEmpty(mPhoneText.getText().toString())) {
                        //设置重发倒计时
                        mTimer.scheduleAtFixedRate(mTask, 0, 1 * 1000);

                        //记录发送短信时的手机号
                        mPhoneStr = mPhoneText.getText().toString();
                        SMSSDK.getVerificationCode("86", mPhoneStr);

                    } else {
                        Toast.makeText(getActivity(),
                                "电话号码不能为空", Toast.LENGTH_SHORT).show();
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

                if (msg.what == MSG_RESEND_BUTTON_CHANGE) {
                    mSendButton.setText("重新发送（"
                            + Integer.toString(msg.arg1)
                            + "秒)");
                } else if (msg.what == MSG_RESEND_BUTTON_RESET) {
                    resetSendButton();
                } else {//接受SMSSDK的回调
                    int event = msg.arg1;
                    int result = msg.arg2;
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                            Log.i(TAG, "Valid Captcha");
                            resetSendButton();
                            storeUserInfo();

                        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                            Log.i(TAG, "Captcha has been sent");
                        }
                    } else {
                        Toast.makeText(getActivity(),
                                "验证码错误", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
    }

    /**
     * 重新发送验证码，初始化参数
     */
    private void resetSendButton() {
        Log.i(TAG, "send captcha button was reset");

        mSendButton.setText(SEND_BUTTON_TEXT);
        mTimeRemain = RESENT_TIME_INTERVAL;
        mTask = new MyTimerTask();
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
     * 撤销注册的sms handler
     */
    @Override
    public void onDetach() {
        super.onDetach();
        SMSSDK.unregisterAllEventHandler();
    }

    /**
     * 手机号验证成功之后，将用户信息存储在本地以及服务器
     */
    private void storeUserInfo() {
        Log.i(TAG, "storing user info");
        if (!HttpUtil.isNetAvailable(getActivity())) {
            //TODO 弹出联网窗口
            Log.i(TAG, "network inactive");
            return;
        }

        final String baseUrl = getString(R.string.server_base_url);
        final HashMap<String, String> params = new HashMap<>();
        params.put("phoneNum", mPhoneStr);
        params.put("nickname", mUser);
        params.put("password", mPassword);

        Log.i(TAG, mPhoneStr + mUser + mPassword);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Log.i(TAG, "data was sent to server");

                //发送数据，获取结果
                JSONObject jsonResult = null;
                try {
                    jsonResult = HttpUtil.doPost(baseUrl + "RegisterServlet", params);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //连接服务器失败
                if (jsonResult == null) {
                    Toast.makeText(getActivity(), "连接服务器失败", Toast.LENGTH_SHORT).show();
                    return;
                }

                //获取返回状态码
                int status = 0;
                try {
                    status = jsonResult.getInt("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //状态码=-1则表示服务器错误
                if (status == -1) {
                    String err = null;
                    try {
                        err = jsonResult.getString("errorStr");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getActivity(), err, Toast.LENGTH_SHORT).show();
                } else {
                    //获取返回的数据，是否能成功注册
                    String result = null;
                    try {
                        result = jsonResult.getString("result");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (result.equalsIgnoreCase("true")) {
                        Toast.makeText(getActivity(), "注册成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "注册失败", Toast.LENGTH_SHORT).show();
                    }
                }
                Looper.loop();
            }
        }).start();

    }

}
