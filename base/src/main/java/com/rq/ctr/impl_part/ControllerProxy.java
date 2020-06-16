package com.rq.ctr.impl_part;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.rq.ctr.BASE;
import com.rq.ctr.R;
import com.rq.ctr.common_util.AppUtil;
import com.rq.ctr.common_util.LOG;
import com.rq.ctr.common_util.image.ImageLoadUtil;
import com.rq.ctr.impl_part.ActivityImpl.ViewParam;
import com.rq.rvlibrary.BaseAdapter;
import com.rq.rvlibrary.RecyclerUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.rq.ctr.controller_part.BaseController.TAG_PASS;
import static com.rq.ctr.impl_part.OnRefuseAndLoadListener.Status.FinishRefuseAndLoad;

public class ControllerProxy {
    Context mContext;
    ProxyObject proxyObject;


    ControllerProxy(Context con, ProxyObject impl) {
        this.mContext = con;
        this.proxyObject = impl;
    }

    public View getLayoutView(int layout) {
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        if (mContext == null) {
            Log.e("ControllerProxy", "初始化异常，请阅读文档");
            return new View(mContext);
        }
        View root;
        if (BASE.commonLayoutId != 0) {
            root = inflater.inflate(BASE.commonLayoutId, null);
        } else if (proxyObject.is(NoScroller.class)) {//是否添加外部滑动控件，可自行适配小屏手机
            root = inflater.inflate(R.layout.activity_base_no_scroller, null);
            final ViewGroup container = root.findViewById(R.id.base_container);
            final View content = inflater.inflate(layout, null);
            container.addView(content);
        } else {
            root = inflater.inflate(R.layout.activity_base, null);
            final ViewGroup container = root.findViewById(R.id.base_container);
            final View content = inflater.inflate(layout, null);
            container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    int height = ((View) container.getParent()).getHeight();
                    int height2 = container.getHeight();
                    if (height2 < height && height2 != 0) {
                        ScrollView.LayoutParams Params = (ScrollView.LayoutParams) container.getLayoutParams();
                        Params.height = height;
                        container.setLayoutParams(Params);
                        content.setLayoutParams(Params);
                    }
                    container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
            container.addView(content);
        }
        if (root.findViewById(R.id.common_status_bar) != null) {
            ViewGroup.LayoutParams params = root.findViewById(R.id.common_status_bar).getLayoutParams();
            params.height = AppUtil.getStatusBarHeight();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            root.findViewById(R.id.common_status_bar).setLayoutParams(params);
        }
        return root;
    }

