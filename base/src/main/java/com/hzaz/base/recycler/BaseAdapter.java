package com.hzaz.base.recycler;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hzaz.base.common_util.LOG;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hzaz.base.recycler.BaseViewHolder.TAG_POSITION;


/**
 * Created by raoqian on 2018/9/21
 */

public class BaseAdapter<DATA, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {
    protected SparseArray<Class<? extends BaseViewHolder>> multipleHolder;
    Context mContext;
    Class<?> mHolder;
    private int itemId;
    private Object mObject;
    private boolean checkLayout = true;
    private List<DATA> showData = new ArrayList<>();
    private SparseArray<Class<? extends BaseViewHolder>> headType = new SparseArray<>();//顶部视图处理器类型
    private SparseArray<Object> headViewData = new SparseArray<>();//顶部视图数据
    private SparseIntArray headViewResId = new SparseIntArray();
    private SparseArray<Class<? extends BaseViewHolder>> footType = new SparseArray<>();//底部视图处理器类型
    private SparseArray<Object> footViewData = new SparseArray<>();//底部视图数据
    private SparseIntArray footViewResId = new SparseIntArray();
    private ActionPasser mActionPasser;
    private HashMap<Class, OnClickMaker> clicks = new HashMap<>();
    private OnClickMaker allOnClickInfo;
    private OnAttachedToBottomListener mOnAttachedToBottomListener;
    private Map<String, Object> contentCash = new HashMap<>();

    public BaseAdapter(Context context, @LayoutRes int itemLayoutId, Class<? extends BaseViewHolder> baseViewHolderClass) {
        this(context, itemLayoutId, baseViewHolderClass, null);
    }

    /**
     * 单布局类型  只要一种子View setData 数据条数大于 0  才会显示
     *
     * @param itemLayoutId 对 BrandHolder 的描述，必须与 BaseViewHolder 使用保持一致
     */
    public BaseAdapter(Context context, @LayoutRes int itemLayoutId, Class<? extends BaseViewHolder> baseViewHolderClass, Object obj) {
        if (context == null || baseViewHolderClass == null || itemLayoutId == 0) {
            throw new AdapterUseException("BaseAdapter.使用三参数构造函数 值不能为空");
        }
        this.mContext = context;
        this.mHolder = baseViewHolderClass;
        this.itemId = itemLayoutId;
        this.mObject = obj;
    }

    public BaseAdapter(Context context, @NonNull SparseArray<Class<? extends BaseViewHolder>> maps) {
        this(context, maps, null);
    }

    /**
     * 无序布局   子视图有多种类型
     * 不固定位置
     *
     * @param maps key  对 BrandHolder 的描述，value key布局对应的 BaseViewHolder.class，必须与 BaseViewHolder 使用保持一致
     */
    public BaseAdapter(Context context, @NonNull SparseArray<Class<? extends BaseViewHolder>> maps, Object obj) {
        this.mContext = context;
        this.multipleHolder = maps;
        this.mObject = obj;
        getMultipleHolderType(null, 0);//进行检测
    }

    /**
     * 追加型布局
     */
    public BaseAdapter(Context context) {
        this.mContext = context;
    }

    public void changeItemView(int viewLayout, boolean refuse) {
        this.itemId = viewLayout;
        this.checkLayout = false;
        if (refuse) {
            notifyDataSetChanged();
        }
    }

    /**
     * 直接刷新视图，数据为空将置空列表
     *
     * @param dataList 填充数据
     */
    public void setData(List dataList) {
        this.showData.clear();
        if (dataList != null) {
            this.showData = dataList;
        }
        this.notifyDataSetChanged();
    }

    public void addData(List dataList) {
        if (dataList != null) {
            this.showData.addAll(dataList);
            this.notifyDataSetChanged();
        }
    }

