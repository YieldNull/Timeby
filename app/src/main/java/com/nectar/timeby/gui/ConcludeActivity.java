package com.nectar.timeby.gui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import com.nectar.timeby.R;
import java.util.ArrayList;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ConcludeActivity extends Activity {

    private SeekBar mSeekBar1;
    private SeekBar mSeekBar2;
    private TextView mPercent1;
    private TextView mPercent2;
    private ArrayList<String> eventList;
    private TextView btnEvent;
    private PopupWindow pw;
    int clickPosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/youyuan.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        setContentView(R.layout.activity_conclude);

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
                pw.setBackgroundDrawable(getResources().getDrawable(R.drawable.event_bkg));
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
