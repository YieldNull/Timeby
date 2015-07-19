package com.nectar.timeby.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.nectar.timeby.R;
import com.nectar.timeby.gui.DigitCountDown.CountDownView;
import com.nectar.timeby.gui.bc_infoList.listviewAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class MainPK extends Activity implements View.OnClickListener {
    private ImageButton Company_back;
    private ArrayList<HashMap<String,Object>> list;
    private long countDownTime;
    private int startHour;
    private int startMin;
    private int endHour;
    private int endMin;
    private String startAPM;
    private String endAPM;
    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_pk);
        bundle = getIntent().getExtras();
        setCountDown();
        getStartHour();
        getStartMin();
        getEndHour();
        getEndMin();
        getStartAPM();
        getEndAPM();
        showDigitCountDown();
        btnBack();
        ListView lview = (ListView) findViewById(R.id.info_list_pk);
        populateList();
        listviewAdapter adapter = new listviewAdapter(this, list);
        lview.setAdapter(adapter);

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

    public void showDigitCountDown(){
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        float density  = dm.density;
        int densityDPI = dm.densityDpi;
        float xdpi = dm.xdpi;
        float ydpi = dm.ydpi;
        Log.e("  DisplayMetrics", "density=" + density + "; densityDPI=" + densityDPI);
        Log.e("  DisplayMetrics", "xdpi=" + xdpi + "; ydpi=" + ydpi);

        int w =(int) xdpi * 2;
        int h =(int) ydpi / 2;

//        Display mDisplay = getWindowManager().getDefaultDisplay();
//        int w = mDisplay.getWidth();
//        int h = mDisplay.getHeight();
        int canColor = Color.rgb(188, 236, 188);
        int penColor = Color.rgb(255, 255, 227);

        long getCountDown = countDownTime;
        int sHour = startHour;
        int sMin = startMin;
        int eHour = endHour;
        int eMin = endMin;
        String sAPM = startAPM;
        String eAPM = endAPM;

        Log.i("coutdowntime",""+getCountDown);
        LinearLayout newL = (LinearLayout) findViewById(R.id.digitAlarmLayout_pk);
        LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams(w,h);
        CountDownView countDownView = new CountDownView(getApplicationContext(),densityDPI,penColor,canColor
                ,getCountDown,sHour,sMin,eHour,eMin,sAPM,eAPM);

        countDownView.setLayoutParams(llparams);
        newL.addView(countDownView);
    }

    public long setCountDown(){
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
    public String getEndAPM(){
        endAPM = bundle.getString("endAPM");
        return endAPM;
    }
    public void btnBack() {
        Company_back = (ImageButton) findViewById(R.id.imageButton_user_return);
        Company_back.setOnClickListener(this);
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
