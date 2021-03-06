package com.rq.ctr.controller_part;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.rq.ctr.NetResponseViewImpl;
import com.rq.ctr.R;
import com.rq.ctr.common_util.AppUtil;
import com.rq.ctr.common_util.LOG;
import com.rq.ctr.common_util.SPUtil;
import com.rq.ctr.common_util.image.ImageLoadUtil;
import com.rq.ctr.impl_part.ActivityImpl.ViewParam;
import com.rq.ctr.impl_part.OnClick;
import com.rq.ctr.impl_part.OnRefuseAndLoad;
import com.rq.ctr.impl_part.OnRefuseAndLoadListener;
import com.rq.ctr.net.BaseBean;
import com.rq.ctr.net.HttpParamUtil;
import com.rq.ctr.net.RequestType;
import com.rq.rvlibrary.BaseAdapter;
import com.rq.rvlibrary.RecyclerUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.rq.ctr.controller_part.BaseActivity.TAG_OPEN_CODE;
import static com.rq.ctr.impl_part.OnRefuseAndLoadListener.Status.FinishRefuseAndLoad;

/**
 * 用  Controller 代替 Activity 和 Fragment 简化维护
 * 逻辑无关：与应用本身没有关系，任何应用可以复制的基类
 */
public abstract class BaseController implements NetResponseViewImpl {

    public static final String TAG_PASS = "pass";
    public static final String TAG_NAME = "controller";
    private static Handler mainHandler = new Handler(Looper.getMainLooper());
    int refuseViewId = 0;
    List<BaseController> attachChild = new ArrayList<>();
    private BaseActivity mActivity;
    private BaseFragment mFragment;
    private View mRootView;
    private SparseArray<View> viewSparseArray = new SparseArray<>();            //页面子控件
    private SparseArray<Object> dataSparseArray = new SparseArray<>();          //页面数据
    private int innerPassCode = 0;

    public <P extends BaseController> void skip(Class<P> clazz, Object... pass) {
        BaseActivity.skip(this, clazz, pass);
    }

    /**
     * 普通的单业务数据专递
     */
    public <P extends BaseController> void openFor(Class<P> clazz, Object... pass) {
        BaseActivity.openFor(this, clazz, pass);
    }

    /**
     * 多业务数据传递
     *
     * @param requestCode 业务标识编码
     * @see #getRequestCode()
     */
    public <P extends BaseController> void openWith(Class<P> clazz, int requestCode, Object... pass) {
        BaseActivity.openWith(this, requestCode, clazz, pass);
    }

    public <P extends BaseController> void open(Class<P> clazz, Object... pass) {
        BaseActivity.open(this, clazz, pass);
    }

    /**
     * 界面加载前 权限检查
     * 必须授权，否则不回调 onViewCreated
     */
    public String[] needPermissions() {
        return null;
    }

    public static <C extends BaseController> Bundle getFraArguments(Class<C> clazz, Serializable... pass) {
        Bundle bundle = new Bundle();
        if (pass != null) {
            for (int i = 0; i < pass.length; i++) {
                bundle.putSerializable(TAG_PASS + i, pass[i]);
            }
        }
        bundle.putSerializable(TAG_NAME, clazz);
        return bundle;
    }

    public BaseController() {//tagInfo
        final String[] tags = watchTag();
        if (tags != null) {
            for (String tag : tags) {
                ControllerWatcher.get().watch(tag, this);
            }
        }
    }

    public int getActivityFlag() {
        return 0;
    }

    /**
     * 重新显示布局，常用于切换语言
     * 仅用于 Activity 模式
     */
    public final void clearView() {
        viewSparseArray.clear();
        dataSparseArray.clear();
    }

    protected BaseActivity getActivity() {
        if (mFragment != null) {
            return (BaseActivity) mFragment.getActivity();
        }
        return mActivity;
    }

