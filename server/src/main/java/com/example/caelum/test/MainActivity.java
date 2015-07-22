package com.example.caelum.test;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    private TextView myTextView;
    private Map<String, Object> map = new HashMap<String, Object>();
    private static int IS_FINISH = 1;
    private String str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myTextView = (TextView)findViewById(R.id.myTextView);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if (msg.what == IS_FINISH)
            {
                //status == 1 ->正确返回结果  result == true->有结果  result == false->无结果
                //status == 0 ->服务器错误（数据库异常）
                //status == 1 ->网络异常
                //status为int  其余类型均为String 视情况转换
                //无附带数据返回类弄为JSONObject, 附带数据返回类型为JSONArray


                JSONObject data = (JSONObject) msg.obj;
                //JSONArray data = (JSONArray) msg.obj;


                /*******************************************
                 * 注册结果
                 */
//                try {
//                    if (data.get("status").equals(-1)) {
//                        str = data.getString("errorStr");
//                    } else if (data.get("status").equals(0)) {
//                        str = "服务器错误";
//                    } else if (data.get("status").equals(1)) {
//                        if (data.getString("result").equals("true"))
//                        {
//                            str = "注册成功";
//                        } else if (data.getString("result").equals("false")) {
//                            str = "注册失败";
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                /*******************************************
                 * 登录结果
                 */
                try {
                    if (data.get("status").equals(-1)) {
                        str = data.getString("errorStr");
                    } else if (data.get("status").equals(0)) {
                        str = "服务器错误";
                    } else if (data.get("status").equals(1)) {
                        if (data.getString("result").equals("true"))
                        {
                            str = "登录成功";
                        } else if (data.getString("result").equals("false")) {
                            str = "登录失败";
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


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
                 * 提交好友申请结果
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
//                                        + ", 用户：" + messageJson.getString("phonenumB")
//                                        + ", 申请时间：" + messageJson.getString("applytime");
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
        public void run()
        {
            /***************************************************
             * 注册调用(phoneNum, userAccount, password)
             */
            //JSONObject json = HttpProcess.register("123456", "abcdef", "123456");

            /***************************************************
             * 登录调用(phoneNum, userAccount, password) phoneNum, userAccount只能传一个参数，另一个为空
             */
            //JSONObject json = HttpProcess.login("123", "", "123");
            JSONObject json = HttpProcess.login("", "abcd", "123");

            /***************************************************
             * 更新密码调用(phoneNum, password)
             */
            //JSONObject json = HttpProcess.updatePassword("123", "123");

            /***************************************************
             * 获取用户基本信息调用(phoneNum)
             */
            //JSONArray json = HttpProcess.getUserMainInfo("123");

            /***************************************************
             * 设置用户基本信息(phoneNum, nickname, sex, birthday)
             */
            //JSONObject json = HttpProcess.setUserMainInfo("1234", "王绍文", "女", "3000-11-11");

            /****************************************************
             * 搜索好友(phoneNum, userAccount) 只能传一个参数，另一个为空
             */
            //JSONArray json = HttpProcess.findFriend("123", "");

            /****************************************************
             * 提交好友申请调用(用户phoneNum, 欲申请好友phoneNum)
             */
            //JSONObject json = HttpProcess.friendApplication("12345", "123");

            /****************************************************
             * 实时消息通知调用(phoneNum)
             */
            //JSONArray json = HttpProcess.messageInform("123");

            Message msg = Message.obtain();
            msg.obj = json;
            msg.what = IS_FINISH;
            handler.sendMessage(msg);
        }
    }


}
