package com.hzaz.base.ui;

import android.content.Context;
import android.widget.TextView;

import com.hzaz.base.BASE;
import com.hzaz.base.BaseController;
import com.hzaz.base.common_util.LOG;
import com.hzaz.base.common_util.SPUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * TextView  倒计时工具
 */

public class CodeHelper {

    private int defaultTimeSave = 60;//默认倒计时时间（秒）
    private OnGetCodeListener onListener;
    private Context mContext;
    private TextView codeBt;
    private Disposable mDisposable;
    private String tag;

    public void setRunTime(int i) {
        this.defaultTimeSave = i;
    }

    public interface ShowInfo {

        String getResendStr(int second);

        String getDefaultStr();

        int getClickAbleBGRes();

        int getUnClickAbleBGRes();
    }

    private static ShowInfo mShowInfo;
    private ShowInfo defaultShowInfo = new ShowInfo() {
        @Override
        public String getResendStr(int second) {
            return second + " s";
        }

        @Override
        public String getDefaultStr() {
            return "发送验证码";
        }

        @Override
        public int getClickAbleBGRes() {
            return 0;
        }

        @Override
        public int getUnClickAbleBGRes() {
            return 0;
        }
    };

    public void setShowInfo(ShowInfo info) {
        this.defaultShowInfo = info;
    }

    public static void initCodeHelper(ShowInfo info) {
        mShowInfo = info;
    }

    public CodeHelper(BaseController controller, int id, String tag) {
        if (mShowInfo == null) {
            LOG.e("CodeHelper", "you must use init Utils in Application onCreate");
            return;
        }
        this.mContext = controller.getContextActivity();
        this.codeBt = controller.getView(id);
        this.tag = BASE.getUserFrom() + tag;
        LOG.e("CodeHelper", "CodeHelper.54:" + tag);
        onResume();
    }

    public CodeHelper(BaseController controller, int id) {
        if (mShowInfo == null) {
            mShowInfo = defaultShowInfo;
        }
        this.mContext = controller.getContextActivity();
        this.codeBt = controller.getView(id);
        this.tag = BASE.getUserFrom();
        LOG.e("CodeHelper", "CodeHelper.66:" + tag);
        onResume();
    }

    public void setListener(OnGetCodeListener codeBt) {
        this.onListener = codeBt;
    }

    /**
     * 开启倒计时
     */
    public void start(final int second, final boolean start) {//start 是否点击调用
        if (mShowInfo == null) {
            mShowInfo = defaultShowInfo;
        }
        //点击后，按钮不可点
        mDisposable = Flowable.intervalRange(0, second, 0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {
                        codeBt.setText(mShowInfo.getResendStr((int) (second - aLong - 1)));
                        if (codeBt.isEnabled()) {
                            codeBt.setBackgroundResource(mShowInfo.getUnClickAbleBGRes());
                            codeBt.setEnabled(false);
                            if (onListener != null) {
                                if (start) {
                                    onListener.onGetCode();
                                }
                            }
                        }
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() {
                        //倒计时完毕置为可点击状态
                        codeBt.setEnabled(true);
                        codeBt.setBackgroundResource(mShowInfo.getClickAbleBGRes());
                        codeBt.setText(mShowInfo.getDefaultStr());
                        if (onListener != null) {
                            onListener.onReleaseCode();
                        }
                    }
                })
                .subscribe();
    }

    /**
     * 开启倒计时
     * 需要本地保存发送时间
     */
    public void start() {
        int time = defaultTimeSave;
        long save = SPUtil.init(mContext).getLong("remain." + tag, 0);
        if (save > System.currentTimeMillis() + 999) {
            time = (int) ((save - System.currentTimeMillis()) / 1000);
        } else {
            SPUtil.setLong(mContext, "remain." + tag, System.currentTimeMillis() + defaultTimeSave * 1000);
        }
        start(time, true);
    }

    /**
     * 开启倒计时
     */
    public void onResume() {
        int time = 0;
        long save = SPUtil.init(mContext).getLong("remain." + tag, 0);
        if (save > System.currentTimeMillis() + 999) {
            time = (int) ((save - System.currentTimeMillis()) / 1000);
        } else {
            return;
        }
        start(time, false);
    }

    /**
     * 请求失败，立即重置状态
     */
    public void reset() {
        if (mShowInfo == null) {
            mShowInfo = defaultShowInfo;
        }
        SPUtil.setLong(mContext, "remain." + tag, 0L);
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        codeBt.setEnabled(true);
        codeBt.setBackgroundResource(mShowInfo.getClickAbleBGRes());
        codeBt.setText(mShowInfo.getDefaultStr());
        //  codeBt.setBackgroundResource(R.drawable.bg_bt_code);
    }

    /**
     * 界面销毁时 释放
     */
    public void stop() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    public interface OnGetCodeListener {
        void onGetCode();

        void onReleaseCode();
    }
}
