package com.nectar.timeby.service.wakeful;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.nectar.timeby.service.ordered.MessageReceiver;
import com.nectar.timeby.util.HttpProcess;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 开机获取Wake锁，轮询，用于与接收好友的请求等工作，开机自起
 * 定时向服务器请求数据。
 */
public class PollingService extends WakefulIntentService {

    private static final String TAG = "PollingService";
    public static final String INTENT_PHONE_NUM = "phone_num";


    public PollingService() {
        super(TAG);

        Log.i(TAG, "Polling service create");
    }

    @Override
    protected void doWakefulWork(Intent intent) {
        final String phone = intent.getStringExtra(INTENT_PHONE_NUM);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Polling tick");
                JSONArray json = HttpProcess.messageInform(phone);
                analyseData(json);
            }
        }).start();
    }

    private void analyseData(JSONArray data) {
        try {
            JSONObject statusJson = data.getJSONObject(0);
            if (statusJson.get("status").equals(-1)) {
                Log.w(TAG, "error:" + statusJson.getString("errorStr"));

            } else if (statusJson.get("status").equals(0)) {
                Log.w(TAG, "Server error");

            } else if (statusJson.get("status").equals(1)) {
                if (statusJson.getString("result").equals("true"))  //正确返回数据
                {
                    //遍历服务器发来的消息
                    for (int i = 1; i < data.length(); ++i) {
                        JSONObject messageJson = data.getJSONObject(i);
                        //申请好友消息(可能有多个）
                        if (messageJson.getString("messageType").equals("1")) {
                            String phoneNum = messageJson.getString("phonenumA");
                            String name = messageJson.getString("name");

                            handleAddFriendRequest(phoneNum, name);
                        }
                        //申请好友成功反馈
                        else if (messageJson.getString("messageType").equals("2")) {
                            String phoneNum = messageJson.getString("phonenumB");
                            String name = messageJson.getString("name");

                            handAddFriendSuccess(phoneNum, name);
                        }
                        //申请任务消息(type 1:合作模式 2:竞争模式）
                        else if (messageJson.getString("messageType").equals("3")) {
//                            str = "申请者：" + messageJson.getString("phonenumA")
//                                    + ", 申请者备注：" + messageJson.getString("remark")
//                                    + ", 用户：" + messageJson.getString("phonenumB")
//                                    + ", 开始时间：" + messageJson.getString("starttime")
//                                    + ", 结束时间：" + messageJson.getString("endtime")
//                                    + ", 类型：" + messageJson.getString("type");
                        }
                        //申请任务消息成功反馈
                        else if (messageJson.getString("messageType").equals("4")) {
//                            str = "已有好友加入  申请者（该用户）：" + messageJson.getString("phonenumA")
//                                    + ", 开始时间：" + messageJson.getString("starttime");
                        }
                        //合作模式某用户失败反馈消息
                        else if (messageJson.getString("messageType").equals("5")) {
//                            str = "失败用户：" + messageJson.getString("phonenumA")
//                                    + ", 备注或昵称：" + messageJson.getString("name")
//                                    + ", 开始时间：" + messageJson.getString("starttime")
//                                    + ", 失败时间：" + messageJson.getString("failtime");
                        }
                        //合作模式某用户失败反馈消息
                        else if (messageJson.getString("messageType").equals("6")) {
//                            str = "失败用户：" + messageJson.getString("phonenumA")
//                                    + ", 备注或昵称：" + messageJson.getString("name")
//                                    + ", 开始时间：" + messageJson.getString("starttime")
//                                    + ", 失败时间：" + messageJson.getString("failtime");
                        }
                    }
                } else if (statusJson.getString("result").equals("false")) {
                    Log.i(TAG, "Not message present");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handAddFriendSuccess(String phoneNum, String name) {
        Log.i(TAG, "Add friend success");
        Log.i(TAG, "user:" + name + " phone:" + phoneNum);

        Intent intent = new Intent();
        intent.setAction(MessageReceiver.INTENT_ACTION);
        intent.putExtra(MessageReceiver.INTENT_EXTRA_TITLE, "添加好友");
        intent.putExtra(MessageReceiver.INTENT_EXTRA_CONTENT, "添加 " + name + " 为好友成功");
        intent.putExtra(MessageReceiver.INTENT_EXTRA_PHONE, phoneNum);
        intent.putExtra(MessageReceiver.INTENT_EXTRA_REMARK, name);

        intent.putExtra(MessageReceiver.INTENT_FLAG, MessageReceiver.FLAG_FRIENDS_ADD_SUCCESS);
        sendBroadcast(intent);

    }

    private void handleAddFriendRequest(String phoneNum, String name) {
        Log.i(TAG, "Received add friend request");
        Log.i(TAG, "user:" + name + " phone:" + phoneNum);

        Intent intent = new Intent();
        intent.setAction(MessageReceiver.INTENT_ACTION);
        intent.putExtra(MessageReceiver.INTENT_EXTRA_TITLE, "好友申请");
        intent.putExtra(MessageReceiver.INTENT_EXTRA_CONTENT, name + "申请添加您为好友");
        intent.putExtra(MessageReceiver.INTENT_EXTRA_PHONE, phoneNum);
        intent.putExtra(MessageReceiver.INTENT_EXTRA_REMARK, name);

        intent.putExtra(MessageReceiver.INTENT_FLAG, MessageReceiver.FLAG_FRIENDS_ADD_REQUEST);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
