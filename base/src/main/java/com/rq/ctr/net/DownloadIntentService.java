package com.rq.ctr.net;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;

import com.rq.ctr.BASE;
import com.rq.ctr.common_util.LOG;

import java.io.File;

/**
 * 下载服务
 * 1.继承IntentService 是因为读写文件比较耗时，且还要进行下载，所以用另开子线程的Service
 * 1.启动apk下载
 * 2.创建通知栏显示下载进度
 */
public class DownloadIntentService extends IntentService {

    public static Intent getTask(Context context, String url, String saveName) {
        Intent intent = new Intent(context, DownloadIntentService.class);
        intent.putExtra("download_url", url);
        intent.putExtra("apk_id", saveName);
        return intent;
    }

    private static final String TAG = "DownloadIntentService";
    public static String DOWNLOAD_DIR;  //下载目录
    private static DownloadListener mDownloadCallBack;
    private NotificationManager mNotifyManager;
    private int downloadId = 101;
    private String mDownloadFileName;

    public DownloadIntentService() {
        super("DownloadService");
        DOWNLOAD_DIR = BASE.getBaseDir() + "/download/";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDownloadCallBack = null;
    }

    public static void setDownloadCallBack(DownloadListener lis) {
        mDownloadCallBack = lis;
    }

    Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null || intent.getExtras() == null) return;
        //下载地址
        String downloadUrl = intent.getExtras().getString("download_url");
        String vNumber = intent.getExtras().getString("apk_id");
        //文件名
        mDownloadFileName = "jbt-v" + vNumber + ".apk";//intent.getExtras().getString("download_file");
        LOG.d(TAG, "下载地址：" + downloadUrl);
        LOG.d(TAG, "文件名" + mDownloadFileName);
        if (!new File(DownloadIntentService.DOWNLOAD_DIR).exists()) {
            new File(DownloadIntentService.DOWNLOAD_DIR).mkdirs();
        }
        //1.判断文件是否存在，具提的下载进度，如果已下载完成，则执行安装
        final File file = new File(DownloadIntentService.DOWNLOAD_DIR + mDownloadFileName);
        long range = 0;
        int progress = 0;
        if (file.exists()) {
            //文件存在，找到文件已下载多少KB，计算下载进度
            //用路径作为key不妥，后台可能设置各个版本下载链接是一样的。所以用apk版本名合适一些
            range = SPDownloadUtil.getInstance().get(mDownloadFileName, 0);
            progress = (int) (range * 100 / Math.max(1, file.length()));
            //判断是否已下载完整
            if (range == file.length()) {
                if (mDownloadCallBack != null) {
                    mDownloadCallBack.onProgress(progress);
                    if (progress == 100) {
                        mDownloadCallBack.onCompleted(file);
                    }
                }
                return;
            }
        }
        final Notification.Builder builder = new Notification.Builder(this);
        builder
//                .setSmallIcon(R.mipmap.logo)
                .setTicker("正在下载")//设置点击后取消Notification
                .setContentTitle("正在下载")//设置点击后取消Notification
                .setProgress(100, progress, false);
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyManager.notify(downloadId, builder.build());
        //3.下载apk
        DownloadRetrofitUtils.getInstance().downloadFile(this, range, downloadUrl, mDownloadFileName, new DownloadCallBack() {
            @Override
            public void onProgress(final int progress) {
                LOG.e("DownloadIntentService", "已下载" + progress + "%");
                builder.setContentTitle("已下载" + progress + "%")
                        .setProgress(100, progress, false);
                mNotifyManager.notify(downloadId, builder.build());
                if (mDownloadCallBack != null) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mDownloadCallBack.onProgress(progress);
                        }
                    });
                }
            }

            @Override
            public void onCompleted() {
                LOG.d(TAG, "下载完成");
                mNotifyManager.cancel(downloadId);
                if (mDownloadCallBack != null) {
                    mDownloadCallBack.onCompleted(file);
                }
            }

            @Override
            public void onError(String msg) {
                if (mDownloadCallBack != null) {
                    mDownloadCallBack.onError();
                }
                LOG.d(TAG, "下载发生错误--" + msg);
                mNotifyManager.cancel(downloadId);
            }
        });
    }


    //    public static String FileProviderName = BuildConfig.APPLICATION_ID + ".fileProvider";//provider
//<provider
//    android:name="android.support.v4.content.FileProvider"
//    android:authorities="${applicationId}.fileProvider"
//    android:exported="false"
//    android:grantUriPermissions="true">
//            <meta-data
//    android:name="android.support.FILE_PROVIDER_PATHS"
//    android:resource="@xml/provider_paths" />
//        </provider>
    public static void installApp(Context context, File file, String FileProviderName) {
        LOG.e("DownloadIntentService", "file == null->" + (file == null));
        if (file == null) return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, FileProviderName, file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public interface DownloadListener {
        void onProgress(int progress);

        void onCompleted(File file);

        void onError();
    }

}