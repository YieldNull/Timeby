package com.nectar.timeby.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class HttpProcess {

    //status == 1 ->
    // ��ȷ���ؽ��  true->�н�� false->�޽��
    //status == 0 ->�������������ݿ��쳣��
    //status == 1 ->�����쳣
    //statusΪint  �������;�ΪString �����ת��

    /************************************************************
     * ����ֻ����Ƿ�ע��
     */
    public static JSONObject checkPhoneNum(String phoneNum)
    {
        JSONArray phoneNums = new JSONArray();
        phoneNums.put(phoneNum);
        try {
            return checkMultiplePhoneNum(phoneNums).getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    /***********************************************
     * ��������ֻ����Ƿ��ѱ�ע��
     */
    public static JSONArray checkMultiplePhoneNum(JSONArray phoneNums)
    {
        JSONObject json = new JSONObject();
        try {
            json.put("phoneNums", phoneNums);
            String jsonStr = json.toString();
            String resultStr = HttpUtil.doPost(HttpUtil.BASEURL + "CheckPhoneNumServlet", jsonStr);
            JSONArray resultJson = new JSONArray(resultStr);
            return resultJson;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    /***********************************************
     * ����û����Ƿ��ѱ�ע��
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
     * ע��
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
     *��¼
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
     *��������
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
     *��ȡ�û�������Ϣ
     */
    public static JSONArray getUserMainInfo(String phoneNum)
    {
        return infoHelper(phoneNum, "UserMainInfoServlet");
    }


    /**************************************************************
     * �����û�������Ϣ
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
     * ��������
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
     * �������
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
     * ʵʱ��Ϣ֪ͨ
     */
    public static JSONArray messageInform(String phoneNum)
    {
        return infoHelper(phoneNum, "MessageInformServlet");
    }


    /**************************************************************
     * �������봦����
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
     * ��ú����б���Ϣ
     */
    public static JSONArray getFriendList(String phoneNum)
    {
        return infoHelper(phoneNum, "FriendListServlet");
    }


    /***********************************************************
     * ���ĺ��ѱ�ע
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
     * ��������   ����ģʽ��1   ����ģʽ��2 (ȷ������������ͬʱ������ ʱ���ʽ(2000-10-10 10:10:10)
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
     * �������뷴�����  phoneNum:�û�  friendPhoneNum:������  startTime:��������ʼʱ�� result: true or false
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
     * ����ʼǰ���������ȡ���������Ϣ
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
     * ����ʧ��
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
     * ��������� ������Ϣ
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
     * �ύ�����ܽ��(�ɹ������ύ��
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
     * ��ȡ�����ܽ�
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
     * ��ȡ�����ϵ��
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


    /************************************************
     *�ϴ�ͼƬ
     *resultJson.getInt("status")
     *status = 1 -> resultJson.getString("result")
     *status = 0 -> ����������
     *status = -1 ->resultJson.getString("errorStr")
     *status = -2 ->resultJson.getString("errorStr")	//ͼƬ��ʽ����
     */
    public static JSONObject uploadImage(String phoneNum, File file)
    {
        //final String[] allowedExt = new String[] { "jpg", "jpeg", "gif", "png"};
        //File file = new File(filePath);
        //String ext = filePath.substring(filePath.lastIndexOf(".") + 1);
        final String[] allowedExt = new String[] { "jpg" };
        String ext = "jpg";
        int allowFlag = 0;
        int allowedExtCount = allowedExt.length;
        for (; allowFlag < allowedExtCount; ++allowFlag)
        {
            if (allowedExt[allowFlag].equals(ext))
            {
                break;
            }
        }
        if (allowFlag == allowedExtCount)
        {
            JSONObject errorJson = new JSONObject();
            try {
                errorJson.put("status", -2);
                errorJson.put("errorStr", "error image��");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return errorJson;
        }
        String fileName = phoneNum + "." + ext;
        String resultStr = HttpUtil.uploadByPost(HttpUtil.BASEURL + "ReceiveImageServlet", file, fileName);
        JSONObject jsonObject = null;
        try {
            JSONArray resultJson = new JSONArray(resultStr);
            jsonObject = resultJson.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    /************************************************************
     * �ж��û����Ƿ����ã�����ͷ��
     */
    public static JSONArray headImageInfo(String phoneNum)
    {
        return infoHelper(phoneNum, "HeadImageInfoServlet");
    }


    /**************************************************************
     *����һ��ͼƬ
     *map.get("status") == 1 -> map.get("phoneNum") map.get("file")
     *map.get("status") == -1 -> map.get("errorStr")
     */
    public static Map<String, Object> getOneImage(String phoneNum, String fileDir)
    {
        List<String> list = new ArrayList<String>();
        list.add(phoneNum);
        return HttpUtil.downloadImage(list, fileDir).get(0);
    }

    /*************************************************************
     *���ض���ͼƬ
     *�ȱ���
     *map.get("status") == 1 -> map.get("phoneNum") map.get("file")
     *map.get("status") == -1 -> map.get("errorStr")
     */
    public static List<Map<String, Object>> getMultipleImage(List<String> phoneNumList, String fileDir)
    {
        return HttpUtil.downloadImage(phoneNumList, fileDir);
    }


    /**************************************************************
     * ��������
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
