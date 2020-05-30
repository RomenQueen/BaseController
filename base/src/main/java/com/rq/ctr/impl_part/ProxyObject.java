package com.rq.ctr.impl_part;

import com.rq.ctr.common_util.LOG;

public class ProxyObject {
    Object mainObject;

    public ProxyObject(Class impl) {
        try {
            mainObject = impl.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public ProxyObject(Object impl) {
        mainObject = impl;
    }

    public boolean is(Class<?> clazz) {
        if (mainObject != null) {
            boolean res = clazz.isAssignableFrom(mainObject.getClass());
            LOG.showUserWhere(clazz.getSimpleName() + "->" + res);
            return res;
        }
        return false;
    }

    public <T> T get(Class<T> c) {
        return (T) mainObject;
    }
}
