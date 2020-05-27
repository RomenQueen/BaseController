package com.rq.demo;

import android.content.Intent;
import android.view.View;

import com.hzaz.base.BaseController;


public class GuideController extends BaseController {
    @Override
    public int getLayoutId() {
        return R.layout.activity_example;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        setData2View(R.id.show, System.currentTimeMillis() + " -> " + this.getClass().getSimpleName());
        setOnClickListener(R.id.btn_1, R.id.btn_2, R.id.btn_3);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btn_1) {
            open(Example1.class);
        } else if (v.getId() == R.id.btn_2) {
            ImplBaseActivity.start(this, Example1Impl.class);
        } else if (v.getId() == R.id.btn_3) {
            getContextActivity().startActivity(new Intent(getContextActivity(), ExampleActivity.class));
        }
    }
}
