package com.nectar.timeby.gui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nectar.timeby.R;
import com.nectar.timeby.db.ClientDao;
import com.nectar.timeby.db.FriendShip;
import com.nectar.timeby.util.HttpProcess;
import com.nectar.timeby.util.HttpUtil;
import com.nectar.timeby.util.PrefsUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddFriendsFromContact extends Activity {

    private static final String TAG = "AddFriendsFromContact";
    private static final String CONTACT_MAP_KEY_ID = "id";
    private static final String CONTACT_MAP_KEY_PHONE = "phone";
    private static final String CONTACT_MAP_KEY_NAME = "name";

    private static final int MSG_REFRESH_LIST = 0x0001;
    private static final int MSG_SERVER_ERROR = 0x0002;
    private static final int MSG_SUCCESS = 0x0003;
    private static final int MSG_FAILURE = 0x0004;

    private static final String MAP_KEY_PHONE = "phone";
    private static final String MAP_KEY_NICKNAME = "nickname";

    private ListView mContactListView;
    private BaseAdapter mContactListAdapter;
    private Handler mHandler;
    private ClientDao mDBManager;

    private ArrayList<String> mRegisteredContacts;
    private ArrayList<String> mAddedContacts;
    private ArrayList<Map<String, String>> mPhoneContacts;

    private String mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_from_contact);

        mContactListView = (ListView) findViewById(R.id.listView_add_friends_from_contact);
        mContactListAdapter = new ContactListAdapter();
        mRegisteredContacts = new ArrayList<>();
        mAddedContacts = new ArrayList<>();
        mContactListView.setAdapter(mContactListAdapter);

        mDBManager = new ClientDao(this);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case MSG_REFRESH_LIST:
                        Log.i(TAG, "REFRESH");
                        mContactListAdapter.notifyDataSetChanged();
                        break;
                    case MSG_SERVER_ERROR:
                        Toast.makeText(AddFriendsFromContact.this,
                                "服务器错误，请稍后再试", Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_FAILURE:
                        Toast.makeText(AddFriendsFromContact.this, "您已发送申请，请等待对方回复", Toast.LENGTH_SHORT).show();
                        break;
                    case MSG_SUCCESS:
                        Toast.makeText(AddFriendsFromContact.this,
                                "已发送申请", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
        mPhone = PrefsUtil.getUserPhone(this);
        getContactList();

        ImageView returnButton = (ImageView) findViewById(
                R.id.imageView_add_friends_from_contact_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    /**
     * ListView 的适配器
     */
    private class ContactListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            //设置列表项的数量
            return mRegisteredContacts.size();
        }

        @Override
        public Object getItem(int position) {
            return mRegisteredContacts.get(position);
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
                convertView = getLayoutInflater().inflate(
                        R.layout.list_item_addfriend_from_contacts, parent, false);
                holder.mContactName = (TextView) convertView.
                        findViewById(R.id.textView_add_friends_from_contact_name);
                holder.mTextButton = (TextView) convertView.findViewById(
                        R.id.textView_btn_add_friends_from_contact_name);

                //设置数据
                String phone = mRegisteredContacts.get(position);
                holder.mContactName.setText(getContactName(phone));
                if (mAddedContacts.contains(phone)) {
                    holder.setButtonEnable(false, phone);
                } else {
                    holder.setButtonEnable(true, phone);
                }

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            return convertView;
        }

        class ViewHolder {
            public TextView mContactName;
            public TextView mTextButton;

            public void setButtonEnable(boolean isEnable, final String phone) {
                if (isEnable) {
                    mTextButton.setBackgroundResource(R.drawable.btn_addfriend_from_contact_add);
                    mTextButton.setText(null);

                    mTextButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (sendAddRequest(phone)) {
                                mTextButton.setBackgroundDrawable(null);
                                mTextButton.setText("已申请");
                                mTextButton.setTextColor(getResources()
                                        .getColor(R.color.add_friends_from_contact_added));
                            }
                        }
                    });

                } else {
                    mTextButton.setBackgroundDrawable(null);
                    mTextButton.setText("已添加");
                    mTextButton.setTextColor(getResources()
                            .getColor(R.color.add_friends_from_contact_added));
                }
            }
        }
    }

    private boolean sendAddRequest(final String phone) {
        if (!HttpUtil.isNetAvailable(this)) {
            Toast.makeText(this, "无网络连接，请打开数据网络", Toast.LENGTH_SHORT).show();
            return false;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject data = HttpProcess.friendApplication(mPhone, phone);
                try {
                    if (data.get("status").equals(-1)) {
                        Log.i(TAG, "sendAddRequest:server error");
                        mHandler.sendEmptyMessage(MSG_SERVER_ERROR);

                    } else if (data.get("status").equals(0)) {
                        Log.i(TAG, "sendAddRequest:server error");
                        mHandler.sendEmptyMessage(MSG_SERVER_ERROR);

                    } else if (data.get("status").equals(1)) {
                        if (data.getString("result").equals("true")) {
                            Log.i(TAG, "sendAddRequest:success");
                            mHandler.sendEmptyMessage(MSG_SUCCESS);

                        } else if (data.getString("result").equals("false")) {
                            Log.i(TAG, "sendAddRequest:failure");
                            mHandler.sendEmptyMessage(MSG_FAILURE);
                        }
                    }
                } catch (JSONException e) {
                    Log.w(TAG, e.getMessage());
                }
            }
        }).start();

        return true;
    }


    /**
     * 通过手机号获取手机通讯录中联系人的名字
     *
     * @param phone
     * @return
     */
    private String getContactName(String phone) {
        for (Map<String, String> contact : mPhoneContacts) {
            if (contact.get(CONTACT_MAP_KEY_PHONE).equals(phone)) {
                return contact.get(CONTACT_MAP_KEY_NAME);
            }
        }
        return null;
    }

    /**
     * 获取联系人列表，先获取手机通讯录，
     * 然后再从本地数据库中读取好友列表，
     * 然后再向服务器请求手机通讯录中已经注册过的联系人列表
     */
    private void getContactList() {
        Log.i(TAG, "Starting get contact list from server");
        if (!HttpUtil.isNetAvailable(this)) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取手机通讯录
                try {
                    mPhoneContacts = getContacts(AddFriendsFromContact.this);
                } catch (Exception e) {
                    Log.w(TAG, e.getMessage());
                }

                //使用本地的信息获取已经加了的联系人
                ArrayList<FriendShip> friends = mDBManager.findFriendInfo(mPhone);
                if (friends.size() != 0) {
                    Log.i(TAG, "Read contacts from local database");

                    for (FriendShip f : friends) {
                        mAddedContacts.add(f.getPhoneNumberB());
                    }
                }

                //筛选手机联系人中已经注册过的联系人
                JSONArray phoneNums = new JSONArray();
                for (Map<String, String> contact : mPhoneContacts) {
                    phoneNums.put(contact.get(CONTACT_MAP_KEY_PHONE));
                }
                JSONArray data = HttpProcess.checkMultiplePhoneNum(phoneNums);

                try {
                    JSONObject statusJson = data.getJSONObject(0);

                    if (statusJson.get("status").equals(-1)) {
                        Log.i(TAG, "error");
                    } else if (statusJson.get("status").equals(0)) {
                        Log.i(TAG, "error");
                    } else if (statusJson.get("status").equals(1)) {
                        if (statusJson.getString("result").equals("true"))  //正确返回数据
                        {
                            Log.i(TAG, "none registered");
                        } else if (statusJson.getString("result").equals("false")) {
                            JSONArray phoneNumJson = data.getJSONArray(1);
                            for (int i = 0; i < phoneNumJson.length(); ++i) {
                                if (!mRegisteredContacts.contains(phoneNumJson.getString(i))) {
                                    mRegisteredContacts.add(phoneNumJson.getString(i));
                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    Log.i(TAG, e.getMessage());
                }

                mHandler.sendEmptyMessage(MSG_REFRESH_LIST);
            }

        }).start();

    }

    public static ArrayList<Map<String, String>> getContacts(Context context) throws Exception {
        ArrayList<Map<String, String>> contacts = new ArrayList<>();

        Uri uri = Uri.parse("content://com.android.contacts/contacts");

        //获得一个ContentResolver数据共享的对象
        ContentResolver reslover = context.getContentResolver();
        //取得联系人中开始的游标，通过content://com.android.contacts/contacts这个路径获得
        Cursor cursor = reslover.query(uri, null, null, null, null);

        //上边的所有代码可以由这句话代替：Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        //Uri.parse("content://com.android.contacts/contacts") == ContactsContract.Contacts.CONTENT_URI

        while (cursor.moveToNext()) {
            HashMap<String, String> infoMap = new HashMap<>();


            //获得联系人ID
            String id = cursor.getString(cursor.getColumnIndex(
                    android.provider.ContactsContract.Contacts._ID));

            //获得联系人姓名
            String name = cursor.getString(cursor.getColumnIndex(
                    android.provider.ContactsContract.Contacts.DISPLAY_NAME));

            //获得联系人手机号码
            Cursor phone = reslover.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);

            int phoneIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            phone.moveToNext();
            String phoneStr = phone.getString(phoneIndex).replace("-", "").replaceAll("\\s+", "");

            infoMap.put(CONTACT_MAP_KEY_ID, id);
            infoMap.put(CONTACT_MAP_KEY_PHONE, phoneStr);
            infoMap.put(CONTACT_MAP_KEY_NAME, name);

            phone.close();
            contacts.add(infoMap);
        }
        cursor.close();

        return contacts;
    }


    /**
     * 批量注册联系人，测试时使用
     */
    private void registerTestContacts() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String mPhoneReg = "^(145|147|176)\\d{8}$|^(1700|1705|1709)\\d{7}$" +
                        "|^1[38]\\d{9}$|^15[012356789]\\d{8}$";

                ArrayList<Map<String, String>> contacts = null;
                try {
                    contacts = AddFriendsFromContact.getContacts(AddFriendsFromContact.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int count = 0;
                for (Map<String, String> contact : contacts) {
                    Pattern pattern = Pattern.compile(mPhoneReg);
                    Matcher matcher = pattern.matcher(contact.get(CONTACT_MAP_KEY_PHONE));

                    if (matcher.matches()) {
                        String userName = contact.get(CONTACT_MAP_KEY_ID);
                        String phone = contact.get(CONTACT_MAP_KEY_PHONE);
                        String passwd = phone;
                        HttpProcess.register(phone, userName, passwd);
                        count++;
                        if (count == 40)
                            break;

                        Log.i(TAG, phone);
                    }
                }
            }
        }).start();
    }
}
