package com.hzaz.base.net;


import android.content.Context;

import com.hzaz.base.BASE;
import com.hzaz.base.common_util.LOG;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author
 * @date 2018/8/3
 * @description 文件下载工具类
 * 独立出来设置ApiService，是因为普通的网络请求可能配置了很多的拦截器，这会大大地影响文件的下载响应数据。
 * 也就是说，可能发起请求，在onNext接收到数据可能要耗时很长时间，目前遇到的是接近30s
 */
public class DownloadRetrofitUtils {

    public static DownloadRetrofitUtils instance;
    private ApiService apiService;

    private DownloadRetrofitUtils() {
        //此处把拦截器都去掉了
        OkHttpClient build = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(build)
                .baseUrl(BASE.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public static DownloadRetrofitUtils getInstance() {
        if (instance == null) {
            synchronized (DownloadRetrofitUtils.class) {
                if (instance == null) {
                    instance = new DownloadRetrofitUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 下载文件
     *
     * @param range            已下载了多少kb
     * @param url              下载链接
     * @param fileName         文件名
     * @param downloadCallback 下载回调
     */
    public void downloadFile(final Context context, final long range, final String url, final String fileName, final DownloadCallBack downloadCallback) {
        File file = new File(DownloadIntentService.DOWNLOAD_DIR, fileName);
        String totalLength = "";
        if (file.exists()) {
            totalLength = Long.toString(file.length());
        }
        //指定要下载的范围 bytes=start-end，如果是bytes=0-，则从头下载
        String unDownloadRangStr = "bytes=" + range + "-" + totalLength;
        LOG.e("DownloadIntentService", "未下载范围：" + unDownloadRangStr);

        //2.开始下载，不知为何返回数据要30s左右
        apiService.executeDownload(unDownloadRangStr, url)
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        LOG.e("DownloadIntentService", "读取到下载数据，开始解析");
                        /**
                         * RandomAccessFile支持跳到文件任意位置读写数据，RandomAccessFile对象包含一个记录指针，
                         * 用以标识当前读写处的位置，当程序创建一个新的RandomAccessFile对象时，该对象的文件记录
                         * 指针对于文件头（也就是0处），当读写n个字节后，文件记录指针将会向后移动n个字节。
                         * */
                        RandomAccessFile randomAccessFile = null;
                        InputStream inputStream = null;
                        long total = range;
                        long responseLength = 0;
                        try {
                            responseLength = responseBody.contentLength();  //获取服务器文件大小
                            inputStream = responseBody.byteStream();        //输入流
                            //创建目录
                            File dir = new File(DownloadIntentService.DOWNLOAD_DIR);
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }
                            //创建文件
                            File file = new File(DownloadIntentService.DOWNLOAD_DIR, fileName);
                            //自由读写文件
                            randomAccessFile = new RandomAccessFile(file, "rwd");
                            //预分配多大的文件空间
                            if (range == 0) {
                                randomAccessFile.setLength(responseLength);
                            }
                            //设置指针位置，实现从断点处继续写入文件。
                            randomAccessFile.seek(range);
                            //byte读写
                            byte[] buf = new byte[2048];
                            int len = 0;
                            int progress = 0;
                            int lastProgress = 0;
                            LOG.e("DownloadIntentService", "开始写入文件");
                            while ((len = inputStream.read(buf)) != -1) {
                                randomAccessFile.write(buf, 0, len);
                                total += len;
                                lastProgress = progress;
                                progress = (int) (total * 100 / randomAccessFile.length());
                                if (progress > 0 && progress != lastProgress) {
                                    downloadCallback.onProgress(progress);
                                }
                            }
                            downloadCallback.onCompleted();
                        } catch (Exception e) {
                            LOG.d("DownloadIntentService", e.getMessage());
                            downloadCallback.onError(e.getMessage());
                            e.printStackTrace();
                        } finally {
                            try {
                                //保存当前下载进度
                                SPDownloadUtil.getInstance().save(fileName, total);
                                if (randomAccessFile != null) {
                                    randomAccessFile.close();
                                }
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        downloadCallback.onError(e.toString());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


}
