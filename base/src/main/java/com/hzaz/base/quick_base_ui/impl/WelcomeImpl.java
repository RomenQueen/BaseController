package com.hzaz.base.quick_base_ui.impl;

import com.hzaz.base.BaseController;
import com.hzaz.base.net.BaseBean;
import com.hzaz.base.net.RequestType;

import java.io.Serializable;

public interface WelcomeImpl extends Serializable {

    String getUpdatePath();

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
     * @param data
     * @return
     * @see #isCancelAble()
     * @see #getUpdatePath()
     * @see #getVersionDescribe()
     * Of course ,you won't get Network info if you haven't used some medth
     * in {@link com.hzaz.base.net.HttpParamUtil}
     */
    boolean is(RequestType type, BaseBean data);

    void skip(BaseController view);

    void init(BaseController view);

    int getLayoutId();
}
