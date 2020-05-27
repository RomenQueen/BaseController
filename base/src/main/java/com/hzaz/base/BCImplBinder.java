package com.hzaz.base;

import android.app.Activity;
import android.app.Fragment;

public class BCImplBinder {

    public static ControllerProxy bind(Activity context, Class impl) {
        ControllerProxy proxy = new ControllerProxy(context, impl);
        return proxy;
    }

    public static ControllerProxy bind(Fragment context, Class impl) {
        ControllerProxy proxy = new ControllerProxy(context, impl);
        return proxy;
    }
}