    public DATA getDataItem(int position) {
        if (position >= 0 && position < showData.size()) {
            return showData.get(position);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (multipleHolder != null) {
            return getMultipleHolderType(getDataItem(position), position);
        }
        if (position < headViewData.size() || position >= headViewData.size() + showData.size()) {
            return position * -1 - 1;
        }

        return position;
    }

    /**
     * @param dataItem 数据内容
     * @param position 数据位置
     * @return 返回布局Id
     */
    protected int getMultipleHolderType(DATA dataItem, int position) {
        throw new AdapterUseException(" 多类型布局使用错误，必须复写 getMultipleHolderType() 方法,并且不调用父类方法  ");
    }

    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VH viewHolder;
        if (multipleHolder != null) {
            Class clazz = multipleHolder.get(viewType);
            if (clazz == null) {
                throw new AdapterUseException(" 多类型布局使用错误，multipleHolder key 为 R.layout.id   value 为 Holder.class  ");
            }
            viewHolder = getViewHolderByClass(clazz, viewType, parent, viewType);
        } else if (viewType < 0) {
            int realPosition = (viewType + 1) * -1;
            if (realPosition < headType.size()) {
                final Class<?> holderRoot = headType.get(realPosition);
                LOG.e("BaseAdapter", "onCreateViewHolder.viewType: " + viewType + "  " + holderRoot);
                viewHolder = getViewHolderByClass(holderRoot, headViewResId.get(realPosition), parent, realPosition);
            } else {
                int tagPosition = realPosition - headViewData.size() - showData.size();
                viewHolder = getViewHolderByClass(footType.get(tagPosition), footViewResId.get(tagPosition), parent, realPosition);
            }

        } else {
            viewHolder = getViewHolderByClass(mHolder, this.itemId, parent, viewType);
        }
        if (viewHolder != null) {
            LOG.e("OnClickMaker", viewHolder.getClass() + " >> " + clicks.get(viewHolder.getClass()));
            OnClickMaker itemOnClickMaker = clicks.get(viewHolder.getClass());
            if (allOnClickInfo != null) {
                viewHolder.setClickInfo(allOnClickInfo);
            } else if (itemOnClickMaker != null) {
                viewHolder.setClickInfo(itemOnClickMaker);
            }
            viewHolder.setContext(mContext);
            viewHolder.itemView.setLayoutParams(viewHolder.getLMLayoutParams(viewHolder.itemView.getLayoutParams()));
        }
        return viewHolder;
    }

    private Object getMore() {
        return mObject;
    }

    /**
     * 通过反射获取Holder实例
     */
    protected VH getViewHolderByClass(@NonNull Class<?> holderRoot, @LayoutRes int resId, ViewGroup parent, int viewType) {
        String error = "";
        try {
            Constructor<?>[] ctors = holderRoot.getDeclaredConstructors();
            if (ctors != null && ctors.length > 0) {
                View itemView = LayoutInflater.from(mContext).inflate(resId, parent, false);
                VH holder;
                try {
                    holder = (VH) ctors[0].newInstance(itemView);
                } catch (IllegalArgumentException e) {
                    if (getMore() != null) {
                        holder = (VH) ctors[0].newInstance(getMore(), itemView);
                    } else {
                        throw e;
                    }
                }

                if (holder == null) {
                    throw new AdapterUseException(holderRoot.getSimpleName() + " 获取到了一个空  Holder -_-||  --> " + viewType);
                }
                holder.setRecyclerView(parent);
                if (checkLayout && holder.inflateLayoutId() != resId) {
                    throw new AdapterUseException(holderRoot.getSimpleName() + " 布局使用错误,请重写viewHolder.inflateLayoutId   --> " + viewType);
                }
                if (mActionPasser != null) {
                    holder.setPasser(mActionPasser);
                }
                return holder;
            }
        } catch (InstantiationException e) {
            error = e.getMessage();
        } catch (IllegalAccessException e) {
            error = e.getMessage();
        } catch (Exception e) {
            error = e.getMessage();
        }
        if (error != null && error.contains("Wrong number of arguments")) {
            error = error + "\n【【【" + holderRoot.getSimpleName() + ".调用类内部类ViewHolder 调用四参数构造方法 或者 重写 getMore() 内容 】】】";
        }
        throw new AdapterUseException(holderRoot.getSimpleName() + " 初始化异常:" + error);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.itemView.setTag(TAG_POSITION, position);
        if (holder instanceof OnContentKeeper) {
            LOG.e("onBindViewHolder", "addView");
            LOG.e("BaseAdapter", "onBindViewHolder: " + holder.getPosition());
            useCash(holder);
            return;
        }
        if (multipleHolder != null) {
            holder.fillObject(getDataItem(position));
        } else if (position < headViewData.size()) {
            holder.setObject(headViewData.get(position));
            holder.fillObject(headViewData.get(position));
        } else if (position >= headViewData.size() + showData.size()) {
            holder.setObject(footViewData.get(position - headViewData.size() - showData.size()));
            holder.fillObject(footViewData.get(position - headViewData.size() - showData.size()));
        } else {
            holder.setData(getDataItem(position - headViewData.size()));
            holder.fillData(position - headViewData.size(), getDataItem(position - headViewData.size()));
        }
    }

