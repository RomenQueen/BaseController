package com.rq.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hzaz.base.BaseController;
import com.hzaz.base.ControllerProxy;
import com.hzaz.base.BCImplBinder;

public class ImplBaseActivity extends Activity {

    public static void start(BaseController controller, Class example1Class) {
        Intent intent = new Intent(controller.getContextActivity(), ImplBaseActivity.class);
        intent.putExtra("clazz", example1Class);
        controller.getContextActivity().startActivity(intent);
    }

    ControllerProxy proxy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Class clazz = (Class) getIntent().getSerializableExtra("clazz");
        proxy = BCImplBinder.bind(this, clazz);
        setContentView(proxy.getLayoutView());
        proxy.initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        proxy.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        proxy.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        proxy.onDestroy();
    }
}
