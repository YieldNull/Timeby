package com.nectar.timeby.gui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.nectar.timeby.R;
import com.nectar.timeby.util.HttpUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 2015.07.16 by finalize
 * 用户注册页面，主要为输入框的合法性验证
 */
public class RegisterFragment extends Fragment {

    private static final String TAG = "RegisterFragment";
    private static final int MAX_USER_LENGTH = 20;
    public static final String INTENT_EXTRA_USER =
            "com.nectar.timeby.ui.RegisterFragment.user";
    public static final String INTENT_EXTRA_PASSWORD =
            "com.nectar.timeby.ui.RegisterFragment.password";

    private static final String passwdReg = "^\\S{6,16}$";
    private static final String passwdNoReg = "^\\d{0,8}$";

    private EditText mUserText;
    private EditText mPasswordText;
    private EditText mPasswordText2;
    private ImageButton mNextButton;

//    private MainFragment.OnToggleClickListener mToggleClickListener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        mToggleClickListener = (MainFragment.OnToggleClickListener) activity;
        Log.i(TAG, "" + HttpUtil.isNetAvailable(activity));
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_register, container, false);

        mUserText = (EditText) rootView.findViewById(R.id.editText_register_user);
        mPasswordText = (EditText) rootView.findViewById(R.id.editText_register_password);
        mPasswordText2 = (EditText) rootView.findViewById(R.id.editText_register_password2);
        mNextButton = (ImageButton) rootView.findViewById(R.id.button_register_next);
        //initEditTextVerify();

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPasswordText2.getText().toString().equals(
                        mPasswordText.getText().toString())) {
                    Toast.makeText(getActivity(), "两次输入密码不匹配"
                            , Toast.LENGTH_SHORT).show();
                } else if (mUserText.getText().length() == 0) {
                    Toast.makeText(getActivity(), "请输入用户名"
                            , Toast.LENGTH_SHORT).show();
                } else {
                    startVerifyPage();
                }

            }
        });

        return rootView;
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
                    Toast.makeText(getActivity(), "用户名长度超过限制",
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
                        Toast.makeText(getActivity(), "用户名不能为空",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        mPasswordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (mPasswordText.getText().length() == 0) {
                        Toast.makeText(getActivity(),
                                "密码不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Pattern pattern = Pattern.compile(passwdReg);
                    Matcher matcher = pattern.matcher(mPasswordText.getText().toString());
                    if (matcher.matches()) {
                        Pattern pattern2 = Pattern.compile(passwdNoReg);
                        Matcher matcher2 = pattern2.matcher(mPasswordText.getText().toString());
                        if (matcher2.matches()) {
                            Toast.makeText(getActivity(),
                                    "不能为9位以下纯数字", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(),
                                "6-16位字符、数字。不能为9位以下纯数字", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    /**
     * 打开手机验证页面，将用户名和密码发送过去
     */
    private void startVerifyPage() {
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_EXTRA_USER,
                mUserText.getText().toString());
        bundle.putString(INTENT_EXTRA_PASSWORD,
                mPasswordText.getText().toString());

        Fragment verifyFragment = new VerifyPhoneFragment();
        verifyFragment.setArguments(bundle);
//        mToggleClickListener.onToggleClick(verifyFragment, true);
    }
}
