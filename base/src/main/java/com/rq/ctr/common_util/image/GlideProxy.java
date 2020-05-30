package com.rq.ctr.common_util.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class GlideProxy implements ImageLoadProxyImpl {

    @Override
    public void display(Context context, Object obj, ImageView iv, int defaultRes, int errRes, int width, int height, @ImageLoadUtil.ImageType int moreTag) {
        Bitmap bit = null;
        Drawable drawable = null;
        String path = null;
        if (obj instanceof Bitmap) {
            bit = (Bitmap) obj;
        } else if (obj instanceof Drawable) {
            drawable = (Drawable) obj;
        } else if (obj instanceof String) {
            path = (String) obj;
        }
        if (bit == null && drawable == null && path == null) {
            Log.e("GlideProxy", "show Object is Null");
            return;
        }
        Log.e("GlideProxy", "path = " + path);
        if (moreTag == ImageLoadUtil.ImageType.HEAD) {
            RequestOptions options = new RequestOptions()
                    .error(errRes)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(errRes)
                    .transforms(new GlideCircleTransform());

            Glide.with(context)
                    .load(path)
                    .apply(options)
                    .error(errRes)
                    .centerCrop()
                    .into(iv);
        } else {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            if (!TextUtils.isEmpty(path)) {
                Glide.with(context)
                        .load(path)
                        .apply(options)
                        .error(errRes)
                        .centerCrop()
                        .into(iv);
            }
        }
    }
}
