package com.rq.demo;

import android.view.View;

import com.rq.ctr.controller_part.BaseController;
import com.rq.ctr.impl_part.OnClick;
import com.rq.ctr.impl_part.OnRefuseAndLoad;
import com.rq.ctr.impl_part.OnRefuseAndLoadListener;

public class CompareController extends BaseController implements OnRefuseAndLoadListener, View.OnClickListener {
    @Override
    public int getLayoutId() {
        return R.layout.activity_ex_diff;
    }


    @OnClick({R.id.tv_show1, R.id.tv_show2, R.id.tv_show3, R.id.tv_show4})
    public void onClick(View v) {
        if (v.getId() == R.id.tv_show1) {
            setData2View(R.id.tv_show1, "CompareController-->填充1：" + System.currentTimeMillis());
        } else if (v.getId() == R.id.tv_show2) {
            setData2View(R.id.tv_show2, "CompareController-->填充2：" + System.currentTimeMillis());
        } else if (v.getId() == R.id.tv_show3) {
            setData2View(R.id.tv_show3, "CompareController-->填充3：" + System.currentTimeMillis());
        } else if (v.getId() == R.id.tv_show4) {
            setData2View(R.id.tv_show4, "CompareController-->填充4：" + System.currentTimeMillis());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        finish();
    }

    @OnRefuseAndLoad(viewId = R.id.refuse_layout)
    public void refuse(final int page) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setData2View(R.id.tv_refuse_child, "加载完成：" + page);
                setData2View(R.id.refuse_layout, Status.FinishLoad);
                setData2View(R.id.refuse_layout, Status.FinishRefuse);
            }
        }, 3000);
    }
}
