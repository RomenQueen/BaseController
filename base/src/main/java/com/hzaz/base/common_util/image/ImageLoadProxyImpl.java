package com.hzaz.base.common_util.image;

import android.content.Context;
import android.widget.ImageView;

public interface ImageLoadProxyImpl {
    /**
     * default int -> 0
     */
    void display(Context context, Object obj, ImageView iv, int defaultRes, int errRes, int width, int height, @ImageLoadUtil.ImageType int moreTag);
}
