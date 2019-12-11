package com.hzaz.base.recycler;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;


import com.hzaz.base.BASE;
import com.hzaz.base.common_util.LOG;

import java.util.List;

public class RecyclerUtil {
    private volatile Builder mBuilder;

    public RecyclerUtil(Type type) {
        mBuilder = new Builder();
        mBuilder.type = type;
        LOG.e("RecyclerUtil", mBuilder + " 57 row = " + mBuilder.row);
    }

    public RecyclerUtil() {
        String classFrom = new Exception().getStackTrace()[1].getClassName();//调用源 类名
        mBuilder = new Builder();
        LOG.e("RecyclerUtil", mBuilder + " 62  " + classFrom + "  row = " + mBuilder.row);
    }
//
//    @NonNull
//    public RecyclerUtil get() {
//        if (mBuilder == null) {
//            synchronized (RecyclerUtil.class) {
//                if (mBuilder == null) {
//                    mBuilder = new Builder();
//                }
//            }
//        }
//        return this;
//    }

    public RecyclerUtil(RecyclerView.Adapter adapter) {
        mBuilder = new Builder();
        mBuilder.refuseType2();
        mBuilder.adapter = adapter;
        mBuilder.refuseType2();
        mBuilder.adapter = adapter;
        LOG.e("RecyclerUtil", mBuilder + " 72 row = " + mBuilder.row);
    }

    public static RecyclerView.ItemDecoration createDecorationDip(int all) {
        Decoration decoration = new Decoration(all);
        return decoration.build();
    }

    public static RecyclerView.ItemDecoration createDecorationDip(int l, int t, int r, int b) {
        Decoration decoration = new Decoration(0);
        decoration.left(dip2px(l)).top(dip2px(t)).right(dip2px(r)).bottom(dip2px(b));
        return decoration.build();
    }

