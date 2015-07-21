package com.nectar.timeby.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by finalize on 7/19/15.
 */
public class PrefsUtil {
    public static final String PREFS_MAP_USER = "user";
    public static final String PREFS_KEY_USER_NAME = "userName";
    public static final String PREFS_KEY_USER_PHONE = "phoneNum";
    public static final String PREFS_KEY_USER_PASSWORD = "password";

    public static void storeUser(Context context, String userName, String password, String phoneNum) {
        SharedPreferences user = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user.edit();
        editor.putString(PrefsUtil.PREFS_KEY_USER_NAME, userName);
        editor.putString(PrefsUtil.PREFS_KEY_USER_PASSWORD, password);
        editor.putString(PrefsUtil.PREFS_KEY_USER_PHONE, phoneNum);
        editor.commit();
    }

    public static boolean isLogin(Context context) {
        SharedPreferences user = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_USER, Context.MODE_PRIVATE);
        String userName = user.getString(PREFS_KEY_USER_NAME, null);
        if (userName == null) {
            return false;
        }
        return true;
    }

    public static void logout(Context context) {
        SharedPreferences user = context.getSharedPreferences(
                PrefsUtil.PREFS_MAP_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = user.edit();
        editor.putString(PrefsUtil.PREFS_KEY_USER_NAME, null);
        editor.putString(PrefsUtil.PREFS_KEY_USER_PASSWORD, null);
        editor.putString(PrefsUtil.PREFS_KEY_USER_PHONE, null);
        editor.commit();
    }
}
