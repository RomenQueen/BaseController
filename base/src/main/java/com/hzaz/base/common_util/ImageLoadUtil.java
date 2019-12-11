package com.hzaz.base.common_util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.hzaz.base.App;
import com.hzaz.base.BASE;
import com.hzaz.base.common_util.glide.GlideCircleTransform;

/**
 * 图片加载 先用工具类形式写 之后再决定具体用哪种方案
 */
public class ImageLoadUtil {
    private final static int DEFAULT_WIDTH = 320;
    private final static int DEFAULT_HEIGHT = 320;
    private static Activity contextActivity;

    public static void display(String path, ImageView view) {
        display(ImageLoadUtil.contextActivity, path, 0, view, DEFAULT_WIDTH, DEFAULT_HEIGHT, 0);
    }

    public static void display(Context context, String path, ImageView view) {
        display(context, path, 0, view, DEFAULT_WIDTH, DEFAULT_HEIGHT, 0);
    }

    public static void display(Context context, String path, ImageView view, int errRes) {
        display(context, path, 0, view, DEFAULT_WIDTH, DEFAULT_HEIGHT, errRes);
    }


    public static void display(String path, ImageView view, int resId) {
        display(ImageLoadUtil.contextActivity, path, 0, view, DEFAULT_WIDTH, DEFAULT_HEIGHT, resId);
    }

    public static void displayHead(final Context context, String path, ImageView view, int errRes) {
        RequestOptions options = new RequestOptions()
                .error(errRes)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(errRes)
                .transforms(new GlideCircleTransform());

        if (!TextUtils.isEmpty(path)) {
            Glide.with(context)
                    .load(path)
                    .apply(options)
                    .error(errRes)
                    .centerCrop()
                    .into(view);
        }
    }


    public static void display(Context context, String path, int defautShowRes, ImageView view, int width, int height, int resId) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        if (!TextUtils.isEmpty(path)) {
            Glide.with(context)
                    .load(path)
                    .apply(options)
                    .error(resId)
                    .centerCrop()
                    .into(view);
        }
    }

    public static void showImgCode(ImageView view) {
        String url = App.getBaseUrl() + "/abc/jbtkc/code.do";
        LOG.e("ImageLoadUtil", "showImgCode.url:" + url);
        String sId = BASE.getSessionId();
        GlideUrl mGlideUrl = new GlideUrl(url, new LazyHeaders.Builder().addHeader("cookie", "JSESSIONID=" + sId).build());
        Glide.with(BASE.getCxt())
                .load(mGlideUrl)
                .centerCrop()
                .skipMemoryCache(true) // 不使用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                .into(view);
    }

    public static void setActivity(Activity activity) {
        if (activity != null)
            ImageLoadUtil.contextActivity = activity;
    }
//
//    /**
//     * 加载圆形图片  网络
//     *
//     * @param context
//     * @param v
//     * @param url
//     */
//    public static void loadCircleImg(Context context, ImageView v, String url) {
//        try {
//            if (v.getTag(R.id.tag_key) != null) {
//                String urlSave = (String) v.getTag(R.id.tag_key);
//                if (TextUtils.equals(urlSave, url)) {//避免图片重复加载
//                    return;
//                }
//            }
//            if (!AppUtils.isUiThread()) return;
//            v.setTag(R.id.tag_key, url);
//            RequestOptions options = new RequestOptions()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)   //在load()和into()之间，可以串连添加各种功能。
//                    .transform(new GlideCircleTransform(v.getContextActivity()));
//            Glide.with(context)
//                    .load(url)
//                    .apply(options)
//                    .error(R.mipmap.ic_launcher)
//                    .into(v);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
