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
import com.nectar.timeby.gui.ConcludeActivity;
import com.nectar.timeby.gui.MessageActivity;
import com.nectar.timeby.gui.fragment.MainFragment;
import com.nectar.timeby.util.HttpProcess;
import com.nectar.timeby.util.PrefsUtil;
import com.nectar.timeby.util.TimeUtil;

import org.json.JSONException;
import org.json.JSONObject;


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
    public static final String INTENT_EXTRA_TASK_FAIL_TIME = "intent_fail_time";
    public static final String INTENT_EXTRA_TASK_TYPE = "intent_task_type";
    public static final String INTENT_EXTRA_MESG_TYPE = "intent_msg_type";

    public static final int FLAG_FRIENDS_ADD_REQUEST = 0x0001;
    public static final int FLAG_FRIENDS_ADD_SUCCESS = 0x0002;
    public static final int FLAG_TASK_FAIL = 0x0003;
    public static final int FLAG_TASK_SUCCESS = 0x0004;
    public static final int FLAG_TASK_REQUEST = 0x0005;
    public static final int FLAG_TASK_ACCEPT = 0x0006;
    public static final int FLAG_TASK_COOP_FAIL = 0x0007;
    public static final int FLAG_TASK_PK_FAIL = 0x0008;

    private static final int NOTIFY_ME_ID = 0x1234;

    private static final String TAG = "MessageReceiver";


    public MessageReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

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
                    null, Message.MSG_TYPE_SYSTEM, Message.MSG_DISPOSED_NONE);

            PrefsUtil.setIsTaskFailed(context, true);

            int type = PrefsUtil.getTaskType(context);

            //设置跳转

            reIntent = new Intent(context, MessageActivity.class);

            reIntent.putExtra(INTENT_EXTRA_MESG_TYPE, Message.MSG_TYPE_SYSTEM);
            reIntent.putExtra(INTENT_EXTRA_TASK_TYPE, MainFragment.TASK_TYPE_SOLO);
            reIntent.putExtra(INTENT_FLAG, FLAG_TASK_FAIL);

            if (type == MainFragment.TASK_TYPE_COOPER
                    || type == MainFragment.TASK_TYPE_PK) {

                //获取任务信息
                final String phone = PrefsUtil.getUserPhone(context);
                final String requestFrom = PrefsUtil.getTaskRequestFrom(context);
                final String startTime = TimeUtil.getTimeStr(PrefsUtil.getTaskStartTime(context));
                final String endTime = TimeUtil.getTimeStr(System.currentTimeMillis());
                final int theType = MainFragment.TASK_TYPE_COOPER == type ? 1 : 2;

                Log.i(TAG, "Sending failure message");
                //发送失败消息
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = HttpProcess.taskFail(
                                phone, requestFrom, startTime, endTime, theType);
                        try {
                            if (data.get("status").equals(-1)) {
                                Log.i(TAG, "Server error");
                            } else if (data.get("status").equals(0)) {
                                Log.i(TAG, "Server error");
                            } else if (data.get("status").equals(1)) {
                                if (data.getString("result").equals("true")) {
                                    Log.i(TAG, "Sending failure message Success");
                                } else if (data.getString("result").equals("false")) {
                                    Log.i(TAG, "Sending failure message fail");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

        } else if (flag == FLAG_TASK_SUCCESS) {//离开App时任务成功
            //设置各种数据
            db.addMessage(System.currentTimeMillis(), null, "恭喜您，成功完任务！",
                    null, Message.MSG_TYPE_SYSTEM, Message.MSG_DISPOSED_NONE);

            //设置跳转
            reIntent = new Intent(context, ConcludeActivity.class);
            reIntent.putExtra(INTENT_FLAG, FLAG_TASK_SUCCESS);

        } else if (flag == FLAG_FRIENDS_ADD_SUCCESS) {//添加好友成功
            //获取传递过来的数据
            String phone = intent.getStringExtra(INTENT_EXTRA_PHONE);
            String remark = intent.getStringExtra(INTENT_EXTRA_REMARK);
            String mPhone = PrefsUtil.getUserPhone(context);

            //存入数据库
            db.addFriend(mPhone, phone, remark);
            db.addMessage(System.currentTimeMillis(), null, content,
                    phone, Message.MSG_TYPE_SYSTEM, Message.MSG_DISPOSED_NONE);

            //设置跳转
            reIntent = new Intent(context, MessageActivity.class);

            reIntent.putExtra(INTENT_EXTRA_MESG_TYPE, Message.MSG_TYPE_SYSTEM);
            reIntent.putExtra(INTENT_FLAG, FLAG_FRIENDS_ADD_SUCCESS);

        } else if (flag == FLAG_FRIENDS_ADD_REQUEST) {//处理好友请求
            //获取传递过来的数据
            String phone = intent.getStringExtra(INTENT_EXTRA_PHONE);
            String remark = intent.getStringExtra(INTENT_EXTRA_REMARK);

            //将消息存入数据库
            db.addMessage(System.currentTimeMillis(), remark, remark + "申请添加您为好友",
                    phone, Message.MSG_TYPE_USER_FRIENDS, Message.MSG_DISPOSED_NOT);

            //设置跳转
            reIntent = new Intent(context, MessageActivity.class);

            reIntent.putExtra(INTENT_EXTRA_MESG_TYPE, Message.MSG_TYPE_USER_FRIENDS);
            reIntent.putExtra(INTENT_FLAG, FLAG_FRIENDS_ADD_REQUEST);

        } else if (flag == FLAG_TASK_REQUEST) {
            //获取数据
            String phone = intent.getStringExtra(INTENT_EXTRA_PHONE);
            String remark = intent.getStringExtra(INTENT_EXTRA_REMARK);
            String startTime = intent.getStringExtra(INTENT_EXTRA_TASK_START_TIME);
            String endTime = intent.getStringExtra(INTENT_EXTRA_TASK_END_TIME);
            int type = intent.getIntExtra(INTENT_EXTRA_TASK_TYPE, -1);

            //时间转long
            long startLong = TimeUtil.getTime(startTime);
            long endLong = TimeUtil.getTime(endTime);

            //存消息
            String msgContent = content + "\n启动时间:" + startTime + "  结束时间:" + endTime;
            db.addMessage(System.currentTimeMillis(), remark, msgContent,
                    phone, Message.MSG_TYPE_USER_TASK, Message.MSG_DISPOSED_NOT);

            //存任务
            PrefsUtil.initTask(context, startLong, endLong, type);
            PrefsUtil.setTaskRequestFrom(context, phone);
            PrefsUtil.setFriendsAccept(context, true);

            //设置跳转
            reIntent = new Intent(context, MessageActivity.class);

            reIntent.putExtra(INTENT_EXTRA_TASK_TYPE, type);
            reIntent.putExtra(INTENT_EXTRA_MESG_TYPE, Message.MSG_TYPE_USER_TASK);
            reIntent.putExtra(INTENT_FLAG, FLAG_TASK_REQUEST);

        } else if (flag == FLAG_TASK_ACCEPT) {

            //任务还没开始则设置已有好友同意
            long start = PrefsUtil.getTaskStartTime(context);
            if (start > System.currentTimeMillis()) {
                PrefsUtil.setFriendsAccept(context, true);
            }

        } else if (flag == FLAG_TASK_COOP_FAIL) {
            //获取数据
            final String phone = intent.getStringExtra(INTENT_EXTRA_PHONE);
            String remark = intent.getStringExtra(INTENT_EXTRA_REMARK);
            String startTime = intent.getStringExtra(INTENT_EXTRA_TASK_START_TIME);
            String failTime = intent.getStringExtra(INTENT_EXTRA_TASK_FAIL_TIME);

            long startLong = TimeUtil.getTime(startTime);
            long failLong = TimeUtil.getTime(failTime);

            String msgContent = remark + "在任务中失败" + "  失败时间" + failTime;
            db.addMessage(System.currentTimeMillis(), remark, msgContent,
                    phone, Message.MSG_TYPE_USER_TASK, Message.MSG_DISPOSED_NONE);

            //发送数据
            reIntent = new Intent(context, MessageActivity.class);
            reIntent.putExtra(INTENT_EXTRA_TASK_TYPE, MainFragment.TASK_TYPE_COOPER);
            reIntent.putExtra(INTENT_EXTRA_MESG_TYPE, Message.MSG_TYPE_USER_TASK);
            reIntent.putExtra(INTENT_FLAG, FLAG_TASK_COOP_FAIL);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpProcess.giveHarmmer(PrefsUtil.getUserPhone(context), phone);
                }
            }).start();

        } else if (flag == FLAG_TASK_PK_FAIL) {
            //获取数据
            String phone = intent.getStringExtra(INTENT_EXTRA_PHONE);
            String remark = intent.getStringExtra(INTENT_EXTRA_REMARK);
            String startTime = intent.getStringExtra(INTENT_EXTRA_TASK_START_TIME);
            String failTime = intent.getStringExtra(INTENT_EXTRA_TASK_FAIL_TIME);

            long startLong = TimeUtil.getTime(startTime);
            long failLong = TimeUtil.getTime(failTime);

            String msgContent = remark + "在PK中失败，您取得了胜利" + "  失败时间" + failTime;
            db.addMessage(System.currentTimeMillis(), remark, msgContent,
                    phone, Message.MSG_TYPE_SYSTEM, Message.MSG_DISPOSED_NONE);


            //发送数据
            reIntent = new Intent(context, MessageActivity.class);
            reIntent.putExtra(INTENT_EXTRA_TASK_TYPE, MainFragment.TASK_TYPE_PK);
            reIntent.putExtra(INTENT_EXTRA_MESG_TYPE, Message.MSG_TYPE_USER_TASK);
            reIntent.putExtra(INTENT_FLAG, FLAG_TASK_PK_FAIL);
        }

        //发送Notification
        if (reIntent != null) {
            reIntent.putExtra(INTENT_EXTRA_CONTENT, content);
            PendingIntent i = PendingIntent.getActivity(context, 0, reIntent, 0);
            note.setLatestEventInfo(context, title, content, i);
            manager.notify(NOTIFY_ME_ID, note);
        }
    }
}
