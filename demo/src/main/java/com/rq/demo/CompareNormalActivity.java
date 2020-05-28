package com.rq.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.hzaz.base.impl_part.ActivityImpl;
import com.hzaz.base.impl_part.BCImplBinder;
import com.hzaz.base.impl_part.ControllerProxy;
import com.hzaz.base.impl_part.OnClick;
import com.hzaz.base.impl_part.OnRefuseAndLoad;
import com.hzaz.base.impl_part.OnRefuseAndLoadListener;

public class CompareNormalActivity extends Activity implements ActivityImpl, View.OnClickListener, OnRefuseAndLoadListener {

    ControllerProxy proxy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        proxy = BCImplBinder.bind(this, this);
        setContentView(proxy.getLayoutView(R.layout.activity_ex_diff));
        proxy.initView();
    }

    @Override
    public void onViewCreated() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("CompareNormalActivity", "onResume:36");
        finish();
    }

    @Override
    public <T extends View> T getView(int viewId) {
        return findViewById(viewId);
    }

    @Override
    public boolean fillCustomViewData(View view, Object obj) {
        return false;
    }


    @OnClick({R.id.tv_show1, R.id.tv_show3, R.id.tv_show4})
    public void onClick(View v) {
        if (v.getId() == R.id.tv_show1) {
            proxy.setData2View(R.id.tv_show1, "CompareNormalActivity-->填充1：" + System.currentTimeMillis());
        } else if (v.getId() == R.id.tv_show2) {
            proxy.setData2View(R.id.tv_show2, "CompareNormalActivity-->填充2：" + System.currentTimeMillis());
        } else if (v.getId() == R.id.tv_show3) {
            proxy.setData2View(R.id.tv_show3, "CompareNormalActivity-->填充3：" + System.currentTimeMillis());
        } else if (v.getId() == R.id.tv_show4) {
            proxy.setData2View(R.id.tv_show4, "CompareNormalActivity-->填充4：" + System.currentTimeMillis());
        }
    }

    @OnRefuseAndLoad(viewId = R.id.refuse_layout)
    public void refuse(final int page) {
        Log.e("CompareNormalActivity", "refuse:" + page);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    proxy.setData2View(R.id.tv_refuse_child, "加载完成：" + page);
                    proxy.setData2View(R.id.refuse_layout, Status.FinishLoad);
                    proxy.setData2View(R.id.refuse_layout, Status.FinishRefuse);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
