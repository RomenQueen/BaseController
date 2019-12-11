package com.hzaz.base.common_util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.hzaz.base.BASE;


public class ToastUtil {
    /**
     * 避免Toast重复显示之前显示的内容
     */
    private static String oldMsg;
    private static Toast toast = null;
    private static long oneTime = 0;
    private static long twoTime = 0;

    public static void show(String text) {
        show(null, text);
    }

    public static void show(int resId) {
        show(null, BASE.getCxt().getString(resId));
    }

    public static void show(final Context context, final String content) {
        showToastAvoidRepeated(context, content);
    }

    public static void show(final Context context, final int content) {
        showToastAvoidRepeated(context, context.getString(content));
    }

    private static Handler mainHandler = new Handler(Looper.getMainLooper());

    private static void showToastAvoidRepeated(Context context, String message) {
        Context ctx = context == null ? BASE.getCxt() : context;
        if (TextUtils.isEmpty(message)) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(ctx, null, Toast.LENGTH_SHORT);
            toast.setText(message);  //先置null，在设值，解决小米带appName问题
            toast.setGravity(Gravity.CENTER, 0, 0);
            showToast();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (message.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    showToast();
                }
            } else {
                oldMsg = message;
                toast.setText(message);
                showToast();
            }
        }
        oneTime = twoTime;
    }

    private static void showToast() {
        if (Looper.myLooper() == Looper.getMainLooper()) {//主线程
            toast.show();
        } else {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    toast.show();
                }
            });
        }
    }

}
