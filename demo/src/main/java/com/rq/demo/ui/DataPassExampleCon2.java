package com.rq.demo.ui;

import android.view.View;

import com.rq.ctr.controller_part.BaseController;
import com.rq.ctr.impl_part.OnClick;
import com.rq.demo.R;

import static com.rq.demo.ui.DataPassExampleCon.REQUEST_CODE;

/**
 * 数据传递演示
 */
public class DataPassExampleCon2 extends BaseController {
    @Override
    public int getLayoutId() {
        return R.layout.activity_pass2;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        setData2View(R.id.input2, getRequestCode() > 0 ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.back)
    public void onClick(View v) {
        if (getRequestCode() == REQUEST_CODE) {
            String input = getInput(R.id.input);
            int code = Integer.parseInt(getInput(R.id.input2));
            finish(code, input);
            return;
        }
        String input = getInput(R.id.input);
        finishOK(input);
    }
}
