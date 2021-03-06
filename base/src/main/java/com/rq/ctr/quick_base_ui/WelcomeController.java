package com.rq.ctr.quick_base_ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.rq.ctr.BASE;
import com.rq.ctr.R;
import com.rq.ctr.common_util.AppUtil;
import com.rq.ctr.common_util.LOG;
import com.rq.ctr.common_util.ToastUtil;
import com.rq.ctr.controller_part.BaseController;
import com.rq.ctr.net.BaseBean;
import com.rq.ctr.net.DownloadIntentService;
import com.rq.ctr.net.RequestType;
import com.rq.ctr.quick_base_ui.impl.WelcomeImpl;
import com.rq.ctr.ui.CommonDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;

import java.io.File;
import java.io.Serializable;

/**
 * 开屏页
 */

public class WelcomeController extends BaseController implements CommonDialog.OnConfirmClickListener, DownloadIntentService.DownloadListener {

    WelcomeImpl bean;

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        toNextView();
        bean.init(this);
    }

    @Override
    public int getLayoutId() {
        try {
            this.bean = (WelcomeImpl) BASE.getWelcomeClazz().newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return this.bean.getLayoutId();
    }

    @Override
    public boolean needOutScroll() {
        return false;
    }

    private void toNextView() {
        long time = bean.getAutoSkipTime();
        if (time > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    skip();
                }
            }, time);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                initQQWeb();
            }
        }).start();
    }

    @Override
    public boolean underStatusBar() {
        return true;
    }

    private void initQQWeb() {
//        设置全局的Header构建器
//        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
//            @Override
//            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
//                final ClassicsHeader mClassicsHeader = new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);
//                mClassicsHeader.setProgressResource(R.drawable.refuse);
//                return mClassicsHeader;//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
//            }
//        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20).setSpinnerStyle(SpinnerStyle.Translate);
            }
        });
        BASE.initQQWeb(getContextActivity());
    }

    boolean needUpload = false;

    private void skip() {
        if (this.downProgress > 0) return;
        if (needUpload) return;
        if (!isShow()) {
            return;
        }
        welcomeSkip();
    }

    @Override
    public <T extends Serializable> void onResponseSucceed(@NonNull RequestType type, T data) {
        super.onResponseSucceed(type, data);
        BaseBean d = (BaseBean) data;
        if (bean.is(type, d)) {
            this.needUpload = needStopToDownload();
        }
    }

    @Override
    public void onResponseError(@NonNull RequestType type) {
        super.onResponseError(type);
        bean.is(type, null);
    }

    protected void welcomeSkip() {
        bean.skip(this);
    }

    /**
     * @return 是否需要更新
     */
    private boolean needStopToDownload() {
        if (this.bean == null || TextUtils.isEmpty(bean.getUpdatePath())) {
            return false;
        }
        if (!TextUtils.equals(AppUtil.getVersionName(), bean.getVname())) {
            if (BASE.isRQ()) {
                ToastUtil.show("测试，跳过更新");
                return false;
            }
            CommonDialog dialog = new CommonDialog(getActivity(), "更新提示", bean.getVersionDescribe());//, R.layout.dialog_custom
            dialog.setCancelable(false);
            if (bean.isCancelAble()) {//必须更新
                dialog.setBtnShow(false, true);
            } else {//可选更新
                dialog.setBtnShow(true, true);
            }
            dialog.show(bean);
            dialog.setClickListener(this);
            return true;
        }
        return false;
    }


    private void installApp(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        getActivity().startActivity(intent);
    }

    int downProgress = 0;

    @Override
    public void onClickConfirm(boolean isConfirm, Object data) {
        if (isConfirm) {
            DownloadIntentService.setDownloadCallBack(this);
            Intent intentService = DownloadIntentService.getTask(getContextActivity(),
                    bean.getUpdatePath(), bean.getVname() + ".apk");
            getActivity().startService(intentService);
        } else {
            downProgress = 0;
            skip();
        }
    }


    @Override
    public void onProgress(int progress) {
        LOG.e("WelcomeController", "onProgress:" + progress);
        downProgress = progress;
        setData2View(R.id.welcome_progress_text, "下载进度" + downProgress + "%");
    }

    @Override
    public void onCompleted(File file) {
        downProgress = 100;
        installApp(file);
    }

    @Override
    public void onError() {
        ToastUtil.show("下载异常");
    }
}