    public void setActivity(BaseActivity pCommonActivity) {
        this.mActivity = pCommonActivity;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public void setFragment(BaseFragment fragment) {
        this.mFragment = fragment;
        this.mActivity = (BaseActivity) fragment.getActivity();
        if (attach() != null) {
            BaseController parent = ControllerWatcher.get().findController(attach());
            if (parent != null) {
                parent.addAttachItem(this);
                LOG.e("BaseController", getClass().getSimpleName() + " attach : " + parent.getClass().getSimpleName());
            } else {
                LOG.e("BaseController", getClass().getSimpleName() + " No Parent > " + attach().getSimpleName());
            }
        }
    }

    public void setFragmentRootView(View root) {
        this.mRootView = root;
    }

    /**
     * 快速获取获取传递数据
     *
     * @see #finishOK(Serializable...)
     * {@link BaseActivity#getInnerIntent(BaseController, Class, Object...)}
     * {@link com.rq.ctr.ui.CommonFragment#instance(Class, Serializable...)}
     */
    public Object getPass(int position, Intent... pass) {
        if (pass != null && pass.length > 0) {
            return pass[0].getSerializableExtra(TAG_PASS + position);
        }
        if (getFragment() != null) {
            if (getFragment().getArguments() != null) {
                return getFragment().getArguments().getSerializable(TAG_PASS + position);
            }
        }
        return mActivity.getIntent().getSerializableExtra(TAG_PASS + position);
    }

    /**
     * @return 请求编码
     * {@link BaseActivity#openFor(BaseController, Class, Object...) getRequestCode()>0}
     * {@link BaseActivity#openWith(BaseController, int, Class, Object...) int --> getRequestCode()}
     * {@link BaseController#openFor(Class, Object...) getRequestCode()>0}
     * {@link BaseController#openWith(Class, int, Object...) int --> getRequestCode()}
     */
    public int getRequestCode() {
        int code = mActivity.getIntent().getIntExtra(TAG_OPEN_CODE, 0);
        LOG.e("BaseController", "getRequestCode:" + code);
        return code;
    }

    int getInnerPassCode() {
        LOG.e("BaseController", "getInnerPassCode:" + innerPassCode);
        return innerPassCode;
    }

    void setInnerPassCode(int requestCode) {
        this.innerPassCode = requestCode;
        LOG.e("BaseController", "saveCode:" + requestCode);
    }

    protected void startActivityForResult(Intent intent) {
        setInnerPassCode((int) (System.currentTimeMillis() % (0xffff)));
        mActivity.startActivityForResult(intent, innerPassCode);
    }

    protected void startActivityForResult(Intent intent, int requestCode) {
        setInnerPassCode(requestCode);
        mActivity.startActivityForResult(intent, requestCode);
    }

    /**
     * @return true 已经处理逻辑  false 交给上层处理
     */
    final boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        LOG.e("BaseController", "onActivityResult.186:");
        if (requestCode == getInnerPassCode() && resultCode == Activity.RESULT_OK && data != null) {
            if (onResultOK(data)) {
                return true;
            } else {
                return onResultOther(requestCode, resultCode, data);
            }
        }
        return onResultOther(requestCode, resultCode, data);
    }

    public void finish() {
        mActivity.setResult(Activity.RESULT_CANCELED);
        mActivity.finish();
    }

    /**
     * 返回结果并且作为成功记过返回，调用<link>getPass(int,Intent)<link/>获取数据
     *
     * @param back
     * @see #getPass(int, Intent...)
     */
    public void finishOK(Serializable... back) {
        Intent intent = new Intent();
        if (back != null) {
            for (int i = 0; i < back.length; i++) {
                intent.putExtra(TAG_PASS + i, back[i]);
            }
        }
        mActivity.setResult(Activity.RESULT_OK, intent);
        mActivity.finish();
    }


    public void finishOK(Intent data) {
        mActivity.setResult(Activity.RESULT_OK, data);
        mActivity.finish();
    }

    public void finish(int resultCode, Intent data) {
        mActivity.setResult(resultCode, data);
        mActivity.finish();
    }

    public void finish(int resultCode, Serializable... back) {
        Intent intent = new Intent();
        if (back != null) {
            for (int i = 0; i < back.length; i++) {
                intent.putExtra(TAG_PASS + i, back[i]);
            }
        }
        mActivity.setResult(resultCode, intent);
        mActivity.finish();
    }

    protected boolean onResultOK(Intent data) {
        return false;
    }

    protected boolean onResultOther(int requestCode, int resultCode, @Nullable Intent data) {
        LOG.e("BaseController", "onResultOther.231:");
        return false;
    }

