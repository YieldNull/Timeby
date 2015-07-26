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
public class UserMsgFragment extends Fragment {
    private ListView userMsgListView;
    private ArrayList<Msg> userMsgList = new ArrayList<Msg>();
    private MsgAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_msg, container, false);
        initUserMsgs();
        adapter = new MsgAdapter(getActivity(), R.layout.list_item_msg_user, userMsgList);
        userMsgListView = (ListView) view.findViewById(R.id.user_msg_list);
        userMsgListView.setAdapter(adapter);


        return view;
    }

    public class Msg {
        private String content;
        private String name;
        private String sHour;
        private String sMin;

        public Msg(String content, String name) {
            this.content = content;
            this.name = name;

        }

        public String getContent() {
            return content;
        }

        public String getName() {
            return name;
        }

        public String getDate() {
            Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH) + 1;
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int min = cal.get(Calendar.MINUTE);
            if (hour < 10) {
                sHour = "0" + hour + "";
            } else {
                sHour = "" + hour;
            }
            if (min < 10) {
                sMin = "0" + min + "";
            } else {
                sMin = "" + min;
            }
            String date = "" + month + "月" + day + "日   " + sHour + ":" + sMin;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            Msg msg = getItem(position);
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.msg_item = (TextView) view.findViewById(R.id.user_msg_item_text);
                viewHolder.msg_name = (TextView) view.findViewById(R.id.user_msg_name_text);
                viewHolder.msg_time = (TextView) view.findViewById(R.id.user_msg_time_text);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.msg_item.setText(msg.getContent());
            viewHolder.msg_name.setText(msg.getName());
            viewHolder.msg_time.setText(msg.getDate());
            return view;
        }

        class ViewHolder {
            TextView msg_item;
            TextView msg_name;
            TextView msg_time;
        }

    }

    private void initUserMsgs() {
        Msg msg1 = new Msg("ccccccccccccccccccccccc", "小星星");
        userMsgList.add(msg1);
        Msg msg2 = new Msg("ddddddddddddddddddddddd", "小月亮");
        userMsgList.add(msg2);

    }

}
