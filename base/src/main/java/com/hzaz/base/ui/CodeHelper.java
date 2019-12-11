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
    private OnRunningListener onListener;
    private Context mContext;
    private TextView codeBt;
    private Disposable mDisposable;
    private String tag;

    public void setRunTime(int i) {
        this.defaultTimeSave = i;
    }

    String runStr;
    String completeStr;
    String initStr;

    /**
     * 0 - 运行状态 不可缺省 可使用 %s 做格式化输出
     * 1 - 结束状态 不可缺省
     * 2 - 初始状态 缺省使用-> 1
     */
    public void setRunAndCompleteShow(String... str) {
        if (str == null || str.length < 2) {
            LOG.showUserWhere("setRunAndCompleteShow.error");
            return;
        }
        this.runStr = str[0];
        this.completeStr = str[1];
        if (str.length > 2) {
            this.initStr = str[2];
        } else {
            this.initStr = str[1];
        }
    }

    public String getRunStr(int second) {
        return String.format(runStr, second);
    }

    public String getCompleteStr() {
        return completeStr;
    }

    public String getInitStr() {
        return initStr;
    }

    public CodeHelper(BaseController controller, int id, String tag) {
        this.mContext = controller.getContextActivity();
        this.codeBt = controller.getView(id);
        this.tag = BASE.getUseFrom() + tag;
        LOG.e("CodeHelper", "CodeHelper.90:" + tag);
        onResume();
    }

    public CodeHelper(BaseController controller, int id) {
        this.mContext = controller.getContextActivity();
        this.codeBt = controller.getView(id);
        this.tag = BASE.getUseFrom();
        LOG.e("CodeHelper", "CodeHelper.66:" + tag);
        onResume();
    }

    public void setListener(OnRunningListener codeBt) {
        this.onListener = codeBt;
    }

    /**
     * 开启倒计时
     */
    public void start(final int second, final boolean start) {//start 是否点击调用
        //点击后，按钮不可点
        mDisposable = Flowable.intervalRange(0, second, 0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {
                        codeBt.setText(getRunStr((int) (second - aLong - 1)));
                        if (codeBt.isEnabled()) {
                            codeBt.setEnabled(false);
                            if (onListener != null) {
                                if (start) {
                                    onListener.onRunningStart();
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
                        codeBt.setText(getCompleteStr());
                        if (onListener != null) {
                            onListener.onRunningComplete();
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
        SPUtil.setLong(mContext, "remain." + tag, 0L);
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        codeBt.setEnabled(true);
        codeBt.setText(getInitStr());
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

    /**
     * 运行监听若未设置的话可以在 View的点击事件中处理
     */
    public interface OnRunningListener {
        void onRunningStart();

        void onRunningComplete();
    }
}
