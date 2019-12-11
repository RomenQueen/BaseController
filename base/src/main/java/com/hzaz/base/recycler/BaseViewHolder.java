package com.hzaz.base.recycler;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hzaz.base.common_util.LOG;


/**
 * Created by raoqian on 2018/9/21.
 */

public class BaseViewHolder<DATA> extends ViewHolder {
    public static final int TAG_POSITION = Integer.MAX_VALUE - 10;
    public ActionPasser mActionPasser;
    private SparseArray<View> itemViews = new SparseArray<>();
    private Context mContext;
    private DATA mData;
    private Object mObject;

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * 为了保证代码的后期维护，该方法必须重写 且必须与Adapter 初始化描述一致
     */
    public int inflateLayoutId() {//不参与诗句逻辑，只作为调试方法，方便维护
        return 0;
    }

    public void setData(DATA dataItem) {
        this.mData = dataItem;
    }

    public void setObject(Object dataItem) {
        this.mObject = dataItem;
    }

    public void fillData(int position, DATA data) {// 所有Item 数据为同类
    }

    protected Object getData() {
        return mData == null ? mObject : mData;
    }


    public RecyclerView.LayoutParams getLMLayoutParams(ViewGroup.LayoutParams oldLM) {//特殊场景使用  如瀑布流  params.setFullSpan(true);//占满全屏
        return (RecyclerView.LayoutParams) oldLM;
    }

    public void fillObject(@Nullable Object data) {
    }

    public <T extends View> T getItemView(int id) {
        if (itemViews.get(id) != null) {
            return (T) itemViews.get(id);
        } else {
            T view = itemView.findViewById(id);
            itemViews.put(id, view);
            return view;
        }
    }

    @UiThread
    protected void setTextToView(@IdRes int textViewId, CharSequence text) {
        if (getItemView(textViewId) != null) {
            ((TextView) getItemView(textViewId)).setText(text);
        } else if (itemView.findViewById(textViewId) instanceof TextView) {
            itemViews.append(textViewId, itemView.findViewById(textViewId));
            ((TextView) itemView.findViewById(textViewId)).setText(text);
        } else {
            LOG.d("BaseViewHolder", this.getClass().getSimpleName() + "(" + LOG.useLine() + ").setTextToView(" + text + "," + textViewId + "): textViewId error -> " + textViewId + " is not belongs TextView");
        }
    }

    protected String getTextFromView(@IdRes int textViewId) {
        if (itemViews.get(textViewId) != null) {
            return ((TextView) getItemView(textViewId)).getText().toString();
        } else if (itemView.findViewById(textViewId) instanceof TextView) {
            itemViews.append(textViewId, itemView.findViewById(textViewId));
            return ((TextView) itemView.findViewById(textViewId)).getText().toString();
        } else {
            LOG.e("BaseViewHolder", "getTextFromView(" + textViewId + "): textViewId error -> " + textViewId + " is not belongs TextView");
            return "";
        }
    }

    public void setPasser(ActionPasser passer) {
        this.mActionPasser = passer;
    }

    public int getMPosition() {
        return (int) itemView.getTag(TAG_POSITION);
    }

    View.OnClickListener itemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mClick.clickListener.onClick(getData(), v);
        }
    };

    private BaseAdapter.OnClickMaker mClick;

    public void setClickInfo(final BaseAdapter.OnClickMaker info) {
        if (info.clickListener == null) {
            LOG.e("BaseViewHolder", "LINE:86  clickListener 不能为空");
            return;
        }
        this.mClick = info;
        LOG.e("BaseViewHolder", getClass() + ".OnClickMaker.length:" + info.clickIds.length);
        if (info.clickIds != null && info.clickIds.length > 0) {
            for (int clickId : info.clickIds) {
                LOG.e("BaseViewHolder", " OnClickMaker :" + clickId);
                if (clickId == 0) {
                    itemView.setOnClickListener(itemClick);
                } else if (getItemView(clickId) != null) {
                    getItemView(clickId).setOnClickListener(itemClick);
                }
            }
        } else if (info.clickListener != null) {
            itemView.setOnClickListener(itemClick);
        }
    }

    protected Context getContext() {
        return mContext;
    }

    void setContext(Context context) {
        this.mContext = context;
    }

   protected ViewGroup parentView;

    public void setRecyclerView(ViewGroup recyclerView) {
        this.parentView = recyclerView;
    }


    /**
     * #############################################################
     * RecyclerView 刷新保存Editext等内容会有错误
     *#############################################################
     */
}
