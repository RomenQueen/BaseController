package com.hzaz.base.net;

import android.content.Context;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.hzaz.base.BASE;
import com.hzaz.base.common_util.LOG;
import com.hzaz.base.common_util.SPUtil;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author
 * @date 2018/8/3
 * @description 网络请求工具类
 */
public class RetrofitUtils {

    final static String baseUrl = "/api/v1/{transCode}";
    public static ApiService apiService;

    public static ApiService getApiService(Context context) {
        if (apiService == null) {
            synchronized (RetrofitUtils.class) {
                apiService = initRetrofitConfig(context);
            }
        }
        return apiService;
    }

    private static ApiService initRetrofitConfig(final Context context) {
        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        //配置超时时间以及拦截器
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder().connectTimeout(30, TimeUnit.SECONDS)
                .cookieJar(cookieJar)
                .readTimeout(30, TimeUnit.SECONDS);
        Interceptor errorInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) {
                Request.Builder builder = chain.request().newBuilder();
                String t = SPUtil.getString("d");
                LOG.e("RetrofitUtils", "ADD.token:" + t);
                Response mResponse = null;
                try {
                    mResponse = chain.proceed(builder.header("token", t).build());
                } catch (IOException e) {
                    CrashReport.postCatchedException(e);
                } catch (RuntimeException e) {
                    CrashReport.postCatchedException(e);
                }
                return mResponse;
            }
        };
        builder.addInterceptor(new LogInterceptor())
                .addInterceptor(errorInterceptor);
        final OkHttpClient build = builder.build();

        //配置Retrofit信息
        Retrofit retrofit = new Retrofit.Builder()
                .client(build)
                .baseUrl(BASE.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        return apiService;
    }
}
