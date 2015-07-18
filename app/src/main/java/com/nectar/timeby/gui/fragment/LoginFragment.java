package com.nectar.timeby.gui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nectar.timeby.R;


/**
 * 2015.7.13 by finalize
 */

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    private EditText mUserText;
    private EditText mPasswordText;
    private Button mLoginButton;
    private TextView mRegisterTextView;
    private TextView mResetTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_login, container, false);
        mUserText = (EditText) rootView.findViewById(R.id.editText_login_user);
        mPasswordText = (EditText) rootView.findViewById(R.id.editText_login_password);
        mLoginButton = (Button) rootView.findViewById(R.id.button_login_login);
        mRegisterTextView = (TextView) rootView.findViewById(R.id.textView_login_register);
        mResetTextView = (TextView) rootView.findViewById(R.id.textView_login_reset);

        initOnClickListening();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

//        mToggleClickListener = (MainFragment.OnToggleClickListener) activity;
    }

    private void initOnClickListening() {
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyUser()) {
                    storeUserInfo();
//                    mToggleClickListener.onToggleClick(new MainFragment(), true);
                }
            }
        });

        mRegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mToggleClickListener.onToggleClick(new RegisterFragment(), true);
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
//                HttpUtil
            }
        }).run();
        return false;
    }

    private void storeUserInfo() {
        //TODO 将用户信息存入ContentProvider以及本地数据库
    }
}
