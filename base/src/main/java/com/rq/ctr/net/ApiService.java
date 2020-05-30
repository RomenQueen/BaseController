package com.rq.ctr.net;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.*;

import static com.rq.ctr.net.RetrofitUtils.baseUrl;

/**
 * @author
 * @date 2018/8/3
 * @description
 */
public interface ApiService {


    //只简单判断是否成功，使用此方法。
    @FormUrlEncoded
    @POST(baseUrl)
    Observable<ResponseBody> post(@Path("transCode") String path, @FieldMap Map<String, String> maps);

    @FormUrlEncoded
    @POST("/api/v1/token/app")
    Observable<ResponseBody> init(@FieldMap Map<String, String> maps);

    @Multipart
    @POST(baseUrl)
    Observable<ResponseBody> uploadFile(
            @Path("transCode") String path,@Part List<MultipartBody.Part> partList);

    //只简单判断是否成功，使用此方法。
    @GET(baseUrl)
    Observable<ResponseBody> get(@Path("transCode") String path, @QueryMap Map<String, String> maps, @HeaderMap Map<String, String> headers);

    /**
     * @Streaming 注解是为了避免将整个文件读进内存，这是在下载大文件时需要注意的地方。在请求头添加Range就可以实现服务器文件的下载内容范围了。
     */
    @Streaming
    @GET
    //range下载参数，传下载区间使用
    //url 下载链接
    Observable<ResponseBody> executeDownload(@Header("Range") String range, @Url() String url);

}
