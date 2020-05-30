package com.rq.demo.net;

import com.rq.ctr.controller_part.BaseController;
import com.rq.ctr.net.HttpParamUtil;
import com.rq.demo.bean.WeatherBean;

import java.util.HashMap;
import java.util.Map;

public class HttpManager {
    public static final int PAGE_SIZE = 20;

    public static HashMap<String, String> getParam() {
        HashMap<String, String> map = new HashMap<>();
//        String token = AppUtils.getToken();
//        LOG.e("HttpManager", "token:" + token);
//        if (!TextUtils.isEmpty(token)) {
//            map.put("T", token);
//        }
        return map;
    }

    public static final String PAGE_NUM_TAG = "pageNo";

    public static HashMap<String, String> getPageParam(int page) {
        HashMap<String, String> map = getParam();
        map.put(PAGE_NUM_TAG, page + "");
        map.put("pageSize", "" + PAGE_SIZE);
        return map;
    }

    public static void getWeather(BaseController view) {
        Map<String, String> maps = getParam();
        maps.put("city", "北京");
        HttpParamUtil.get(view, Constants.TransCode.code_1, maps, WeatherBean.class);
    }
}
