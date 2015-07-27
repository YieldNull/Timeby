package com.nectar.timeby.gui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.nectar.timeby.R;
import com.nectar.timeby.util.HttpProcess;
import com.nectar.timeby.util.HttpUtil;
import com.nectar.timeby.util.PrefsUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddFriendActivity extends ActionBarActivity {

    private static final String TAG = "AddFriendActivity";

    private static final int MSG_SERVER_ERROR = 0x0002;
    private static final int MSG_SUCCESS_FIND = 0x0003;
    private static final int MSG_FAILURE_FIND = 0x0004;
    private static final int MSG_SUCCESS_ADD = 0x0005;
    private static final int MSG_FAILURE_ADD = 0x0006;

    private ImageButton mAddFromContact;

    private Handler mHandler;
    private ImageButton mSearchButton;
    private EditText mSearchText;

    private CircularImageView mHeadImg;
    private TextView mFriendName;
    private TextView mAddTextButton;

    private String mSearchPhone;
    private String mSearchName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        mHeadImg = (CircularImageView) findViewById(R.id.imageView_friends_headimg);
        mFriendName = (TextView) findViewById(R.id.textView_friends_name);
        mAddTextButton = (TextView) findViewById(R.id.textView_friends_list_request);

        mHeadImg.setVisibility(View.INVISIBLE);
        mFriendName.setVisibility(View.INVISIBLE);
        mAddTextButton.setVisibility(View.INVISIBLE);


        mAddFromContact = (ImageButton) findViewById(R.id.imageButton_addfriend_mobilecommunication);
        mAddFromContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddFriendActivity.this, AddFriendsFromContact.class));
            }
        });

        mSearchText = (EditText) findViewById(R.id.tv_addfriend_input);
        mSearchButton = (ImageButton) findViewById(R.id.imageButton_add_friend_search);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        ImageButton returnBtn = (ImageButton) findViewById(R.id.imgageButton_addfriend_return);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initHandler();
    }

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case MSG_SERVER_ERROR:
                        Log.i(TAG, "Server error");
                        Toast.makeText(AddFriendActivity.this,
                                "服务器错误，请稍后再试", Toast.LENGTH_SHORT).show();
                        break;

                    case MSG_FAILURE_FIND:
                        Log.i(TAG, "No such user");

                        Toast.makeText(AddFriendActivity.this, "未找到该用户",
                                Toast.LENGTH_SHORT).show();
                        break;

                    case MSG_FAILURE_ADD:
                        Log.i(TAG, "add error");
                        Toast.makeText(AddFriendActivity.this, "您已发送申请，请等待对方回复",
                                Toast.LENGTH_SHORT).show();
                        break;

                    case MSG_SUCCESS_FIND:
                        Log.i(TAG, "find success");
                        showFriend();
                        break;

                    case MSG_SUCCESS_ADD:
                        Log.i(TAG, "add success");
                        Toast.makeText(AddFriendActivity.this,
                                "您已发送申请，请等待对方回复,", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;

                }
            }
        };
    }

    /**
     * 显示
     */
    private void showFriend() {

        mHeadImg.setVisibility(View.VISIBLE);
        mAddTextButton.setVisibility(View.VISIBLE);
        mFriendName.setVisibility(View.VISIBLE);

        mHeadImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
        mAddTextButton.setEnabled(true);
        mAddTextButton.setText("添加");
        mAddTextButton.setBackgroundColor(getResources().getColor(R.color.friends_list_request));
        mAddTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAdd();
                mAddTextButton.setText("已申请");
                mAddTextButton.setEnabled(false);
            }
        });
    }

    /**
     * 查找
     */
    private void performSearch() {
        mHeadImg.setVisibility(View.INVISIBLE);
        mAddTextButton.setVisibility(View.INVISIBLE);
        mFriendName.setVisibility(View.INVISIBLE);

        final String key = mSearchText.getText().toString();

        if (!HttpUtil.isNetAvailable(this)) {
            Toast.makeText(this, "无网络连接，请先打开数据网络", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, "Searching " + key);

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray data = HttpProcess.findFriend(key);
                try {
                    JSONObject statusJson = data.getJSONObject(0);
                    if (statusJson.get("status").equals(-1)) {
                        mHandler.sendEmptyMessage(MSG_SERVER_ERROR);
                    } else if (statusJson.get("status").equals(0)) {
                        mHandler.sendEmptyMessage(MSG_SERVER_ERROR);
                    } else if (statusJson.get("status").equals(1)) {
                        if (statusJson.getString("result").equals("true"))  //正确返回数据
                        {
                            JSONObject dataJson = data.getJSONObject(1);
                            mSearchPhone = dataJson.getString("phonenum");
                            mSearchName = dataJson.getString("nickname");
                            mHandler.sendEmptyMessage(MSG_SUCCESS_FIND);

                        } else if (statusJson.getString("result").equals("false")) {
                            mHandler.sendEmptyMessage(MSG_FAILURE_FIND);
                        }
                    }
                } catch (JSONException e) {
                    Log.i(TAG, e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 添加
     */
    private void performAdd() {
        if (!HttpUtil.isNetAvailable(this)) {
            Toast.makeText(this, "无网络连接，请打开数据网络", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(TAG, "Addding " + mSearchPhone);

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject data = HttpProcess.friendApplication(
                        PrefsUtil.getUserPhone(AddFriendActivity.this), mSearchPhone);
                try {
                    if (data.get("status").equals(-1)) {
                        Log.i(TAG, "sendAddRequest:server error");
                        mHandler.sendEmptyMessage(MSG_SERVER_ERROR);

                    } else if (data.get("status").equals(0)) {
                        Log.i(TAG, "sendAddRequest:server error");
                        mHandler.sendEmptyMessage(MSG_SERVER_ERROR);

                    } else if (data.get("status").equals(1)) {
                        if (data.getString("result").equals("true")) {
                            Log.i(TAG, "sendAddRequest:success");
                            mHandler.sendEmptyMessage(MSG_SUCCESS_ADD);

                        } else if (data.getString("result").equals("false")) {
                            Log.i(TAG, "sendAddRequest:failure");
                            mHandler.sendEmptyMessage(MSG_FAILURE_ADD);
                        }
                    }
                } catch (JSONException e) {
                    Log.w(TAG, e.getMessage());
                }
            }
        }).start();
    }

}
