package com.hzaz.base.impl_part;

import android.view.View;

public interface ActivityImpl {
    enum ViewParam {
        ENABLE           //View.setEnable(true)
        , UNABLE           //View.setEnable(false)
        , CHECKABLE          //View.setCheckable(true)
        , UNCHECKABLE          //View.setCheckable(false)
    }

    void onViewCreated();

    <T extends View> T getView(int viewId);

    boolean fillCustomViewData(View view, Object obj);
}
