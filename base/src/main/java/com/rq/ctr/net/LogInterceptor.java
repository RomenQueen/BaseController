package com.rq.ctr.net;

import android.util.Log;

import com.rq.ctr.common_util.LOG;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by raoqian on 2019/6/23.
 */

public class LogInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        LOG.e("LogInterceptor", "####################### LogInterceptor #######################");
        Request request = chain.request();
        long startTime = System.currentTimeMillis();
        Response response = chain.proceed(chain.request());
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        okhttp3.MediaType mediaType = response.body().contentType();
        String content = response.body().string();
        String TAG = "Param";
        String url = request.url().toString();
        TAG = "PARAM";
        try {
            TAG = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
        } catch (Exception e) {
            LOG.e("LogInterceptor", "intercept.url:" + url);
        }
        Log.d(TAG, "\n");
        Log.d(TAG, "----------------Start----------------");
        Log.d(TAG, "url = " + request.url());
        String method = request.method();
        if ("POST".equals(method)) {
            StringBuilder sb = new StringBuilder();
            if (request.body() instanceof FormBody) {
                FormBody body = (FormBody) request.body();
                if (body != null) {
                    for (int i = 0; i < body.size(); i++) {
                        sb.append(body.name(i) + ":" + body.value(i) + "<,");
                    }
                }
                int start = Math.max(0, sb.length() - 1);
                sb.delete(start, sb.length());
                LOG.bean(TAG + ".POST", sb.toString(), url);
            }
            LOG.bean(TAG, content);
        } else {
            LOG.e("GET", "------" + url + "------");
            LOG.bean("GET", content);
        }
        Log.d(TAG, "----------End:" + duration + "毫秒----------");
        return response.newBuilder()
                .body(okhttp3.ResponseBody.create(mediaType, content))
                .build();
    }
}