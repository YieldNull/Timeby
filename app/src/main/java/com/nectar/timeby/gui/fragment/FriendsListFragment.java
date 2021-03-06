package com.nectar.timeby.gui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.nectar.timeby.R;
import com.nectar.timeby.db.ClientDao;
import com.nectar.timeby.db.FriendShip;
import com.nectar.timeby.gui.interfaces.OnSubmitTaskListener;
import com.nectar.timeby.util.HttpProcess;
import com.nectar.timeby.util.PrefsUtil;
import com.nectar.timeby.util.TimeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fangdongliang on 15/7/23.
 */
public class FriendsListFragment extends Fragment
        implements OnSubmitTaskListener {


    public static final String ARG_PARAM_TASK_TYPE = "task_type";

    private static final String TAG = "FriendsListActivity";
    private static final String MAP_KEY_PHONE = "phone";
    private static final String MAP_KEY_NICKNAME = "nickname";

    private static final int MSG_REFRESH_LIST = 0x0001;
    private static final int MSG_SERVER_ERROR = 0x0002;
    private static final int MSG_SUCCESS = 0x0003;
    private static final int MSG_FAILURE = 0x0004;

    private String mPhone;
    private int mTaskType;//是否有合作任务，有则显示选择按钮

    private ClientDao mDBManager;
    private Handler mHandler;


    private ContactListAdapter mListAdapter;
    private SwipeMenuListView mListView;
    private ArrayList<Map<String, String>> mFriendsList;


    //如果是选择用户共同完成任务，此为合作方手机号列表
    private ArrayList<String> mCoopFriends;
    private OnFinishListener mListener;

    public interface OnFinishListener {
        void onFinish();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mListener = (OnFinishListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mTaskType = getArguments().getInt(ARG_PARAM_TASK_TYPE);

            mCoopFriends = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_friends_list, container, false);

        mPhone = PrefsUtil.getUserPhone(getActivity());
        mDBManager = new ClientDao(getActivity());

        mFriendsList = new ArrayList<>();

        mListView = (SwipeMenuListView) mRootView.findViewById(R.id.listView_friends_contacts);

        initHandler();

        if (mTaskType == -1) {
            initSwipeMenu();
        }

        mListAdapter = new ContactListAdapter();
        mListView.setAdapter(mListAdapter);

        getFriends();
        return mRootView;
    }

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case MSG_REFRESH_LIST:
                        Log.i(TAG, "REFRESH");
                        mListAdapter.notifyDataSetChanged();
                        break;
                    case MSG_SERVER_ERROR:
                        Toast.makeText(getActivity(),
                                "服务器错误，请稍后再试", Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_FAILURE:
                        Toast.makeText(getActivity(), "发送请求失败，请稍后再试", Toast.LENGTH_SHORT).show();
                        break;

                    case MSG_SUCCESS:
                        mListener.onFinish();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void initSwipeMenu() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(120);
                // set item title
                openItem.setTitle("备注");
                // set item title fontsize
                openItem.setTitleSize(16);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);


                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(120);
                // set
                deleteItem.setTitle("删除");
                deleteItem.setTitleSize(16);
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        mListView.setMenuCreator(creator);

        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu swipeMenu, int index) {
                switch (index) {
                    case 0:
                        Log.i(TAG, position + "Remark");
                        handleRemarkFriend(position);
                        break;
                    case 1:
                        Log.i(TAG, position + "Delete");
                        handleDeleteFriend(position);
                        break;
                }
                return false;
            }
        });
    }

    private void handleDeleteFriend(int position) {

    }

    private void handleRemarkFriend(int position){
        EditText editText = new EditText(getActivity());
        new AlertDialog.Builder(getActivity())
                .setTitle("请输入备注:")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("取消",null)
                .show();
    }

    public static FriendsListFragment newInstance(int taskType) {
        FriendsListFragment fragment = new FriendsListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_TASK_TYPE, taskType);
        fragment.setArguments(args);
        return fragment;
    }

    private void getFriends() {
        //如果本地已经有好友信息，则使用本地的信息
        ArrayList<FriendShip> friends = mDBManager.findFriendInfo(mPhone);
        if (friends.size() != 0) {
            Log.i(TAG, "Read contacts from local database");

            for (FriendShip f : friends) {
                Map<String, String> map = new HashMap<String, String>();
                map.put(MAP_KEY_PHONE, f.getPhoneNumberB());
                map.put(MAP_KEY_NICKNAME, f.getRemark());
                mFriendsList.add(map);

                mListAdapter.notifyDataSetChanged();
            }
            return;
        }

        //如果本地没有好友信息，先将信息从服务器搞下来，然后存入本地
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Download contacts from server");

                //获取好友列表
                try {
                    JSONArray data = HttpProcess.getFriendList(mPhone);
                    Log.i(TAG, mPhone);
                    JSONObject statusJson = data.getJSONObject(0);
                    if (statusJson.get("status").equals(1)) {
                        if (statusJson.getString("result").equals("true"))  //正确返回数据
                        {
                            for (int i = 1; i < data.length(); ++i) {
                                Map<String, String> map = new HashMap<String, String>();

                                JSONObject dataJson = data.getJSONObject(i);
                                String phoneB = dataJson.getString("phonenumB");
                                String remark = dataJson.getString("remark");
                                mDBManager.addFriend(mPhone, phoneB, remark);

                                map.put(MAP_KEY_PHONE, phoneB);
                                map.put(MAP_KEY_NICKNAME, remark);

                                mFriendsList.add(map);
                            }

                            mHandler.sendEmptyMessage(MSG_REFRESH_LIST);
                        }
                    }
                } catch (JSONException e) {
                    Log.w(TAG, e.getMessage());
                }
            }
        }).start();
    }

    @Override
    public void onSubmitTask() {
        final JSONArray jsonArray = new JSONArray();
        for (String phone : mCoopFriends) {
            jsonArray.put(phone);
            Log.i(TAG, phone);
        }

        final int type = mTaskType == MainFragment.TASK_TYPE_COOPER ? 1 : 2;
        long startTime = PrefsUtil.getTaskStartTime(getActivity());
        long endTime = PrefsUtil.getTaskEndTime(getActivity());

        final String startStr = TimeUtil.getTimeStr(startTime);
        final String endStr = TimeUtil.getTimeStr(endTime);

        Log.i(TAG, startStr + "," + endStr);

        mHandler.sendEmptyMessage(MSG_SUCCESS);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject data = HttpProcess.taskApplication(
                        mPhone, jsonArray, startStr, endStr, type);
                try {
                    if (data.get("status").equals(-1)) {
                        mHandler.sendEmptyMessage(MSG_SERVER_ERROR);
                    } else if (data.get("status").equals(0)) {
                        mHandler.sendEmptyMessage(MSG_SERVER_ERROR);
                    } else if (data.get("status").equals(1)) {
                        if (data.getString("result").equals("true")) {
                            mHandler.sendEmptyMessage(MSG_SUCCESS);
                        } else if (data.getString("result").equals("false")) {
                            mHandler.sendEmptyMessage(MSG_FAILURE);
                        }
                    }
                } catch (JSONException e) {
                    Log.w(TAG, e.getMessage());
                }
            }
        }).start();
    }

    private class ContactListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            //设置列表项的数量
            return mFriendsList.size();
        }

        @Override
        public Object getItem(int position) {
            return mFriendsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                //获取引用
                holder = new ViewHolder();
                convertView = getActivity().getLayoutInflater().inflate(
                        R.layout.list_item_friends_contacts, parent, false);
                holder.mContactName = (TextView) convertView.
                        findViewById(R.id.textView_friends_name);
                holder.mHeadImg = (ImageView) convertView.findViewById(
                        R.id.imageView_friends_headimg);

                holder.requestCheckbox = (CheckBox) convertView.findViewById(
                        R.id.checkBox_friends_list_request);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //设置头像与姓名
            holder.mContactName.setText(mFriendsList.get(position).get(MAP_KEY_NICKNAME));
            holder.setImage(mFriendsList.get(position).get(MAP_KEY_PHONE));

            holder.initRequestButton(position);
            return convertView;
        }

        class ViewHolder {
            public TextView mContactName;
            public ImageView mHeadImg;
            public CheckBox requestCheckbox;

            public void setImage(String phone) {
                //TODO 从服务器获取图片信息

                mHeadImg.setImageDrawable(getResources()
                        .getDrawable(R.drawable.img_friends_headimg_default));
            }

            public void initRequestButton(final int position) {
                if (mTaskType != -1) {
                    requestCheckbox.setVisibility(View.VISIBLE);
                    requestCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                mCoopFriends.add(getPhoneNum(position));
                            } else {
                                mCoopFriends.remove(getPhoneNum(position));
                            }
                        }
                    });
                } else {
                    requestCheckbox.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private String getPhoneNum(int position) {
        String phone = mFriendsList.get(position).get(MAP_KEY_PHONE);

        return phone;
    }
}
