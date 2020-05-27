package com.hzaz.base.impl_part;

import android.view.View;

public interface ViewController {

    enum ViewParam {
        ENABLE           //View.setEnable(true)
        , UNABLE           //View.setEnable(false)
        , CHECKABLE          //View.setCheckable(true)
        , UNCHECKABLE          //View.setCheckable(false)
    }

    <T extends View> T getView(int viewId);

    /**
     * 自动判断数据类型填入视图
     *
     * @param viewId 视图 id 值
     * @param obj    <true,false> -> setSelect
     *               View.OnClickListener -> setOnClickListener
     */
    void setView2Data(int viewId, Object obj);

    void onResume();

    void onPause();

    void onDestroy();

    void post(Runnable action, long time);
}