    @Override
    public int getItemCount() {
        return showData.size() + headViewData.size() + footViewData.size();
    }

    /**
     * 由于 SparseArray 的特性，add***Holder只能从前面往后面追加，要增加指定顺序需更换为HasMap
     * 需要进行替换，使用以下方法
     *
     * @see #setFootHolder(int, Object, Class, int, Object)
     * @see #setHeadHolder(int, Object, Class, int, Object)
     */
    public void addFootHolder(Object object, Class<? extends BaseViewHolder> footHolder, @LayoutRes int resId, Object more) {
        int oldSize = footType.size();
        footType.put(footType.size(), footHolder);
        footViewData.put(oldSize, object);
        footViewResId.put(oldSize, resId);
        this.mObject = more;
    }

    public void addFootHolder(Class<? extends BaseViewHolder> footHolder, @LayoutRes int resId, Object more) {
        addFootHolder(null, footHolder, resId, more);
    }

    public void addHeadHolder(Class<? extends BaseViewHolder> headHolder, @LayoutRes int resId, Object more) {
        addHeadHolder(null, headHolder, resId, more);
    }

    public void addHeadHolder(Object object, Class<? extends BaseViewHolder> headHolder, @LayoutRes int resId, Object more) {
        int oldSize = headType.size();
        headType.put(headType.size(), headHolder);
        headViewData.put(oldSize, object);
        headViewResId.put(oldSize, resId);
        this.mObject = more;
    }

    /**
     * @see #addFootHolder(Object, Class, int, Object)
     */
    public void setFootHolder(int position, Object object, Class<? extends BaseViewHolder> footHolder, @LayoutRes int resId, Object more) {
        if (footType.size() < position) {
            addHeadHolder(object, footHolder, resId, more);
            return;
        }
        int oldSize = footType.size();
        footType.put(footType.size(), footHolder);
        footViewData.put(oldSize, object);
        footViewResId.put(oldSize, resId);
        this.mObject = more;
    }

    public void setHeadHolder(int position, Object object, Class<? extends BaseViewHolder> headHolder, @LayoutRes int resId, Object more) {
        if (headType.size() < position) {
            addHeadHolder(object, headHolder, resId, more);
            return;
        }
        headType.delete(position);
        headType.put(position, headHolder);
        headViewData.delete(position);
        headViewData.put(position, object);
        headViewResId.delete(position);
        headViewResId.put(position, resId);
        this.mObject = more;
    }

    public void clearHeadView() {
        headType.clear();
        headViewData.clear();
        headViewResId.clear();
        notifyDataSetChanged();
    }

    public void clearFootView() {
        footType.clear();
        footViewData.clear();
        footViewResId.clear();
    }

    public void setActionPasser(ActionPasser passer) {
        this.mActionPasser = passer;
    }

//    public void addOnClick(Class<ThirdAppViewHolder> thirdAppViewHolderClass, ThirdController thirdController, int i, int iv_delete) {
//    }

