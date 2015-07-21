package com.nectar.timeby.gui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nectar.timeby.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MainCooperActivity extends Activity {


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
                convertView = inflater.inflate(R.layout.list_item_countdown, null);
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
