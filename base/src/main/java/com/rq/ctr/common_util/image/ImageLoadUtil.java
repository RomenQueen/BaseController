package com.rq.ctr.common_util.image;

import android.content.Context;
import android.widget.ImageView;

/**
 * 图片加载 先用工具类形式写 之后再决定具体用哪种方案
 */
public class ImageLoadUtil {

    public @interface ImageType {
        int HEAD = 0x001;//头像
        int BORDER = 0x002;//边框
    }

    private final static int DEFAULT_WIDTH = 320;
    private final static int DEFAULT_HEIGHT = 320;

    private static ImageLoadProxyImpl mImageLoadProxyImpl = new GlideProxy();

    public static void init(ImageLoadProxyImpl imp) {
        mImageLoadProxyImpl = imp;
    }

    public static void display(Context context, String path, ImageView view) {
        display(context, path, 0, view, DEFAULT_WIDTH, DEFAULT_HEIGHT, 0);
    }

    public static void display(Context context, String path, ImageView view, int errRes) {
        display(context, path, 0, view, DEFAULT_WIDTH, DEFAULT_HEIGHT, errRes);
    }

    public static void displayHead(final Context context, String path, ImageView view, int errRes) {
        mImageLoadProxyImpl.display(context, path, view, 0, errRes, 0, 0, ImageType.HEAD);
    }

    public static void displayBorder(final Context context, String path, ImageView view, int errRes) {
        mImageLoadProxyImpl.display(context, path, view, 0, errRes, 0, 0, ImageType.BORDER);
    }

    public static void display(Context context, String path, int defautShowRes, ImageView view, int width, int height, int errRes) {
        mImageLoadProxyImpl.display(context, path, view, defautShowRes, errRes, width, height, 0);
    }

    public static void display(Context context, String path, int defautShowRes, ImageView view, int width, int height, int errRes, @ImageLoadUtil.ImageType int moreTag) {
        mImageLoadProxyImpl.display(context, path, view, defautShowRes, errRes, width, height, moreTag);
    }

}
