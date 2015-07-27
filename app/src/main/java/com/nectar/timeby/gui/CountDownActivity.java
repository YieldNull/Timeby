package com.nectar.timeby.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nectar.timeby.R;
import com.nectar.timeby.gui.fragment.MainFragment;
import com.nectar.timeby.gui.widget.TopNotification;
import com.nectar.timeby.service.countdown.APPStateReceiver;
import com.nectar.timeby.service.countdown.ScreenStateReceiver;
import com.nectar.timeby.util.HttpProcess;
import com.nectar.timeby.util.HttpUtil;
import com.nectar.timeby.util.PrefsUtil;
import com.nectar.timeby.util.TimeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by finalize on 7/23/15.
 */
public class CountDownActivity extends Activity {

    private static final String TAG = "CountDownActivity";

    private static final int MSG_CLOCK_TICK = 0x0004;
    private static final int MSG_CLOCK_STOP = 0x0005;
    private static final int MSG_REFRESH_LIST = 0x0006;
    private static final int MSG_SERVER_ERROR = 0x0007;
    private static final int MSG_DOWNLOAD_SUCCESS = 0x0008;
    private static final int MSG_DOWNLOAD_FAILURE = 0x0009;

    private static final String MAP_KEY_PHONE = "phone";
    private static final String MAP_KEY_NAME = "name";
    private static final String MAP_KEY_SHELL = "shell";
    private static final String MAP_KEY_HAMMER = "hammer";

    private int mCurrHour;
    private int mCurrMin;
    private int mCurrSec;
    private int mSumSec;

    private TextView mHourText;
    private TextView mMinText;
    private TextView mSecText;

    private ListView mDataListView;
    private BaseAdapter mAdapter;
    private ArrayList<HashMap<String, String>> mDataList;


    private Handler mHandler;
    private Runnable mTickRunnable;


    private int mType;

