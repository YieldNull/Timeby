package com.nectar.timeby.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nectar.timeby.R;
import com.nectar.timeby.service.APPStateReceiver;
import com.nectar.timeby.util.PrefsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by finalize on 7/23/15.
 */
public class CountDownActivity extends Activity {

    private static final String TAG = "CountDownActivity";
    public static final int TASK_TYPE_SOLO = 0x0001;
    public static final int TASK_TYPE_COOPER = 0x0002;
    public static final int TASK_TYPE_PK = 0x0003;

    private static final int MSG_CLOCK_TICK = 0x0004;
    private static final int MSG_CLOCK_STOP = 0x0005;

    private int mCurrHour;
    private int mCurrMin;
    private int mCurrSec;
    private int mSumSec;

    private TextView mHourText;
    private TextView mMinText;
    private TextView mSecText;

    private Handler mHandler;
    private Runnable mTickRunnable;


    private ListView mResultList;

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

        ImageButton returnButton = (ImageButton) findViewById(
                R.id.imageButton_countdown_return);

        mHourText = (TextView) findViewById(R.id.textView_main_countdown_hour);
        mMinText = (TextView) findViewById(R.id.textView_main_countdown_min);
        mSecText = (TextView) findViewById(R.id.textView_main_countdown_sec);
        mResultList = (ListView) findViewById(R.id.listView_main_countdown);

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
                        break;
                }
            }
        };

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initAdapter();
    }

    /**
     * 从SharedReference中读取结束时间，与当前时间比较，算出倒计时
     */
    @Override
    protected void onResume() {
        super.onResume();

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

    /**
     * 停止界面的倒计时,发送Broadcast，开始计算离开倒计时界面的时间
     */
    @Override
    protected void onPause() {
        super.onPause();

        mHandler.removeCallbacks(mTickRunnable);
        if (PrefsUtil.isOnTask(this)) {
            sendBroadcast(new Intent(APPStateReceiver.ACTION_NAME_QUIT));
        }
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


    private void initAdapter() {
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> temp = new HashMap<String, Object>();
        temp.put("FIRST_COLUMN", "萌萌哒贝壳");
        temp.put("SECOND_COLUMN", "100");
        temp.put("THIRD_COLUMN", R.drawable.icn_user_shell);

        list.add(temp);

        HashMap<String, Object> temp1 = new HashMap<String, Object>();
        temp1.put("FIRST_COLUMN", "Diaries");
        temp1.put("SECOND_COLUMN", "200");
        temp1.put("THIRD_COLUMN", R.drawable.icn_user_shell);

        list.add(temp1);
        mResultList.setAdapter(new CountdownListViewAdapter(this, list));
    }

    /**
     * 计数页面ListView列表项，by Dean
     */
    public static class CountdownListViewAdapter extends BaseAdapter {
        public ArrayList<HashMap<String, Object>> list;
        Activity activity;

        public CountdownListViewAdapter(Activity activity, ArrayList<HashMap<String, Object>> list) {
            super();
            this.activity = activity;
            this.list = list;
        }


        public int getCount() {
            return list.size();
        }


        public Object getItem(int position) {
            return list.get(position);
        }


        public long getItemId(int position) {
            return 0;
        }

        private class ViewHolder {
            TextView txtFirst;
            TextView txtSecond;
            ImageView imgThrid;

        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            LayoutInflater inflater = activity.getLayoutInflater();

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_countdown, null);
                holder = new ViewHolder();
                holder.txtFirst = (TextView) convertView.findViewById(R.id.FirstText);
                holder.txtSecond = (TextView) convertView.findViewById(R.id.SecondText);
                holder.imgThrid = (ImageView) convertView.findViewById(R.id.ThirdImg);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            HashMap<String, Object> map = list.get(position);
            holder.txtFirst.setText((String) map.get("FIRST_COLUMN"));
            holder.txtSecond.setText((String) map.get("SECOND_COLUMN"));
            holder.imgThrid.setBackgroundResource((Integer) map.get("THIRD_COLUMN"));


            return convertView;
        }
    }
}
