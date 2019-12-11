package com.hzaz.base.common_util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.hzaz.base.BASE;

/**
 * 小数据手机本地存储
 */

public class SPUtil {
    public static SharedPreferences init(Context context) {
        if (context == null) {
            LOG.e("SPUtil", "ERROR : Line 14");
        }
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp;
    }

    public static void saveString(String key, String value) {
        saveString(BASE.getCxt(), key, value);
    }

    /**
     * 添加数据
     *
     * @param key
     * @param value
     */
    public static void saveString(Context context, String key, String value) {
        SharedPreferences.Editor edit = init(context).edit();
        edit.putString(key, value);
        edit.apply();
    }

    public static void saveInt(Context context, String key, int value) {
        SharedPreferences.Editor edit = init(context).edit();
        edit.putInt(key, value);
        edit.apply();
    }

    public static int getInt(Context context, String key) {
        return init(context).getInt(key, -1);
    }

    public static void setString(Context welcomeActivity, String key, String value) {
        SharedPreferences.Editor edit = init(welcomeActivity).edit();
        edit.putString(key, value);
        edit.apply();
    }

    public static String getString(String key) {
        return init(BASE.getCxt()).getString(key, "");
    }

    public static String getString(Context context, String key) {
        return init(context).getString(key, "");
    }

    public static void setBoolean(Context context, String key, Boolean b) {
        SharedPreferences.Editor edit = init(context).edit();
        edit.putBoolean(key, b);
        edit.apply();
    }

    public static boolean getBoolean(Context context, String key) {
        return init(context).getBoolean(key, false);
    }

    public static void setLong(Context context, String key, Long b) {
        SharedPreferences.Editor edit = init(context).edit();
        edit.putLong(key, b);
        edit.apply();
    }

    public static Long getLong(Context context, String key) {
        return init(context).getLong(key, 0L);
    }

    /**
     * 获取数据
     *
     * @param key
     */
    public static String getSharePreferencesData(Context context, String key) {
        return init(context).getString(key, "");

    }

    /**
     * 删除数据
     *
     * @param key
     */
    public static void deleteSharePreferencesData(Context context, String key) {
        SharedPreferences.Editor edit = init(context).edit();
        edit.putString(key, "");
        edit.apply();
    }

    public static void deleteInt(Context context, String key) {
        SharedPreferences.Editor edit = init(context).edit();
        edit.putInt(key, -1);
        edit.apply();
    }

    /**
     * 是否存在数据
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean isExistData(Context context, String key) {
        if (!TextUtils.isEmpty(getSharePreferencesData(context, key))) {
            return true;
        } else {
            return false;
        }
    }

}
