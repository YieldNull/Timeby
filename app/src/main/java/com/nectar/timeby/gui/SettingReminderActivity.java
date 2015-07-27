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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nectar.timeby.R;

import java.util.ArrayList;

/**
 * Created by fangdongliang on 15/7/25.
 */
public class SettingReminderActivity extends Activity {

    private TextView ctl_model;
    private TextView ctl_reminder;
    private PopupWindow pw;
    private PopupWindow pw_reminder;
    private ArrayList<String> modelList;
    private ArrayList<String> reminderList;
    int clickPosition = -1;
    int clickPosition2 = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_reminder);

        ImageButton button = (ImageButton) findViewById(R.id.btn_return);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ctl_model = (TextView) findViewById(R.id.tv_model);
        ctl_reminder = (TextView) findViewById(R.id.time_reminder);
        modelList = getList();
        reminderList = getReminderList();
        ctl_reminder.setText(reminderList.get(0));

        ctl_model.setText(modelList.get(0));
        ctl_model.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view = getLayoutInflater().inflate(R.layout.layout_conclude_pop_window, null);
                        DisplayMetrics dm = new DisplayMetrics();
                        dm = getResources().getDisplayMetrics();
                        int densityDPI = dm.densityDpi;
                        pw = new PopupWindow(view, 2 * densityDPI / 3, 2 * densityDPI / 3, true);
                        pw.setBackgroundDrawable(getResources().getDrawable(R.drawable.img_conclude_spinner));
                        pw.setFocusable(true);
                        pw.showAsDropDown(ctl_model);
                        ListView lv = (ListView) view.findViewById(R.id.lv_pop);
                        lv.setAdapter(new ListViewAdapter(SettingReminderActivity.this, modelList));
                        lv.setOnItemClickListener(
                                new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        ctl_model.setText(modelList.get(position));
                                        if (clickPosition != position) {
                                            clickPosition = position;
                                        }
                                        pw.dismiss();
                                    }
                                }
                        );

                    }
                }
        );


        ctl_reminder.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view = getLayoutInflater().inflate(R.layout.layout_conclude_pop_window, null);
                        DisplayMetrics dm = new DisplayMetrics();
                        dm = getResources().getDisplayMetrics();
                        int densityDPI = dm.densityDpi;
                        pw_reminder = new PopupWindow(view, 2 * densityDPI / 3, 2 * densityDPI / 3, true);
                        pw_reminder.setBackgroundDrawable(getResources().getDrawable(R.drawable.img_conclude_spinner));
                        pw_reminder.setFocusable(true);
                        pw_reminder.showAsDropDown(ctl_reminder);
                        ListView lv = (ListView) view.findViewById(R.id.lv_pop);
                        lv.setAdapter(new ListViewAdapter(SettingReminderActivity.this, reminderList));
                        lv.setOnItemClickListener(
                                new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        ctl_reminder.setText(reminderList.get(position));
                                        if (clickPosition2 != position) {
                                            clickPosition2 = position;
                                        }
                                        pw_reminder.dismiss();
                                    }
                                }
                        );

                    }
                }
        );
    }

    public ArrayList<String> getReminderList() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(" 1 0 秒 ");
        list.add(" 1 5 秒 ");
        list.add(" 2 0 秒 ");
        list.add(" 2 5 秒");
        return list;

    }

    public ArrayList<String> getList() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(" 10 : 00 ");
        list.add(" 10 : 30 ");
        list.add(" 10 : 45 ");
        list.add(" 11 : 00");
        return list;

    }


    public class ListViewAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        private ArrayList<String> list;


        public ListViewAdapter(Context context, ArrayList<String> list) {
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
