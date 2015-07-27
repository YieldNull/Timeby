package com.nectar.timeby.gui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.nectar.timeby.R;

import java.util.ArrayList;


public class LineChartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);

        ArrayList<Entry> completionDegree = new ArrayList<Entry>();

        Entry e1=new Entry((float)Math.random()*100,0);
        Entry e2=new Entry((float)Math.random()*100,1);
        Entry e3=new Entry((float)Math.random()*100,2);
        Entry e4=new Entry((float)Math.random()*100,3);
        Entry e5=new Entry((float)Math.random()*100,4);
        Entry e6=new Entry((float)Math.random()*100,5);
        Entry e7=new Entry((float)Math.random()*100,6);
        completionDegree.add(e1);
        completionDegree.add(e2);
        completionDegree.add(e3);
        completionDegree.add(e4);
        completionDegree.add(e5);
        completionDegree.add(e6);
        completionDegree.add(e7);



//        ArrayList<Entry> valsComp2 = new ArrayList<Entry>();

//        Entry c1e1 = new Entry(100.000f, 0); // 0 == quarter 1
//        valsComp1.add(c1e1);
//        Entry c1e2 = new Entry(50.000f, 1); // 1 == quarter 2 ...
//        valsComp1.add(c1e2);
//        Entry c1e3 = new Entry(150.000f, 2); // 2 == quarter 3 ...
//        valsComp1.add(c1e3);
//        Entry c1e4 = new Entry(80.000f, 3); // 3 == quarter 4 ...
//        valsComp1.add(c1e4);
//        Entry c1e5=new Entry(123.000f,4);
//        valsComp1.add(c1e5);
//        Entry c1e6=new Entry(80.000f,5);
//        valsComp1.add(c1e6);


//        Entry c2e1 = new Entry(120.000f, 0); // 0 == quarter 1
//        valsComp2.add(c2e1);
//        Entry c2e2 = new Entry(110.000f, 1); // 1 == quarter 2 ...
//        valsComp2.add(c2e2);
//        Entry c2e3 = new Entry(160.000f, 2); // 2 == quarter 3 ...
//        valsComp2.add(c2e3);
//        Entry c2e4 = new Entry(140.000f, 3); // 3 == quarter 4 ...
//        valsComp2.add(c2e4);
//        Entry c3e5=new Entry(150.000f,4);
//        valsComp2.add(c3e5);
//        Entry c3e6=new Entry(96.000f,5);
//        valsComp2.add(c3e6);

        LineDataSet setCompletionDegree = new LineDataSet(completionDegree, "完成度");
        setCompletionDegree.setColors(new int[]{R.color.green}, this);
        setCompletionDegree.setAxisDependency(YAxis.AxisDependency.LEFT);
//        LineDataSet setComp2 = new LineDataSet(valsComp2, "Part 2");
//        setComp2.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(setCompletionDegree);
//        dataSets.add(setComp2);

        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("6天前"); xVals.add("5天前"); xVals.add("4天前"); xVals.add("3天前");
        xVals.add("前天"); xVals.add("昨天");xVals.add("今天");

        LineData lineData = new LineData(xVals, dataSets);
        lineData.setValueFormatter(new PercentFormatter());

        LineChart lineChart=(LineChart)findViewById(R.id.line_chart);

        lineChart.setData(lineData);
        lineChart.setDescription("最近7天每天完成度");
        lineChart.setDescriptionTextSize(13.0f);
        lineChart.invalidate(); // refresh

        lineChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);

        ((ImageView)findViewById(R.id.line_chart_activity_return)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stay_motionless, R.anim.right_top_disappear);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_line_chart, menu);
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
}
