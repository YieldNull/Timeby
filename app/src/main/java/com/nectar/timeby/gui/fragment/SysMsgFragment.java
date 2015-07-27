package com.nectar.timeby.gui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nectar.timeby.R;
import com.nectar.timeby.db.ClientDao;
import com.nectar.timeby.db.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dean on 15/7/23.
 */
public class SysMsgFragment extends Fragment {
    private static final String TAG = "SysMsgFragment";
    private ListView sysMessageListView;
    private ArrayList<Message> sysMessageList = new ArrayList<Message>();
    private MessageAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sys_msg, container, false);
        initSysMessages();
        adapter = new MessageAdapter(getActivity(), R.layout.list_item_msg_sys, sysMessageList);
        sysMessageListView = (ListView) view.findViewById(R.id.sys_msg_list);
        sysMessageListView.setAdapter(adapter);
        return view;
    }

    private void initSysMessages() {
        ClientDao db = new ClientDao(getActivity());
        ArrayList<Message> messages = db.querySysMessage();
        Log.d(TAG, messages.size() + "");
        for (Message message : messages) {
            sysMessageList.add(message);
        }
    }


    public class MessageAdapter extends ArrayAdapter<Message> {
        private int resourceId;

        public MessageAdapter(Context context, int textViewResourceId, List<Message> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Message Message = getItem(position);
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.Message_item = (TextView) view.findViewById(R.id.sys_msg_item_text);
                viewHolder.Message_time = (TextView) view.findViewById(R.id.sys_msg_time_text);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.Message_item.setText(Message.getContent());
            viewHolder.Message_time.setText(Message.getTimeStr());
            return view;
        }

        class ViewHolder {
            TextView Message_time;
            TextView Message_item;
        }
    }
}
