package com.rq.ctr.impl_part;

import android.app.Activity;
import android.content.Context;

public class BCImplBinder {

    public static ControllerProxy bind(Activity context, Class impl) {
        ProxyObject object = new ProxyObject(impl);
        ControllerProxy proxy = new ControllerProxy(context, object);
        return proxy;
    }

    public static ControllerProxy bind(Context context, Object impl) {
        ProxyObject object = new ProxyObject(impl);
        ControllerProxy proxy = new ControllerProxy(context, object);
        return proxy;
    }

}
