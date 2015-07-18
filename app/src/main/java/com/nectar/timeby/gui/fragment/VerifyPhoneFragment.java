package com.nectar.timeby.gui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.nectar.timeby.R;
import com.nectar.timeby.util.HttpUtil;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class VerifyPhoneFragment extends Fragment {
    private static final String TAG = "VerifyPhoneFragment";
    private static final String SEND_BUTTON_TEXT = "点击获取验证码";

    //有强迫症的小子发这么多消息干嘛啊啊啊啊啊啊啊
    private static final int MSG_RESEND_BUTTON_CHANGE = 0x0001;
    private static final int MSG_RESEND_BUTTON_RESET = 0x0002;
    private static final int MSG_SMSSDK_RECALL = 0x0003;
    private static final int MSG_PHONE_VALID = 0x0004;
    private static final int MSG_PHONE_INVALID = 0x0005;
    private static final int MSG_REGISTRE_SUCCESS = 0x0006;
    private static final int MSG_SERVER_ERROR = 0x0007;
    private static final int MSG_NET_INACTIVE = 0x0008;

    private static final int RESENT_TIME_INTERVAL = 60;
    private int mTimeRemain = RESENT_TIME_INTERVAL;
    private static final String phoneReg = "^(145|147|176)\\d{8}$|^(1700|1705|1709)\\d{7}$" +
            "|^1[38]\\d{9}$|^15[012356789]\\d{8}$";


    //记录发送短信时输入框内的手机号，防止申请验证码后改变手机号
    private String mPhoneStr = TAG;
    private String mUser;
    private String mPassword;

    private ImageButton mSubmitButton;
    private TextView mSendButton;
    private ImageButton mBackButton;

    private EditText mPhoneText;
    private EditText mCaptchaText;

    private Handler mHandler;
    private Timer mTimer;
    private TimerTask mTask;
    private EventHandler mSMSHandler;


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
        mSendButton = (TextView) rootView.findViewById(R.id.button_register_phone_send);
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
                msg.what = MSG_SMSSDK_RECALL;
                msg.arg1 = event;
                msg.arg2 = resultCode;
                msg.obj = data;
                mHandler.sendMessage(msg);
            }
        };

        SMSSDK.registerEventHandler(mSMSHandler);
    }

    /**
     * 初始化提交按钮
     */
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
                    if (mPhoneStr.equals(TAG)) {
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
                    //电话号码合法则发送验证码
                    Pattern pattern = Pattern.compile(phoneReg);
                    Matcher matcher = pattern.matcher(mPhoneText.getText().toString());

                    if (matcher.matches()) {
                        //记录发送短信时的手机号
                        mPhoneStr = mPhoneText.getText().toString();
                        checkPhoneUnique(true);//判断是否注册过
                    } else {
                        Toast.makeText(getActivity(),
                                "电话号码非法", Toast.LENGTH_SHORT).show();
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
                        //手机号没被注册，发送验证码
                        mTimer.scheduleAtFixedRate(mTask, 0, 1 * 1000);
                        Drawable rightImg = getActivity().getResources()
                                .getDrawable(R.drawable.icn_login_valid);
                        mPhoneText.setCompoundDrawablesWithIntrinsicBounds(null, null, rightImg, null);
                        SMSSDK.getVerificationCode("86", mPhoneStr);
                        break;
                    case MSG_PHONE_INVALID:
                        //手机号已被注册，不发送验证码
                        Toast.makeText(getActivity(), "手机号已被注册", Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_SERVER_ERROR:
                        //服务器错误
                        Toast.makeText(getActivity(), "服务器错误，请稍后再试",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_NET_INACTIVE:
                        //没有连接互联网
                        Toast.makeText(getActivity(), "无网络连接，请打开数据网络", Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_REGISTRE_SUCCESS:
                        //存储信息成功
                        break;
                    case MSG_SMSSDK_RECALL:
                        int event = msg.arg1;
                        int result = msg.arg2;
                        switch (result) {
                            case SMSSDK.RESULT_COMPLETE:
                                Log.i(TAG, "Valid Captcha");
                                resetSendButton();
                                storeUserInfo();
                                break;
                            case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                                Log.i(TAG, "Captcha has been sent");
                                break;
                            default:
                                Toast.makeText(getActivity(),
                                        "验证码错误", Toast.LENGTH_SHORT).show();
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

    }

    /**
     * @param isTest 是否是在测试手机号是否已被注册
     */
    private void checkPhoneUnique(final boolean isTest) {
        if (!HttpUtil.isNetAvailable(getActivity())) {
            mHandler.sendEmptyMessage(MSG_NET_INACTIVE);
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
                    mHandler.sendEmptyMessage(MSG_SERVER_ERROR);
                    return;
                }

                //获取返回状态码
                int status = 0;
                try {
                    status = jsonResult.getInt("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(MSG_SERVER_ERROR);
                    return;
                }

                //状态码=-1则表示服务器错误
                if (status == -1) {
                    mHandler.sendEmptyMessage(MSG_SERVER_ERROR);
                } else {
                    //获取返回的数据，是否能成功注册
                    String result = null;
                    try {
                        result = jsonResult.getString("result");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //根据返回结果判断手机号是否被注册过
                    if (result.equalsIgnoreCase("true")) {
                        if (isTest)
                            mHandler.sendEmptyMessage(MSG_PHONE_VALID);
                        else
                            mHandler.sendEmptyMessage(MSG_REGISTRE_SUCCESS);
                    } else {
                        mHandler.sendEmptyMessage(MSG_PHONE_INVALID);
                    }
                }
            }
        }).start();
    }
}
