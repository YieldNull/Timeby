package com.nectar.timeby.gui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nectar.timeby.R;
import com.nectar.timeby.db.ClientDao;
import com.nectar.timeby.db.FriendShip;
import com.nectar.timeby.util.HttpProcess;
import com.nectar.timeby.util.PrefsUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fangdongliang on 15/7/23.
 */
public class FriendsListFragment extends Fragment {
    private static final String TAG = "FriendsActivity";
    private static final String MAP_KEY_PHONE = "phone";
    private static final String MAP_KEY_NICKNAME = "nickname";
    private static final int MSG_REFRESH_LIST = 0x0001;

    private String mPhone;
    private ClientDao mDBManager;
    private Handler mHandler;

    private ArrayList<Map<String, String>> mFriendsList;
    private ContactListAdapter mListAdapter;
    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_friends_list, container, false);

        mListView = (ListView) mRootView.findViewById(R.id.listView_friends_contacts);
        mFriendsList = new ArrayList<>();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MSG_REFRESH_LIST) {
                    mListAdapter.notifyDataSetChanged();
                }
            }
        };
        mPhone = PrefsUtil.getUserPhone(getActivity());
        mDBManager = new ClientDao(getActivity());

        mListAdapter = new ContactListAdapter();
        mListView.setAdapter(mListAdapter);

        getFriends();
        return mRootView;
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

                //设置头像与姓名
                holder.mContactName.setText(mFriendsList.get(position).get(MAP_KEY_NICKNAME));
                holder.setImage(mFriendsList.get(position).get(MAP_KEY_PHONE));

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            return convertView;
        }

        class ViewHolder {
            public TextView mContactName;
            public ImageView mHeadImg;

            public void setImage(String phone) {
                //TODO 从服务器获取图片信息

                mHeadImg.setImageDrawable(getResources()
                        .getDrawable(R.drawable.img_friends_headimg_default));
            }
        }
    }
}
