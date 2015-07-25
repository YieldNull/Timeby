package com.nectar.timeby.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nectar.timeby.R;
import com.nectar.timeby.util.HttpProcess;
import com.nectar.timeby.util.HttpUtil;
import com.nectar.timeby.util.PrefsUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by finalize on 7/18/15.
 */
public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";

    private static final int MSG_USER_INVALID = 0x0001;
    private static final int MSG_USER_VALID = 0x0004;
    private static final int MSG_NET_INACTIVE = 0x0002;
    private static final int MSG_SERVER_ERROR = 0x0003;

    private EditText mUserText;
    private EditText mPasswordText;
    private Button mLoginButton;
    private TextView mRegisterTextView;
    private TextView mResetTextView;

    private Handler mHandler;
    private String mUserStr;
    private String mPasswordStr;
    private String mPhoneStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUserText = (EditText) findViewById(R.id.editText_login_user);
        mPasswordText = (EditText) findViewById(R.id.editText_login_password);
        mLoginButton = (Button) findViewById(R.id.button_login_login);
        mRegisterTextView = (TextView) findViewById(R.id.textView_login_register);
        mResetTextView = (TextView) findViewById(R.id.textView_login_reset);

        initEditTextVerify();
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserText.getText().length() == 0) {
                    Toast.makeText(LoginActivity.this, "用户名不能为空",
                            Toast.LENGTH_SHORT).show();
                } else if (mPasswordText.getText().length() == 0) {
                    Toast.makeText(LoginActivity.this, "密码不能为空",
                            Toast.LENGTH_SHORT).show();
                } else {
                    mUserStr = mUserText.getText().toString();
                    mPasswordStr = mPasswordText.getText().toString();
                    verifyUser();
                }
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case MSG_USER_VALID:
                        Log.i(TAG, "Valid user");
                        startMainActivity();
                        break;
                    case MSG_USER_INVALID:
                        Log.i(TAG, "InValid user");
                        Toast.makeText(LoginActivity.this, "用户名或密码错误，请重新登录",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_NET_INACTIVE:
                        Log.i(TAG, "Net inactive");
                        Toast.makeText(LoginActivity.this, "无网络连接，请打开数据网络",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_SERVER_ERROR:
                        //服务器错误
                        Log.i(TAG, "Server error");
                        Toast.makeText(LoginActivity.this, "服务器错误，请稍后再试",
                                Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
    }


    /**
     * 输入框合法性验证
     */
    private void initEditTextVerify() {

        mRegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        mResetTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordPhoneActivity.class));
            }
        });

        mUserText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //此时s为输入后的值
                //TODO 监听非法字符，在显示之前提示用户非法输入
            }

            @Override
            public void afterTextChanged(Editable s) {
                //TODO 输入合法时更改登录按钮的颜色
            }
        });
    }

    /**
     * 把用户信息存到本地，进入主界面
     */
    private void startMainActivity() {
        Log.i(TAG, "Register successfully!");

        //把数据存到本地
        Log.i(TAG, "Storing data in SharedPreference");
        PrefsUtil.login(this, mUserStr, mPasswordStr, mPhoneStr);

        //进入MainActivity
        Log.i(TAG, "Starting DemoActivity");
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);//进入主界面后将之前的Activity栈清空
        LoginActivity.this.startActivity(intent);
    }

    /**
     * 与服务器端进行验证
     */
    private void verifyUser() {
        if (!HttpUtil.isNetAvailable(this)) {
            mHandler.sendEmptyMessage(MSG_NET_INACTIVE);
            return;
        }

        final String userName = mUserText.getText().toString();
        Log.i(TAG, "Checking " + mPhoneStr + mUserStr + mPasswordStr + "is a valid user or not");

        new Thread(new Runnable() {
            @Override
            public void run() {

                JSONObject data = HttpProcess.login(userName, mPasswordStr);
                try {
                    if (data.get("status").equals(-1)) {
                        mHandler.sendEmptyMessage(MSG_SERVER_ERROR);
                    } else if (data.get("status").equals(0)) {
                        mHandler.sendEmptyMessage(MSG_SERVER_ERROR);
                    } else if (data.get("status").equals(1)) {
                        if (data.getString("result").equals("true")) {
                            mPhoneStr = data.getString("phonenum");
                            mHandler.sendEmptyMessage(MSG_USER_VALID);
                        } else if (data.getString("result").equals("false")) {
                            mHandler.sendEmptyMessage(MSG_USER_INVALID);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
