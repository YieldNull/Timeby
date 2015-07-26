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
public class UserMsgFragment extends Fragment {
    private static final String TAG = "UserMsgFragment";

    private ListView userMsgListView;
    private ArrayList<Message> userMsgList = new ArrayList<Message>();
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

    private void initUserMsgs() {
        ClientDao db = new ClientDao(getActivity());
        ArrayList<Message> messages = db.queryMessage(0);
        Log.d(TAG, messages.size() + "");
        for (Message message : messages) {
            userMsgList.add(message);
        }
    }


    public class MsgAdapter extends ArrayAdapter<Message> {
        private int resourceId;

        public MsgAdapter(Context context, int textViewResourceId, List<Message> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Message msg = getItem(position);
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.msg_item = (TextView) view.findViewById(R.id.user_msg_item_text);
                viewHolder.msg_name = (TextView) view.findViewById(R.id.user_msg_name_text);
                viewHolder.msg_time = (TextView) view.findViewById(R.id.user_msg_time_text);
                viewHolder.msg_agree = (TextView) view.findViewById(R.id.user_msg_item_text_agree);
                viewHolder.msg_time = (TextView) view.findViewById(R.id.user_msg_item_text_refuse);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.msg_item.setText(msg.getContent());
            viewHolder.msg_name.setText(msg.getTitle());
            viewHolder.msg_time.setText(msg.getTimeStr());
            if (msg.getDisposed() == 1) {
                viewHolder.msg_agree.setText("已处理");
                viewHolder.msg_refuse.setText("");
            } else {
                viewHolder.msg_agree.setText("同意");
                viewHolder.msg_refuse.setText("拒绝");
            }
            return view;
        }

        class ViewHolder {
            TextView msg_item;
            TextView msg_name;
            TextView msg_time;

            TextView msg_agree;
            TextView msg_refuse;

        }
    }
}
