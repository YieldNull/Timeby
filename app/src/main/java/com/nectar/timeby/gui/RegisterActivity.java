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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.nectar.timeby.R;
import com.nectar.timeby.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by finalize on 7/18/15.
 */
public class RegisterActivity extends Activity {
    private static final String TAG = "RegisterActivity";
    private static final int MAX_USER_LENGTH = 20;

    public static final String INTENT_EXTRA_USER =
            "com.nectar.timeby.ui.RegisterActivity.user";
    public static final String INTENT_EXTRA_PASSWORD =
            "com.nectar.timeby.ui.RegisterActivity.password";

    private static final String mPasswordReg = "^\\S{6,16}$";
    private static final String mPasswordNoReg = "^\\d{0,8}$";
    private static final int MSG_USERNAME_INVALID = 0x0001;
    private static final int MSG_USERNAME_VALID = 0x0004;
    private static final int MSG_NET_INACTIVE = 0x0002;
    private static final int MSG_SERVER_ERROR = 0x0003;


    private EditText mUserText;
    private EditText mPasswordText;
    private EditText mPasswordText2;
    private ImageButton mNextButton;

    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUserText = (EditText) findViewById(R.id.editText_register_user);
        mPasswordText = (EditText) findViewById(R.id.editText_register_password);
        mPasswordText2 = (EditText) findViewById(R.id.editText_register_password2);
        mNextButton = (ImageButton) findViewById(R.id.button_register_next);
        initEditTextVerify();

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPasswordText2.getText().toString().equals(
                        mPasswordText.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "两次输入密码不匹配"
                            , Toast.LENGTH_SHORT).show();
                } else if (mUserText.getText().length() == 0) {
                    Toast.makeText(RegisterActivity.this, "请输入用户名"
                            , Toast.LENGTH_SHORT).show();
                } else {
                    RegisterActivity.this.startVerifyPage();
                }
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_USERNAME_INVALID:
                        Toast.makeText(RegisterActivity.this, "用户名已被注册",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_NET_INACTIVE:
                        Toast.makeText(RegisterActivity.this, "无网络连接，请打开数据网络",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_SERVER_ERROR:
                        //服务器错误
                        Toast.makeText(RegisterActivity.this, "服务器错误，请稍后再试",
                                Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    /**
     * 各种合法性验证
     */
    private void initEditTextVerify() {
        mUserText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mUserText.getText().length() == MAX_USER_LENGTH + 1) {
                    Toast.makeText(RegisterActivity.this, "用户名长度超过限制",
                            Toast.LENGTH_SHORT).show();
                    s.delete(s.length() - 1, s.length());
                }
            }
        });
        mUserText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (mUserText.getText().length() == 0) {
                        Toast.makeText(RegisterActivity.this, "用户名不能为空",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        RegisterActivity.this.checkUserName();
                    }
                }
            }
        });
        mPasswordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (mPasswordText.getText().length() == 0) {
                        Toast.makeText(RegisterActivity.this,
                                "密码不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Pattern pattern = Pattern.compile(mPasswordReg);
                    Matcher matcher = pattern.matcher(mPasswordText.getText().toString());
                    if (matcher.matches()) {
                        Pattern pattern2 = Pattern.compile(mPasswordNoReg);
                        Matcher matcher2 = pattern2.matcher(mPasswordText.getText().toString());
                        if (matcher2.matches()) {
                            Toast.makeText(RegisterActivity.this,
                                    "不能为9位以下纯数字", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                "密码为6-16位字符、数字。不能为9位以下纯数字", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    /**
     * 打开手机验证页面，将用户名和密码发送过去
     */
    private void startVerifyPage() {
        Intent intent = new Intent(this, RegisterPhoneActivity.class);
        intent.putExtra(INTENT_EXTRA_USER, mUserText.getText().toString());
        intent.putExtra(INTENT_EXTRA_PASSWORD, mPasswordText.getText().toString());

        mPasswordText.setText("");
        mPasswordText2.setText("");

        startActivity(intent);
    }

    private void checkUserName() {
        if (!HttpUtil.isNetAvailable(this)) {
            mHandler.sendEmptyMessage(MSG_NET_INACTIVE);
            return;
        }

        String userName = mUserText.getText().toString();
        Log.i(TAG, "check " + userName + " has been registered or not");

        final HashMap<String, String> params = new HashMap<>();
        params.put("userName", userName);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "data was sent to server");

                //发送数据，获取结果
                JSONObject jsonResult = null;
                try {
                    jsonResult = HttpUtil.doPost(HttpUtil.URL_LOGIN_CHECK, params);
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
                    //获取返回的数据，是否已经被注册
                    String result = null;
                    try {
                        result = jsonResult.getString("result");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (result == null) {
                        mHandler.sendEmptyMessage(MSG_SERVER_ERROR);
                    } else {
                        if (result.equalsIgnoreCase("true")) {
                            mHandler.sendEmptyMessage(MSG_USERNAME_VALID);
                        } else {
                            mHandler.sendEmptyMessage(MSG_USERNAME_INVALID);
                        }
                    }
                }
            }
        }).start();
    }
}
