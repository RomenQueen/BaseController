package com.hzaz.base;

import android.text.TextUtils;
import android.util.SparseArray;

import com.hzaz.base.common_util.LOG;

import java.util.ArrayList;
import java.util.List;

/**
 * 控制器集中管理
 * watch 、 post 实现类似于 EventBus 进行 Controller 之间的数据传递
 */
public class ControllerWatcher {

    private static ControllerWatcher mControllerWatcher;
    SparseArray<BaseController> watcher = new SparseArray<>();
    SparseArray<String> watcherTag = new SparseArray<>();
    List<BaseController> visibleFragmentController = new ArrayList<>();
    BaseController visibleActivityController = null;

    private ControllerWatcher() {
    }

    public static ControllerWatcher get() {
        if (mControllerWatcher == null) {
            synchronized (ControllerWatcher.class) {
                if (mControllerWatcher == null) {
                    mControllerWatcher = new ControllerWatcher();
                }
            }
        }
        return mControllerWatcher;
    }

    public void watch(String tag, BaseController controller) {
        synchronized (ControllerWatcher.class) {
            if (!TextUtils.isEmpty(tag)) {
                watcherTag.append(watcherTag.size(), tag);
                watcher.append(watcher.size(), controller);
            }
        }
    }

    public List<BaseController> getAllVisibleController() {
        List<BaseController> all = new ArrayList<>();
        all.add(visibleActivityController);
        all.addAll(visibleFragmentController);
        return all;
    }

    public List<BaseController> getVisibleFragmentController() {
        return visibleFragmentController;
    }

    public BaseController getVisibleActivityController() {
        return visibleActivityController;
    }

    public void addActivityController(BaseController controller) {
        this.visibleActivityController = controller;
    }

    public void removerActivityController(BaseController controller) {
        this.visibleActivityController = null;
    }

    public void addFragmentController(BaseController controller) {
        LOG.d("ControllerWatcher", "AddFra : " + controller.getClass().getSimpleName());
        visibleFragmentController.add(controller);
        LOG.d("ControllerWatcher", "Res : " + visibleFragmentController.size());
    }

    public void removerFragmentController(BaseController controller) {
        visibleFragmentController.remove(controller);
    }

    public void post(String tag, Object object) {
        for (int i = 0; i < watcherTag.size(); i++) {
            if (TextUtils.equals(tag, watcherTag.get(i))) {
                try {
                    if (watcherTag.get(i) != null) {
                        watcher.get(i).tagInfo(watcherTag.get(i), object);
                    } else {
                        LOG.e("ControllerWatcher", "post.error:" + object);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void notifyFragmentOnResume() {
        for (BaseController item : ControllerWatcher.get().getVisibleFragmentController()) {
            LOG.d("ControllerWatcher", "notifyFragmentOnResume item --> " + item.getClass().getSimpleName());
            item.onHttpGet();
        }
    }

    public BaseController findController(Class<?> attach) {
        if (attach != null && getAllVisibleController() != null) {
            for (BaseController baseController : getAllVisibleController()) {
                if (baseController.getClass() == attach) {
                    return baseController;
                }
            }
        }
        return null;
    }
}
