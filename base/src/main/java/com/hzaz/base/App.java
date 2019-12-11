package com.hzaz.base;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.hzaz.base.common_util.LOG;
import com.tencent.smtt.sdk.QbSdk;

public class App {

    private static String url = "";

    public static void setNetInfo(String baseUrl) {
        if (TextUtils.isEmpty(url)) {
            url = baseUrl;
        }
    }

    public static String getBaseUrl() {
        return url;
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
}
