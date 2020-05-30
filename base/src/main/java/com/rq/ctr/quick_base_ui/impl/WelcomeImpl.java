package com.rq.ctr.quick_base_ui.impl;

import com.rq.ctr.controller_part.BaseController;
import com.rq.ctr.net.BaseBean;
import com.rq.ctr.net.RequestType;

import java.io.Serializable;

public interface WelcomeImpl extends Serializable {

    String getUpdatePath();

    /**
     * -1 不自动跳转
     *
     * @return
     */
    long getAutoSkipTime();

    /**
     * @return the application's name,will append ".apk" automatically
     */
    String getVname();

    /**
     * 弹窗内容
     *
     * @return the dialog message to show which updates
     */
    String getVersionDescribe();

    /**
     * @return 是否强制更新
     */
    boolean isCancelAble();

    /**
     * Whether to check for updates
     * if true ,the next param can't be null:
     *
     * @param type
     * @param data 为空 接口返回错误
     * @return
     * @see #isCancelAble()
     * @see #getUpdatePath()
     * @see #getVersionDescribe()
     * Of course ,you won't get Network info if you haven't used some medth
     * in {@link com.rq.ctr.net.HttpParamUtil}
     */
    boolean is(RequestType type, BaseBean data);

    void skip(BaseController view);

    void init(BaseController view);

    int getLayoutId();
}
