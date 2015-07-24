package com.nectar.timeby.gui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nectar.timeby.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Dean on 15/7/23.
 */
public class SysMsgFragment extends Fragment{
    private ListView sysMsgListView;
    private ArrayList<Msg> sysMsgList = new ArrayList<Msg>();
    private MsgAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                                Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_sys_msg,container,false);
        initSysMsgs();
        adapter = new MsgAdapter(getActivity(),R.layout.sys_msg_item,sysMsgList);
        sysMsgListView = (ListView) view.findViewById(R.id.sys_msg_list);
        sysMsgListView.setAdapter(adapter);
        return view;
    }

    public class Msg{
        private String content;
        private String sHour;
        private String sMin;
        public Msg(String content){
            this.content = content;
        }
        public String getContent() {
            return content;
        }
        public String getDate(){
            Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH)+1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int min = cal.get(Calendar.MINUTE);
            if (hour < 10){
                sHour = "0"+hour+"";
            }else {
                sHour = ""+hour;
            }
            if (min < 10){
                sMin = "0"+min+"";
            }else {
                sMin = ""+min;
            }
            String date = ""+month+"月"+day+"日   "+sHour+":"+sMin;
            return date;
        }
    }

    public class MsgAdapter extends ArrayAdapter<Msg> {
        private int resourceId;
        public MsgAdapter(Context context, int textViewResourceId, List<Msg> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }
        @Override
        public View getView(int position,View convertView,ViewGroup parent){
            Msg msg = getItem(position);
            View view;
            ViewHolder viewHolder;
            if(convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.msg_item = (TextView) view.findViewById(R.id.sys_msg_item_text);
                viewHolder.msg_time = (TextView) view.findViewById(R.id.sys_msg_time_text);
                view.setTag(viewHolder);
            }else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.msg_item.setText(msg.getContent());
            viewHolder.msg_time.setText(msg.getDate());
            return view;
        }
        class ViewHolder {
            TextView msg_time;
            TextView msg_item;
        }

    }

    private void initSysMsgs(){
        Msg msg1 = new Msg("aaaaaaaaaaaaaaaaaaaaaa");
        sysMsgList.add(msg1);
        Msg msg2 = new Msg("bbbbbbbbbbbbbbbbbbbbbbb");
        sysMsgList.add(msg2);
        Msg msg3 = new Msg("aaaaaaaaaaaaaaaaaaaaaa");
        sysMsgList.add(msg3);
    }
}
