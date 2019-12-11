package com.rq.demo;

import android.app.Application;

import com.hzaz.base.BASE;
import com.rq.demo.quick_ui.WelcomePage;

import java.util.HashMap;
import java.util.Map;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BASE.init(this, "demo");
        Map<String, String> head = new HashMap<>();
        head.put("token", "123321");
        BASE.setHead(head);
//        BASE.setBaseUrl(Constants.host, Constants.baseUrl);
        BASE.setQuickUi(WelcomePage.class);
    }
}
