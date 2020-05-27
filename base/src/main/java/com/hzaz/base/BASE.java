package com.hzaz.base;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.hzaz.base.common_util.LOG;
import com.hzaz.base.quick_base_ui.impl.WelcomeImpl;
import com.tencent.smtt.sdk.QbSdk;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class BASE {
    static boolean useCommonLayout;
    static int commonLayoutId;
    static int statusColorId;
    private static Context ctx;
    private static String mSessionId;
    private static int mNetErrorToast;
    private static String ROOT_DIR;
    private static String BASE_DIR;
    public static int PAGE_SIZE_START = 1;

    private static String url = "";
    private static String urlAddress = "";

    private static Map<String, String> headMap = new HashMap<>();

    public static void setHead(Map<String, String> headers) {
        headMap.clear();
        headMap.putAll(headers);
    }

    public static Map<String, String> getHead() {
        return headMap;
    }

    public static void setBaseUrl(String baseUrl, String content) {
        BASE.url = baseUrl;
        BASE.urlAddress = content;
    }

    public static String getUrlContent() {
        return urlAddress;
    }

    public static String getBaseUrl() {
        return url;
    }

    public static Context getCxt() {
        return ctx;
    }


    public static String getBaseDir() {
        return BASE_DIR;
    }

    public static void setCommonLayout(int layout, int color) {
        useCommonLayout = true;
        commonLayoutId = layout;
        statusColorId = color;
    }

    public static void setSessionId(String id) {
        mSessionId = id;
    }

    public static String getSessionId() {
        return mSessionId;
    }

    public static String getString(int res) {//字模块方法
        return getCxt().getString(res);
    }

    public static void refuseContext(Context context) {
        ctx = context;
    }

    public static void init(Application context, String baseDirName) {
        ctx = context;
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            ROOT_DIR = System.getenv("EXTERNAL_STORAGE");
        }
        BASE_DIR = ROOT_DIR + "/" + baseDirName;
        refuseBase();
    }

    public static void refuseBase() {
        File base = new File(BASE_DIR);
        if (!base.exists()) {
            base.mkdirs();
            LOG.e("BASE", "refuseBase.66:");
        }
        LOG.e("BASE", "refuseBase.70:");
    }

    public static boolean hasSDPermission() {
        return new File(BASE_DIR).exists();
    }


    public static void setNetErrorToast(int res) {
        mNetErrorToast = res;
    }

    public static int getNetErrorToast() {
        return mNetErrorToast;
    }

    /**
     * @return getUseFrom 所在方法的上层调用处
     */
    public static String getUseFrom() {
        StackTraceElement[] stacks = new Exception().getStackTrace();
        if (stacks != null) {
            try {
                String classname = stacks[2].getFileName(); //获取调用者的类名
                String method_name = stacks[2].getMethodName(); //获取调用者的方法名
                int line = stacks[2].getLineNumber(); //获取调用者的方法名
                return classname + "[" + method_name + "]." + line;
            } catch (Exception e) {
            }
        }
        return "";
    }

    public static boolean isDebug() {
        return BuildConfig.LOG_DEBUG;
    }

    public static boolean isRQ() {//调试版本，跳过某些判断
        return new File(ROOT_DIR + "/rq").exists();
    }

    public static void initQQWeb(Context context) {//    implementation 'com.tencent.tbs.tbssdk:sdk:43697'
        QbSdk.initX5Environment(context, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                LOG.e("WelcomeController", "initQQWeb:136");
                //x5内核初始化完成回调接口，此接口回调并表示已经加载起来了x5，有可能特殊情况下x5内核加载失败，切换到系统内核。
            }

            @Override
            public void onViewInitFinished(boolean b) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.e("WelcomeController", "initQQWeb.加载内核是否成功:" + b);
            }
        });
    }

    private static Class<?> welcomeClazz;

    public static <C extends WelcomeImpl> void setQuickUi(Class<C> welcomeClass) {
        welcomeClazz = welcomeClass;
    }

    public static Class<?> getWelcomeClazz() {
        return welcomeClazz;
    }
}
