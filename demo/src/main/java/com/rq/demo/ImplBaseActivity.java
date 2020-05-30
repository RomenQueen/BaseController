package com.rq.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.rq.ctr.impl_part.BCImplBinder;
import com.rq.ctr.impl_part.ControllerProxy;

public class ImplBaseActivity extends Activity {

    ControllerProxy proxy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Class clazz = (Class) getIntent().getSerializableExtra("clazz");
        proxy = BCImplBinder.bind(this, clazz);
        setContentView(proxy.getLayoutView(R.layout.activity_ex_diff));
        proxy.initView();
    }
}
