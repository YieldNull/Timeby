package com.nectar.timeby.gui;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nectar.timeby.R;

import java.util.ArrayList;

public class AddFriendsFromContact extends AppCompatActivity {
    private Context mContext = null;
    //从手机的数据库中所要获取的字段
    private static final String[] PHONES_PROJECTION = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
    //联系人显示名称
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    /**
     * 电话号码*
     */
    private static final int PHONES_NUMBER_INDEX = 1;
//    //头像ID
//    private static final int PHONES_PHOTO_ID_INDEX = 2;
//    //联系人的ID
//    private static final int PHONES_CONTACT_ID_INDEX = 3;


    //联系人名称组
    private ArrayList<String> mContactsName = new ArrayList<String>();
    //联系人号码组
    private ArrayList<String> mContactsNumber = new ArrayList<String>();
    //联系人头像组
    //private ArrayList<Bitmap> mContactsPhonto = new ArrayList<Bitmap>();

    private ListView mListView = null;
    private MyListAdapter myListAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_from_contact);

        mContext = this;
        mListView = (ListView) findViewById(R.id.listView_add_friends_from_contact);
    }

    //对联系人表项进行操作的回调函数
    private class MyItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
            调用系统方法拨打电话
            Intent dialIntent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+mContactsNumber.get(position)));
            startActivity(dialIntent);
             */
            Toast.makeText(AddFriendsFromContact.this, "user:" + mContactsName.get(position), Toast.LENGTH_SHORT).show();

        }
    }

    //获取手机通讯录联系人信息
    private void getPhoneContactor() {
        ContentResolver resolver = mContext.getContentResolver();
        //获取手机联系人
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                mContactsName.add(contactName);
                mContactsNumber.add(phoneNumber);
            }

            phoneCursor.close();
        }
    }

    //获取手机SIM卡联系人信息
    private void getSIMContactor() {
        ContentResolver resolver = mContext.getContentResolver();

        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null, null);

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {

                // 得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                // 得到联系人名称
                String contactName = phoneCursor
                        .getString(PHONES_DISPLAY_NAME_INDEX);

                //Sim卡中是没有联系人头像的

                mContactsName.add(contactName);
                mContactsNumber.add(phoneNumber);
            }

            phoneCursor.close();
        }
    }

    class MyListAdapter extends BaseAdapter {
        public MyListAdapter(Context context) {
            mContext = context;
        }

        public int getCount() {
            //设置列表项的数量
            return mContactsName.size();
        }


        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_addfriendbymaillist, null);
                holder.ContactorName = (TextView) convertView.findViewById(R.id.addfriendbymaillist_friendname);
                //holder.ContactorNumber = (TextView) convertView.findViewById(R.id.ContactorNumber);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.ContactorName = (TextView) convertView.findViewById(R.id.addfriendbymaillist_friendname);
                //holder.ContactorNumber = (TextView) convertView.findViewById(R.id.ContactorNumber);
            }
            //绘制联系人名称
            holder.ContactorName.setText(mContactsName.get(position));
            //绘制联系人号码
            // holder.ContactorNumber.setText(mContactsNumber.get(position));
            //绘制联系人头像
            return convertView;
        }

        class ViewHolder {
            public TextView ContactorName;
            public TextView ContactorNumber;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
