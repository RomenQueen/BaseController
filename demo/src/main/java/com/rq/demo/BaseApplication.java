package com.rq.demo;

import android.app.Application;

import com.rq.ctr.BASE;
import com.rq.demo.net.Constants;
import com.rq.demo.quick_ui.WelcomePage;

import java.util.HashMap;
import java.util.Map;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BASE.init(this, "demo");
//        ImageLoadUtil.init(new GlideProxy());  网络图片加载框架，可自行切换，默认Glide
        Map<String, String> head = new HashMap<>();// TODO: 2019/12/11 测试 添加公共头
        head.put("token", "123321");
        BASE.setHead(head);
        // TODO: 2019/12/11 下面写入项目地址和后续方法
        BASE.setBaseUrl(Constants.baseUrl);
        BASE.setQuickUi(WelcomePage.class);
    }
}
