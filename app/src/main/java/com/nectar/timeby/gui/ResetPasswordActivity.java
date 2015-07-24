package com.nectar.timeby.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.nectar.timeby.R;
import com.nectar.timeby.util.HttpProcess;
import com.nectar.timeby.util.HttpUtil;
import com.nectar.timeby.util.PrefsUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class ResetPasswordActivity extends Activity {

    public static final String INTENT_PHONENUM = "intent_phone_num";

    private static final String TAG = "ResetPasswordActivity";

    //有强迫症的小子发这么多消息干嘛啊啊啊啊啊啊啊
    private static final int MSG_UPDATE_SUCCESS = 0x0006;
    private static final int MSG_UPDATE_FAILURE = 0x0007;
    private static final int MSG_SERVER_ERROR = 0x0008;
    private static final int MSG_NET_INACTIVE = 0x0009;

    private ImageButton mReturnButton;
    private ImageButton mSubmitButton;
    private EditText mNewPassword;
    private EditText mPasswordConfirm;

    private Handler mHandler;
    private String mPhoneNum;
    private String mPasswordStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mReturnButton = (ImageButton) findViewById(R.id.imageButton_reset_passwd_return);
        mSubmitButton = (ImageButton) findViewById(R.id.imageButton_reset_passwd_submit);
        mNewPassword = (EditText) findViewById(R.id.editText_resetpasswd_new_passwd);
        mPasswordConfirm = (EditText) findViewById(R.id.editText_resetpasswd_confirm_passwd);


        mPhoneNum = getIntent().getStringExtra(INTENT_PHONENUM);

        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_SERVER_ERROR:
                        //服务器错误
                        Log.i(TAG, "Server error");
                        Toast.makeText(ResetPasswordActivity.this, "服务器错误，请稍后再试",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_NET_INACTIVE:
                        //没有连接互联网
                        Log.i(TAG, "Network inactive");
                        Toast.makeText(ResetPasswordActivity.this, "无网络连接，请打开数据网络",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_UPDATE_SUCCESS:
                        //更新成功
                        Log.i(TAG, "Update successfully!");

                        //更新本地密码
                        PrefsUtil.updatePassword(ResetPasswordActivity.this, mPasswordStr);
                        Toast.makeText(ResetPasswordActivity.this,
                                "更新成功，请重新登录", Toast.LENGTH_SHORT).show();
                        startLoginActivity();

                        break;
                    case MSG_UPDATE_FAILURE:
                        //更新失败
                        Log.i(TAG, "Same user has been registered");
                        Toast.makeText(ResetPasswordActivity.this,
                                "更新失败，请稍后再试", Toast.LENGTH_SHORT).show();

                        startLoginActivity();
                        break;
                    default:
                        break;
                }
            }
        };

        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordStr = mNewPassword.getText().toString();
                String confirm = mPasswordConfirm.getText().toString();

                if (mPasswordStr.length() == 0 || confirm.length() == 0) {
                    Toast.makeText(ResetPasswordActivity.this,
                            "密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (!mPasswordStr.equals(confirm)) {
                    Toast.makeText(ResetPasswordActivity.this,
                            "两次密码不匹配", Toast.LENGTH_SHORT).show();
                } else {

                    updatePassword();

                }
            }
        });

    }

    private void startLoginActivity() {
        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ResetPasswordActivity.this.startActivity(intent);
    }

    private void updatePassword() {
        if (!HttpUtil.isNetAvailable(this)) {
            mHandler.sendEmptyMessage(MSG_NET_INACTIVE);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject data = HttpProcess.updatePassword(mPhoneNum, mPasswordStr);
                try {
                    if (data.get("status").equals(-1)) {
                        mHandler.sendEmptyMessage(MSG_SERVER_ERROR);
                    } else if (data.get("status").equals(0)) {
                        mHandler.sendEmptyMessage(MSG_SERVER_ERROR);
                    } else if (data.get("status").equals(1)) {
                        if (data.getString("result").equals("true")) {
                            mHandler.sendEmptyMessage(MSG_UPDATE_SUCCESS);
                        } else if (data.getString("result").equals("false")) {
                            mHandler.sendEmptyMessage(MSG_UPDATE_FAILURE);
                        }
                    }
                } catch (JSONException e) {
                }
            }
        }).start();
    }
}
