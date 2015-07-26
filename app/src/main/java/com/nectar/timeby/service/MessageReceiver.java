package com.nectar.timeby.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.nectar.timeby.R;
import com.nectar.timeby.db.ClientDao;
import com.nectar.timeby.db.Message;
import com.nectar.timeby.gui.MainConcludeActivity;
import com.nectar.timeby.gui.MessageActivity;
import com.nectar.timeby.util.PrefsUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 接收好友请求，系统消息等，然后启动Notification
 */
public class MessageReceiver extends BroadcastReceiver {

    public static final String INTENT_ACTION =
            "com.nectar.timeby.service.MessageReceiver.BROADCAST";

    public static final String INTENT_FLAG = "intent_flag";
    public static final String INTENT_EXTRA_TITLE = "intent_extra_title";
    public static final String INTENT_EXTRA_CONTENT = "intent_extra_content";
    public static final String INTENT_EXTRA_PHONE = "intent_extra_phone";
    public static final String INTENT_EXTRA_REMARK = "intent_extra_remark";
    public static final String INTENT_EXTRA_TASK_START_TIME = "intent_start_time";
    public static final String INTENT_EXTRA_TASK_END_TIME = "intent_end_time";
    public static final String INTENT_EXTRA_TASK_TYPE = "intent_task_type";

    public static final int FLAG_TASK_FAIL = 0x0001;
    public static final int FLAG_TASK_SUCCESS = 0x0004;
    public static final int FLAG_FRIENDS_ADD_REQUEST = 0x0002;
    public static final int FLAG_FRIENDS_ADD_SUCCESS = 0x0003;
    public static final int FLAG_TASK_REQUEST = 0x0004;

    private static final int NOTIFY_ME_ID = 0x1234;

    private static final String TAG = "MessageReceiver";


    public MessageReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //获取Notification标题与内容
        String title = intent.getStringExtra(INTENT_EXTRA_TITLE);
        String content = intent.getStringExtra(INTENT_EXTRA_CONTENT);

        //获取消息类型
        int flag = intent.getIntExtra(INTENT_FLAG, -1);

        Log.i(TAG, "Received Message:" + title);
        Log.i(TAG, "Starting Notification");

        //设置Notification
        NotificationManager manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification note = new Notification(R.drawable.ic_launcher,
                title, System.currentTimeMillis());
        note.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;


        //抽屉消息图标更改
        PrefsUtil.setDrawerRefresh(context, true);

        //获取数据库连接
        ClientDao db = new ClientDao(context);

        Intent reIntent = null;

        //根据flag生成各种reIntent
        if (flag == FLAG_TASK_FAIL) {//离开App时任务失败
            //设置各种数据
            db.addMessage(System.currentTimeMillis(), null, "离开APP过长时间，您的任务失败",
                    null, Message.MSG_TYPE_SYSTEM, Message.MSG_DISPOSED_NOT);

            PrefsUtil.setIsTaskFailed(context, true);

            //设置跳转
            reIntent = new Intent(context, MessageActivity.class);
            reIntent.putExtra(INTENT_FLAG, FLAG_TASK_FAIL);

        } else if (flag == FLAG_TASK_SUCCESS) {//离开App时任务成功
            //设置各种数据
            db.addMessage(System.currentTimeMillis(), null, "恭喜您，成功完任务！",
                    null, Message.MSG_TYPE_SYSTEM, Message.MSG_DISPOSED_NOT);

            //设置跳转
            reIntent = new Intent(context, MainConcludeActivity.class);
            reIntent.putExtra(INTENT_FLAG, FLAG_TASK_FAIL);

        } else if (flag == FLAG_FRIENDS_ADD_SUCCESS) {//添加好友成功
            //获取传递过来的数据
            String phone = intent.getStringExtra(INTENT_EXTRA_PHONE);
            String remark = intent.getStringExtra(INTENT_EXTRA_REMARK);
            String mPhone = PrefsUtil.getUserPhone(context);

            //存入数据库
            db.addFriend(mPhone, phone, remark);
            db.addMessage(System.currentTimeMillis(), null, content,
                    phone, Message.MSG_TYPE_SYSTEM, Message.MSG_DISPOSED_NOT);

            //设置跳转
            reIntent = new Intent(context, MessageActivity.class);
            reIntent.putExtra(INTENT_EXTRA_PHONE, phone);
            reIntent.putExtra(INTENT_EXTRA_REMARK, remark);
            reIntent.putExtra(INTENT_FLAG, FLAG_FRIENDS_ADD_SUCCESS);

        } else if (flag == FLAG_FRIENDS_ADD_REQUEST) {//处理好友请求
            //获取传递过来的数据
            String phone = intent.getStringExtra(INTENT_EXTRA_PHONE);
            String remark = intent.getStringExtra(INTENT_EXTRA_REMARK);

            //将消息存入数据库
            db.addMessage(System.currentTimeMillis(), remark, remark + "申请添加您为好友",
                    phone, Message.MSG_TYPE_USER, Message.MSG_DISPOSED_NOT);

            //设置跳转
            reIntent = new Intent(context, MessageActivity.class);
            reIntent.putExtra(INTENT_EXTRA_PHONE, phone);
            reIntent.putExtra(INTENT_EXTRA_REMARK, remark);
            reIntent.putExtra(INTENT_FLAG, FLAG_FRIENDS_ADD_REQUEST);
        } else if (flag == FLAG_TASK_REQUEST) {
            //获取数据
            String phone = intent.getStringExtra(INTENT_EXTRA_PHONE);
            String remark = intent.getStringExtra(INTENT_EXTRA_REMARK);
            String startTime = intent.getStringExtra(INTENT_EXTRA_TASK_START_TIME);
            String endTime = intent.getStringExtra(INTENT_EXTRA_TASK_END_TIME);
            int type = intent.getIntExtra(INTENT_EXTRA_TASK_TYPE, -1);

            //时间转long
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long startLong = 0, endLong = 0;
            try {
                startLong = format.parse(startTime).getTime();
                endLong = format.parse(endTime).getTime();
            } catch (ParseException e) {
                Log.w(TAG, e.getMessage());
            }

            //存消息
            String msgContent = content + "\n启动时间:" + startTime + "  结束时间:" + endTime;
            db.addMessage(System.currentTimeMillis(), remark, msgContent,
                    phone, Message.MSG_TYPE_USER, Message.MSG_DISPOSED_NOT);

            //设置跳转
            reIntent = new Intent(context, MessageActivity.class);
            reIntent.putExtra(INTENT_EXTRA_PHONE, phone);
            reIntent.putExtra(INTENT_EXTRA_REMARK, remark);
            reIntent.putExtra(INTENT_EXTRA_TASK_START_TIME, startLong);
            reIntent.putExtra(INTENT_EXTRA_TASK_END_TIME, endLong);
            reIntent.putExtra(INTENT_EXTRA_TASK_TYPE, type);
            reIntent.putExtra(INTENT_FLAG, FLAG_TASK_REQUEST);
        }

        //发送Notification
        if (reIntent != null) {

            PendingIntent i = PendingIntent.getActivity(context, 0, reIntent, 0);
            note.setLatestEventInfo(context, title, content, i);
            manager.notify(NOTIFY_ME_ID, note);
        }
    }
}