    public void initView() {
        if (proxyObject.is(View.OnClickListener.class)) {
            Method method = null;
            try {
                method = proxyObject.get(View.OnClickListener.class).getClass().getDeclaredMethod("onClick", View.class);
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
                                setData2View(id, proxyObject.get(View.OnClickListener.class));
                            }
                        }
                    }
                }
            }
        }
        if (proxyObject.is(OnRefuseAndLoadListener.class)) {
            Method method = null;
            try {
                method = proxyObject.get(OnRefuseAndLoadListener.class).getClass().getDeclaredMethod("refuse", int.class);
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
                                    proxyObject.get(OnRefuseAndLoadListener.class)
                                    , inject.loadAble()
                                    , inject.refuseAble());
                        }
                    }
                }
            }
        }
        if (proxyObject.is(ActivityImpl.class)) {
            proxyObject.get(ActivityImpl.class).onViewCreated();
        }
        LOG.e("ControllerProxy", "end:initView");
    }

    /**
     * RefreshAble LoadAble 默认 true
     * 推荐使用 @OnRefuseAndLoad
     *
     * @param refuseAndLoad boolean + boolean >>LoadAble + RefreshAble
     *                      null 根据 SmartRefreshLayout父布局 外层高度 与 SmartRefreshLayout 高度自行设置 LoadAble
     * @see OnRefuseAndLoad
     */
    @Deprecated
    public void setOnRefuseAndLoadListener(int viewId, final OnRefuseAndLoadListener listener, Object... refuseAndLoad) {
        final SmartRefreshLayout rsLayout = getView(viewId);
        if (rsLayout == null || listener == null) return;
        rsLayout.setTag(R.id.tag_refuse_page, BASE.PAGE_SIZE_START);
        rsLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                rsLayout.setTag(R.id.tag_refuse_page, BASE.PAGE_SIZE_START);
                listener.refuse(BASE.PAGE_SIZE_START);
            }

        });
        rsLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                int page = (int) rsLayout.getTag(R.id.tag_refuse_page);
                page++;
                rsLayout.setTag(R.id.tag_refuse_page, page);
                listener.refuse(page);
            }
        });
        if (refuseAndLoad.length > 0) {
            if (refuseAndLoad[0] instanceof Boolean) {
                rsLayout.setEnableLoadMore((Boolean) refuseAndLoad[0]);
            } else {
                rsLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        rsLayout.setEnableLoadMore(v.getHeight() >= ((View) v.getParent().getParent()).getHeight());
                    }
                });
            }
        }
        if (refuseAndLoad.length > 1) {
            if (refuseAndLoad[1] instanceof Boolean) {
                rsLayout.setEnableRefresh((Boolean) refuseAndLoad[1]);
            }
        }
    }

    public void setView2Data(int viewId, Object obj) {
        setData2View(viewId, obj);
    }

    public void post(Runnable action, long time) {
        if (time <= 0) {
            mainHandler.post(action);
        } else {
            mainHandler.postDelayed(action, time);
        }
    }

    Handler mainHandler = new Handler(Looper.getMainLooper());

    public void setData2View(final int viewId, final Object obj) {
        if (Looper.myLooper() == Looper.getMainLooper()) {//主线程
            setDataToView(viewId, obj);
        } else {
            if (mainHandler == null) {
                mainHandler = new Handler(Looper.getMainLooper());
            }
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    setDataToView(viewId, obj);
                }
            });
        }
    }

    private SparseArray<View> viewSparseArray = new SparseArray<>();            //页面子控件

    private void setDataToView(int viewId, Object object) {
        if (viewSparseArray.get(viewId) != null) {
            finalSetDataToView(viewSparseArray.get(viewId), object);
        } else {
            View v = getView(viewId);
            LOG.d("BaseController", viewId + ".finalSetDataToView  " + viewSparseArray.indexOfValue(v));
            if (v != null) {
                if (viewSparseArray.indexOfValue(v) < 0) {
                    viewSparseArray.append(viewId, v);
                }
                finalSetDataToView(v, object);
            } else {
                LOG.utilLog("Can't find View with id -> ");
            }
        }
    }

    public <T extends View> T getView(int viewId) {
        return proxyObject.get(ActivityImpl.class).findViewById(viewId);
    }

    private final void finalSetDataToView(View view, Object obj) {
        if (view == null) {
            LOG.utilLog("ViewEmpty");
            return;
        }
        if (proxyObject.is(ViewSetter.class) && (proxyObject.get(ViewSetter.class)).fillCustomViewData(view, obj)) {
            return;
        }
        if (obj == null) {
            LOG.utilLog("DataEmpty");
            return;
        }
        if (obj instanceof View.OnClickListener) {
            view.setOnClickListener((View.OnClickListener) obj);
            return;
        }
        if (obj instanceof Integer && ((int) obj == View.VISIBLE || (int) obj == View.GONE || (int) obj == View.INVISIBLE)) {
            view.setVisibility((int) obj);
            return;
        }
        if (obj instanceof ActivityImpl.ViewParam) {
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
        } else {
            LOG.e(proxyObject.get(Object.class).getClass().getSimpleName(), "ControllerProxy can't find finalSetDataToView(" + view.getClass().getSimpleName() + ",Object),please Override Method fillViewData and return true");
        }
    }

    private void fillImageView(ImageView view, Object obj) {
        if (obj instanceof Bitmap) {
            view.setImageBitmap((Bitmap) obj);
        } else if (obj instanceof Integer) {
            view.setImageResource((int) obj);
        } else if (obj instanceof Drawable) {
            view.setImageDrawable((Drawable) obj);
        } else if (obj instanceof String) {
            ImageLoadUtil.display(mContext, (String) obj, view);
        }
    }

    private void fillTextView(TextView view, Object obj) {
        if (obj instanceof CharSequence) {
            view.setText((CharSequence) obj);
        } else if (obj instanceof Integer) {
            view.setText((int) obj);
        }
    }

    private void fillRecyclerViewData(RecyclerView recyclerView, Object obj) {
        if (obj instanceof RecyclerView.Adapter) {
            if (recyclerView.getLayoutManager() == null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            }
            recyclerView.setAdapter((RecyclerView.Adapter) obj);
        } else if (obj instanceof RecyclerView.LayoutManager) {
            recyclerView.setLayoutManager((RecyclerView.LayoutManager) obj);
        }
    }

    private void fillRecyclerViewData(RecyclerView recyclerView, RecyclerUtil util) {
        util.context(mContext);
        if (recyclerView.getLayoutManager() == null && util.context(mContext).build() != null) {
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

    public void startActivity(Class activity, Serializable... pass) {
        Intent intent = new Intent(mContext, activity);
        if (pass != null && pass.length > 0) {
            for (int i = 0; i < pass.length; i++) {
                intent.putExtra(TAG_PASS + i, pass[i]);
            }
        }
        mContext.startActivity(intent);
    }

    @SuppressWarnings({"unchecked"})
    public <T extends Serializable> T getPass(int position, Intent... pass) {
        LOG.e("ControllerProxy", "getPass:400");
        T result = null;
        if (pass != null && pass.length > 0) {
            result = (T) pass[0].getSerializableExtra(TAG_PASS + position);
        }
        if (proxyObject.is(Fragment.class) && proxyObject.get(Fragment.class) != null && proxyObject.get(Fragment.class).getArguments() != null)
            result = (T) proxyObject.get(Fragment.class).getArguments().getSerializable(TAG_PASS + position);
        if (proxyObject.is(Activity.class) && proxyObject.get(Activity.class) != null) {
            result = (T) proxyObject.get(Activity.class).getIntent().getSerializableExtra(TAG_PASS + position);
        }
        if (proxyObject.is(Bundle.class) && proxyObject.get(Bundle.class) != null) {
            result = (T) proxyObject.get(Bundle.class).getSerializable(TAG_PASS + position);
        }
        LOG.e("ControllerProxy", "getPass:410");
        try {
//          根据方法名和参数获取getPass方法
            LOG.e("ControllerProxy", "getPass:413");
            Method method = this.getClass().getMethod("getPass", int.class, Intent[].class);
            Type type = method.getGenericReturnType();// 获取返回值类型
            show(type);
            if (type instanceof ParameterizedType) { // 判断获取的类型是否是参数类型
                Type[] typesto = ((ParameterizedType) type).getActualTypeArguments();// 强制转型为带参数的泛型类型，
                // getActualTypeArguments()方法获取类型中的实际类型，如map<String,Integer>中的
                // String，integer因为可能是多个，所以使用数组
                for (Type type2 : typesto) {
                    LOG.e("ControllerProxy", "getPass:423");
                    Log.e("ControllerProxy", "泛型类型" + type2);
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void show(Type t) {
        Log.e("test", "getClass--:" + t.getClass());
//            Log.e("test", "getClass--:" + t.getClass());
//            Log.e("test", "getClass--:" + t.getClass());
//            Log.e("test", "getClass--:" + t.getClass());
//            Log.e("test", "getClass--:" + t.getClass());
//            Log.e("test", "getClass--:" + t.getClass());
//            Log.e("test", "getClass--:" + t.getClass());
    }
}
