package com.nectar.timeby.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.nectar.timeby.R;

public class ShowReportFormActivity extends Activity {

//    final static int LEFT_TOP_CLICK=0;
//    final static int RIGHT_TOP_CLICK=1;
//    final static int LEFT_BOTTOM_CLICK=2;
//    final static int RIGHT_BOTTOM_CLICK=3;

    private ImageView returnButton;
    private ImageView leftTopButton;
    private ImageView rightTopButton;
    private ImageView leftBottomButton;
    private ImageView rightBottomButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_report_form);
        
        returnButton=(ImageView)findViewById(R.id.show_report_form_activity_return);
        leftTopButton=(ImageView)findViewById(R.id.left_top_image_button);
        rightTopButton=(ImageView)findViewById(R.id.right_top_image_button);
        leftBottomButton=(ImageView)findViewById(R.id.left_bottom_image_button);
        rightBottomButton=(ImageView)findViewById(R.id.right_bottom_image_button);

        returnButton.setOnClickListener(new OnClickListener());
        leftTopButton.setOnClickListener(new OnClickListener());
        rightTopButton.setOnClickListener(new OnClickListener());
        leftBottomButton.setOnClickListener(new OnClickListener());
        rightBottomButton.setOnClickListener(new OnClickListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_report_form, menu);
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

    private class OnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(v==returnButton){
                onBackPressed();
            }
            else if(v==leftTopButton){
                Intent intent = new Intent();
                intent.setClass(ShowReportFormActivity.this, PieChartActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_top_appear, R.anim.stay_motionless);
            }
            else if(v==rightTopButton){
                Intent intent = new Intent();
                intent.setClass(ShowReportFormActivity.this, LineChartActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_top_appear, R.anim.stay_motionless);
            }
            else if(v==leftBottomButton){
                Intent intent = new Intent();
                intent.setClass(ShowReportFormActivity.this, LineChart2Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_bottom_appear, R.anim.stay_motionless);
            }
            else if(v==rightBottomButton){
                Intent intent = new Intent();
                intent.setClass(ShowReportFormActivity.this, DataComparedWithLastWeekActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_bottom_appear, R.anim.stay_motionless);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

}
