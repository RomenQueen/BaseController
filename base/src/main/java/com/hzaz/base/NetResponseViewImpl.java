package com.hzaz.base;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.hzaz.base.net.BaseBean;
import com.hzaz.base.net.RequestType;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.io.Serializable;

public interface NetResponseViewImpl {//统一网络回调

    void handleFailResponse(BaseBean baseBean);  //统一处理响应失败

    void showLoading();

    void dismissLoading();

    Activity getContextActivity();

    <T> LifecycleTransformer<T> bindToLifecycle();   //为了让接口可以调用 RxFragmentActivity的bindToLifecycle()方法

    <T extends Serializable> void onResponseSucceed(@NonNull RequestType type, @NonNull T data);//使用 Bean文件 作为回调 的 成功回调 code = 00000

    void onResponseError(@NonNull RequestType type);
}
