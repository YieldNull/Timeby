package com.nectar.timeby.service.ordered;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.nectar.timeby.R;
import com.nectar.timeby.gui.MainActivity;
import com.nectar.timeby.util.PrefsUtil;

/**
 * 接收好友请求，系统消息等，然后启动Notification
 */
public class MessageReceiver extends BroadcastReceiver {

    public static final String INTENT_ACTION =
            "com.nectar.timeby.service.ordered.MessageReceiver.BROADCAST";
    public static final String INTENT_EXTRA_TITLE = "intent_message_title";
    public static final String INTENT_EXTRA_CONTENT = "intent_message_contont";

    public static final String INTENT_FLAG = "intent_flag";
    public static final int FLAG_TASK_FAIL = 0x0001;

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


        NotificationManager manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Notification note = new Notification(R.drawable.ic_launcher,
                title, System.currentTimeMillis());

        Intent reIntent = null;

        if (flag == FLAG_TASK_FAIL) {
            //设置任务失败，设置跳转界面为主界面
            reIntent = new Intent(context, MainActivity.class);
            PrefsUtil.setIsTaskFailed(context, true);
        }

        if (reIntent != null) {
            //发送Notification
            PendingIntent i = PendingIntent.getActivity(context, 0, reIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            note.setLatestEventInfo(context, title, content, i);
            manager.notify(NOTIFY_ME_ID, note);
        }
    }
}
