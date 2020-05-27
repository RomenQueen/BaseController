package com.rq.demo;

import android.util.Log;
import android.view.View;

import com.hzaz.base.impl_part.BaseControllerImpl;
import com.hzaz.base.impl_part.OnClick;
import com.hzaz.base.impl_part.OnRefuseAndLoad;
import com.hzaz.base.impl_part.OnRefuseAndLoadListener;

public class Example1Impl extends BaseControllerImpl implements View.OnClickListener, OnRefuseAndLoadListener {

    @Override
    public int getLayoutId() {
        return R.layout.activity_ex_diff;
    }

    @Override
    public void initView() {
//        initTitle();
    }

    @OnClick({R.id.tv_show1, R.id.tv_show3, R.id.tv_show4})
    public void onClick(View v) {
        if (v.getId() == R.id.tv_show1) {
            setData2View(R.id.tv_show1, "Example1Impl-->填充1：" + System.currentTimeMillis());
        } else if (v.getId() == R.id.tv_show2) {
            setData2View(R.id.tv_show2, "Example1Impl-->填充2：" + System.currentTimeMillis());
        } else if (v.getId() == R.id.tv_show3) {
            setData2View(R.id.tv_show3, "Example1Impl-->填充3：" + System.currentTimeMillis());
        } else if (v.getId() == R.id.tv_show4) {
            setData2View(R.id.tv_show4, "Example1Impl-->填充4：" + System.currentTimeMillis());
        }
    }

    @OnRefuseAndLoad(viewId = R.id.refuse_layout)
    public void refuse(final int page) {
        Log.e("Example1Impl", "refuse:" + page);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("Example1Impl", "refuse.over:" + page);
                setData2View(R.id.refuse_layout, Status.FinishLoad);
                setData2View(R.id.refuse_layout, Status.FinishRefuse);
            }
        }, 4000);
    }
}
