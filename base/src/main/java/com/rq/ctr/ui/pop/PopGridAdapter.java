package com.rq.ctr.ui.pop;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PopGridAdapter<E extends PopGridAdapter.ShowTextGetter> extends BaseAdapter {

    public static List<PopGridAdapter.ShowTextGetter> getListForStrArr(String... param) {
        if (param == null || param.length == 0) {
            return new ArrayList<>();
        }
        List<PopGridAdapter.ShowTextGetter> result = new ArrayList<>();
        for (int i = 0; i < param.length; i++) {
            result.add(new Debug(param[i]));
        }
        return result;
    }

    static class Debug implements PopGridAdapter.ShowTextGetter {
        String data;

        Debug(String data) {
            this.data = data;
        }

        @Override
        public String getContent() {
            return data;
        }
    }

    public interface ShowTextGetter {
        String getContent();
    }

    private Context mContext;
    private List<E> mBeanList;
    private List<String> mStringList;
    private int itemHeight;
    private int defStyleRes = 0;
    private int layoutId = 0;
    private int textId = 0;

    public PopGridAdapter(Context context, int itemHeight, int defStyleRes, List<E> mBeanList) {
        this.mContext = context;
        this.mBeanList = mBeanList;
        this.itemHeight = itemHeight;
        this.defStyleRes = defStyleRes;
    }

    public PopGridAdapter(Context context, List<String> mBeanList, int itemHeight, int defStyleRes) {
        this.mContext = context;
        this.mStringList = mBeanList;
        this.itemHeight = itemHeight;
        this.defStyleRes = defStyleRes;
    }

    public void setLayout(int layoutId, int textId) {
        this.layoutId = layoutId;
        this.textId = textId;
    }


    @Override
    public int getCount() {
        return mBeanList != null ? mBeanList.size() : mStringList != null ? mStringList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View root;
        TextView textView;
        if (layoutId != 0) {
            root = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
            textView = root.findViewById(textId);
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH && defStyleRes != 0) {
                textView = new TextView(mContext, null, 0, defStyleRes);
            } else {
                textView = new TextView(mContext);
                textView.setGravity(Gravity.CENTER);
            }
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight));
            root = textView;
        }
        if (mBeanList != null) {
            textView.setText(mBeanList.get(position).getContent());
        } else {
            textView.setText(mStringList.get(position));
        }
        int select = 0;
        if (parent != null && parent.getTag() != null && parent.getTag() instanceof Integer) {
            select = (int) parent.getTag();
        }
        textView.setSelected(select == position);
        return root;
    }
}
