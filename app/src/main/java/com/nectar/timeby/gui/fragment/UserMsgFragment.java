package com.nectar.timeby.gui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nectar.timeby.R;
import com.nectar.timeby.db.ClientDao;
import com.nectar.timeby.db.Message;
import com.nectar.timeby.util.HttpProcess;
import com.nectar.timeby.util.HttpUtil;
import com.nectar.timeby.util.PrefsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dean on 15/7/23.
 */
public class UserMsgFragment extends Fragment {
    private static final String TAG = "UserMsgFragment";

    private static final int MSG_SERVER_ERROR = 0x0002;
    private static final int MSG_AGREE_SUCCESS = 0x0003;
    private static final int MSG_AGREE_FAILURE = 0x0004;
    private static final int MSG_REFUSE_SUCCESS = 0x0005;
    private static final int MSG_REFUSE_FAILURE = 0x0006;

    private Handler mHandler;

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

        mHandler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_SERVER_ERROR:
                    case MSG_AGREE_FAILURE:
                    case MSG_REFUSE_FAILURE:
                        Toast.makeText(getActivity(), "服务器错误，请稍后再试", Toast.LENGTH_SHORT).show();
                        break;

                    case MSG_AGREE_SUCCESS:
                        Toast.makeText(getActivity(), "已同意申请", Toast.LENGTH_SHORT).show();
                        userMsgList.get(msg.arg1).setDisposed(Message.MSG_DISPOSED_AGREE);

                        //功能=更新各种数据
                        ClientDao db = new ClientDao(getActivity());
                        db.updateMessage((Message) msg.obj);

                        adapter.notifyDataSetChanged();
                        break;

                    case MSG_REFUSE_SUCCESS:
                        Toast.makeText(getActivity(), "已拒绝申请", Toast.LENGTH_SHORT).show();
                        userMsgList.get(msg.arg1).setDisposed(Message.MSG_DISPOSED_REFUSE);

                        //功能=更新各种数据
                        ClientDao db2 = new ClientDao(getActivity());
                        db2.updateMessage((Message) msg.obj);
                        adapter.notifyDataSetChanged();

                        break;

                    default:
                        break;
                }
            }
        };
        return view;
    }

    private void initUserMsgs() {
        ClientDao db = new ClientDao(getActivity());
        ArrayList<Message> messages = db.queryMessage(Message.MSG_TYPE_USER);
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Message msg = getItem(position);
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.msg_item = (TextView) view.findViewById(R.id.user_msg_item_text);
                viewHolder.msg_name = (TextView) view.findViewById(R.id.user_msg_name_text);
                viewHolder.msg_time = (TextView) view.findViewById(R.id.user_msg_time_text);
                viewHolder.msg_agree = (TextView) view.findViewById(R.id.user_msg_item_text_agree);
                viewHolder.msg_refuse = (TextView) view.findViewById(R.id.user_msg_item_text_refuse);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.msg_item.setText(msg.getContent());
            viewHolder.msg_name.setText(msg.getTitle());
            viewHolder.msg_time.setText(msg.getTimeStr());
            if (msg.getDisposed() == Message.MSG_DISPOSED_AGREE) {
                viewHolder.msg_agree.setText("已同意");
                viewHolder.msg_refuse.setText("");
            } else if (msg.getDisposed() == Message.MSG_DISPOSED_REFUSE) {
                viewHolder.msg_agree.setText("已拒绝");
                viewHolder.msg_refuse.setText("");
            } else {
                viewHolder.msg_agree.setText("同意");
                viewHolder.msg_refuse.setText("拒绝");

                viewHolder.msg_agree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        handleAddFriends(position, msg, true);
                    }
                });

                viewHolder.msg_refuse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleAddFriends(position, msg, false);
                    }
                });
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


    private void handleAddFriends(final int position, final Message message, final boolean isAgree) {
        if (!HttpUtil.isNetAvailable(getActivity())) {
            Toast.makeText(getActivity(), "无网络连接，请打开数据网络", Toast.LENGTH_SHORT).show();
            return;
        }

        final String oper = isAgree ? "true" : "false";

        new Thread(new Runnable() {
            @Override
            public void run() {
                String mPhone = PrefsUtil.getUserPhone(getActivity());
                JSONObject data = HttpProcess.friendApplicationResult(
                        mPhone, message.getPhoneNum(), oper);

                Log.i(TAG, mPhone + ":" + message.getPhoneNum() + ":" + oper);

                try {
                    if (data.get("status").equals(-1)) {
                        mHandler.sendEmptyMessage(MSG_SERVER_ERROR);

                    } else if (data.get("status").equals(0)) {
                        mHandler.sendEmptyMessage(MSG_SERVER_ERROR);

                    } else if (data.get("status").equals(1)) {
                        if (data.getString("result").equals("true")) {

                            //成功之后，传递massage对象，用于更新数据库
                            //传递position用于更新显示状态
                            if (isAgree) {
                                android.os.Message msg = android.os.Message.obtain();
                                msg.arg1 = position;
                                msg.obj = message;
                                msg.what = MSG_AGREE_SUCCESS;
                                mHandler.sendMessage(msg);

                            } else {
                                android.os.Message msg = android.os.Message.obtain();
                                msg.arg1 = position;
                                msg.obj = message;
                                msg.what = MSG_REFUSE_SUCCESS;
                                mHandler.sendMessage(msg);
                            }

                        } else if (data.getString("result").equals("false")) {

                            if (isAgree)
                                mHandler.sendEmptyMessage(MSG_AGREE_FAILURE);
                            else
                                mHandler.sendEmptyMessage(MSG_REFUSE_FAILURE);
                        }
                    }
                } catch (JSONException e) {
                    Log.w(TAG, e.getMessage());
                }
            }
        }).start();
    }
}
