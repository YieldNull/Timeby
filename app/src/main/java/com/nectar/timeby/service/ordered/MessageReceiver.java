package com.nectar.timeby.service.ordered;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.nectar.timeby.R;
import com.nectar.timeby.db.ClientDao;
import com.nectar.timeby.gui.MainActivity;
import com.nectar.timeby.gui.MessageActivity;
import com.nectar.timeby.util.PrefsUtil;

/**
 * 接收好友请求，系统消息等，然后启动Notification
 */
public class MessageReceiver extends BroadcastReceiver {

    public static final String INTENT_ACTION =
            "com.nectar.timeby.service.ordered.MessageReceiver.BROADCAST";
    public static final String INTENT_EXTRA_TITLE = "intent_extra_title";
    public static final String INTENT_EXTRA_CONTENT = "intent_extra_content";
    public static final String INTENT_EXTRA_PHONE = "intent_extra_phone";
    public static final String INTENT_EXTRA_REMARK = "intent_extra_remark";

    public static final String INTENT_FLAG = "intent_flag";
    public static final int FLAG_TASK_FAIL = 0x0001;
    public static final int FLAG_FRIENDS_ADD_REQUEST = 0x0002;
    public static final int FLAG_FRIENDS_ADD_SUCCESS = 0x0003;

    private static final int NOTIFY_ME_ID = 0x1234;

    private static final String TAG = "MessageReceiver";


    public MessageReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra(INTENT_EXTRA_TITLE);
        String content = intent.getStringExtra(INTENT_EXTRA_CONTENT);
        int flag = intent.getIntExtra(INTENT_FLAG, -1);

        Log.i(TAG, "Received Message:" + title);
        Log.i(TAG, "Starting Notification");


        //设置Notification
        NotificationManager manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Notification note = new Notification(R.drawable.ic_launcher,
                title, System.currentTimeMillis());
        note.flags = Notification.FLAG_AUTO_CANCEL;

        Intent reIntent = null;

        //根据flag生成各种reIntent
        if (flag == FLAG_TASK_FAIL) {
            //设置各种数据
            ClientDao db = new ClientDao(context);
            db.addMessage(System.currentTimeMillis(), null,
                    "离开APP过长时间，您的任务失败", 0, 0);

            PrefsUtil.setIsTaskFailed(context, true);
            PrefsUtil.setDrawerRefresh(context, true);

            //设置跳转
            reIntent = new Intent(context, MessageActivity.class);
            reIntent.putExtra(INTENT_FLAG, FLAG_TASK_FAIL);

        } else if (flag == FLAG_FRIENDS_ADD_SUCCESS) {
            //获取传递过来的数据
            String phone = intent.getStringExtra(INTENT_EXTRA_PHONE);
            String remark = intent.getStringExtra(INTENT_EXTRA_REMARK);
            String mPhone = PrefsUtil.getUserPhone(context);

            //存入数据库
            ClientDao db = new ClientDao(context);
            db.addFriend(mPhone, phone, remark);

            //设置跳转
            reIntent = new Intent(context, MessageActivity.class);
            reIntent.putExtra(INTENT_EXTRA_PHONE, phone);
            reIntent.putExtra(INTENT_EXTRA_REMARK, remark);
            reIntent.putExtra(INTENT_FLAG, FLAG_FRIENDS_ADD_SUCCESS);

        } else if (flag == FLAG_FRIENDS_ADD_REQUEST) {
            //获取传递过来的数据
            String phone = intent.getStringExtra(INTENT_EXTRA_PHONE);
            String remark = intent.getStringExtra(INTENT_EXTRA_REMARK);

            //设置跳转
            reIntent = new Intent(context, MessageActivity.class);
            reIntent.putExtra(INTENT_EXTRA_PHONE, phone);
            reIntent.putExtra(INTENT_EXTRA_REMARK, remark);
            reIntent.putExtra(INTENT_FLAG, FLAG_FRIENDS_ADD_REQUEST);
        }

        //发送Notification
        if (reIntent != null) {
            PendingIntent i = PendingIntent.getActivity(context, 0, reIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            note.setLatestEventInfo(context, title, content, i);
            manager.notify(NOTIFY_ME_ID, note);
        }
    }
}
