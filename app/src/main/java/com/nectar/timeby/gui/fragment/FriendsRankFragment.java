package com.nectar.timeby.gui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nectar.timeby.R;
import com.nectar.timeby.gui.widget.TopNotification;
import com.nectar.timeby.util.HttpProcess;
import com.nectar.timeby.util.PrefsUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fangdongliang on 15/7/22.
 */
public class FriendsRankFragment extends Fragment {

    private static final String ARG_PARAM_LIST_TYPE = "task_type";

    public static final int LIST_TYPE_WINNER = 0x0001;
    public static final int LIST_TYPE_FAILURE = 0x0002;
    private static final String MAP_KEY_NICKNAME = "nickname";
    private static final String MAP_KEY_COUNT = "count";
    private static final int MSG_REFRESH = 0x1111;
    private static final String TAG = "FriendsRankFragment";


    private Handler mHandler;

    private int mType;
    private ListView mList;
    private BaseAdapter mAdapter;
    private ArrayList<HashMap<String, String>> mFriendsList;
    private String mPhone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_rank, container, false);

        mPhone = PrefsUtil.getUserPhone(getActivity());

        mFriendsList = new ArrayList<>();
        mAdapter = new ContactListAdapter();
        mList = (ListView) view.findViewById(R.id.listView_friends_rank);
        mList.setAdapter(mAdapter);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (msg.what == MSG_REFRESH) {
                    if (mFriendsList.size() == 0) {
                        new TopNotification(getActivity(), "您尚未添加任何好友", 3000).show();
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        };

        new TopNotification(getActivity(), "正在查询好友信息，请稍后", 3000).show();

        downloadData();
        return view;
    }

    private void downloadData() {
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
                                HashMap<String, String> map = new HashMap<String, String>();

                                JSONObject dataJson = data.getJSONObject(i);
                                String phoneB = dataJson.getString("phonenumB");
                                String remark = dataJson.getString("remark");

                                map.put(MAP_KEY_NICKNAME, remark);
                                map.put(MAP_KEY_COUNT, getCount(phoneB));

                                mFriendsList.add(map);

                                sortList();
                            }

                            mHandler.sendEmptyMessage(MSG_REFRESH);
                        }
                    }
                } catch (JSONException e) {
                    Log.w(TAG, e.getMessage());
                }
            }
        }).start();
    }

    private void sortList() {
        Collections.sort(mFriendsList, new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                int lcount = Integer.parseInt(lhs.get(MAP_KEY_COUNT));
                int rcount = Integer.parseInt(rhs.get(MAP_KEY_COUNT));
                return rcount - lcount;
            }
        });
    }


    public static FriendsRankFragment newInstance(int taskType) {
        FriendsRankFragment fragment = new FriendsRankFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_LIST_TYPE, taskType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt(ARG_PARAM_LIST_TYPE);
        }
    }

    /**
     * 获取锤子数或贝壳数
     *
     * @param phoneB
     * @return
     */
    public String getCount(String phoneB) {
        JSONArray data = HttpProcess.getUserMainInfo(phoneB);
        try {
            JSONObject statusJson = data.getJSONObject(0);
            if (statusJson.get("status").equals(-1)) {
            } else if (statusJson.get("status").equals(0)) {
            } else if (statusJson.get("status").equals(1)) {
                if (statusJson.getString("result").equals("true"))  //正确返回数据
                {
                    JSONObject dataJson = data.getJSONObject(1);
                    if (mType == LIST_TYPE_WINNER) {
                        return dataJson.getString("shellnum");
                    } else {
                        return dataJson.getString("harmmernum");
                    }
                } else if (statusJson.getString("result").equals("false")) {
                }
            }
        } catch (JSONException e) {
            Log.i(TAG, e.getMessage());
        }
        return null;
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
                        R.layout.list_item_friends_ranking, parent, false);

                holder.mContactName = (TextView) convertView.
                        findViewById(R.id.textView_friends_rank_name);
                holder.mHeadImg = (ImageView) convertView.findViewById(
                        R.id.imageView_friends_rank);
                holder.mCount = (TextView) convertView
                        .findViewById(R.id.textView_friends_rank_count);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //设置头像与姓名
            holder.mContactName.setText(mFriendsList.get(position).get(MAP_KEY_NICKNAME));
            holder.mCount.setText(mFriendsList.get(position).get(MAP_KEY_COUNT));

            if (mType == LIST_TYPE_WINNER) {
                holder.mHeadImg.setImageDrawable(getResources()
                        .getDrawable(R.drawable.icn_user_shell));
            } else {
                holder.mHeadImg.setImageDrawable(getResources()
                        .getDrawable(R.drawable.icn_user_hammer));
            }

            return convertView;
        }

        class ViewHolder {
            public TextView mContactName;
            public ImageView mHeadImg;
            public TextView mCount;
        }
    }
}