    @Override
    public void onViewDetachedFromWindow(@NonNull VH holder) {
        if (holder instanceof OnContentKeeper) {
            setCash(holder);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull VH holder) {
        if (holder instanceof OnContentKeeper) {
            useCash(holder);
        }
        if (holder.getAdapterPosition() == getItemCount() - 1) {
            onAttachedToBottom(holder.getAdapterPosition());
        } else if (holder.getAdapterPosition() == 0) {
            onAttachedToTop();
        }
    }

    public void removeItem(int deletePosition) {
        if (deletePosition >= 0 && showData.size() > 0 && showData.size() > deletePosition) {
            showData.remove(deletePosition);
            notifyDataSetChanged();
        }
    }

    // id = 0   或者不设置 id 则将点击事件绑定到最外层 ItemView
    public <T extends BaseViewHolder> void addOnClick(Class<T> clazz, OnItemClickListener clickListener, int... ids) {
        if (clazz != null && clickListener != null) {
            clicks.put(clazz, new OnClickMaker(clickListener, ids));
        } else if (clickListener != null) {
            setChildClick(clickListener, ids);
        }
    }

    public void setChildClick(OnItemClickListener clickListener, int... ids) {
        allOnClickInfo = new OnClickMaker(clickListener, ids);
    }

    public void setOnAttachedToBottomListener(OnAttachedToBottomListener l) {
        this.mOnAttachedToBottomListener = l;
    }

    protected void onAttachedToBottom(int position) {
        if (mOnAttachedToBottomListener != null) {
            mOnAttachedToBottomListener.onAttachedToBottom(position);
        }
        LOG.d("BaseAdapter", "onAttachedToBottom: ");
    }

    Object headData;

    public void setHeadData(Object headData) {
        this.headData = headData;
        if (headViewData.size() == 0) {
            headViewData.append(0, headData);
        } else if (headViewData.get(0) == null) {
            headViewData.remove(0);
            headViewData.append(0, headData);
        }
        this.notifyDataSetChanged();
    }

    public interface DisplayOption {
        /**
         * @param data 需要判断的数据
         * @param tag  提供的判断条件
         * @return 是否显示该数据
         */
        boolean show(Object data, Object tag);
    }

    private DisplayOption mDisplayOption;

    public void setDisplay(DisplayOption mDisplayOption) {
        this.mDisplayOption = mDisplayOption;
    }

    List<DATA> originData = new ArrayList<>();

    /**
     * you must used {@link #setDisplay(DisplayOption)} before display
     *
     * @param tag
     * @return
     */
    public boolean display(Object tag) {
        final int showSize = showData.size();
        final int originSize = originData.size();
        LOG.e("BaseAdapter", originSize + " display " + showSize);
        if (mDisplayOption == null) {
            LOG.e("BaseAdapter", "display error:you must used setDisplay before display");
            return false;
        }
        if (tag == null) {
            if (originData.size() > showData.size()) {
                setData(deepCopy(originData));
                return true;
            }
            return false;
        }
        if (showSize > originSize) {
            originData.clear();
            originData.addAll(showData);
        }
        boolean hasChange = false;
        List<DATA> newShow = new ArrayList<>();
        for (DATA item : originData) {
            LOG.e("BaseAdapter", "display.for");
            if (mDisplayOption.show(item, tag)) {
                LOG.e("BaseAdapter", "display.:" + item);
                newShow.add(item);
                hasChange = true;
            }
        }
        setData(newShow);
        LOG.e("BaseAdapter", "display.end " + newShow.size() + "  " + showData.size() + "  " + originData.size());
        return hasChange;
    }

    public static <E> List<E> deepCopy(List<E> src) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            @SuppressWarnings("unchecked")
            List<E> dest = (List<E>) in.readObject();
            return dest;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    protected void onAttachedToTop() {
        LOG.d("BaseAdapter", "onAttachedToTop: ");
    }

    /**
     * ##########################################################################
     * RecyclerView 快速滑动时 EditText 等视图内容会随着视图复用而造成数据多余
     * Holder.class 实现 OnContentKeeper 即可解决此问题
     * ##########################################################################
     */

    public void setCash(VH holder) {//存储数据
        if (((OnContentKeeper) holder).getSaveViewId() == null || ((OnContentKeeper) holder).getSaveViewId().length == 0) {
            return;
        }
        for (int saveContentId : ((OnContentKeeper) holder).getSaveViewId()) {
            Object save = ((OnContentKeeper) holder).getSave(saveContentId);
            if (save != null) {
                contentCash.put(holder.getPosition() + "" + saveContentId, save);
                LOG.e("BaseAdapter", holder.getPosition() + " setCash: " + save);
            }
        }
    }

    public void useCash(VH holder) {//使用存储数据填充视图
        if (((OnContentKeeper) holder).getSaveViewId() == null || ((OnContentKeeper) holder).getSaveViewId().length == 0) {
            return;
        }
        for (int saveContentId : ((OnContentKeeper) holder).getSaveViewId()) {
            Object value = contentCash.get(holder.getPosition() + "" + saveContentId);
            ((OnContentKeeper) holder).onRelease(value, saveContentId);
        }
    }

    public interface OnAttachedToBottomListener {
        void onAttachedToBottom(int position);
    }

    class OnClickMaker {

        OnItemClickListener clickListener;
        int[] clickIds;

        public OnClickMaker(OnItemClickListener clickListener, int[] ids) {
            this.clickListener = clickListener;
            this.clickIds = ids;
        }
    }

    public interface OnItemClickListener<DATA> {
        void onClick(DATA data, View view);
    }

}
