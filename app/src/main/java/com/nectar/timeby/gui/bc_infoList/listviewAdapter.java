package com.nectar.timeby.gui.bc_infoList;

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

/**
 * Created by Dean on 15/7/17.
 */
public class listviewAdapter extends BaseAdapter {
    public ArrayList<HashMap<String, Object>> list;
    Activity activity;

    public listviewAdapter(Activity activity, ArrayList<HashMap<String, Object>> list) {
        super();
        this.activity = activity;
        this.list = list;
    }


    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }


    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }


    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    private class ViewHolder {
        TextView txtFirst;
        TextView txtSecond;
        ImageView imgThrid;

    }


    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        // TODO Auto-generated method stub
        ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.bc_infolist, null);
            holder = new ViewHolder();
            holder.txtFirst = (TextView) convertView.findViewById(R.id.FirstText);
            holder.txtSecond = (TextView) convertView.findViewById(R.id.SecondText);
            holder.imgThrid = (ImageView) convertView.findViewById(R.id.ThirdImg);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HashMap<String, Object> map = list.get(position);
        holder.txtFirst.setText((String)map.get("FIRST_COLUMN"));
        holder.txtSecond.setText((String)map.get("SECOND_COLUMN"));
        holder.imgThrid.setBackgroundResource((Integer)map.get("THIRD_COLUMN"));


        return convertView;
    }
}