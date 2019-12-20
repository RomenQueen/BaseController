package com.rq.demo.ui;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;

import com.hzaz.base.BaseController;
import com.hzaz.base.common_util.LOG;
import com.rq.demo.R;

/**
 * 数据传递演示
 * DataPassExampleCon  (E1)  ->  DataPassExampleCon2 (E2)
 * E1 在 onResultOK 、 onResultOther 中处理回调结果
 * E2 可以根据 getOpenCode() 判断意图 分别以 finish**返回结果
 * 可以多传多
 */
public class DataPassExampleCon extends BaseController {
    @Override
    public int getLayoutId() {
        return R.layout.activity_pass1;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        setOnClickListener(R.id.click1, R.id.click2);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.click1) {
            openFor(DataPassExampleCon2.class);
        } else {
            openWith(DataPassExampleCon2.class, REQUEST_CODE);
        }
    }

    public static final int REQUEST_CODE = 1001;

    @Override
    protected boolean onResultOK(Intent data) {
        setData2View(R.id.show, getPass(0, data));
        return true;
    }

    @Override
    protected boolean onResultOther(int requestCode, int resultCode, @Nullable Intent data) {
        LOG.e("DataPassExampleCon", "requestCode = " + requestCode + " ，resultCode = " + resultCode);
        setData2View(R.id.show2_tip, getInput(R.id.show2_tip) + " " + resultCode);
        setData2View(R.id.show2, getInput(R.id.show2) + " " + getPass(0, data));
        return true;
    }
}