    /**
     * 直接退出程序，便于计算在非倒计时界面停留的时间
     */
    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_countdown);

        mType = getIntent().getIntExtra(MainFragment.INTENT_TASK_TYPE, -1);
        if ((mType == MainFragment.TASK_TYPE_COOPER)
                || (mType == MainFragment.TASK_TYPE_PK)) {

            if (PrefsUtil.hasFriendsAccept(this)) {
                downloadFriendsInfo();
            } else if (PrefsUtil.getTaskRequestFrom(this)
                    .equals(PrefsUtil.getUserPhone(this))) {
                downloadFriendsInfo();
            } else {
                new TopNotification(this, "没有好友同意您的请求\n您将独自完成任务", 3000).show();
            }
        }

        mHourText = (TextView) findViewById(R.id.textView_main_countdown_hour);
        mMinText = (TextView) findViewById(R.id.textView_main_countdown_min);
        mSecText = (TextView) findViewById(R.id.textView_main_countdown_sec);

        mDataListView = (ListView) findViewById(R.id.listView_main_countdown);
        mDataList = new ArrayList<>();
        mAdapter = new ContactListAdapter();
        mDataListView.setAdapter(mAdapter);

        initTick();
        initHandler();

        ImageButton returnButton = (ImageButton) findViewById(
                R.id.imageButton_countdown_return);

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    /**
     * 从SharedReference中读取结束时间，与当前时间比较，算出倒计时
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (PrefsUtil.isOnTask(this)) {
            Log.i(TAG, "Enter CountDownActivity,Send APP Enter broadcast");
            sendBroadcast(new Intent(APPStateReceiver.ACTION_NAME_ENTER));

            Map<String, Long> task = PrefsUtil.readTask(this);
            int DSeconds = (int) ((task.get(PrefsUtil.PREFS_KEY_TASK_END_TIME_MILLIS)
                    - System.currentTimeMillis()) / 1000);

            mCurrHour = DSeconds / 3600;
            mCurrMin = DSeconds / 60;
            mCurrSec = DSeconds % 60;
            mSumSec = mCurrHour * 3600 + mCurrMin * 60 + mCurrSec;

            mHandler.post(mTickRunnable);
        }
    }

    /**
     * 停止界面的倒计时,发送Broadcast，开始计算离开倒计时界面的时间
     */
    @Override
    protected void onPause() {
        super.onPause();

        mHandler.removeCallbacks(mTickRunnable);
        if (PrefsUtil.isOnTask(this)
                && (!ScreenStateReceiver.isScreenOff(this))) {
            Log.i(TAG, "Leave CountDownActivity, Send APP QUIT broadcast");
            sendBroadcast(new Intent(APPStateReceiver.ACTION_NAME_QUIT));
        }
    }

    /**
     * 初始化倒计时
     */
    private void initTick() {
        mTickRunnable = new Runnable() {
            @Override
            public void run() {
                mSumSec -= 1;
                mCurrSec = mSumSec % 60;
                mCurrMin = (mSumSec % 3600) / 60;
                mCurrHour = mSumSec / 3600;

                if (mSumSec == 0) {
                    mHandler.sendEmptyMessage(MSG_CLOCK_STOP);
                } else {
                    mHandler.sendEmptyMessage(MSG_CLOCK_TICK);
                }

                //1秒之后再次发送消息，更新界面
                mHandler.postDelayed(this, 1000);
            }
        };
    }

    /**
     * 初始化Handler
     */
    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_CLOCK_TICK:
                        refreshTimeText();
                        break;
                    case MSG_CLOCK_STOP:
                        refreshTimeText();
                        mHandler.removeCallbacks(mTickRunnable);

                        startActivity(new Intent(
                                CountDownActivity.this, ConcludeActivity.class));
                        break;
                    case MSG_DOWNLOAD_SUCCESS:
                        mAdapter.notifyDataSetInvalidated();
                        break;
                    case MSG_DOWNLOAD_FAILURE:
                        break;
                    default:
                        break;
                }
            }
        };
    }

    /**
     * 更新界面显示的倒计时
     */
    private void refreshTimeText() {
        mHourText.setText(getTimeStr(mCurrHour));
        mMinText.setText(getTimeStr(mCurrMin));
        mSecText.setText(getTimeStr(mCurrSec));
    }

    /**
     * 根据时间int值格式化出其字符串形式如“00”，“09”
     *
     * @param time
     * @return
     */
    private CharSequence getTimeStr(int time) {
        return time > 9 ? "" + time : "0" + time;
    }


    /**
     * 下载共同完成任务的信息
     */
    private void downloadFriendsInfo() {
        if (!HttpUtil.isNetAvailable(this)) {
            new TopNotification(this, "无网络连接\n暂时无法获取好友信息", 3000).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String phone = PrefsUtil.getUserPhone(CountDownActivity.this);
                String requestPhone = PrefsUtil.getTaskRequestFrom(CountDownActivity.this);
                String startTime = TimeUtil.getTimeStr(
                        PrefsUtil.getTaskStartTime(CountDownActivity.this));

                JSONArray data = HttpProcess.getTaskInfo(phone, requestPhone, startTime);
                try {
                    JSONObject statusJson = data.getJSONObject(0);
                    if (statusJson.get("status").equals(-1)) {
                        mHandler.sendEmptyMessage(MSG_SERVER_ERROR);
                    } else if (statusJson.get("status").equals(0)) {
                        mHandler.sendEmptyMessage(MSG_SERVER_ERROR);
                    } else if (statusJson.get("status").equals(1)) {
                        if (statusJson.getString("result").equals("true"))  //正确返回数据
                        {
                            for (int i = 1; i < data.length(); ++i) {
                                HashMap<String, String> friend = new HashMap<String, String>();
                                JSONObject dataJson = data.getJSONObject(i);
                                String phoneNum = dataJson.getString("phonenum");
                                String name = dataJson.getString("name");

                                friend.put(MAP_KEY_PHONE, phoneNum);
                                friend.put(MAP_KEY_NAME, name);

                                mDataList.add(friend);
                            }

                            mHandler.sendEmptyMessage(MSG_DOWNLOAD_SUCCESS);
                        } else if (statusJson.getString("result").equals("false")) {
                            mHandler.sendEmptyMessage(MSG_DOWNLOAD_FAILURE);
                        }
                    }
                } catch (JSONException e) {
                    Log.w(TAG, e.getMessage());
                }

            }
        }).start();
    }


    private class ContactListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            //设置列表项的数量
            if (mDataList.size() == 0) {
                return 1;
            } else {
                return mDataList.size();
            }
        }

        @Override
        public Object getItem(int position) {
            if (mDataList.size() != 0) {
                return mDataList.get(position);
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (mDataList.size() == 0) {
                return CountDownActivity.this.getLayoutInflater().inflate(
                        R.layout.list_item_woniu, parent, false);
            }

            if (convertView == null) {
                //获取引用
                holder = new ViewHolder();
                convertView = CountDownActivity.this.getLayoutInflater().inflate(
                        R.layout.list_item_countdown, parent, false);
                holder.mContactName = (TextView) convertView.
                        findViewById(R.id.textView_countdown_name);
                holder.mHeadImg = (ImageView) convertView.findViewById(
                        R.id.imageView_countdown_headimg);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            //设置头像与姓名
            holder.mContactName.setText(mDataList.get(position).get(MAP_KEY_NAME));
            holder.setImage(mDataList.get(position).get(MAP_KEY_PHONE));


            return convertView;
        }

        class ViewHolder {
            public TextView mContactName;
            public ImageView mHeadImg;

            public void setImage(String phone) {
                //TODO 从服务器获取图片信息

                mHeadImg.setImageDrawable(getResources()
                        .getDrawable(R.drawable.img_friends_headimg_default));
            }

        }
    }

    private String getPhoneNum(int position) {
        String phone = mDataList.get(position).get(MAP_KEY_PHONE);

        return phone;
    }
}
