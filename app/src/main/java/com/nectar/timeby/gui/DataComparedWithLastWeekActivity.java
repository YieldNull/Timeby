package com.nectar.timeby.gui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nectar.timeby.R;


public class DataComparedWithLastWeekActivity extends Activity {

    private int rateSuccess=0;
    private int rateConcentration=0;
    private int rateEfficiency=0;
    private TextView rateSuccessView;
    private TextView rateConcentrationView;
    private TextView rateEfficiencyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_compared_with_last_week);
        rateSuccessView=(TextView)findViewById(R.id.rate_success);
        rateConcentrationView=(TextView)findViewById(R.id.rate_concentration);
        rateEfficiencyView=(TextView)findViewById(R.id.rate_efficiency);

        double sign;
        sign=Math.random()-0.5;
        if(sign==0.0)sign=-1.0;
        sign=sign/Math.abs(sign);
        rateSuccess=(int)(sign*Math.random()*100);
        sign=Math.random()-0.5;
        if(sign==0.0)sign=-1.0;
        sign=sign/Math.abs(sign);
        rateConcentration=(int)(sign*Math.random()*100);
        sign=Math.random()-0.5;
        if(sign==0.0)sign=-1.0;
        sign=sign/Math.abs(sign);
        rateEfficiency=(int)(sign*Math.random()*100);

        updateData();

        ((ImageView)findViewById(R.id.data_compared_with_last_week_activity_return)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stay_motionless, R.anim.right_bottom_disappear);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_compared_with_last_week, menu);
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

    private void updateData(){
        if(rateSuccess>0&&rateSuccess<10){
            rateSuccessView.setText("＋ 0"+rateSuccess+"%");
            rateSuccessView.setTextColor(Color.rgb(173, 204, 173));
        }
        else if(rateSuccess<0&&rateSuccess>-10){
            rateSuccessView.setText("－ 0"+(-rateSuccess)+"%");
            rateSuccessView.setTextColor(Color.rgb(253, 109, 102));
        }
        else if(rateSuccess>=10){
            rateSuccessView.setText("＋ "+rateSuccess+"%");
            rateSuccessView.setTextColor(Color.rgb(173, 204, 173));
        }
        else if(rateSuccess<=-10){
            rateSuccessView.setText("－ "+(-rateSuccess)+"%");
            rateSuccessView.setTextColor(Color.rgb(253, 109, 102));
        }

        if(rateConcentration>0&&rateConcentration<10){
            rateConcentrationView.setText("＋ 0"+rateConcentration+"%");
            rateConcentrationView.setTextColor(Color.rgb(173, 204, 173));
        }
        else if(rateConcentration<0&&rateConcentration>-10){
            rateConcentrationView.setText("－ 0"+(-rateConcentration)+"%");
            rateConcentrationView.setTextColor(Color.rgb(253, 109, 102));
        }
        else if(rateConcentration>=10){
            rateConcentrationView.setText("＋ "+rateConcentration+"%");
            rateConcentrationView.setTextColor(Color.rgb(173, 204, 173));
        }
        else if(rateConcentration<=-10){
            rateConcentrationView.setText("－ "+(-rateConcentration)+"%");
            rateConcentrationView.setTextColor(Color.rgb(253, 109, 102));
        }

        if(rateEfficiency>0&&rateEfficiency<10){
            rateEfficiencyView.setText("＋ 0"+rateEfficiency+"%");
            rateEfficiencyView.setTextColor(Color.rgb(173, 204, 173));
        }
        else if(rateEfficiency<0&&rateEfficiency>-10){
            rateEfficiencyView.setText("－ 0"+(-rateEfficiency)+"%");
            rateEfficiencyView.setTextColor(Color.rgb(253, 109, 102));
        }
        else if(rateEfficiency>=10){
            rateEfficiencyView.setText("＋ "+rateEfficiency+"%");
            rateEfficiencyView.setTextColor(Color.rgb(173, 204, 173));
        }
        else if(rateEfficiency<=-10){
            rateEfficiencyView.setText("－ "+(-rateEfficiency)+"%");
            rateEfficiencyView.setTextColor(Color.rgb(253, 109, 102));
        }
    }

}