    /**
     * @see #needPermissions() - will check permisiion before onViewCreated,if permisson granted fail,
     * then won't attach this medth
     */
    public void onViewCreated() {
        if (is(View.OnClickListener.class)) {
            Method method = null;
            try {
                method = get(View.OnClickListener.class).getClass().getDeclaredMethod("onClick", View.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            if (method == null) return;
            Annotation[] annotations = method.getAnnotations();
            if (annotations.length > 0) {
                for (Annotation annotation : annotations) {
                    if (annotation instanceof OnClick) {
                        OnClick inject = (OnClick) annotation;
                        int[] value = inject.value();
                        if (value.length > 0) {
                            for (int id : value) {
                                setData2View(id, get(View.OnClickListener.class));
                            }
                        }
                    }
                }
            }
        }
        if (is(OnRefuseAndLoadListener.class)) {
            Method method = null;
            try {
                method = get(OnRefuseAndLoadListener.class).getClass().getDeclaredMethod("refuse", int.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            if (method == null) return;
            Annotation[] annotations = method.getAnnotations();
            if (annotations.length > 0) {
                for (Annotation annotation : annotations) {
                    if (annotation instanceof OnRefuseAndLoad) {
                        OnRefuseAndLoad inject = (OnRefuseAndLoad) annotation;
                        int id = inject.viewId();
                        if (id > 0) {
                            setOnRefuseAndLoadListener(
                                    id,
                                    get(OnRefuseAndLoadListener.class)
                                    , inject.loadAble()
                                    , inject.refuseAble());
                        }
                    }
                }
            }
        }
        ImmersionBar.with(getActivity()).init();
    }

    protected final boolean is(Class<?> clazz) {
        boolean res = clazz.isAssignableFrom(this.getClass());
        return res;
    }

    public <T> T get(Class<T> type) {
        return type.cast(this);
    }

    public final void runOnUiThread(Runnable action) {
        mActivity.runOnUiThread(action);
    }

    final Handler mHandler = new Handler(Looper.getMainLooper());

    public final void runOnUiThread(Runnable action, long delayMillis) {
        mHandler.postDelayed(action, delayMillis);
    }


    protected void initTitle(String title, int... click) {
        setData2View(R.id.common_title, title);
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, R.id.common_left_back);
        if (click != null && click.length > 0) {
            ((ImageView) getView(R.id.common_left_back)).setImageResource(click[0]);
        }
    }

    protected void setImmersion(boolean isWhite, int... color) {//是否为白色顶部  沉浸式显示优化
        if (isWhite) {
            ImmersionBar.with(mActivity)
                    .statusBarColor(R.color.white)
                    .statusBarDarkFont(true, 0.2F)
                    .init();
        } else {
            if (color != null && color.length > 0 && color[0] != 0) {
                int colorRes = color[0];
                ImmersionBar.with(getActivity()).fitsSystemWindows(true).statusBarColor(colorRes).init();
            } else {
                ImmersionBar.with(getActivity()).init();
            }
        }
    }

    public void setData2View(final int viewId, final Object obj) {
        if (Looper.myLooper() == Looper.getMainLooper()) {//主线程
            setDataToView(viewId, obj);
        } else {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    setDataToView(viewId, obj);
                }
            });
        }
    }

