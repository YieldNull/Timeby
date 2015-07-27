package com.nectar.timeby.service.wakeful;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.nectar.timeby.gui.fragment.MainFragment;
import com.nectar.timeby.service.MessageReceiver;
import com.nectar.timeby.util.HttpProcess;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;


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

    /**
     * 分析从服务器获取到的数据
     *
     * @param data
     */
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

                            handleAddFriendSuccess(phoneNum, name);
                        }
                        //申请任务消息(type 1:合作模式 2:竞争模式）
                        else if (messageJson.getString("messageType").equals("3")) {
                            String phoneNum = messageJson.getString("phonenumA");
                            String name = messageJson.getString("remark");
                            String startTime = messageJson.getString("starttime");
                            String endTime = messageJson.getString("endtime");
                            String typeStr = messageJson.getString("type");

                            handleTaskRequest(phoneNum, name, startTime, endTime, typeStr);
                        }
                        //申请任务消息成功反馈
                        else if (messageJson.getString("messageType").equals("4")) {

                            handleFriendsAgree();
                        }
                        //合作模式某用户失败反馈消息
                        else if (messageJson.getString("messageType").equals("5")) {
                            String phoneNum = messageJson.getString("phonenumA");
                            String name = messageJson.getString("name");
                            String startTime = messageJson.getString("starttime");
                            String failedTime = messageJson.getString("failtime");

                            handleCoopFailure(phoneNum, name, startTime, failedTime);
                        }
                        //竞争模式某用户失败反馈消息
                        else if (messageJson.getString("messageType").equals("6")) {
                            String phoneNum = messageJson.getString("phonenumA");
                            String name = messageJson.getString("name");
                            String startTime = messageJson.getString("starttime");
                            String failedTime = messageJson.getString("failtime");
                            handlePKFailure(phoneNum, name, startTime, failedTime);
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


    /**
     * 处理好友申请
     *
     * @param phoneNum
     * @param name
     */
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
        sendOrderedBroadcast(intent, null);
    }


    /**
     * 处理添加好友成功消息
     *
     * @param phoneNum
     * @param name
     */
    private void handleAddFriendSuccess(String phoneNum, String name) {
        Log.i(TAG, "Add friend success");
        Log.i(TAG, "user:" + name + " phone:" + phoneNum);

        Intent intent = new Intent();
        intent.setAction(MessageReceiver.INTENT_ACTION);
        intent.putExtra(MessageReceiver.INTENT_EXTRA_TITLE, "添加好友");
        intent.putExtra(MessageReceiver.INTENT_EXTRA_CONTENT, "添加 " + name + " 为好友成功");
        intent.putExtra(MessageReceiver.INTENT_EXTRA_PHONE, phoneNum);
        intent.putExtra(MessageReceiver.INTENT_EXTRA_REMARK, name);

        intent.putExtra(MessageReceiver.INTENT_FLAG, MessageReceiver.FLAG_FRIENDS_ADD_SUCCESS);
        sendOrderedBroadcast(intent, null);
    }


    /**
     * 任务申请已有好友同意
     */
    private void handleFriendsAgree() {
        Log.i(TAG, "Friends agree to do task with you");

        Intent intent = new Intent();
        intent.setAction(MessageReceiver.INTENT_ACTION);
        intent.putExtra(MessageReceiver.INTENT_FLAG, MessageReceiver.FLAG_TASK_ACCEPT);
        sendOrderedBroadcast(intent, null);
    }


    /**
     * 处理任务合作申请
     *
     * @param phoneNum
     * @param name
     * @param startTime
     * @param endTime
     * @param typeStr
     */
    private void handleTaskRequest(String phoneNum, String name,
                                   String startTime, String endTime, String typeStr) {

        Log.i(TAG, "Receive task request");
        Log.i(TAG, "user:" + name + " phone:" + phoneNum);
        Log.i(TAG, "Start time:" + startTime);
        Log.i(TAG, "End time:" + endTime);

        //类型转int
        int type = 0;
        String typeMsg = "";
        if (typeStr.equals("1")) {
            type = MainFragment.TASK_TYPE_COOPER;
            typeMsg = "合作";
            Log.i(TAG, "Type:Cooperation");
        } else {
            type = MainFragment.TASK_TYPE_PK;
            typeMsg = "PK";
            Log.i(TAG, "Type:PK");
        }

        Intent intent = new Intent();
        intent.setAction(MessageReceiver.INTENT_ACTION);
        intent.putExtra(MessageReceiver.INTENT_EXTRA_TITLE, "任务申请");
        intent.putExtra(MessageReceiver.INTENT_EXTRA_CONTENT, name + "申请与您" + typeMsg + "完成任务");
        intent.putExtra(MessageReceiver.INTENT_EXTRA_PHONE, phoneNum);
        intent.putExtra(MessageReceiver.INTENT_EXTRA_REMARK, name);
        intent.putExtra(MessageReceiver.INTENT_EXTRA_TASK_START_TIME, startTime);
        intent.putExtra(MessageReceiver.INTENT_EXTRA_TASK_END_TIME, endTime);
        intent.putExtra(MessageReceiver.INTENT_EXTRA_TASK_TYPE, type);

        intent.putExtra(MessageReceiver.INTENT_FLAG, MessageReceiver.FLAG_TASK_REQUEST);
        sendOrderedBroadcast(intent, null);
    }


    /**
     * PK过程中有好友失败
     *
     * @param phoneNum
     * @param name
     * @param startTime
     * @param failedTime
     */
    private void handlePKFailure(String phoneNum, String name, String startTime, String failedTime) {
        Intent intent = new Intent();
        intent.setAction(MessageReceiver.INTENT_ACTION);

        intent.putExtra(MessageReceiver.INTENT_EXTRA_TITLE, "好友失败");
        intent.putExtra(MessageReceiver.INTENT_EXTRA_CONTENT, name + "在任务中失败，您取得了胜利");
        intent.putExtra(MessageReceiver.INTENT_EXTRA_PHONE, phoneNum);
        intent.putExtra(MessageReceiver.INTENT_EXTRA_REMARK, name);
        intent.putExtra(MessageReceiver.INTENT_EXTRA_TASK_START_TIME, startTime);
        intent.putExtra(MessageReceiver.INTENT_EXTRA_TASK_FAIL_TIME, failedTime);

        intent.putExtra(MessageReceiver.INTENT_FLAG, MessageReceiver.FLAG_TASK_PK_FAIL);
        sendOrderedBroadcast(intent, null);
    }

    /**
     * 合作过程中有好友失败
     *
     * @param phoneNum
     * @param name
     * @param startTime
     * @param failedTime
     */
    private void handleCoopFailure(String phoneNum, String name, String startTime, String failedTime) {
        Intent intent = new Intent();
        intent.setAction(MessageReceiver.INTENT_ACTION);

        intent.putExtra(MessageReceiver.INTENT_EXTRA_TITLE, "好友失败");
        intent.putExtra(MessageReceiver.INTENT_EXTRA_CONTENT, name + "在任务中失败");
        intent.putExtra(MessageReceiver.INTENT_EXTRA_PHONE, phoneNum);
        intent.putExtra(MessageReceiver.INTENT_EXTRA_REMARK, name);
        intent.putExtra(MessageReceiver.INTENT_EXTRA_TASK_START_TIME, startTime);
        intent.putExtra(MessageReceiver.INTENT_EXTRA_TASK_FAIL_TIME, failedTime);

        intent.putExtra(MessageReceiver.INTENT_FLAG, MessageReceiver.FLAG_TASK_COOP_FAIL);
        sendOrderedBroadcast(intent, null);
    }

}
