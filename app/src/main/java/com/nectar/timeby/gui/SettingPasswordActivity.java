package com.nectar.timeby.gui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.nectar.timeby.R;
import com.nectar.timeby.util.HttpProcess;

import org.json.JSONArray;

public class SettingPasswordActivity extends Activity {


    private EditText mOldPasswdText;
    private EditText mNewPasswdText;
    private EditText mConfirmText;
    private ImageView mSubmitButton;


    private String mOldPasswdStr;
    private String mNewPasswdStr;
    private String mConfirmStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_reset_password);

//        mOldPasswdText = (EditText) findViewById(R.id.tv_changepassword_presentpassword);
//        mNewPasswdText = (EditText) findViewById(R.id.tv_changepassword_newpassword);
//        mConfirmText = (EditText) findViewById(R.id.tv_changepassword_confirmnewpassword);
//        mSubmitButton = (ImageView) findViewById(R.id.btn_changepassword_submit);
//
//        mSubmitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mOldPasswdStr = mOldPasswdText.getText().toString();
//                mNewPasswdStr = mNewPasswdText.getText().toString();
//                mConfirmStr = mConfirmText.getText().toString();
//
//            }
//        });
    }
}
