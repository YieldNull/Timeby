package com.nectar.timeby.gui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nectar.timeby.R;
import com.nectar.timeby.gui.widget.TopNotification;
import com.nectar.timeby.util.HttpProcess;
import com.nectar.timeby.util.HttpUtil;
import com.nectar.timeby.util.PrefsUtil;
import com.nectar.timeby.util.TimeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ConcludeActivity extends Activity {

    private static final String TAG = "ConcludeActivity";
    private static final int IS_FINISH = 1;
    private static final int MSG_SUCCESS = 2;
    private static final int MSG_FAILURE = 3;
    private static final int MSG_SERVER_ERROR = 4;

    private SeekBar mSeekBar1;
    private SeekBar mSeekBar2;
    private TextView mPercent1;
    private TextView mPercent2;
    private ArrayList<String> eventList;
    private TextView btnEvent;
    private PopupWindow pw;


    private int clickPosition = -1;

    private Button mCancelButon;
    private Button mSubmitButton;

    private Handler mHandler;

    private String mTaskContentStr;
    private float mFoucusDegree, mEfficiency;

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/youyuan.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        setContentView(R.layout.activity_conclude);

        mCancelButon = (Button) findViewById(R.id.cCancell);
        mSubmitButton = (Button) findViewById(R.id.cEnsure);
        mCancelButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSubmit();
            }
        });

        mSeekBar1 = (SeekBar) findViewById(R.id.ConcentrationsB);
        mSeekBar2 = (SeekBar) findViewById(R.id.EfficiencysB);
        mPercent1 = (TextView) findViewById(R.id.ConcentrationPer);
        mPercent2 = (TextView) findViewById(R.id.EfficiencyPer);

        mSeekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPercent1.setText(progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSeekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPercent2.setText(progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnEvent = (TextView) findViewById(R.id.btn_pw);
        eventList = getList();
        btnEvent.setText(eventList.get(0));
        btnEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.layout_conclude_pop_window, null);
                DisplayMetrics dm = new DisplayMetrics();
                dm = getResources().getDisplayMetrics();
                int densityDPI = dm.densityDpi;

                pw = new PopupWindow(view, 7 * densityDPI / 9, 2 * densityDPI / 3, true);
                pw.setBackgroundDrawable(getResources().getDrawable(R.drawable.img_conclude_spinner));
                pw.setFocusable(true);
                pw.showAsDropDown(btnEvent);

                ListView lv = (ListView) view.findViewById(R.id.lv_pop);
                lv.setAdapter(new ListViewAdapter2(ConcludeActivity.this, eventList));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        btnEvent.setText(eventList.get(position));
                        if (clickPosition != position) {
                            clickPosition = position;
                        }
                        pw.dismiss();
                    }
                });

            }
        });

        initHandler();
        new TopNotification(this, "任务成功，请进行自我评价", 3000);
    }


    /**
     * 初始化handler
     */
    private void initHandler() {
        mHandler = new Handler() {
            public void handleMessage(Message msg) {

                switch (msg.what) {
                    case MSG_SERVER_ERROR:
                        Toast.makeText(ConcludeActivity.this,
                                "服务器错误，请稍后再试", Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_FAILURE:
                        Toast.makeText(ConcludeActivity.this,
                                "发送请求失败，请稍后再试", Toast.LENGTH_SHORT).show();
                        break;

                    case MSG_SUCCESS:
                        onBackPressed();

                        break;
                    default:
                        break;
                }
            }
        };
    }

    /**
     * 提交申请
     */
    private void performSubmit() {

        if (!HttpUtil.isNetAvailable(this)) {
            Toast.makeText(this, "无网络连接，请打开数据网络", Toast.LENGTH_SHORT).show();
            return;
        }

        mTaskContentStr = btnEvent.getText().toString();
        mFoucusDegree = (float) mSeekBar1.getProgress();
        mEfficiency = (float) mSeekBar2.getProgress();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Sending conclusion to server");
                String mPhoneStr = PrefsUtil.getUserPhone(ConcludeActivity.this);
                String mStartTimeStr = TimeUtil.getTimeStr(
                        PrefsUtil.getTaskStartTime(ConcludeActivity.this));
                String mEndTimeStr = TimeUtil.getTimeStr(
                        PrefsUtil.getTaskEndTime(ConcludeActivity.this));

                JSONObject data = HttpProcess.submitTaskSummary(
                        mPhoneStr, mStartTimeStr, mEndTimeStr, mTaskContentStr, mFoucusDegree, mEfficiency);

                try {
                    if (data.get("status").equals(-1)) {
                        mHandler.sendEmptyMessage(MSG_SERVER_ERROR);
                    } else if (data.get("status").equals(0)) {
                        mHandler.sendEmptyMessage(MSG_SERVER_ERROR);
                    } else if (data.get("status").equals(1)) {
                        if (data.getString("result").equals("true")) {
                            mHandler.sendEmptyMessage(MSG_SUCCESS);
                        } else if (data.getString("result").equals("false")) {
                            mHandler.sendEmptyMessage(MSG_FAILURE);
                        }
                    }
                } catch (JSONException e) {
                    Log.w(TAG, e.getMessage());
                }
            }
        }).start();
    }


    public ArrayList<String> getList() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("运  动");
        list.add("读  书");
        list.add("看电影");
        list.add("睡  觉");
        return list;

    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public class ListViewAdapter2 extends BaseAdapter {

        private LayoutInflater inflater;

        private ArrayList<String> list;


        public ListViewAdapter2(Context context, ArrayList<String> list) {
            super();
            this.inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_conclude, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.lv_text);
            tv.setText(list.get(position));

            return convertView;
        }
    }

}
