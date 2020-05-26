package com.hzaz.base.impl_part;

import android.content.Context;

import com.hzaz.base.ControllerProxy;

public class BCImplBinder {

    public static ControllerProxy bind(Context context, Class impl){
        ControllerProxy proxy = new ControllerProxy(context, impl);
        return proxy;
    }
}
