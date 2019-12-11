package com.hzaz.base.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hzaz.base.R;
import com.hzaz.base.common_util.LOG;


/**
 * android.app.AlertDialog
 */
public class CommonDialog extends AlertDialog implements View.OnClickListener {
    OnConfirmClickListener mOnConfirmClickListener;
    Object clickData;
    private TextView cancel;
    private TextView confirm;
    private View vLine;

    public CommonDialog(@NonNull Context activity) {
        super(activity);
        init(activity, null);
    }

    public CommonDialog(@NonNull Context activity, String title, String res) {
        super(activity);
        init(activity, title, res);
    }
//
//    public CommonDialog(@NonNull Context activity, String res, int layout) {
//        super(activity);
//        init(activity, res, layout);
//        autoSize();
//        LOG.e("CommonDialog", "CommonDialog.42:");
//    }

    View view;

    private CommonDialog init(final Context activity, String msg) {
        return init(activity, null, msg, R.layout.dialog_common);
    }

    private CommonDialog init(final Context activity, String title, String msg) {
        return init(activity, title, msg, R.layout.dialog_common);
    }

    private CommonDialog init(final Context activity, String titleStr, String msg, int layout) {
        LOG.e("CommonDialog", "init.48:");
        view = LayoutInflater.from(activity).inflate(layout, null);

        // 服务未开启
        cancel = view.findViewById(R.id.dialog_cancel);
        confirm = view.findViewById(R.id.dialog_confirm);
        vLine = view.findViewById(R.id.dialog_line);
        LOG.e("CommonDialog", "init.52:");
        view.findViewById(R.id.dialog_cancel).setOnClickListener(this);
        view.findViewById(R.id.dialog_confirm).setOnClickListener(this);
        if (view.findViewById(R.id.dialog_dismiss) != null) {
            view.findViewById(R.id.dialog_dismiss).setOnClickListener(this);
        }
        if (!TextUtils.isEmpty(titleStr)) {
            TextView title = view.findViewById(R.id.dialog_title);
            title.setText(Html.fromHtml(titleStr));
        }
        if (!TextUtils.isEmpty(msg)) {
            TextView content = view.findViewById(R.id.dialog_content);
            if (msg.contains("br")) {
                content.setGravity(Gravity.LEFT);
            }
            content.setText(Html.fromHtml(msg));
        }
        return this;
    }

    public void setBtnShow(boolean cancel, boolean confirm) {
        if (this.cancel != null) this.cancel.setVisibility(cancel ? View.VISIBLE : View.GONE);
        if (this.confirm != null) this.confirm.setVisibility(confirm ? View.VISIBLE : View.GONE);
        if (vLine != null) this.vLine.setVisibility(confirm && cancel ? View.VISIBLE : View.GONE);
    }

    public void setClickListener(OnConfirmClickListener listener) {
        this.mOnConfirmClickListener = listener;
    }

    @Override
    public final void show() {
        this.show(null);
    }

    boolean isAutoSize = false;
    boolean cancelAble = false;

    public void autoSize() {
        isAutoSize = true;
    }

    public void show(Object obj) {
        this.clickData = obj;
        super.show();
        setContentView(view);
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
        int width = getWindow().getContext().getResources().getDisplayMetrics().widthPixels;
        int height = getWindow().getContext().getResources().getDisplayMetrics().heightPixels;
        int vWidth = view.getPaddingLeft();
        LOG.e("CommonDialog", vWidth + ".99:" + width);
        if (this.isAutoSize) {
            lp.width = width;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        } else {
            lp.width = (int) (width * 0.6F);
            lp.height = (int) (width * 0.6F);
        }
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
        cancelAble = flag;
    }

    @Override
    public void onClick(View v) {
        if (mOnConfirmClickListener != null) {
            mOnConfirmClickListener.onClickConfirm(v.getId() == R.id.dialog_confirm, clickData);
        }
        if (v.getId() == R.id.dialog_confirm || this.cancelAble) {
            dismiss();
        }
    }

    public interface OnConfirmClickListener<T> {
        // isConfirm 取消按钮
        void onClickConfirm(boolean isConfirm, T data);
    }
}