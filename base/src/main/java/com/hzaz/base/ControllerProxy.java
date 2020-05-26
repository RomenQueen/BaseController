package com.hzaz.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
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

import com.hzaz.base.common_util.AppUtil;
import com.hzaz.base.common_util.LOG;
import com.hzaz.base.common_util.image.ImageLoadUtil;
import com.hzaz.base.impl_part.BaseControllerImpl;
import com.hzaz.base.impl_part.ViewController;
import com.rq.rvlibrary.BaseAdapter;
import com.rq.rvlibrary.RecyclerUtil;

public class ControllerProxy implements ViewController {
    BaseControllerImpl impl;
    Context mContext;
    View rootView;

    public ControllerProxy(Context con, Class impl) {
        try {
            this.impl = (BaseControllerImpl) impl.newInstance();
            this.mContext = con;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public View getLayoutView() {
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        if (impl == null) {
            Log.e("ControllerProxy", "初始化异常，请阅读文档");
            return new View(mContext);
        }
        View root;
        if (impl.needOutScroll()) {//是否添加外部滑动控件，可自行适配小屏手机
            if (BASE.commonLayoutId != 0) {
                root = inflater.inflate(BASE.commonLayoutId, null);
            } else {
                root = inflater.inflate(R.layout.activity_base, null);
            }
            final ViewGroup container = root.findViewById(R.id.base_container);
            final View content = inflater.inflate(impl.getLayoutId(), null);
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
        } else {
            root = inflater.inflate(R.layout.activity_base_no_scroller, null);
            final ViewGroup container = root.findViewById(R.id.base_container);
            final View content = inflater.inflate(impl.getLayoutId(), null);
            container.addView(content);
        }
        if (root.findViewById(R.id.common_status_bar) != null) {
            ViewGroup.LayoutParams params = root.findViewById(R.id.common_status_bar).getLayoutParams();
            params.height = AppUtil.getStatusBarHeight();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            root.findViewById(R.id.common_status_bar).setLayoutParams(params);
        }
        rootView = root;
        impl.setViewController(this);
        return root;
    }

    public void initView() {
        impl.initView();
    }

    @Override
    public void setView2Data(int viewId, Object obj) {
        setData2View(viewId, obj);
    }

    @Override
    public void setOnClickListener(int[] ids, View.OnClickListener clickListener) {
        if (ids != null && clickListener != null) {
            for (int i = 0; i < ids.length; i++) {
                if (getView(ids[i]) != null) getView(ids[i]).setOnClickListener(clickListener);
            }
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
    private SparseArray<Object> dataSparseArray = new SparseArray<>();          //页面数据

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

    public View getView(int viewId) {
        return rootView.findViewById(viewId);
    }

    protected final synchronized void setDataToView(View view, Object obj) {
        if (view == null) {
            LOG.utilLog("ViewEmpty");
            return;
        }
        if (impl.fillCustomViewData(view, obj)) {
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

}
