package com.hzaz.base.impl_part;

import android.view.View;

public abstract class BaseControllerImpl {
    ViewController mViewController;

    public abstract int getLayoutId();

    public boolean needOutScroll() {
        return false;
    }

    public abstract void initView();

    protected final void setData2View(int viewId, Object object) {
        mViewController.setView2Data(viewId, object);
    }

    public boolean fillCustomViewData(View view, Object obj) {
        return false;
    }

    public final void setViewController(ViewController controllerProxy) {
        this.mViewController = controllerProxy;
    }

    public final void runOnUiThread(Runnable action) {
        mViewController.post(action, 0);
    }

    public final void runOnUiThread(Runnable action, long delayMillis) {
        mViewController.post(action, delayMillis);
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onDestroy() {

    }
}
