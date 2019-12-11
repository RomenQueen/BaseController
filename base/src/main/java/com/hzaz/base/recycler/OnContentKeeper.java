package com.hzaz.base.recycler;

import android.support.annotation.IdRes;
import android.support.annotation.Nullable;

/**
 * Created by rq on 2018/9/28
 */

public interface OnContentKeeper {

    /**
     * @param saveContentId 视图ID
     * @return 需要保存的数据，edg: EditText -> String   CheckBox -> boolean
     */
    Object getSave(@IdRes int saveContentId);

    /**
     * @param save          保存的数据，可能为空
     * @param saveContentId 被保存数据的视图ID
     */
    void onRelease(@Nullable Object save, @IdRes int saveContentId);

    /**
     * @return 需要保存数据的视图ID
     */
    int[] getSaveViewId();
}
