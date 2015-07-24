package com.nectar.timeby.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class HttpProcess {

    //status == 1 ->
    // 正确返回结果  true->有结果 false->无结果
    //status == 0 ->服务器错误（数据库异常）
    //status == 1 ->网络异常
    //status为int  其余类型均为String 视情况转换

    /************************************************************
     * 检查手机号是否被注册
     */
    public static JSONObject checkPhoneNum(String phoneNum)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("phoneNum", phoneNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultHelper(json, "CheckPhoneNumServlet");
    }


    /***********************************************
     * 检测用户名是否已被注册
     */
    public static JSONObject checkUserAccount(String userAccount)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("userAccount", userAccount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultHelper(json, "CheckUserAccountServlet");
    }


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
    public static JSONObject login(String phonenumOrUseraccount, String password)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("paramStr", phonenumOrUseraccount);
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
    public static JSONArray findFriend(String phonenumOrUseraccount)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("paramStr", phonenumOrUseraccount);
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
    public static JSONObject friendApplicationResult(String phoneNum, String friendPhoneNum, String result)
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


    /***********************************************************
     * 更改好友备注
     */
    public static JSONObject updateRemark(String phoneNum, String friendPhoneNum, String remark)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("phoneNum", phoneNum);
            json.put("friendPhoneNum", friendPhoneNum);
            json.put("remark", remark);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultHelper(json, "UpdateRemarkServlet");
    }


    /************************************************************
     * 申请任务   合作模式：1   竞争模式：2 (确保不能申请多次同时间任务） 时间格式(2000-10-10 10:10:10)
     */
    public static JSONObject taskApplication(String phoneNum, JSONArray friendPhoneNum, String startTime, String endTime, int type)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("phoneNum", phoneNum);
            json.put("friendPhoneNum", friendPhoneNum);
            json.put("startTime", startTime);
            json.put("endTime", endTime);
            json.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultHelper(json, "TaskApplicationServlet");
    }


    /************************************************************
     * 任务申请反馈结果  phoneNum:用户  friendPhoneNum:申请者  startTime:申请任务开始时间 result: true or false
     */
    public static JSONObject taskApplicationResult(String phoneNum, String friendPhoneNum, String startTime, String result)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("phoneNum", phoneNum);
            json.put("friendPhoneNum", friendPhoneNum);
            json.put("startTime", startTime);
            json.put("result", result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultHelper(json, "TaskApplicationResultServlet");
    }


    /***********************************************************
     * 任务开始前向服务器获取任务相关信息
     */
    public static JSONArray getTaskInfo(String phoneNum, String applicantPhoneNum, String startTime)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("phoneNum", phoneNum);
            json.put("applicantPhoneNum", applicantPhoneNum);
            json.put("startTime", startTime);
            String jsonStr = json.toString();
            String resultStr = HttpUtil.doPost(HttpUtil.BASEURL + "TaskInfoServlet", jsonStr);
            JSONArray resultJson = new JSONArray(resultStr);
            return resultJson;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    /********************************************************
     * 任务失败
     */
    public static JSONObject taskFail(String phoneNum, String applicantPhoneNum, String startTime, String failTime, int type)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("phoneNum", phoneNum);
            json.put("applicantPhoneNum", applicantPhoneNum);
            json.put("startTime", startTime);
            json.put("failTime", failTime);
            json.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultHelper(json, "TaskFailServlet");
    }


    /**********************************************************
     * 如果给锤子 则发送消息
     */
    public static JSONObject giveHarmmer(String phoneNum, String failPhoneNum)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("phoneNum", phoneNum);
            json.put("failPhoneNum", failPhoneNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultHelper(json, "GiveHarmmerServlet");
    }


    /************************************************************
     * 提交任务总结表单(成功才能提交）
     */
    public static JSONObject submitTaskSummary(String phoneNum, String startTime, String endTime, String taskContent, float foucusDegree, float efficiency)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("phoneNum", phoneNum);
            json.put("startTime", startTime);
            json.put("endTime", endTime);
            json.put("taskContent", taskContent);
            json.put("foucusDegree", foucusDegree);
            json.put("efficiency", efficiency);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultHelper(json, "TaskSummaryServlet");
    }


    /************************************************************
     * 获取任务总结
     */
    public static JSONArray getTaskSummary(String phoneNum, String applicantPhoneNum, String startTime)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("phoneNum", phoneNum);
            json.put("applicantPhoneNum", applicantPhoneNum);
            json.put("startTime", startTime);
            String jsonStr = json.toString();
            String resultStr = HttpUtil.doPost(HttpUtil.BASEURL + "TaskSummaryInfoServlet", jsonStr);
            JSONArray resultJson = new JSONArray(resultStr);
            return resultJson;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*********************************************************
     * 获取最近联系人
     */
    public static JSONArray getRecentContact(String phoneNum, int start, int count)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("phoneNum", phoneNum);
            json.put("start", start);
            json.put("count", count);
            String jsonStr = json.toString();
            String resultStr = HttpUtil.doPost(HttpUtil.BASEURL + "RecentContactServlet", jsonStr);
            JSONArray resultJson = new JSONArray(resultStr);
            return resultJson;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
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
