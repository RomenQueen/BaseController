package com.hzaz.base.ui;

import android.os.Bundle;

import com.hzaz.base.BaseController;
import com.hzaz.base.BaseFragment;
import com.hzaz.base.common_util.LOG;

import java.io.Serializable;

import static com.hzaz.base.BaseController.TAG_NAME;


public class CommonFragment extends BaseFragment {

    public CommonFragment() {//throws Exception
        String classFrom = new Exception().getStackTrace()[1].getClassName();//调用源 类名
        LOG.e("CommonFragment", "classFrom  = " + classFrom);
        LOG.e("CommonFragment", "CommonFragment.class  = " + CommonFragment.class.getName());
//        if (!CommonFragment.class.getName().equals(classFrom)) {//只能本类中用
//            LOG.utilLog("CommonFragment");
//            throw new Exception("本项目中常规调用请使用 CommonFragment.get()");
//        }
    }

    public static synchronized <C extends BaseController> CommonFragment instance(Class<C> clazz, Serializable... pass) {
        CommonFragment con = null;
        try {
            con = new CommonFragment();
            Bundle bundle = BaseController.getFraArguments(clazz, pass);
            con.setArguments(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }

    @Override
    public BaseController getController(Object o, int i) {
        if (getArguments().getSerializable(TAG_NAME) == null) return null;
        return super.getController((Class) getArguments().getSerializable(TAG_NAME));
    }
}
