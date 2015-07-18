package com.nectar.timeby.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nectar.timeby.R;

/**
 * Created by finalize on 7/18/15.
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


    private void initOnClickListening() {
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyUser()) {
                    storeUserInfo();
                }
            }
        });

        mRegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
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
        new Thread(new Runnable() {
            @Override
            public void run() {
            }
        }).run();
        return false;
    }

    private void storeUserInfo() {
        //TODO 将用户信息存入ContentProvider以及本地数据库
    }

}
