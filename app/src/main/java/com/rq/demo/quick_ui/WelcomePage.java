package com.rq.demo.quick_ui;

import com.hzaz.base.BaseController;
import com.hzaz.base.common_util.LOG;
import com.hzaz.base.net.BaseBean;
import com.hzaz.base.net.RequestType;
import com.hzaz.base.quick_base_ui.impl.WelcomeImpl;
import com.hzaz.base.ui.CodeHelper;
import com.rq.demo.R;


public class WelcomePage implements WelcomeImpl {

    @Override
    public int getLayoutId() {
        return R.layout.activity_welcome;
    }

    CodeHelper codeHelper;

    @Override
    public void init(final BaseController view) {
        LOG.e("WelcomePage", "LINE:19");
        view.setData2View(R.id.text, "这只是一个开屏页，正常项目都会用到，所以单独提取到Base" +
                "基类中，实际应用 ：\n" +
                "1.实现 WelcomeImpl 类 方法\n" +
                "2.在Application onCreate 中  调用 BASE.setQuickUi(**.class)\n" +
                "3.注册启动 ACT -> com.hzaz.base.WelcomeActivity");
        codeHelper = new CodeHelper(view, R.id.next);
        codeHelper.setRunTime(3);
        codeHelper.start();
//        HttpManager.refuse(view);
//        HttpManager.getTopNames(view);
//        ResourceStreamLoader resourceLoader = new ResourceStreamLoader(view.getContextActivity(), R.mipmap.logo_gif1);
//        final APNGDrawable apngDrawable = new APNGDrawable(resourceLoader);
//        view.setData2View(R.id.img, apngDrawable);
//        apngDrawable.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
//            @Override
//            public void onAnimationStart(Drawable drawable) {
//                super.onAnimationStart(drawable);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(2600);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        view.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                apngDrawable.pause();
//                            }
//                        });
//                    }
//                }).start();
//            }
//        });
    }

    @Override
    public String getUpdatePath() {
        return uploadPath;
    }

    @Override
    public long getAutoSkipTime() {
        return 2000;
    }

    @Override
    public String getVname() {
        return Vname;
    }

    @Override
    public String getVersionDescribe() {
        return VersionDescribe;
    }

    @Override
    public boolean isCancelAble() {
        return UpdateFlag;
    }

    String uploadPath;
    String Vname;
    String VersionDescribe;
    boolean UpdateFlag;

    @Override
    public boolean is(RequestType type, BaseBean data) {
//        if (type.is("ColumnMenuListV1")) {
//            List<TopNameBean.ColumnMenuListBean> list = data.getList(TopNameBean.ColumnMenuListBean.class, "ColumnMenuList");
//            LOG.bean("WelcomePage", list);
//            for (int i = 0; i < list.size(); i++) {
//                LOG.e("WelcomePage", i + ".is:" + list.get(i).toString());
//            }
//            return false;
//        } else if (type.is(VersionV1_1)) {
//            this.uploadPath = data.get(String.class, "vMap", "updatePath");
//            this.Vname = "hc-v" + data.get(String.class, "vMap", "vname");
//            this.VersionDescribe = data.get(String.class, "vMap", "vdescribe");
//            this.UpdateFlag = 9 == (int) data.get(Integer.class, "vMap", "updateFlag");
//            return true;
//        }
        return false;
    }

    @Override
    public void skip(BaseController view) {
//        view.skip(LoginController.class);
    }

}
