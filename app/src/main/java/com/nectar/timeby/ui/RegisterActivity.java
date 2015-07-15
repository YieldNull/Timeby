package com.nectar.timeby.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nectar.timeby.R;

public class RegisterActivity extends Activity {

    private static final String TAG = "RegisterActivity";

    private EditText mUserText;
    private EditText mPasswordText;
    private Button mNextButton;

    public static final String INTENT_EXTRA_USER = "com.nectar.timeby.ui.RegisterActivity.user";
    public static final String INTENT_EXTRA_PASSWORD = "com.nectar.timeby.ui.RegisterActivity.password";
    public static final String INTENT_EXTRA_SUCCESS_REGISTER = "com.nectar.timeby.ui.RegisterActivity.finishregister";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUserText = (EditText) findViewById(R.id.editText_register_user);
        mPasswordText = (EditText) findViewById(R.id.editText_register_password);
        mNextButton = (Button) findViewById(R.id.button_register_login);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, RegisterPhoneActivity.class);
                intent.putExtra(INTENT_EXTRA_USER, mUserText.getText().toString());
                intent.putExtra(INTENT_EXTRA_PASSWORD, mPasswordText.getText().toString());

                RegisterActivity.this.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //当完成手机号绑定时，finish绑定页面，会发送成功消息到此处
        //接收到成功消息之后，此页面finish
        //否则视为一般的返回
        boolean isSuccess = data.getBooleanExtra(INTENT_EXTRA_SUCCESS_REGISTER, false);
        if (isSuccess) {
            RegisterActivity.this.finish();
        }
    }
}
