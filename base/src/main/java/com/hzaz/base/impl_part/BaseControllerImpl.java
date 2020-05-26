package com.hzaz.base.impl_part;

import android.view.View;

public abstract class BaseControllerImpl implements View.OnClickListener {
    ViewController mViewController;

    public abstract int getLayoutId();

    public boolean needOutScroll() {
        return true;
    }

    public abstract void initView();

    protected final void setData2View(int viewId, Object object) {
        mViewController.setView2Data(viewId, object);
    }

    protected final void setOnClickListener(int ... ids){
        mViewController.setOnClickListener(ids,this);
    }

    public boolean fillCustomViewData(View view, Object obj) {
        return false;
    }

    public final void setViewController(ViewController controllerProxy) {
        this.mViewController = controllerProxy;
    }

    @Override
    public void onClick(View v) {

    }
}
