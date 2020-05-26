package com.rq.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hzaz.base.impl_part.BCImplBinder;
import com.hzaz.base.BaseController;
import com.hzaz.base.ControllerProxy;

public class ExampleBaseActivity extends Activity {

    public static void start(BaseController controller, Class example1Class) {
        Intent intent = new Intent(controller.getContextActivity(), ExampleBaseActivity.class);
        intent.putExtra("clazz", example1Class);
        controller.getContextActivity().startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Class clazz = (Class) getIntent().getSerializableExtra("clazz");
        ControllerProxy proxy = BCImplBinder.bind(this, clazz);
        setContentView(proxy.getLayoutView());
        proxy.initView();
    }
}
