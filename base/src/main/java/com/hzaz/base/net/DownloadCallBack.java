package com.hzaz.base.net;

public interface DownloadCallBack {

    void onProgress(int progress);

    void onCompleted();

    void onError(String msg);

}
