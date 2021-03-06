package com.nectar.timeby.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.nectar.timeby.gui.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by finalize on 7/19/15.
 */
public class PrefsUtil {
    public static final String PREFS_MAP_INSTALL = "install";
    public static final String PREFS_KEY_INSTALL_FIRST = "install_first";

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
    public static final String PREFS_KEY_TASK_START_TIME_MILLIS = "task_start";
    public static final String PREFS_KEY_TASK_END_TIME_MILLIS = "task_end";
    public static final String PREFS_KEY_TASK_TYPE = "task_type";
    public static final String PREFS_KEY_TASK_FAIL = "task_fail";
    public static final String PREFS_KEY_TASK_WITH_FRIENDS = "task_with_friends";
    public static final String PREFS_KEY_TASK_REQUEST_FROM = "task_request_from";

    public static final String PREFS_MAP_SETTING = "setting";
    public static final String PREFS_KEY_SETTING_NOTIFICATION_BGD = "setting_notification_bgd";
    public static final String PREFS_KEY_SETTING_SOUND_EFFECT = "setting_sound_effect";
    public static final String PREFS_KEY_SETTING_ESCAPE_TIME = "setting_escape_time";
    public static final String PREFS_KEY_SETTING_SLEEP_TIME = "setting_sleep_time";


    public static final String PREFS_MAP_DRAWER_NOTIFY = "drawer_notify";
    public static final String PREFS_KEY_DRAWER_NOTIFY = "drawer_message_come";


    /**
     * 登录，存入手机号、用户名、密码。初始化用户信息
     *
     * @param context
     * @param userName
     * @param password
     * @param phoneNum
     */
    public static void login(Context context, String userName, String password, String phoneNum) {

        //登录信息
        SharedPreferences user = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user.edit();
        editor.putString(PrefsUtil.PREFS_KEY_USER_NAME, userName);
        editor.putString(PrefsUtil.PREFS_KEY_USER_PASSWORD, password);
        editor.putString(PrefsUtil.PREFS_KEY_USER_PHONE, phoneNum);
        editor.commit();

        //存入默认用户信息
        HashMap<String, String> infoMap = new HashMap<>();
        infoMap.put(PREFS_KEY_USER_INFO_NICKNAME, userName);
        infoMap.put(PREFS_KEY_USER_INFO_YEAR, "年龄：0");
        infoMap.put(PREFS_KEY_USER_INFO_GENDER, "male");
        infoMap.put(PREFS_KEY_USER_INFO_SHELL, "0");
        infoMap.put(PREFS_KEY_USER_INFO_HAMMER, "0");
        infoMap.put(PREFS_KEY_USER_INFO_PHOTO, ImgUtil.getHeadImgFileName(userName));
        storeUserInfo(context, infoMap);

        //存入默认设置
        storeSettings(context, true, true, 30, "10:00");
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

    public static String getUserPhone(Context context) {
        SharedPreferences user = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_USER, Context.MODE_PRIVATE);
        return user.getString(PREFS_KEY_USER_PHONE, null);
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

    /**
     * 判断用户是否处于任务期间
     *
     * @param context
     * @return
     */
    public static boolean isOnTask(Context context) {

        SharedPreferences task = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_TASK, Context.MODE_PRIVATE);
        long startTime = task.getLong(PREFS_KEY_TASK_START_TIME_MILLIS, 0);
        long endTime = task.getLong(PREFS_KEY_TASK_END_TIME_MILLIS, 0);
        long current = System.currentTimeMillis();

        if (isTaskFail(context))
            return false;

        if (startTime < current && endTime > current) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将任务信息存入本地
     *
     * @param context
     * @param startMillis
     * @param endMillis
     * @param type
     */
    public static void initTask(Context context, long startMillis, long endMillis, int type) {
        SharedPreferences user = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_TASK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user.edit();

        editor.putLong(PREFS_KEY_TASK_START_TIME_MILLIS, startMillis);
        editor.putLong(PREFS_KEY_TASK_END_TIME_MILLIS, endMillis);
        editor.putInt(PREFS_KEY_TASK_TYPE, type);
        editor.commit();

        setIsTaskFailed(context, false);
        setFriendsAccept(context, false);
        setTaskRequestFrom(context, getUserPhone(context));
    }

    /**
     * 是否有朋友同意
     *
     * @param context
     * @return
     */
    public static boolean hasFriendsAccept(Context context) {
        SharedPreferences task = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_TASK, Context.MODE_PRIVATE);

        return task.getBoolean(PREFS_KEY_TASK_WITH_FRIENDS, false);
    }

    /**
     * 设置是否有朋友同意
     *
     * @param context
     * @param hasAccept
     */
    public static void setFriendsAccept(Context context, boolean hasAccept) {
        SharedPreferences task = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_TASK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = task.edit();
        editor.putBoolean(PREFS_KEY_TASK_WITH_FRIENDS, hasAccept);
        editor.commit();
    }

    public static void setTaskRequestFrom(Context context, String phone) {
        SharedPreferences task = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_TASK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = task.edit();
        editor.putString(PREFS_KEY_TASK_REQUEST_FROM, phone);

        editor.commit();
    }

