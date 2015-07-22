package com.example.caelum.test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class HttpProcess {

    //status == 1 ->正确返回结果  true->有结果 false->无结果
    //status == 0 ->服务器错误（数据库异常）
    //status == 1 ->网络异常

    /***********************************************
     * 注册
     */
    public static JSONObject register(String phoneNum, String userAccount, String password)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("phoneNum", phoneNum);
            json.put("userAccount", userAccount);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultHelper(json, "RegisterServlet");
    }


    /************************************************
     *登录
     */
    public static JSONObject login(String phoneNum, String userAccount, String password)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("phoneNum", phoneNum);
            json.put("userAccount", userAccount);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultHelper(json, "LoginServlet");
    }


    /*************************************************
     *更新密码
     */
    public static JSONObject updatePassword(String phoneNum, String newPassword)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("phoneNum", phoneNum);
            json.put("newPassword", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultHelper(json, "UpdatePasswordServlet");
    }


    /**************************************************************
     *获取用户基本信息
     */
    public static JSONArray getUserMainInfo(String phoneNum)
    {
        return infoHelper(phoneNum, "UserMainInfoServlet");
    }


    /**************************************************************
     * 设置用户基本信息
     */
    public static JSONObject setUserMainInfo(String phoneNum, String nickname, String sex, String birthday)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("phoneNum", phoneNum);
            json.put("nickname", nickname);
            json.put("sex", sex);
            json.put("birthday", birthday);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultHelper(json, "SetUserMainInfoServlet");
    }


    /*************************************************************
     * 搜索好友
     */
    public static JSONArray findFriend(String phoneNum, String userAccount)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("phoneNum", phoneNum);
            json.put("userAccount", userAccount);
            String jsonStr = json.toString();
            String resultStr = HttpUtil.doPost(HttpUtil.BASEURL + "FindFriendServlet", jsonStr);
            JSONArray resultJson = new JSONArray(resultStr);
            return resultJson;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*************************************************************
     * 申请好友
     */
    public static JSONObject friendApplication(String phoneNum, String friendPhoneNum)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("phoneNum", phoneNum);
            json.put("friendPhoneNum", friendPhoneNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultHelper(json, "FriendApplicationServlet");
    }


    /*************************************************************
     * 实时消息通知
     */
    public static JSONArray messageInform(String phoneNum)
    {
        return infoHelper(phoneNum, "MessageInformServlet");
    }


    /**************************************************************
     * 好友申请处理结果
     */
    public static JSONObject friendApplication(String phoneNum, String friendPhoneNum, String result)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("phoneNum", phoneNum);
            json.put("friendPhoneNum", friendPhoneNum);
            json.put("result", result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultHelper(json, "FriendApplicationResultServlet");
    }


    /**************************************************************
     * 获得好友列表信息
     */
    public static JSONArray getFriendList(String phoneNum)
    {
        return infoHelper(phoneNum, "FriendListServlet");
    }


    /**************************************************************
     * 辅助方法
     */
    private static JSONArray infoHelper(String phoneNum, String servlet)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("phoneNum", phoneNum);
            String jsonStr = json.toString();
            String resultStr = HttpUtil.doPost(HttpUtil.BASEURL + servlet, jsonStr);
            JSONArray resultJson = new JSONArray(resultStr);
            return resultJson;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JSONObject resultHelper(JSONObject json, String servlet)
    {
        String jsonStr = json.toString();
        String resultStr = HttpUtil.doPost(HttpUtil.BASEURL + servlet, jsonStr);
        try {
            JSONArray jsonArray = new JSONArray(resultStr);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
