package com.hzaz.base.common_util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.text.TextUtils;

import com.hzaz.base.BASE;

import java.util.List;

public class AppUtil {

    //获取状态栏高度
    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = BASE.getCxt().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = BASE.getCxt().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取当前本地apk的版本
     *
     * @return
     */
    public static int getVersionCode() {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = BASE.getCxt().getPackageManager().getPackageInfo(BASE.getCxt().getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取当前本地apk的版本
     *
     * @return
     */
    public static String getVersionName() {
        String versionCode = "";
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = BASE.getCxt().getPackageManager().getPackageInfo(BASE.getCxt().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 是否是UI线程
     *
     * @return
     */
    public static boolean isUiThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param activity 要判断的Activity
     * @return 是否在前台显示
     */
    public static boolean isForeground(Activity activity) {
        return isForeground(activity, activity.getClass().getName());
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param context   Context
     * @param className 界面的类名
     * @return 是否在前台显示
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className))
            return false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName()))
                return true;
        }
        return false;
    }
}
