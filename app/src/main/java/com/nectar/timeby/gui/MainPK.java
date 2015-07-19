package com.nectar.timeby.gui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.nectar.timeby.R;
import com.nectar.timeby.gui.DigitCountDown.CustomDigitalClock;
import com.nectar.timeby.gui.bc_infoList.listviewAdapter;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MainPK extends Activity implements View.OnClickListener {
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
    private ImageButton PK_back;
    private ArrayList<HashMap<String,Object>> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pk);
        btnBack();
        bundle = getIntent().getExtras();
        getCountDown();
        Log.i("countDownTime",""+countDownTime);
        long convertTime = countDownTime * 60 * 1000;
        Log.i("convertTime",""+convertTime);

        Calendar cal=Calendar.getInstance();
        int year=cal.get(Calendar.YEAR);
        Log.i("year",""+year);
        int month=cal.get(Calendar.MONTH)+1;
        Log.i("month", "" + month);
        int day=cal.get(Calendar.DAY_OF_MONTH);
        Log.i("day:",""+day);
        int hour=cal.get(Calendar.HOUR_OF_DAY);
        Log.i("hour:",""+hour);
        getStartHour();
        getStartMin();
        getStartAPM();
        getEndHour();
        getEndMin();
        getEndAPM();

        if (startAPM.equals("AM")&&endAPM.equals("AM")){
            if (hour >= 12){
//                sHour += 24;
//                eHour += 24;
                day += 1;
            }
        }
        else if (startAPM.equals("AM")&&endAPM.equals("PM")){
            if (hour >= 12){
//                sHour += 24;
//                eHour += 36;
                day += 1;
                endHour += 12;
            }else {
                endHour += 12;
            }
        }else if(startAPM.equals("PM")&&endAPM.equals("AM")){
            if (hour >= 12) {
//                sHour += 36;
//                eHour += 48;
                day += 1;
                startHour += 12;
                endHour += 24;
            } else {
                startHour += 12;
                endHour += 24;
            }
        }else if(startAPM.equals("PM")&&endAPM.equals("PM")){
            if (hour >= 12){
                if((hour-12)>startHour) {
                    day += 1;
                    startHour += 12;
                    endHour += 12;
                }else {
                    startHour += 12;
                    endHour += 12;
                }
            }else {
                startHour += 12;
                endHour += 12;
            }
        }

        startTime = strToDateLong(""+year+"-"+month+"-"+day+" "+startHour+":"+startMin+":00");
        endTime = strToDateLong(""+year+"-"+month+"-"+day+" "+endHour+":"+endMin+":00");

        timeClock=(CustomDigitalClock) findViewById(R.id.time);
        timeClock.setStartTime(startTime);
        timeClock.setEndTime(endTime);
        ListView lview = (ListView) findViewById(R.id.info_list_pk);
        populateList();
        listviewAdapter adapter = new listviewAdapter(this, list);
        lview.setAdapter(adapter);

    }

    @SuppressLint("SimpleDateFormat")
    public Date strToDateLong(String strDate)
    {
        if("".equals(strDate)||null==strDate){
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    public long getCountDown(){
        countDownTime = bundle.getInt("CountDownTime");
        return countDownTime;
    }

    public int getStartHour(){
        startHour = bundle.getInt("startHour");
        return startHour;
    }
    public int getStartMin(){
        startMin = bundle.getInt("startMin");
        return startMin;
    }
    public int getEndHour(){
        endHour = bundle.getInt("endHour");
        return endHour;
    }
    public int getEndMin(){
        endMin = bundle.getInt("endMin");
        return endMin;
    }
    public String getStartAPM(){
        startAPM = bundle.getString("startAPM");
        return startAPM;
    }
    public String getEndAPM() {
        endAPM = bundle.getString("endAPM");
        return endAPM;
    }
    private void populateList() {

        list = new ArrayList<HashMap<String,Object>>();

        HashMap<String,Object> temp = new HashMap<String,Object>();
        temp.put("FIRST_COLUMN", "萌萌哒贝壳");
        temp.put("SECOND_COLUMN", "100");
        temp.put("THIRD_COLUMN", R.drawable.mmdshell);

        list.add(temp);

        HashMap<String,Object> temp1 = new HashMap<String,Object>();
        temp1.put("FIRST_COLUMN", "Diaries");
        temp1.put("SECOND_COLUMN", "200");
        temp1.put("THIRD_COLUMN", R.drawable.mmdshell);

        list.add(temp1);

    }
    public void btnBack() {
        PK_back = (ImageButton) findViewById(R.id.imageButton_user_return);
        PK_back.setOnClickListener(this);
    }

    public void exitDialog(){
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_pk, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        exitDialog();
    }
}