    public static String getTaskRequestFrom(Context context) {
        SharedPreferences task = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_TASK, Context.MODE_PRIVATE);
        return task.getString(PREFS_KEY_TASK_REQUEST_FROM, PrefsUtil.getUserPhone(context));
    }

    /**
     * 读取任务信息
     *
     * @param context
     * @return
     */
    public static Map<String, Long> readTask(Context context) {
        Map<String, Long> taskMap = new HashMap<>();

        SharedPreferences task = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_TASK, Context.MODE_PRIVATE);

        taskMap.put(PREFS_KEY_TASK_START_TIME_MILLIS, task.getLong(PREFS_KEY_TASK_START_TIME_MILLIS, 0));
        taskMap.put(PREFS_KEY_TASK_END_TIME_MILLIS, task.getLong(PREFS_KEY_TASK_END_TIME_MILLIS, 0));

        return taskMap;
    }

    /**
     * 是否有任务尚未开始
     *
     * @param context
     * @return
     */
    public static boolean hasTask(Context context) {
        SharedPreferences task = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_TASK, Context.MODE_PRIVATE);
        long start = task.getLong(PREFS_KEY_TASK_START_TIME_MILLIS, 0);

        if (start > System.currentTimeMillis())
            return true;
        else
            return false;
    }

    /**
     * 当前任务是否失败
     *
     * @param context
     * @return
     */
    public static boolean isTaskFail(Context context) {
        SharedPreferences task = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_TASK, Context.MODE_PRIVATE);

        return task.getBoolean(PREFS_KEY_TASK_FAIL, false);
    }


    public static int getTaskType(Context context) {
        SharedPreferences task = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_TASK, Context.MODE_PRIVATE);
        return task.getInt(PREFS_KEY_TASK_TYPE, -1);
    }

    /**
     * 设置任务是否失败
     *
     * @param context
     * @param isFailed
     */
    public static void setIsTaskFailed(Context context, boolean isFailed) {
        SharedPreferences task = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_TASK, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = task.edit();

        editor.putBoolean(PREFS_KEY_TASK_FAIL, isFailed);
        editor.commit();
    }

    /**
     * 取消任务
     *
     * @param context
     */
    public static void cancelTask(Context context) {
        SharedPreferences task = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_TASK, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = task.edit();

        editor.putLong(PREFS_KEY_TASK_START_TIME_MILLIS, 0);
        editor.putLong(PREFS_KEY_TASK_END_TIME_MILLIS, 0);
        editor.putInt(PREFS_KEY_TASK_TYPE, -1);
        editor.putBoolean(PREFS_KEY_TASK_FAIL, false);
        editor.commit();

        setFriendsAccept(context, false);
        setTaskRequestFrom(context, getUserPhone(context));
    }

    /**
     * 获取任务结束时间
     *
     * @param context
     * @return
     */
    public static long getTaskEndTime(Context context) {
        return readTask(context).get(PREFS_KEY_TASK_END_TIME_MILLIS);
    }

    public static long getTaskStartTime(Context context) {
        return readTask(context).get(PREFS_KEY_TASK_START_TIME_MILLIS);
    }

    /**
     * 存储设置信息
     *
     * @param context
     * @param useSoundEffect
     * @param allowNotification
     * @param escapeTime
     * @param sleepTime
     */
    public static void storeSettings(Context context, boolean useSoundEffect,
                                     boolean allowNotification,
                                     int escapeTime, String sleepTime) {
        SharedPreferences setting = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        editor.putBoolean(PREFS_KEY_SETTING_SOUND_EFFECT, useSoundEffect);
        editor.putBoolean(PREFS_KEY_SETTING_NOTIFICATION_BGD, allowNotification);
        editor.putInt(PREFS_KEY_SETTING_ESCAPE_TIME, escapeTime);
        editor.putString(PREFS_KEY_SETTING_SLEEP_TIME, sleepTime);

        editor.commit();

    }

    /**
     * 获取预设可离开时间
     *
     * @param context
     * @return
     */
    public static int getSupposedEscapeTime(Context context) {
        SharedPreferences setting = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_SETTING, Context.MODE_PRIVATE);
        return setting.getInt(PREFS_KEY_SETTING_ESCAPE_TIME, 0);
    }

    /**
     * 更新密码
     *
     * @param context
     * @param mPasswordStr
     */
    public static void updatePassword(Context context, String mPasswordStr) {
        SharedPreferences setting = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        editor.putString(PREFS_KEY_USER_PASSWORD, mPasswordStr);
        editor.commit();
    }

    /**
     * 获取是否是第一次使用APP
     *
     * @param context
     * @return
     */
    public static boolean isFirstUse(Context context) {
        SharedPreferences install = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_INSTALL, Context.MODE_PRIVATE);
        return install.getBoolean(PREFS_KEY_INSTALL_FIRST, true);
    }

    /**
     * 设置是否第一次使用APP
     *
     * @param context
     * @param isFirstUse
     */
    public static void setFirstUse(Context context, boolean isFirstUse) {
        SharedPreferences install = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_INSTALL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = install.edit();
        editor.putBoolean(PREFS_KEY_INSTALL_FIRST, isFirstUse);
        editor.commit();
    }


    /**
     * 抽屉图标是否刷新
     *
     * @param context
     * @return
     */
    public static boolean hasDrawerRefresh(Context context) {
        SharedPreferences drawer = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_DRAWER_NOTIFY, Context.MODE_PRIVATE);
        return drawer.getBoolean(PREFS_KEY_DRAWER_NOTIFY, false);

    }

    /**
     * 设置抽屉图标是否刷新
     *
     * @param context
     * @param hasRefresh
     */
    public static void setDrawerRefresh(Context context, boolean hasRefresh) {
        SharedPreferences drawer = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_DRAWER_NOTIFY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = drawer.edit();
        editor.putBoolean(PREFS_KEY_DRAWER_NOTIFY, hasRefresh);
        editor.commit();
    }
}
