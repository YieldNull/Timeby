package com.nectar.timeby.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nectar.timeby.R;
import com.nectar.timeby.gui.widget.CustomDigitalClock;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MainCooperActivity extends Activity
        implements View.OnClickListener {

    private CustomDigitalClock timeClock;
    private Bundle bundle;
    private Date startTime;
    private Date endTime;
    private long countDownTime;
    private int startHour;
    private int startMin;
    private int endHour;
    private int endMin;
    private String startAPM;
    private String endAPM;
    private ImageButton Company_back;
    private ArrayList<HashMap<String, Object>> list;

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
        setContentView(R.layout.activity_main_company);
        btnBack();
        bundle = getIntent().getExtras();
        getCountDown();
        Log.i("countDownTime", "" + countDownTime);

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        Log.i("year", "" + year);
        int month = cal.get(Calendar.MONTH) + 1;
        Log.i("month", "" + month);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Log.i("day:", "" + day);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        Log.i("hour:", "" + hour);
        int min = cal.get(Calendar.MINUTE);
        getStartHour();
        getStartMin();
        getStartAPM();
        getEndHour();
        getEndMin();
        getEndAPM();

        if (startAPM.equals("AM") && endAPM.equals("AM")) {
            if (hour >= 12) {
//                sHour += 24;
//                eHour += 24;
                day += 1;
            } else {
                if (hour <= startHour) {
                    if (min > startMin) {
                        day += 1;
                    }
                    ;
                } else {
                    day += 1;
                }
            }
        } else if (startAPM.equals("AM") && endAPM.equals("PM")) {
            if (hour >= 12) {
//                sHour += 24;
//                eHour += 36;
                day += 1;
                endHour += 12;
            } else {
                if (hour <= startHour) {
                    if (min > startMin) {
                        day += 1;
                    }
                    endHour += 12;
                } else {
                    day += 1;
                    endHour += 12;
                }
            }
        } else if (startAPM.equals("PM") && endAPM.equals("AM")) {
            if (hour >= 12) {
                if ((hour - 12) > startHour) {
//                sHour += 36;
//                eHour += 48;
                    day += 1;
                    startHour += 12;
                    endHour += 24;
                } else {
                    if (min > startMin) {
                        day += 1;
                    }
                    startHour += 12;
                    endHour += 24;
                }
            } else {
                startHour += 12;
                endHour += 24;
            }
        } else if (startAPM.equals("PM") && endAPM.equals("PM")) {
            if (hour >= 12) {
                if ((hour - 12) > startHour) {
                    day += 1;
                    startHour += 12;
                    endHour += 12;
                } else {
                    if (min > startMin) {
                        day += 1;
                    }
                    startHour += 12;
                    endHour += 12;
                }
            } else {
                startHour += 12;
                endHour += 12;
            }
        }

        startTime = strToDateLong("" + year + "-" + month + "-" + day + " " + startHour + ":" + startMin + ":00");
        endTime = strToDateLong("" + year + "-" + month + "-" + day + " " + endHour + ":" + endMin + ":00");

        timeClock = (CustomDigitalClock) findViewById(R.id.time);
        timeClock.setStartTime(startTime);
        timeClock.setEndTime(endTime);
        ListView lview = (ListView) findViewById(R.id.info_list_company);
        populateList();
        MyListViewAdapter adapter = new MyListViewAdapter(this, list);
        lview.setAdapter(adapter);

    }

    public Date strToDateLong(String strDate) {
        if ("".equals(strDate) || null == strDate) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    public long getCountDown() {
        countDownTime = bundle.getInt("CountDownTime");
        return countDownTime;
    }

    public int getStartHour() {
        startHour = bundle.getInt("startHour");
        return startHour;
    }

    public int getStartMin() {
        startMin = bundle.getInt("startMin");
        return startMin;
    }

    public int getEndHour() {
        endHour = bundle.getInt("endHour");
        return endHour;
    }

    public int getEndMin() {
        endMin = bundle.getInt("endMin");
        return endMin;
    }

    public String getStartAPM() {
        startAPM = bundle.getString("startAPM");
        return startAPM;
    }

    public String getEndAPM() {
        endAPM = bundle.getString("endAPM");
        return endAPM;
    }

    private void populateList() {

        list = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> temp = new HashMap<String, Object>();
        temp.put("FIRST_COLUMN", "萌萌哒贝壳");
        temp.put("SECOND_COLUMN", "100");
        temp.put("THIRD_COLUMN", R.drawable.img_countdown_mmdshell);

        list.add(temp);

        HashMap<String, Object> temp1 = new HashMap<String, Object>();
        temp1.put("FIRST_COLUMN", "Diaries");
        temp1.put("SECOND_COLUMN", "200");
        temp1.put("THIRD_COLUMN", R.drawable.img_countdown_mmdshell);

        list.add(temp1);

    }

    public void btnBack() {
        Company_back = (ImageButton) findViewById(R.id.imageButton_user_return);
        Company_back.setOnClickListener(this);
    }

    public void exitDialog() {
        AlertDialog isExit = new AlertDialog.Builder(this).create();
        // 设置对话框标题
        isExit.setTitle("任务未完成");
        // 设置对话框消息
        isExit.setMessage("确定要放弃任务吗?");
        // 添加选择按钮并注册监听
        isExit.setButton("确定", listener);
        isExit.setButton2("取消", listener);
        // 显示对话框
        isExit.show();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitDialog();

        }

        return false;

    }


    /**
     * 监听对话框里面的button点击事件
     */
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void onClick(View v) {
        exitDialog();
    }

    public static class MyListViewAdapter extends BaseAdapter {
        public ArrayList<HashMap<String, Object>> list;
        Activity activity;

        public MyListViewAdapter(Activity activity, ArrayList<HashMap<String, Object>> list) {
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
                convertView = inflater.inflate(R.layout.countdown_list_item, null);
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
