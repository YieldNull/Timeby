package com.nectar.timeby.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nectar.timeby.R;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

/**
 * 2015.7.13 by finalize
 */

public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";

    private EditText mUserText;
    private EditText mPasswordText;
    private Button mLoginButton;
    private TextView mRegisterTextView;
    private TextView mResetTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUserText = (EditText) findViewById(R.id.editText_login_user);
        mPasswordText = (EditText) findViewById(R.id.editText_login_password);
        mLoginButton = (Button) findViewById(R.id.button_login_login);
        mRegisterTextView = (TextView) findViewById(R.id.textView_login_register);
        mResetTextView = (TextView) findViewById(R.id.textView_login_reset);

        initOnClickListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    private void initOnClickListening() {
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyUser()) {
                    storeUserInfo();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    LoginActivity.this.startActivity(intent);

                    LoginActivity.this.finish();
                }
            }
        });

        mRegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.startActivity(
                        new Intent(LoginActivity.this, RegisterActivity.class));

                LoginActivity.this.finish();
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


    private boolean verifyUser() {
        //TODO 验证用户登录
        return false;
    }

    private void storeUserInfo() {
        //TODO 将用户信息存入ContentProvider以及本地数据库
    }
}
