package com.hzaz.base.impl_part;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OnClick {
    /**
     * 继承 BaseControllerImpl 并实现 View。OnClickListener 即可
     *
     * @OnClick{R.id.abc,R.cde} public void OnClick(View v){...}
     */
    int[] value();
}
