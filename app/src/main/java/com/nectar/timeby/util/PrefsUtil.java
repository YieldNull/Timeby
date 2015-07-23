package com.nectar.timeby.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.nectar.timeby.gui.UserInfoEditActivity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by finalize on 7/19/15.
 */
public class PrefsUtil {
    public static final String PREFS_MAP_USER = "user";
    public static final String PREFS_KEY_USER_NAME = "userName";
    public static final String PREFS_KEY_USER_PHONE = "phoneNum";
    public static final String PREFS_KEY_USER_PASSWORD = "password";

    public static final String PREFS_MAP_USER_INFO = "userInfo";
    public static final String PREFS_KEY_USER_INFO_NICKNAME = "userInfo_nickname";
    public static final String PREFS_KEY_USER_INFO_YEAR = "userInfo_year";
    public static final String PREFS_KEY_USER_INFO_GENDER = "userInfo_gender";
    public static final String PREFS_KEY_USER_INFO_SHELL = "userInfo_shell";
    public static final String PREFS_KEY_USER_INFO_HAMMER = "userInfo_hammer";
    public static final String PREFS_KEY_USER_INFO_PHOTO = "userInfo_photo";

    public static final String PREFS_MAP_TASK = "task";
    public static final String PREFS_KEY_START_TIME_MILLIS = "task_start";
    public static final String PREFS_KEY_END_TIME_MILLIS = "task_end";

    /**
     * 登录，存入手机号、用户名、密码。初始化用户信息
     *
     * @param context
     * @param userName
     * @param password
     * @param phoneNum
     */
    public static void login(Context context, String userName, String password, String phoneNum) {

        SharedPreferences user = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user.edit();
        editor.putString(PrefsUtil.PREFS_KEY_USER_NAME, userName);
        editor.putString(PrefsUtil.PREFS_KEY_USER_PASSWORD, password);
        editor.putString(PrefsUtil.PREFS_KEY_USER_PHONE, phoneNum);
        editor.commit();

        HashMap<String, String> infoMap = new HashMap<>();
        infoMap.put(PREFS_KEY_USER_INFO_NICKNAME, userName);
        infoMap.put(PREFS_KEY_USER_INFO_YEAR, "年龄：0");
        infoMap.put(PREFS_KEY_USER_INFO_GENDER, "male");
        infoMap.put(PREFS_KEY_USER_INFO_SHELL, "0");
        infoMap.put(PREFS_KEY_USER_INFO_HAMMER, "0");
        infoMap.put(PREFS_KEY_USER_INFO_PHOTO, ImgUtil.getHeadImgFileName(userName));
        storeUserInfo(context, infoMap);
    }

    /**
     * 判断是否有用户登入
     *
     * @param context
     * @return
     */
    public static boolean isLogin(Context context) {
        if (getUserName(context) == null)
            return false;
        else
            return true;
    }

    /**
     * 获取已登入的用户名
     *
     * @param context
     * @return
     */
    public static String getUserName(Context context) {
        SharedPreferences user = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_USER, Context.MODE_PRIVATE);
        return user.getString(PREFS_KEY_USER_NAME, null);
    }

    /**
     * 登出，清空用户信息
     *
     * @param context
     */
    public static void logout(Context context) {
        SharedPreferences user = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user.edit();
        editor.putString(PrefsUtil.PREFS_KEY_USER_NAME, null);
        editor.putString(PrefsUtil.PREFS_KEY_USER_PASSWORD, null);
        editor.putString(PrefsUtil.PREFS_KEY_USER_PHONE, null);
        editor.commit();
    }

    /**
     * 存储用户信息，昵称、年龄、性别、贝壳数、锤子数，头像名称
     *
     * @param context
     * @param infoMap
     */
    public static void storeUserInfo(Context context, Map<String, String> infoMap) {
        SharedPreferences user = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_USER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user.edit();
        editor.putString(PREFS_KEY_USER_INFO_NICKNAME, infoMap.get(PREFS_KEY_USER_INFO_NICKNAME));
        editor.putString(PREFS_KEY_USER_INFO_YEAR, infoMap.get(PREFS_KEY_USER_INFO_YEAR));
        editor.putString(PREFS_KEY_USER_INFO_GENDER, infoMap.get(PREFS_KEY_USER_INFO_GENDER));
        editor.putString(PREFS_KEY_USER_INFO_SHELL, infoMap.get(PREFS_KEY_USER_INFO_SHELL));
        editor.putString(PREFS_KEY_USER_INFO_HAMMER, infoMap.get(PREFS_KEY_USER_INFO_HAMMER));
        editor.putString(PREFS_KEY_USER_INFO_PHOTO, infoMap.get(PREFS_KEY_USER_INFO_PHOTO));
        editor.commit();
    }

    /**
     * 读取用户信息，昵称、年龄、性别、贝壳数、锤子数，头像名称
     *
     * @param context
     * @return
     */
    public static Map<String, String> readUserInfo(Context context) {
        Map<String, String> infoMap = new HashMap<>();

        SharedPreferences user = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_USER_INFO, Context.MODE_PRIVATE);

        infoMap.put(PREFS_KEY_USER_INFO_NICKNAME, user.getString(PREFS_KEY_USER_INFO_NICKNAME, null));
        infoMap.put(PREFS_KEY_USER_INFO_YEAR, user.getString(PREFS_KEY_USER_INFO_YEAR, null));
        infoMap.put(PREFS_KEY_USER_INFO_GENDER, user.getString(PREFS_KEY_USER_INFO_GENDER, null));
        infoMap.put(PREFS_KEY_USER_INFO_SHELL, user.getString(PREFS_KEY_USER_INFO_SHELL, null));
        infoMap.put(PREFS_KEY_USER_INFO_HAMMER, user.getString(PREFS_KEY_USER_INFO_HAMMER, null));
        infoMap.put(PREFS_KEY_USER_INFO_PHOTO, user.getString(PREFS_KEY_USER_INFO_PHOTO, null));

        return infoMap;
    }

    /**
     * 更新用户信息，昵称，年龄，性别
     *
     * @param context
     * @param infoMap
     */
    public static void updateInfo(Context context, HashMap<String, String> infoMap) {
        SharedPreferences user = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_USER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user.edit();
        editor.putString(PREFS_KEY_USER_INFO_NICKNAME, infoMap.get(PREFS_KEY_USER_INFO_NICKNAME));
        editor.putString(PREFS_KEY_USER_INFO_YEAR, infoMap.get(PREFS_KEY_USER_INFO_YEAR));
        editor.putString(PREFS_KEY_USER_INFO_GENDER, infoMap.get(PREFS_KEY_USER_INFO_GENDER));
        editor.commit();
    }

    public static void storeTask(Context context, long startMillis, long endMillis) {
        SharedPreferences user = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_TASK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user.edit();

        editor.putLong(PREFS_KEY_START_TIME_MILLIS, startMillis);
        editor.putLong(PREFS_KEY_END_TIME_MILLIS, endMillis);
        editor.commit();
    }

    public static Map<String, Long> readTask(Context context) {
        Map<String, Long> taskMap = new HashMap<>();

        SharedPreferences task = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_TASK, Context.MODE_PRIVATE);

        taskMap.put(PREFS_KEY_START_TIME_MILLIS, task.getLong(PREFS_KEY_START_TIME_MILLIS, 0));
        taskMap.put(PREFS_KEY_END_TIME_MILLIS, task.getLong(PREFS_KEY_END_TIME_MILLIS, 0));

        return taskMap;
    }
}
