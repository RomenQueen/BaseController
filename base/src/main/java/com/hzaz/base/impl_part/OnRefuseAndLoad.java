package com.hzaz.base.impl_part;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OnRefuseAndLoad {

    int viewId() default 0;

    boolean refuseAble() default true;

    boolean loadAble() default true;
}
