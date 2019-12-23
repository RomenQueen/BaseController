package com.rq.demo.ui;

import android.view.View;

import com.hzaz.base.BaseController;
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
        setOnClickListener(R.id.back);
        setData2View(R.id.input2, getRequestCode() > 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
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