    public void setData2View(final View viewId, final Object obj) {
        if (Looper.myLooper() == Looper.getMainLooper()) {//主线程
            setDataToView(viewId, obj);
        } else {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    setDataToView(viewId, obj);
                }
            });
        }
    }

    public void detachView() {
        mRootView = null;
    }

    protected String[] watchTag() {//tagInfo 接受数据变动
        return new String[]{};
    }

    /**
     * {@link #tagInfo(String, Object...)}
     * ost 发送
     */
    protected void tagInfo(String tag, Object... object) {
    }

    /**
     * {@link #tagInfo(String, Object...)}
     * tagInfo  接受数据变动
     */
    public final void post(String tag, Object... object) {
        LOG.e(getClass().getSimpleName(), tag + " post.data:" + object);
        if (tag != null) {
            try {
                ControllerWatcher.get().post(tag, object);
            } catch (Exception e) {

            }
        }
    }

    public View getRootView() {
        return mRootView;
    }

    /**
     * {@link Activity#onBackPressed()}
     */
    public boolean interruptBack() {
        return false;
    }

    public void setOnClickListener(View.OnClickListener listener, int... ids) {
        if (ids == null || ids.length == 0 || listener == null) return;
        for (int id : ids) {
            if (getView(id) != null) {
                getView(id).setOnClickListener(listener);
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    public String getInput(int viewId) {
        try {
            return ((EditText) getView(viewId)).getText().toString().trim();
        } catch (Exception e) {
            try {
                return ((TextView) getView(viewId)).getText().toString().trim();
            } catch (Exception e1) {
                return "";
            }
        }
    }

    /**
     * 不建议再调用改法 直接继承 OnRefuseAndLoadListener
     * 将 Override 复写方法改为 OnRefuseAndLoad
     *
     * @param refuseAndLoad boolean + boolean >>LoadAble + RefreshAble
     *                      null 根据 SmartRefreshLayout父布局 外层高度 与 SmartRefreshLayout 高度自行设置 LoadAble
     * @see OnRefuseAndLoad
     * 为了简化逻辑 ，会在设置View的时候加载一次 refuse(1)
     * RefreshAble 默认 true
     */
    @Deprecated
    public void setOnRefuseAndLoadListener(int viewId, final OnRefuseAndLoadListener listener, Object... refuseAndLoad) {
        this.refuseViewId = viewId;
        final SmartRefreshLayout refreshLayout = getView(viewId);
        if (refreshLayout == null || listener == null) return;
        refreshLayout.setTag(R.id.tag_refuse_page, 1);
        listener.refuse(1);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshLayout.setTag(R.id.tag_refuse_page, 1);
                listener.refuse(1);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                int page = (int) refreshLayout.getTag(R.id.tag_refuse_page);
                page++;
                refreshLayout.setTag(R.id.tag_refuse_page, page);
                listener.refuse(page);
            }
        });
        if (refuseAndLoad.length > 0) {
            if (refuseAndLoad[0] instanceof Boolean) {
                refreshLayout.setEnableLoadMore((Boolean) refuseAndLoad[0]);
            } else {
                refreshLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        refreshLayout.setEnableLoadMore(v.getHeight() >= ((View) v.getParent().getParent()).getHeight());
                    }
                });
            }
        }
        if (refuseAndLoad.length > 1) {
            if (refuseAndLoad[1] instanceof Boolean) {
                refreshLayout.setEnableRefresh((Boolean) refuseAndLoad[1]);
            }
        }
    }

    public void finishRAL() {
        if (refuseViewId == 0) return;
        final SmartRefreshLayout refreshLayout = getView(refuseViewId);
        if (refreshLayout == null) return;
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
    }

    public <T extends View> T getView(int id) {
        View finView = this.viewSparseArray.get(id);
        if (finView != null) {
            return (T) finView;
        }
        if (mRootView != null) {
            return mRootView.findViewById(id);
        }
        if (mActivity == null) return null;
        return mActivity.findViewById(id);
    }

    protected void setSave(String key, Object value) {
        if (value instanceof String) {
            SPUtil.setString(mActivity, key, (String) value);
        } else if (value instanceof Integer) {
            SPUtil.saveInt(mActivity, key, (Integer) value);
        } else if (value instanceof Boolean) {
            SPUtil.setBoolean(mActivity, key, (Boolean) value);
        } else if (value instanceof Long) {
            SPUtil.setLong(mActivity, key, (Long) value);
        }
    }

    protected Class<?> attach() {
        return null;
    }

    public List<BaseController> getAttachChild() {
        return attachChild;
    }

    void addAttachItem(BaseController item) {
        attachChild.add(item);
    }

    public final void onControllerResume() {
        onResume();
        if (getAttachChild().size() > 0) {
            for (BaseController item : getAttachChild()) {
                item.onResume();
            }
        }
    }

    public final void onControllerPause() {
        onPause();
        if (getAttachChild().size() > 0) {
            for (BaseController item : getAttachChild()) {
                item.onPause();
            }
        }
    }

    public void onActivityResume() {
    }

    public void onActivityPause() {
    }

    public void onActivityDestroy() {
    }

    protected void onPause() {
        LOG.e("BaseController", "    onPause  ->  " + getClass().getSimpleName());
    }

    protected void onResume() {
        LOG.e("BaseController", "    onResume  ->  " + getClass().getSimpleName());
    }

    @LayoutRes
    public abstract int getLayoutId();

    private void setDataToView(int viewId, Object object) {
        if (viewSparseArray.get(viewId) != null) {
            setDataToView(viewSparseArray.get(viewId), object);
        } else {
            View v = getView(viewId);
            LOG.d("BaseController", viewId + ".setDataToView  " + viewSparseArray.indexOfValue(v));
            if (v != null) {
                if (viewSparseArray.indexOfValue(v) < 0) {
                    viewSparseArray.append(viewId, v);
                }
                setDataToView(v, object);
            }
        }
    }

    protected final synchronized void setDataToView(View view, Object obj) {

        if (view == null) {
            LOG.utilLog("ViewEmpty");
            return;
        }
        if (fillViewData(view, obj)) {
            return;
        }
        if (obj == null) {
            LOG.utilLog("DataEmpty");
            return;
        }
        if (obj instanceof Integer && ((int) obj == View.VISIBLE || (int) obj == View.GONE || (int) obj == View.INVISIBLE)) {
            view.setVisibility((int) obj);
            return;
        }
        if (obj instanceof ViewParam) {
            if (obj == ViewParam.ENABLE) {
                view.setEnabled(true);
            } else if (obj == ViewParam.UNABLE) {
                view.setEnabled(false);
            } else if (obj == ViewParam.CHECKABLE) {
                view.setClickable(true);
            } else if (obj == ViewParam.UNCHECKABLE) {
                view.setClickable(false);
            }
            return;
        }
        if (obj instanceof View.OnClickListener) {
            view.setOnClickListener((View.OnClickListener) obj);
            return;
        }
        if (obj instanceof OnRefuseAndLoadListener.Status && view instanceof SmartRefreshLayout) {
            if (obj == FinishRefuseAndLoad) {
                ((SmartRefreshLayout) view).finishLoadMore();
                ((SmartRefreshLayout) view).finishRefresh();
            } else if (obj == OnRefuseAndLoadListener.Status.FinishLoad) {
                ((SmartRefreshLayout) view).finishLoadMore();
            } else if (obj == OnRefuseAndLoadListener.Status.FinishRefuse) {
                ((SmartRefreshLayout) view).finishRefresh();
            } else if (obj == OnRefuseAndLoadListener.Status.loadAble_False) {
                ((SmartRefreshLayout) view).setEnableLoadMore(false);
            } else if (obj == OnRefuseAndLoadListener.Status.loadable_True) {
                ((SmartRefreshLayout) view).setEnableLoadMore(true);
            } else if (obj == OnRefuseAndLoadListener.Status.refreshable_False) {
                ((SmartRefreshLayout) view).setEnableRefresh(false);
            } else if (obj == OnRefuseAndLoadListener.Status.refreshable_True) {
                ((SmartRefreshLayout) view).setEnableRefresh(true);
            }
            return;
        }
        if (obj instanceof Boolean) {
            view.setSelected((boolean) obj);
            return;
        }
        if (view instanceof ImageView) {
            fillImageView((ImageView) view, obj);
        } else if (view instanceof TextView) {
            fillTextView((TextView) view, obj);
        } else if (view instanceof RecyclerView) {
            if (obj instanceof RecyclerUtil) {
                fillRecyclerViewData((RecyclerView) view, (RecyclerUtil) obj);
            } else {
                fillRecyclerViewData((RecyclerView) view, obj);
            }
        }
    }

    protected boolean fillViewData(@NonNull View view, @Nullable Object obj) {
        return false;
    }

    protected void fillImageView(ImageView view, Object obj) {
        if (obj instanceof Bitmap) {
            view.setImageBitmap((Bitmap) obj);
        } else if (obj instanceof Integer) {
            view.setImageResource((int) obj);
        } else if (obj instanceof Drawable) {
            view.setImageDrawable((Drawable) obj);
        } else if (obj instanceof String) {
            ImageLoadUtil.display(getContextActivity(), (String) obj, view);
        }
    }

    protected void fillTextView(TextView view, Object obj) {
        if (obj instanceof CharSequence) {
            view.setText((CharSequence) obj);
        } else if (obj instanceof Integer) {
            view.setText((int) obj);
        }
    }

    protected void fillRecyclerViewData(RecyclerView recyclerView, Object obj) {
        if (obj instanceof RecyclerView.Adapter) {
            if (recyclerView.getLayoutManager() == null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
            }
            recyclerView.setAdapter((RecyclerView.Adapter) obj);
        } else if (obj instanceof RecyclerView.LayoutManager) {
            recyclerView.setLayoutManager((RecyclerView.LayoutManager) obj);
        }
    }

    protected void fillRecyclerViewData(RecyclerView recyclerView, RecyclerUtil util) {
        util.context(mActivity);
        if (recyclerView.getLayoutManager() == null && util.context(mActivity).build() != null) {
            recyclerView.setLayoutManager(util.build());
        }
        if (util.adapter() != null) {
            recyclerView.setAdapter(util.adapter());
        }
        if (util.getData() != null) {
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter instanceof BaseAdapter) {
                if (util.getPage() == 1) {
                    ((BaseAdapter) adapter).setData(util.getData());
                } else {
                    ((BaseAdapter) adapter).addData(util.getData());
                }
            } else if (adapter == null) {
                adapter = util.adapter();
                recyclerView.setAdapter(adapter);
            }
        }
        if (util.getDecoration() != null) {
            recyclerView.addItemDecoration(util.getDecoration());
        }
    }

    @Override
    public void handleFailResponse(BaseBean baseBean) {
        mActivity.handleFailResponse(baseBean);
    }

    @Override
    public void showLoading() {
        if (mFragment != null) {
            mFragment.showLoading();
        } else {
            mActivity.showLoading();
        }
    }

    @Override
    public void dismissLoading() {
        finishRAL();
        if (mFragment != null) {
            mFragment.dismissLoading();
        } else {
            mActivity.dismissLoading();
        }
    }

    @Override
    public Activity getContextActivity() {
        return getActivity();
    }

    @Override
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        if (mFragment != null) {
            return mFragment.bindToLifecycle();
        } else {
            return mActivity.bindToLifecycle();
        }
    }

    @Override
    public <T extends Serializable> void onResponseSucceed(@NonNull RequestType type, T data) {
        finishRAL();
        if (mFragment != null) {
            mFragment.onResponseSucceed(type, data);
        } else {
            mActivity.onResponseSucceed(type, data);
        }
    }


    @Override
    public void onResponseError(@NonNull RequestType type) {
        finishRAL();
        HttpParamUtil.commonError(this, type);
        if (mFragment != null) {
            mFragment.onResponseError(type);
        } else {
            mActivity.onResponseError(type);
        }
    }

    public void startActivity(Intent intent) {//兼容写法
        mActivity.startActivity(intent);
    }

    /**
     * 适用于 跳转特殊ACT的回参获取
     */
    @Deprecated
    protected Intent getIntent() {
        return mActivity.getIntent();
    }

    public String getString(int strRes) {
        if (mActivity == null) return "";
        return mActivity.getString(strRes);
    }

    /**
     * @return 是否显示状态栏 true -> 不显示
     */
    public boolean isFullScreen() {
        return false;
    }

    public boolean isTranslucent() {
        return false;
    }

    /**
     * @return 是否布局到正常状态栏位置 true 布局会上提
     */
    public boolean underStatusBar() {
        return false;
    }

    public boolean needOutScroll() {
        return true;
    }

    protected boolean isShow() {
        if (getActivity() == null) return false;
        if (AppUtil.isForeground(getActivity())) {
            return true;
        }
        if (mFragment != null && mFragment.isResumed()) {
            return true;
        }
        return false;
    }

    /**
     * @deprecated scrollY 计算方式不科学，只适用于单一子项的RV
     */
    public boolean recyclerToTop(final int hideViewId, int listId) {
        if (getView(hideViewId) == null || getView(listId) == null) {
            return false;
        }
        if (getView(listId) instanceof RecyclerView) {
            ((RecyclerView) getView(listId)).addOnScrollListener(new RecyclerView.OnScrollListener() {
                int scrollY = 0;

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    int targetHeight = getView(hideViewId).getMeasuredHeight();
                    scrollY += dy;
                    setData2View(hideViewId, Math.abs(scrollY) > targetHeight ? View.GONE : View.VISIBLE);
                    LOG.e("BaseController", scrollY + ".757:" + targetHeight);
                }
            });
            return true;
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getView(listId).setOnScrollChangeListener(new View.OnScrollChangeListener() {
//                @Override
//                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                    int targetHeight = getView(hideViewId).getHeight();
//                    setData2View(hideViewId, scrollY > targetHeight ? View.GONE : View.VISIBLE);
//                    LOG.e("BaseController", scrollY + ".768:" + targetHeight);
//                }
//            });
//            return true;
//        }
        return false;
    }
}
