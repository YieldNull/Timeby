package com.nectar.timeby.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class DemoActivity extends ActionBarActivity {

    private TextView myTextView;
    private Map<String, Object> map = new HashMap<String, Object>();
    private static int IS_FINISH = 1;
    private String str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        myTextView = (TextView) findViewById(R.id.myTextView);

//        Map<String, Object> map = new HashMap<>();
//        map.put("aaa", "aaaa");
//        map.put("bbb", "bbbb");
//        JSONObject json = new JSONObject();
//        try {
//            json.put("ccc", "cccc");
//            json.put("ddd", "dddd");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JSONArray jsonArray = new JSONArray();
//        jsonArray.put(map);
//        jsonArray.put(json);
//        Log.v("xxxxxxxxxxxxx", jsonArray.toString());
//        String jsoStr = jsonArray.toString();
//        try {
//            Log.v("aaaaaaaaaaaaaaa", jsoStr);
//            JSONArray resultJson = new JSONArray(jsoStr);
//            Log.v("bbbbbbbbbbbbbbb", resultJson.toString());
//
//            JSONObject jsonObject = resultJson.getJSONObject(1);
//            Log.v("ccccccccccccccc", "ccccccccccccc");
//            myTextView.setText((String)jsonObject.get("ccc"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


        new Thread(new MyThread()).start();
    }


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == IS_FINISH) {
                //status == 1 ->正确返回结果  result == true->有结果  result == false->无结果
                //status == 0 ->服务器错误（数据库异常）
                //status == 1 ->网络异常
                //status为int  其余类型均为String 视情况转换
                //无附带数据返回类型为JSONObject, 附带数据返回类型为JSONArray


                JSONObject data = (JSONObject) msg.obj;
                //JSONArray data = (JSONArray) msg.obj;

                /*******************************************
                 * 更新密码结果
                 */
//                try {
//                    if (data.get("status").equals(-1)) {
//                        str = data.getString("errorStr");
//                    } else if (data.get("status").equals(0)) {
//                        str = "服务器错误";
//                    } else if (data.get("status").equals(1)) {
//                        if (data.getString("result").equals("true"))
//                        {
//                            str = "密码更新成功";
//                        } else if (data.getString("result").equals("false")) {
//                            str = "密码更新失败";
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                /********************************************
                 * 获取用户基本信息结果
                 */
//                try {
//                    JSONObject statusJson = data.getJSONObject(0);
//                    if (statusJson.get("status").equals(-1)) {
//                        str = statusJson.getString("errorStr");
//                    } else if (statusJson.get("status").equals(0)) {
//                        str = "服务器错误";
//                    } else if (statusJson.get("status").equals(1)) {
//                        if (statusJson.getString("result").equals("true"))  //正确返回数据
//                        {
//                            JSONObject dataJson = data.getJSONObject(1);
//                            //注意大小写
//                            str = "手机号：" + dataJson.getString("phonenum") + ", 昵称：" + dataJson.getString("nickname")
//                                + ", 生日：" + dataJson.getString("birthday") + ", 性别：" + dataJson.getString("sex")
//                                + ", 贝壳数：" + dataJson.getString("shellnum") + ", 锤子数：" + dataJson.getString("harmmernum");
//                        } else if (statusJson.getString("result").equals("false")) {
//                            str = "查无此人";
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                /*******************************************
                 * 设置用户基本信息结果
                 */
//                try {
//                    if (data.get("status").equals(-1)) {
//                        str = data.getString("errorStr");
//                    } else if (data.get("status").equals(0)) {
//                        str = "服务器错误";
//                    } else if (data.get("status").equals(1)) {
//                        if (data.getString("result").equals("true"))
//                        {
//                            str = "信息设置成功";
//                        } else if (data.getString("result").equals("false")) {
//                            str = "信息设置失败";
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                /******************************************
                 * 搜索好友结果
                 */
//                try {
//                    JSONObject statusJson = data.getJSONObject(0);
//                    if (statusJson.get("status").equals(-1)) {
//                        str = statusJson.getString("errorStr");
//                    } else if (statusJson.get("status").equals(0)) {
//                        str = "服务器错误";
//                    } else if (statusJson.get("status").equals(1)) {
//                        if (statusJson.getString("result").equals("true"))  //正确返回数据
//                        {
//                            JSONObject dataJson = data.getJSONObject(1);
//                            //注意大小写
//                            str = "手机号：" + dataJson.getString("phonenum") + ", 昵称：" + dataJson.getString("nickname");
//                        } else if (statusJson.getString("result").equals("false")) {
//                            str = "查无此人";
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                /*******************************************
                 * 提交好友申请
                 */
//                try {
//                    if (data.get("status").equals(-1)) {
//                        str = data.getString("errorStr");
//                    } else if (data.get("status").equals(0)) {
//                        str = "服务器错误";
//                    } else if (data.get("status").equals(1)) {
//                        if (data.getString("result").equals("true"))
//                        {
//                            str = "申请成功";
//                        } else if (data.getString("result").equals("false")) {
//                            str = "申请失败";
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                /************************************************
                 * 好友申请处理结果
                 */
//                try {
//                    if (data.get("status").equals(-1)) {
//                        str = data.getString("errorStr");
//                    } else if (data.get("status").equals(0)) {
//                        str = "服务器错误";
//                    } else if (data.get("status").equals(1)) {
//                        if (data.getString("result").equals("true"))
//                        {
//                            str = "操作成功";
//                        } else if (data.getString("result").equals("false")) {
//                            str = "操作失败";
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                /***************************************************
                 * 获取好友列表
                 */
//                try {
//                    JSONObject statusJson = data.getJSONObject(0);
//                    if (statusJson.get("status").equals(-1)) {
//                        str = statusJson.getString("errorStr");
//                    } else if (statusJson.get("status").equals(0)) {
//                        str = "服务器错误";
//                    } else if (statusJson.get("status").equals(1)) {
//                        if (statusJson.getString("result").equals("true"))  //正确返回数据
//                        {
//                            for (int i = 1; i < data.length(); ++i) {
//                                JSONObject dataJson = data.getJSONObject(i);
//                                //注意大小写
//                                str = str + "手机号：" + dataJson.getString("phonenumB")
//                                        + ", 备注：" + dataJson.getString("remark");
//                            }
//                        } else if (statusJson.getString("result").equals("false")) {
//                            str = "查无此人";
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                /***************************************************
                 * 更新备注
                 */
//                try {
//                    if (data.get("status").equals(-1)) {
//                        str = data.getString("errorStr");
//                    } else if (data.get("status").equals(0)) {
//                        str = "服务器错误";
//                    } else if (data.get("status").equals(1)) {
//                        if (data.getString("result").equals("true"))
//                        {
//                            str = "更新成功";
//                        } else if (data.getString("result").equals("false")) {
//                            str = "更新失败";
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                /****************************************************
                 * 任务申请
                 */
//                try {
//                    if (data.get("status").equals(-1)) {
//                        str = data.getString("errorStr");
//                    } else if (data.get("status").equals(0)) {
//                        str = "服务器错误";
//                    } else if (data.get("status").equals(1)) {
//                        if (data.getString("result").equals("true"))
//                        {
//                            str = "申请成功";
//                        } else if (data.getString("result").equals("false")) {
//                            str = "申请失败";
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                /****************************************************
                 * 任务申请处理结果
                 */
//                try {
//                    if (data.get("status").equals(-1)) {
//                        str = data.getString("errorStr");
//                    } else if (data.get("status").equals(0)) {
//                        str = "服务器错误";
//                    } else if (data.get("status").equals(1)) {
//                        if (data.getString("result").equals("true"))
//                        {
//                            str = "处理成功";
//                        } else if (data.getString("result").equals("false")) {
//                            str = "处理失败";
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                /******************************************************
                 * 任务开始前获取相关信息
                 */
//                try {
//                    JSONObject statusJson = data.getJSONObject(0);
//                    if (statusJson.get("status").equals(-1)) {
//                        str = statusJson.getString("errorStr");
//                    } else if (statusJson.get("status").equals(0)) {
//                        str = "服务器错误";
//                    } else if (statusJson.get("status").equals(1)) {
//                        if (statusJson.getString("result").equals("true"))  //正确返回数据
//                        {
//                            str = "共有" + (data.length() - 1) + "个好友陪你进行生任务";
//                            for (int i = 1; i < data.length(); ++i) {
//                                JSONObject dataJson = data.getJSONObject(i);
//                                //注意大小写
//                                str = str + "手机号：" + dataJson.getString("phonenum")
//                                        + ", 备注或昵称：" + dataJson.getString("name");
//                            }
//                        } else if (statusJson.getString("result").equals("false")) {
//                            str = "当前无其他好友";
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                /*****************************************
                 * 任务失败
                 */
//                try {
//                    if (data.get("status").equals(-1)) {
//                        str = data.getString("errorStr");
//                    } else if (data.get("status").equals(0)) {
//                        str = "服务器错误";
//                    } else if (data.get("status").equals(1)) {
//                        if (data.getString("result").equals("true"))
//                        {
//                            str = "操作成功";
//                        } else if (data.getString("result").equals("false")) {
//                            str = "操作失败";
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                /*******************************************
                 * 给失败用户锤子 (phoneNum, failPhoneNum)
                 */
//                try {
//                    if (data.get("status").equals(-1)) {
//                        str = data.getString("errorStr");
//                    } else if (data.get("status").equals(0)) {
//                        str = "服务器错误";
//                    } else if (data.get("status").equals(1)) {
//                        if (data.getString("result").equals("true"))
//                        {
//                            str = "操作成功";
//                        } else if (data.getString("result").equals("false")) {
//                            str = "操作失败";
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                /************************************************
                 * 提交任务总结
                 */
//                try {
//                    if (data.get("status").equals(-1)) {
//                        str = data.getString("errorStr");
//                    } else if (data.get("status").equals(0)) {
//                        str = "服务器错误";
//                    } else if (data.get("status").equals(1)) {
//                        if (data.getString("result").equals("true"))
//                        {
//                            str = "提交成功";
//                        } else if (data.getString("result").equals("false")) {
//                            str = "提交失败";
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                /**************************************************
                 * 获取任务总结信息
                 */
//                try {
//                    JSONObject statusJson = data.getJSONObject(0);
//                    if (statusJson.get("status").equals(-1)) {
//                        str = statusJson.getString("errorStr");
//                    } else if (statusJson.get("status").equals(0)) {
//                        str = "服务器错误";
//                    } else if (statusJson.get("status").equals(1)) {
//                        if (statusJson.getString("result").equals("true"))  //正确返回数据
//                        {
//                            str = "共有" + (data.length() - 1) + "个好友已经确定完成任务";
//                            for (int i = 1; i < data.length(); ++i) {
//                                JSONObject dataJson = data.getJSONObject(i);
//                                //注意大小写
//                                str = str + "手机号：" + dataJson.getString("phonenum")
//                                        + ", 备注或昵称：" + dataJson.getString("name")
//                                        + ", 开始时间:" + dataJson.getString("starttime")
//                                        + ", 结束时间：" + dataJson.getString("endtime")
//                                        + ", 任务内容：" + dataJson.getString("taskcontent")
//                                        + ", 集中度：" + dataJson.getString("foucusdegree")
//                                        + ", 效率：" + dataJson.getString("efficiency");
//                            }
//                        } else if (statusJson.getString("result").equals("false")) {
//                            str = "当前无其他好友";
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                /*********************************************
                 * 获取最近联系人
                 */
//                try {
//                    JSONObject statusJson = data.getJSONObject(0);
//                    if (statusJson.get("status").equals(-1)) {
//                        str = statusJson.getString("errorStr");
//                    } else if (statusJson.get("status").equals(0)) {
//                        str = "服务器错误";
//                    } else if (statusJson.get("status").equals(1)) {
//                        if (statusJson.getString("result").equals("true"))  //正确返回数据
//                        {
//                            for (int i = 1; i < data.length(); ++i) {
//                                JSONObject dataJson = data.getJSONObject(i);
//                                //注意大小写
//                                str = str + "手机号：" + dataJson.getString("phonenumB")
//                                        + ", 备注：" + dataJson.getString("remark")
//                                        + ", 时间:" + dataJson.getString("time");
//                            }
//                        } else if (statusJson.getString("result").equals("false")) {
//                            str = "当前无联系好友";
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                /********************************************
                 * 实时消息通知结果
                 */
//                try {
//                    JSONObject statusJson = data.getJSONObject(0);
//                    if (statusJson.get("status").equals(-1)) {
//                        str = statusJson.getString("errorStr");
//                    } else if (statusJson.get("status").equals(0)) {
//                        str = "服务器错误";
//                    } else if (statusJson.get("status").equals(1)) {
//                        if (statusJson.getString("result").equals("true"))  //正确返回数据
//                        {
//                            //遍历服务器发来的消息
//                            for (int i = 1; i < data.length(); ++i)
//                            {
//                                JSONObject messageJson = data.getJSONObject(i);
//                                //申请好友消息(可能有多个）
//                                if (messageJson.getString("messageType").equals("1"))
//                                {
//                                    str = "申请者：" + messageJson.getString("phonenumA")
//                                        + ", 申请者昵称：" + messageJson.getString("name")
//                                        + ", 用户：" + messageJson.getString("phonenumB")
//                                        + ", 申请时间：" + messageJson.getString("applytime");
//                                }
//                                //申请好友成功反馈
//                                else if (messageJson.getString("messageType").equals("2")) {
//                                    str = "申请者(该用户）：" + messageJson.getString("phonenumA")
//                                            + ", 添加的好友：" + messageJson.getString("phonenumB")
//                                            + ", 添加的好友昵称：" + messageJson.getString("name");
//                                }
//                                //申请任务消息(type 1:合作模式 2:竞争模式）
//                                else if (messageJson.getString("messageType").equals("3")) {
//                                    str = "申请者：" + messageJson.getString("phonenumA")
//                                            + ", 申请者备注：" + messageJson.getString("remark")
//                                            + ", 用户：" + messageJson.getString("phonenumB")
//                                            + ", 开始时间：" + messageJson.getString("starttime")
//                                            + ", 结束时间：" + messageJson.getString("endtime")
//                                            + ", 类型：" + messageJson.getString("type");
//                                }
//                                //申请任务消息成功反馈
//                                else if (messageJson.getString("messageType").equals("4")) {
//                                    str = "已有好友加入  申请者（该用户）：" + messageJson.getString("phonenumA")
//                                            + ", 开始时间：" + messageJson.getString("starttime");
//                                }
//                                //合作模式某用户失败反馈消息
//                                else if (messageJson.getString("messageType").equals("5")) {
//                                    str = "失败用户：" + messageJson.getString("phonenumA")
//                                            + ", 备注或昵称：" + messageJson.getString("name")
//                                            + ", 开始时间：" + messageJson.getString("starttime")
//                                            + ", 失败时间：" + messageJson.getString("failtime");
//                                }
//                                //合作模式某用户失败反馈消息
//                                else if (messageJson.getString("messageType").equals("6")) {
//                                    str = "失败用户：" + messageJson.getString("phonenumA")
//                                            + ", 备注或昵称：" + messageJson.getString("name")
//                                            + ", 开始时间：" + messageJson.getString("starttime")
//                                            + ", 失败时间：" + messageJson.getString("failtime");
//                                }
//                            }
//                        } else if (statusJson.getString("result").equals("false")) {
//                            str = "当前无消息通知";
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                myTextView.setText(str);
            }
        }
    };

    public class MyThread implements Runnable {
        public void run() {


            /***************************************************
             * 获取用户基本信息调用(phoneNum)
             */
            //JSONArray json = HttpProcess.getUserMainInfo("123");

            /***************************************************
             * 设置用户基本信息(phoneNum, nickname, sex, birthday)
             */
            //JSONObject json = HttpProcess.setUserMainInfo("123456", "小狗", "男", "1000-01-01");

            /****************************************************
             * 搜索好友(phoneNumOruserAccount)
             */
            //JSONArray json = HttpProcess.findFriend("123");

            /****************************************************
             * 提交好友申请调用(用户phoneNum, 欲申请好友phoneNum)
             */
            //JSONObject json = HttpProcess.friendApplication("1234", "12345");

            /************************************************
             * 好友申请处理结果(成功后务必不能再让用户有选择的可能） （用户phoneNum 申请者phoneNum result:true or false）
             */
            // JSONObject json = HttpProcess.friendApplicationResult("12345", "1234", "true");

            /***************************************************
             * 获取好友列表(phoneNum)
             */
            //JSONArray json = HttpProcess.getFriendList("123");

            /***************************************************
             * 更新备注(phoneNum, friendPhoneNum, remark)
             */
            //JSONObject json = HttpProcess.updateRemark("123", "123456", "逗比3");

            /****************************************************
             * 任务申请(phoneNum, 多个friendPhoneNum, startTime, endTime, type 1:合作模式 2：竞争模式)
             */
//            JSONArray friendPhoneNum = new JSONArray();
//            friendPhoneNum.put("1234");
//            friendPhoneNum.put("12345");
//            friendPhoneNum.put("123456");
//            JSONObject json = HttpProcess.taskApplication("123", friendPhoneNum, "2015-10-10 10:00:00", "2015-10-10 11:00:00", 2);


            /****************************************************
             * 任务申请处理结果   （ phoneNum + 申请者PhoneNum + startTime + true or false）
             */
            //JSONObject json= HttpProcess.taskApplicationResult("1234", "123", "2015-10-10 10:00:00", "false");

            /******************************************************
             * 任务开始前获取相关信息 (phoneNum + 申请者phoneNum + startTime)
             */
            //JSONArray json = HttpProcess.getTaskInfo("12345", "123", "2015-10-10 10:00:00");

            /*****************************************
             * 任务失败 (phoneNum + 申请者phoneNum + startTime + failTime + type 1:合作 2：竞争)
             */
            //JSONObject json= HttpProcess.taskFail("123456", "123", "2015-10-10 10:00:00", "2015-10-10 10:30:00", 1);

            /*******************************************
             * 给失败用户锤子 (phoneNum, failPhoneNum)
             */
            //JSONObject json = HttpProcess.giveHarmmer("1234", "123456");

            /************************************************
             * 提交任务总结 (phoneNum + startTime + endTime + taskContent + foucusDegree + efficiency)
             */
            //JSONObject json = HttpProcess.submitTaskSummary("123", "2015-10-10 10:00:00", "2015-10-10 11:00:00", "读书", 0.8F, 0.9F);

            /**************************************************
             * 获取任务总结信息 (phoneNum + 申请者phoneNum + startTime)
             */
            //JSONArray json = HttpProcess.getTaskSummary("123", "123", "2015-10-10 10:00:00");

            /*********************************************
             * 获取最近联系人 (phoneNum, startRow, count)
             */
            //JSONArray json = HttpProcess.getRecentContact("123", 0, 10);


            /****************************************************
             * 实时消息通知调用(phoneNum)
             */
            //JSONArray json = HttpProcess.messageInform("123");

//            Message msg = Message.obtain();
//            msg.obj = json;
//            msg.what = IS_FINISH;
//            handler.sendMessage(msg);
        }
    }


}
