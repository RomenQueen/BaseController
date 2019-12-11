package com.hzaz.base.net;

import android.os.Build;

import com.google.gson.Gson;
import com.hzaz.base.common_util.LOG;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.Serializable;
import java.net.ConnectException;
import java.net.UnknownHostException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * 专用于 MVC 架构的观察者
 * 用以和MyObserver 区别开，避免修改造成不可预知的连锁影响
 * Observer 观察者桌一件事情，将正确数据转化出来然后传递给调用层
 */
public abstract class MvcObserver<T extends Serializable> implements Observer<ResponseBody> {
    private Class<?> clazz;//该参数不能为空，为指定返回数据格式
    private RequestType httpCode;//该参数不能为空，为 接口标识编码

    public MvcObserver(Class<T> clazz, RequestType type) {//没使用MVP架构
        this.clazz = clazz;
        this.httpCode = type;
    }

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        LOG.e("MvcObserver", "onNext.41:");
        String s = null;
        try {
            s = responseBody.string();
            LOG.e("MvcObserver", "onNext.43:" + s);
            T bean = (T) new Gson().fromJson(s, clazz);
            LOG.bean("MvcObserver", bean);
            dealBean(s);
        } catch (Exception e) {
//            e.printStackTrace();
            try {
                onSuccess((T) s);
                return;
            } catch (Exception e1) {
                LOG.e("MvcObserver", "onNext.51:");
            }
            Exception eIn;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                eIn = new Exception(e);
                eIn.addSuppressed(new Throwable());
            } else {
                eIn = new Exception("接口数据 " + httpCode + " 解析异常 : " + s);
            }
            eIn.printStackTrace();
            onCommonError("-1", "数据解析异常，请确认网络连接正常后重试");
        }
    }

    private void dealBean(String s) {
        T bean = (T) new Gson().fromJson(s, clazz);
//        if (post != null && post instanceof BaseBean) {
//            if (!TextUtils.equals(BASE.getSessionId(), post.getSessionId())) {
//                BASE.setSessionId(post.getSessionId());
//            }
//            post.setAll(s);
//            LOG.e("MvcObserver", post.getCode() + "  " + post.getRespMsg());
////            if ("00000".equals(post.getCode())) {
        if (bean == null) {
            bean = (T) new BaseBean();
        }
        if (bean instanceof BaseBean) {
            BaseBean baseBean = (BaseBean) bean;
            if (baseBean.getError_code() != 201 && baseBean.getError_code() != 0) {
                onCommonError("" + baseBean.getError_code(), baseBean.getMsg());
                return;
            }
            baseBean.setAll(s);
        }
        onSuccess(bean);
////            } else if (!TextUtils.isEmpty(post.getCode())) {
////                onCommonError(post.getCode(), post.getRespMsg());
////            }
//        } else {
//            LOG.e("MyObserver", "后台返回数据异常");
//        }
    }

    protected void onCommonError(String respCode, String respMsg) {
        LOG.e("MvcObserver", "onCommonError.75:");
        LOG.e("onCommonError", respCode + "." + respMsg);
//        ToastUtil.show(respMsg);
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof UnknownHostException) {
//            onCommonError("-1", "请求异常，请稍后重试1");
        } else if (e instanceof ConnectException) {
//            onCommonError("-2", "请求异常，请稍后重试2");
        } else if (e instanceof NullPointerException) {
            LOG.e("MvcObserver", "RetrofitUtils 已处理异常");
//            onCommonError("-3", "请求异常，请稍后重试3");
        } else if (clazz != null) {
//            onCommonError("-4", "请求异常，请稍后重试4");
        }
        if (e != null) {
            e.printStackTrace();
            Exception eIn = new Exception("主动上抛.onError：接口数据 解析异常 : " + e.getMessage());
            CrashReport.postCatchedException(eIn);
        } else {
            Exception eIn = new Exception("捕获未知异常");
            CrashReport.postCatchedException(eIn);
        }
    }

    @Override
    public void onComplete() {
    }

    public abstract void onSuccess(T t);
}
