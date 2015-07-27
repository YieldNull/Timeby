package com.nectar.timeby.gui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.nectar.timeby.R;

import java.util.ArrayList;

public class PieChartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);

        PieChart pieChart = (PieChart) findViewById(R.id.pie_chart);

        pieChart.setDescription("本周各事件所用总时间比");
        pieChart.setDescriptionTextSize(20.0f);
        pieChart.setCenterText("各事件\n总时间比");
        pieChart.setCenterTextSize(20.0f);
        pieChart.setUsePercentValues(true);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColorTransparent(true);
        pieChart.setDrawCenterText(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColorTransparent(true);
        pieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);

        ArrayList<Entry> entryArrayList=new ArrayList<Entry>();
        Math.random();
        Entry e1=new Entry((float)Math.random(),0);
        Entry e2=new Entry((float)Math.random(),1);
        Entry e3=new Entry((float)Math.random(),2);
        Entry e4=new Entry((float)Math.random(),3);
        Entry e5=new Entry((float)Math.random(),4);
        entryArrayList.add(e1);
        entryArrayList.add(e2);
        entryArrayList.add(e3);
        entryArrayList.add(e4);
        entryArrayList.add(e5);

        PieDataSet pieDataSet=new PieDataSet(entryArrayList,"各事件");
        pieDataSet.setSliceSpace(4f);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setColors(new int[] { R.color.pie_brown,
                        R.color.pie_blue,
                        R.color.pie_grey,
                        R.color.pie_reddish,
                        R.color.pie_green},
                this);
        //ArrayList<PieDataSet> pieDataSetArrayList=new ArrayList<PieDataSet>();
        //pieDataSetArrayList.add(pieDataSet);
        ArrayList<String> stringArrayList=new ArrayList<String>();
        stringArrayList.add("事件 A");
        stringArrayList.add("事件 B");
        stringArrayList.add("事件 C");
        stringArrayList.add("事件 D");
        stringArrayList.add("事件 E");

        PieData pieData=new PieData(stringArrayList,pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(15f);
        pieData.setValueTextColor(Color.BLACK);
        pieChart.setData(pieData);
        pieChart.invalidate();

        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        pieChart.animateY(2200, Easing.EasingOption.EaseInOutQuad);

        ((ImageView)findViewById(R.id.pie_chart_activity_return)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stay_motionless, R.anim.left_top_disappear);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pie_chart, menu);
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
