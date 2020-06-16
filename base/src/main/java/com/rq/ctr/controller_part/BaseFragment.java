package com.rq.ctr.controller_part;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rq.ctr.NetResponseViewImpl;
import com.rq.ctr.R;
import com.rq.ctr.common_util.AppUtil;
import com.rq.ctr.common_util.LOG;
import com.rq.ctr.net.BaseBean;
import com.rq.ctr.net.RequestType;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

public class BaseFragment<P extends BaseController> extends RxFragment implements NetResponseViewImpl {
    protected P mPresenter;
    boolean isCreate = false;
    View fraRoot;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = getController(this, 0);
        if (mPresenter != null) {
            mPresenter.setFragment(this);
        }
    }

    @Nullable
    @Override
    public Activity getContextActivity() {
        return getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mPresenter != null) {
            Object pass1 = mPresenter.getPass(0);
            LOG.e("BaseFragment", "onCreateView " + mPresenter.getClass().getSimpleName() + "  " + pass1);
        }
        if (fraRoot != null) return fraRoot;
        if (mPresenter != null) {
            fraRoot = inflater.inflate(mPresenter.getLayoutId(), container, false);
            return fraRoot;
        }
        fraRoot = super.onCreateView(inflater, container, savedInstanceState);
        return fraRoot;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mPresenter != null) {
            if (mPresenter.onActivityResult(requestCode, resultCode, data)) return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public final void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            onHideToUser();
        } else {
            onShowToUser();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isCreate) {
            onShowToUser();
        } else {
            isCreate = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        onHideToUser();
    }

    protected void onHideToUser() {
        if (mPresenter != null) {
            ControllerWatcher.get().removerFragmentController(mPresenter);
            mPresenter.onControllerPause();
        }
    }

    //展示到用户手机上
    protected void onShowToUser() {
        if (mPresenter == null) return;
        if (mPresenter.attach() == null) {
            ControllerWatcher.get().addFragmentController(mPresenter);
        }
        mPresenter.onControllerResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (fraRoot.findViewById(R.id.common_status_bar) != null) {
            ViewGroup.LayoutParams params = fraRoot.findViewById(R.id.common_status_bar).getLayoutParams();
            params.height = AppUtil.getStatusBarHeight();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            fraRoot.findViewById(R.id.common_status_bar).setLayoutParams(params);
        }
        LOG.e("BaseFragment", "onViewCreated.128:" + mPresenter);
        mPresenter.setFragmentRootView(view);
        onShowToUser();
        LOG.e("BaseFragment", "onViewCreated.132:" + mPresenter);
        mPresenter.onViewCreated();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            Object pass1 = mPresenter.getPass(0);
            LOG.e("BaseFragment", "onDestroyView " + mPresenter.getClass().getSimpleName() + "  " + pass1);
        }
    }

    //反射获取当前Presenter对象
    protected P getController(Object o, int i) {
        return getController((Class<P>) ((ParameterizedType) (o.getClass().getGenericSuperclass())).getActualTypeArguments()[i]);
    }

    public P getController(Class<P> clazz) {
        try {
            return clazz.newInstance();
        } catch (java.lang.InstantiationException e) {
            LOG.showUserWhere();
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            LOG.showUserWhere();
            e.printStackTrace();
        } catch (ClassCastException e) {
            LOG.showUserWhere();
            e.printStackTrace();
        } catch (NullPointerException e) {
            LOG.showUserWhere();
            e.printStackTrace();
        }
        return null;
    }

    //反射获取当前Presenter对象
    public BaseController getController() {
        return mPresenter;
    }

    @Override
    public void handleFailResponse(BaseBean baseBean) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public <T extends Serializable> void onResponseSucceed(@NonNull RequestType type, T data) {

    }

    @Override
    public void onResponseError(@NonNull RequestType type) {

    }

    public boolean onBreakPress() {
        if (mPresenter != null) {
            return mPresenter.interruptBack();
        }
        return false;
    }
}