    public static int dip2px(float dpValue) {
        final float scale = BASE.getCxt().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public RecyclerUtil setAdapter(RecyclerView.Adapter adapter) {
        mBuilder.refuseType2();
        mBuilder.adapter = adapter;
        return this;
    }

    public RecyclerView.ItemDecoration getDecoration() {
        return mBuilder.itemDecoration;
    }

    public RecyclerUtil setDecoration(RecyclerView.ItemDecoration build) {
        mBuilder.itemDecoration = build;
        return this;
    }

    public RecyclerUtil orientation(@RecyclerView.Orientation int orientation) {
        mBuilder.refuseType();
        mBuilder.orientation = orientation;
        return this;
    }

    public RecyclerUtil row(int row) {
        LOG.e("RecyclerUtil", mBuilder + " LINE:86.row = " + row);
        mBuilder.refuseType();
        mBuilder.row = row;
        LOG.e("RecyclerUtil", mBuilder + " 89 row = " + mBuilder.row);
        return this;
    }

    public RecyclerUtil context(Context orientation) {
        mBuilder.context = orientation;
        return this;
    }

    public RecyclerUtil viewHolderContext(Object obj) {//into 指向ViewHolder 为内部类需要此方法
        mBuilder.refuseType();
        mBuilder.objHolder = obj;
        return this;
    }

    public RecyclerUtil canScrollVertically(boolean obj) {//into 指向ViewHolder 为内部类需要此方法
        mBuilder.refuseType();
        mBuilder.canScrollVertical = obj;
        return this;
    }

    public RecyclerUtil setLayoutManager(RecyclerView.LayoutManager manager) {//into 指向ViewHolder 为内部类需要此方法
        mBuilder.refuseType();
        mBuilder.manager = manager;
        return this;
    }

    public Builder getBuilder() {
        return mBuilder;
    }

    public RecyclerView.LayoutManager build() {
        if (mBuilder.manager != null) {
            LOG.e("RecyclerUtil", "StaggeredGridLayoutManager");
            return mBuilder.manager;
        }
        LOG.e("RecyclerUtil", getBuilder() + " 84 row = " + getBuilder().row);
        RecyclerView.LayoutManager llm = getBuilder().row > 1 ? new GridLayoutManager(getBuilder().context, getBuilder().row) {
            @Override
            public boolean canScrollVertically() {
                return getBuilder().canScrollVertical;
            }
        } : new LinearLayoutManager(getBuilder().context) {
            @Override
            public boolean canScrollVertically() {
                return getBuilder().canScrollVertical;
            }
        };
        if (getBuilder().row == 1) {
            ((LinearLayoutManager) llm).setOrientation(getBuilder().orientation);
        } else {
            ((GridLayoutManager) llm).setOrientation(getBuilder().orientation);
        }
        return llm;
    }

    public RecyclerUtil setPageNum(int pageNum) {
        mBuilder.refuseType2();
        mBuilder.pageNum = pageNum;
        return this;
    }

    public RecyclerUtil into(Class clazz) {
        mBuilder.refuseType2();
        mBuilder.viewHolder = clazz;
        return this;
    }

    public RecyclerUtil layout(int layoutId) {
        mBuilder.refuseType2();
        mBuilder.layoutId = layoutId;
        return this;
    }

    public Type getType() {
        return mBuilder.type;
    }

    public int getPage() {
        return mBuilder.pageNum;
    }

    public List getData() {
        return mBuilder.data;
    }

    public RecyclerUtil setData(List pageItems) {
        mBuilder.refuseType2();
        mBuilder.data = pageItems;
        return this;
    }

    public RecyclerView.Adapter adapter() {
        if (getBuilder().adapter != null) return getBuilder().adapter;
        if (getBuilder().layoutId == 0 || getBuilder().viewHolder == null) {
            Log.e("RecyclerUtil", "<layoutId != 0> ===》 " + getBuilder().layoutId + " , <viewHolder != null> ===》 viewHolder = " + getBuilder().viewHolder);
            return null;
        }
        BaseAdapter adapter;
        if (getBuilder().objHolder != null) {
            adapter = new BaseAdapter(getBuilder().context, getBuilder().layoutId, getBuilder().viewHolder, getBuilder().objHolder);
        } else {
            adapter = new BaseAdapter(getBuilder().context, getBuilder().layoutId, getBuilder().viewHolder);
        }
        return adapter;
    }

    public enum Type {
        Adapter, LM, Adp_LM, DATA
    }

    public static class Decoration {

        private int baseInt = 1;
        private int left = 0;
        private int right = 0;
        private int top = 0;
        private int bottom = 0;
        private int all = 0;

        public Decoration() {
        }

        public Decoration(int px) {
            this.baseInt = px;
        }

        public Decoration left(int px) {
            this.left = px;
            return this;
        }

        public Decoration right(int px) {
            this.right = px;
            return this;
        }

        public Decoration top(int px) {
            this.top = px;
            return this;
        }

        public Decoration bottom(int px) {
            this.bottom = px;
            return this;
        }

        public Decoration all(int px) {
            this.all = px;
            return this;
        }

        public ItemDecoration build() {
            return new ItemDecoration(baseInt);
        }

        public class ItemDecoration extends RecyclerView.ItemDecoration {
            private int space;

            /**
             * @param space 子项之间的间距
             */
            public ItemDecoration(float space) {
                this.space = (int) space;
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                // Add top margin only for the no first item to avoid double space between items
                outRect.left = Decoration.this.left * space + Decoration.this.all;
                outRect.right = Decoration.this.right * space + Decoration.this.all;
                outRect.bottom = Decoration.this.top * space + Decoration.this.all;
                outRect.top = Decoration.this.bottom * space + Decoration.this.all;
            }
        }
    }

    public class Builder {
        public List data;
        public int pageNum = 1;
        Type type = Type.Adapter;
        RecyclerView.Adapter adapter;
        @RecyclerView.Orientation
        int orientation = LinearLayout.VERTICAL;
        int row = 1;
        Context context;
        Class viewHolder;
        int layoutId;
        boolean canScrollVertical = true;
        RecyclerView.ItemDecoration itemDecoration;
        Object objHolder;
        RecyclerView.LayoutManager manager;

        void refuseType() {
            if (type == Type.Adapter) {
                mBuilder.type = Type.Adp_LM;
            } else {
                mBuilder.type = Type.LM;
            }
        }

        void refuseType2() {
            if (type == Type.LM) {
                mBuilder.type = Type.Adp_LM;
            } else {
                mBuilder.type = Type.Adapter;
            }
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "type=" + type +
                    ", adapter=" + adapter +
                    ", orientation=" + orientation +
                    ", row=" + row +
                    ", context=" + context +
                    ", data=" + data +
                    ", viewHolder=" + viewHolder +
                    ", layoutId=" + layoutId +
                    ", pageNum=" + pageNum +
                    ", itemDecoration=" + itemDecoration +
                    ", objHolder=" + objHolder +
                    '}';
        }
    }
}
