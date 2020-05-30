package com.rq.demo.quick_ui;

import com.rq.ctr.common_util.LOG;
import com.rq.ctr.common_util.ToastUtil;
import com.rq.ctr.controller_part.BaseController;
import com.rq.ctr.net.BaseBean;
import com.rq.ctr.net.RequestType;
import com.rq.ctr.quick_base_ui.impl.WelcomeImpl;
import com.rq.ctr.ui.CodeHelper;
import com.rq.demo.R;
import com.rq.demo.bean.WeatherBean;
import com.rq.demo.net.HttpManager;
import com.rq.demo.ui.MainController;

import static com.rq.demo.net.Constants.TransCode.code_1;


public class WelcomePage implements WelcomeImpl {

    @Override
    public int getLayoutId() {
        return R.layout.activity_welcome;
    }

    CodeHelper codeHelper;
    BaseController controller;

    @Override
    public void init(final BaseController view) {
        this.controller = view;
        LOG.e("WelcomePage", "LINE:19");
        view.setData2View(R.id.text, "这只是一个开屏页，正常项目都会用到，所以单独提取到Base" +
                "基类中，实际应用 ：\n" +
                "1.实现 WelcomeImpl 类 方法\n" +
                "2.在Application onCreate 中  调用 BASE.setQuickUi(**.class)\n" +
                "3.注册启动 ACT -> com.hzaz.base.controller_part.WelcomeActivity");
        codeHelper = new CodeHelper(view, R.id.next);
        codeHelper.setRunAndCompleteShow("%s 秒");
        codeHelper.setListener(new CodeHelper.OnRunningListener() {
            @Override
            public void onRunningStart() {
            }

            @Override
            public void onRunningComplete() {
                LOG.e("WelcomePage", "LINE:60");
            }
        });
        codeHelper.setRunTime(4);
        codeHelper.start();
        HttpManager.getWeather(view);
    }


    @Override
    public String getUpdatePath() {
        return uploadPath;
    }

    @Override
    public long getAutoSkipTime() {
        return -1;
    }

    @Override
    public String getVname() {
        return Vname;
    }

    @Override
    public String getVersionDescribe() {
        return VersionDescribe;
    }

    @Override
    public boolean isCancelAble() {
        return UpdateFlag;
    }

    String uploadPath;
    String Vname;
    String VersionDescribe;
    boolean UpdateFlag;

    @Override
    public boolean is(RequestType type, BaseBean data) {
        if (type.is(code_1)) {
            final WeatherBean bean = (WeatherBean) data;
            if (bean == null) {
                ToastUtil.show("接口异常");
                return false;
            }
            controller.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    controller.skip(MainController.class, bean);
                }
            }, 1000);
        }

        return false;
    }

    @Override
    public void skip(BaseController view) {
        view.skip(MainController.class);
    }

}
