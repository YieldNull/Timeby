package com.nectar.timeby.gui;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nectar.timeby.R;
import com.nectar.timeby.util.PrefsUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by finalize on 7/23/15.
 */
public class MainCountDownActivity extends Activity {

    private static final String TAG = "MainCountDownActivity";

    private static final int MSG_CLOCK_TICK = 0x0001;
    private static final int MSG_CLOCK_STOP = 0x0002;

    private int mCurrHour;
    private int mCurrMin;
    private int mCurrSec;
    private int mSumSec;

    private TextView mHourText;
    private TextView mMinText;
    private TextView mSecText;

    private Handler mHandler;
    private Runnable mTickRunnable;


    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * 初始化计时器，在ContentView之后再调用
     */
    protected void init() {
        ImageButton returnButton = (ImageButton) findViewById(R.id.imageButton_countdown_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        Map<String, Long> task = PrefsUtil.readTask(this);
        int DSeconds = (int) ((task.get(PrefsUtil.PREFS_KEY_END_TIME_MILLIS)
                - System.currentTimeMillis()) / 1000);


        mCurrHour = DSeconds / 3600;
        mCurrMin = DSeconds / 60;
        mCurrSec = DSeconds % 60;

        mSumSec = mCurrHour * 3600 + mCurrMin * 60 + mCurrSec;


        mHourText = (TextView) findViewById(R.id.textView_main_countdown_hour);
        mMinText = (TextView) findViewById(R.id.textView_main_countdown_min);
        mSecText = (TextView) findViewById(R.id.textView_main_countdown_sec);

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

        mHandler.post(mTickRunnable);
    }

    private void refreshTimeText() {
        mHourText.setText(getTimeStr(mCurrHour));
        mMinText.setText(getTimeStr(mCurrMin));
        mSecText.setText(getTimeStr(mCurrSec));
    }

    private CharSequence getTimeStr(int time) {
        return time > 9 ? "" + time : "0" + time;
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
