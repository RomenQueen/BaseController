package com.rq.ctr.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.rq.ctr.BASE;
import com.rq.ctr.NetResponseViewImpl;
import com.rq.ctr.common_util.LOG;
import com.rq.ctr.common_util.SPUtil;
import com.rq.ctr.common_util.ToastUtil;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * backToast 简单接口提示内容<接口成功与失败的提示语  固定位置> 0-成功提示语，1-失败提示语
 */
public class HttpParamUtil {

    private static OnCallBackListener mOnCallBackListener;

    public static void setOnCallBackListener(OnCallBackListener l) {
        mOnCallBackListener = l;
    }

    public interface OnCallBackListener {
        //true-已处理数据
        boolean onSuccess(Object object);

        boolean onFail(RequestType type);
    }


    public static <T extends Serializable> void post(final NetResponseViewImpl view, final String type, @NonNull Map<String, String> maps, Class<T> clazz, String... backToast) {
        RequestType requestType = new RequestType(type, maps);
        dealCommonRequest(RetrofitUtils.getApiService(view.getContextActivity()).post(type, maps), view, requestType, clazz, backToast);
    }

    public static <T extends Serializable> void uploadFile(final NetResponseViewImpl view, final String type, @NonNull Map<String, String> maps, String pathName, String path, Class<T> clazz, String... backToast) {
        view.showLoading();
//        LOG.e("HttpParamUtil", "uploadFile.path:" + path);
        RequestType requestType = new RequestType(type);
        LOG.bean("HttpManager", maps);
////        File file = new File(path); // 你要上传的文件
//// 创建RequestBody，传入参数："multipart/form-data"，File
//
//        Map<String, RequestBody> params = new HashMap<>();
//        for (Map.Entry<String, String> entry : maps.entrySet()) {
//            String mapKey = entry.getKey();
//            String mapValue = entry.getValue();
//            params.put(mapKey, RequestBody.create(MediaType.parse("text/plain"), mapValue));
//        }
//        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
////        // 创建MultipartBody.Part，用于封装文件数据
//        MultipartBody.Part requestImgPart = MultipartBody.Part.createFormData("headimgurl", "headimgurl", fileRequestBody);
//        params.put("headimgurl", fileRequestBody);


        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String, String> entry : maps.entrySet()) {
            String mapKey = entry.getKey();
            String mapValue = entry.getValue();
            builder.addFormDataPart(mapKey, mapValue);
        }
        File file = new File(path);
        RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        builder.addFormDataPart(pathName, file.getName(), imageBody);
        List<MultipartBody.Part> parts = builder.build().parts();

        dealCommonRequest(RetrofitUtils.getApiService(view.getContextActivity()).uploadFile(type, parts), view, requestType, clazz, backToast);
    }

    public static <T extends Serializable> void init(final NetResponseViewImpl view, final String type, @NonNull Map<String, String> maps, Class<T> clazz, String... backToast) {
        RequestType requestType = new RequestType(type, maps);
        dealCommonRequest(RetrofitUtils.getApiService(view.getContextActivity()).init(maps), view, requestType, clazz, backToast);
    }


    public static HashMap<String, String> getParam() {
        HashMap<String, String> map = new HashMap<>();
        String token = SPUtil.getString("d");
        LOG.e("HttpManager", "getParam.token:" + token);
        map.put("token", token);
        return map;
    }

    public static <T extends Serializable> void get(final NetResponseViewImpl view, final String type, @NonNull Map<String, String> param, Class<T> clazz, String... backToast) {
        view.showLoading();
        RequestType requestType = new RequestType(type);
        dealCommonRequest(RetrofitUtils.getApiService(view.getContextActivity()).get(type, param, getParam()), view, requestType, clazz, backToast);
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    private static <T extends Serializable> void dealCommonRequest(Observable<ResponseBody> post, final NetResponseViewImpl view, final RequestType type, Class<T> clazz, String... backToast) {
        LOG.e("HttpParamUtil", "dealCommonRequest.isNetworkConnected:"+isNetworkConnected(view.getContextActivity()));
        if (!isNetworkConnected(view.getContextActivity())) {
            type.setRespMsg("网络连接异常，请检查连接");
            view.onResponseError(type);
            return;
        }
        view.showLoading();
        String successToast = "";
        String errorToast = "";
        if (backToast != null) {
            if (backToast.length > 0) {
                successToast = backToast[0];
            }
            if (backToast.length > 1) {
                errorToast = backToast[1];
            }
        }
        final String finalSuccessToast = successToast;
        final String finalErrorToast = errorToast;
        LOG.e("HttpParamUtil", "dealCommonRequest.73:");
        post.compose(RxUtil.rxSchedulerHelper(view.<ResponseBody>bindToLifecycle()))
                .subscribe(new MvcObserver<T>(clazz, type) {

                    @Override
                    public void onSuccess(T result) {
                        if (mOnCallBackListener != null && mOnCallBackListener.onSuccess(result)) {
                            return;
                        }
                        view.dismissLoading();
                        view.onResponseSucceed(type, result);
                        if (!TextUtils.isEmpty(finalSuccessToast)) {
                            ToastUtil.show(view.getContextActivity(), finalSuccessToast);
                        }
                    }

                    @Override
                    protected void onCommonError(String respCode, String respMsg) {
                        if (!TextUtils.isEmpty(finalErrorToast)) {
                            ToastUtil.show(view.getContextActivity(), finalErrorToast);
                            return;
                        }
                        if (mOnCallBackListener != null && mOnCallBackListener.onFail(type.with(respCode, respMsg))) {
                            return;
                        }
                        view.onResponseError(type.with(respCode, respMsg));
                    }
                });
    }

    public static void commonError(NetResponseViewImpl view, RequestType type) {
        if (type.isNetError() && BASE.getNetErrorToast() > 0) {
            ToastUtil.show(view.getContextActivity(), BASE.getNetErrorToast());
            return;
        }
        ToastUtil.show(view.getContextActivity(), type.getErrorMsg());
        view.dismissLoading();
    }
}
