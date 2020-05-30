package com.rq.ctr.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.rq.ctr.NetResponseViewImpl;
import com.rq.ctr.R;
import com.rq.ctr.common_util.image.ImageLoadUtil;
import com.rq.ctr.ui.pop.CustomPopWindow;
import com.rq.ctr.ui.pop.PopGridAdapter;

import java.util.List;

import static com.rq.ctr.ui.PopWindowManager.OnPhotoFromSelectListener.CANCEL;
import static com.rq.ctr.ui.PopWindowManager.OnPhotoFromSelectListener.SELECT_PHOTO;
import static com.rq.ctr.ui.PopWindowManager.OnPhotoFromSelectListener.SELECT_TAKE;

/**
 * 注意在Onclick 事件中使用 get***Window();
 */
public class PopWindowManager {

    public interface OnPopWindowSelectListener {
        void onItemClick(int position, Object data, View clickFrom);
    }

    public static CustomPopWindow getListPop(
            Context context, View view, final List<String> list,
            final OnPopWindowSelectListener mClickListener) {
        return getListPop(context, view, list, mClickListener, 0, 0);
    }

    /**
     * @param context
     * @param clickFrom
     * @param list
     * @param mClickListener
     * @param itemTheme      子项的xml特殊属性 style="@style/..."### API >= 21生效 ###
     * @param parentTheme    列表的xml特殊属性 style="@style/..."### API >= 21生效 ###
     * @return
     */
    public static CustomPopWindow getListPop(
            Context context, final View clickFrom, final List<String> list,
            final OnPopWindowSelectListener mClickListener, int itemTheme, int parentTheme) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_brand_grid, null);
        //处理popWindow 显示内容
        final PopGridAdapter mAdapter;
        ListView listView;//= contentView.findViewById(R.id.gridview);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            if (parentTheme == 0) {
                parentTheme = R.style.PopList;
            }
            listView = new ListView(context, null, 0, parentTheme);
        } else {
            listView = new ListView(context);
        }
        LinearLayout container = contentView.findViewById(R.id.ll_container);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        container.addView(listView, lp);
        final CustomPopWindow popWindow = new CustomPopWindow.PopupWindowBuilder(context)
                .setView(contentView)
                .sizeAs(clickFrom)
                .create()
                .showAsDropDown(clickFrom);
        contentView.findViewById(R.id.view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindow.dissmiss();
            }
        });
        mAdapter = new PopGridAdapter(context, list, clickFrom.getHeight(), itemTheme);
//        mBrandPopGridAdapter.setSelectPosition(selectPosition);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.notifyDataSetChanged();
                popWindow.dissmiss();
                if (mClickListener != null) {
                    mClickListener.onItemClick(position, list.get(position), clickFrom);
                }
            }
        });
        //创建并显示popWindow
        return popWindow;
    }

    /**
     * @param context
     * @param clickFrom
     * @param list
     * @param mClickListener
     * @param itemTheme      子项的xml特殊属性 style="@style/..."### API >= 21生效 ###
     * @param parentTheme    列表的xml特殊属性 style="@style/..."### API >= 21生效 ###
     * @return
     */
    public static <E extends PopGridAdapter.ShowTextGetter> CustomPopWindow getObjectPop(
            Context context, final View clickFrom,
            final OnPopWindowSelectListener mClickListener, int itemTheme, int parentTheme, final List<E> list) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_brand_grid, null);
        //处理popWindow 显示内容
        final PopGridAdapter mAdapter;
        ListView listView;//= contentView.findViewById(R.id.gridview);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            if (parentTheme == 0) {
                parentTheme = R.style.PopList;
            }
            listView = new ListView(context, null, 0, parentTheme);
        } else {
            listView = new ListView(context);
        }
        LinearLayout container = contentView.findViewById(R.id.ll_container);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        container.addView(listView, lp);
        final CustomPopWindow popWindow = new CustomPopWindow.PopupWindowBuilder(context)
                .setView(contentView)
                .sizeAs(clickFrom)
                .create()
                .showAsDropDown(clickFrom);
        contentView.findViewById(R.id.view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindow.dissmiss();
            }
        });
        mAdapter = new PopGridAdapter(context, clickFrom.getHeight(), itemTheme, list);
//        mBrandPopGridAdapter.setSelectPosition(selectPosition);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.notifyDataSetChanged();
                popWindow.dissmiss();
                if (mClickListener != null) {
                    mClickListener.onItemClick(position, list.get(position), clickFrom);
                }
            }
        });
        //创建并显示popWindow
        return popWindow;
    }

    public interface OnSelectListener {
        void onItemClick(SelectListener data, View clickFrom);
    }

    public static <E extends PopGridAdapter.ShowTextGetter> CustomPopWindow getShanXuanPop(
            Context context, View view,
            final List<E> time,
            final List<E> type,
            final OnSelectListener mClickListener) {
        return getShanXuanPop(context, view, type, time, mClickListener, 0, 0);
    }

    public static class SelectListener {
        PopGridAdapter.ShowTextGetter time;
        PopGridAdapter.ShowTextGetter type;

        public PopGridAdapter.ShowTextGetter getTime() {
            return time;
        }

        public void setTime(PopGridAdapter.ShowTextGetter time) {
            this.time = time;
        }

        public PopGridAdapter.ShowTextGetter getType() {
            return type;
        }

        public void setType(PopGridAdapter.ShowTextGetter type) {
            this.type = type;
        }
    }

    public static <E extends PopGridAdapter.ShowTextGetter> CustomPopWindow getShanXuanPop(
            Context context, final View clickFrom,
            final List<E> time,
            final List<E> type,
            final OnSelectListener mClickListener, int itemRes, int itemTheme) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_shanxuan_grid, null);
        //处理popWindow 显示内容
        GridView timeGrid = contentView.findViewById(R.id.grid_time);
        GridView typeGrid = contentView.findViewById(R.id.grid_type);
        final SelectListener selectListener = new SelectListener();
        final PopGridAdapter timeAdapter;
        final PopGridAdapter typeAdapter;

        timeAdapter = new PopGridAdapter(context, clickFrom.getHeight(), itemRes, time);
        timeAdapter.setLayout(R.layout.base_pop, R.id.base_text);
        timeGrid.setNumColumns(3);
        timeGrid.setAdapter(timeAdapter);

        typeAdapter = new PopGridAdapter(context, clickFrom.getHeight(), itemRes, type);
        typeAdapter.setLayout(R.layout.base_pop, R.id.base_text);
        typeGrid.setNumColumns(3);
        typeGrid.setAdapter(typeAdapter);

        final CustomPopWindow popWindow = new CustomPopWindow.PopupWindowBuilder(context)
                .setView(contentView)
                .sizeAs(null)
                .create()
                .showAsDropDown(clickFrom);
        View.OnClickListener disMiss = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindow.dissmiss();
            }
        };
        contentView.findViewById(R.id.view_left).setOnClickListener(disMiss);
        contentView.findViewById(R.id.view).setOnClickListener(disMiss);
        contentView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dissmiss();
                if (mClickListener != null) {
                    mClickListener.onItemClick(new SelectListener(), clickFrom);
                }
            }
        });
        contentView.findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dissmiss();
                if (mClickListener != null) {
                    mClickListener.onItemClick(selectListener, clickFrom);
                }
            }
        });
        timeGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                parent.setTag(position);
                timeAdapter.notifyDataSetChanged();
                selectListener.setTime(time.get(position));
            }
        });
        typeGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                parent.setTag(position);
                typeAdapter.notifyDataSetChanged();
                selectListener.setType(type.get(position));
            }
        });
        //创建并显示popWindow
        return popWindow;
    }


    public interface OnPhotoFromSelectListener {
        int SELECT_PHOTO = 1;
        int SELECT_TAKE = 2;
        int CANCEL = 0;

        boolean onSelect(int select);
    }

    public static CustomPopWindow getBottomPopwindow(
            NetResponseViewImpl view, @NonNull final OnPhotoFromSelectListener listener) {
        View contentView = LayoutInflater.from(view.getContextActivity()).inflate(R.layout.pop_photo, null);
        View root = view.getContextActivity().getWindow().getDecorView();
        //处理popWindow 显示内容
        final CustomPopWindow popWindow = new CustomPopWindow.PopupWindowBuilder(view.getContextActivity())
                .setView(contentView)
                .sizeAs(null)
                .create()
                .showAtLocation(root, Gravity.BOTTOM, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentView.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener.onSelect(CANCEL)) {
                    popWindow.dissmiss();
                }
            }
        });
        contentView.findViewById(R.id.tv_take).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener.onSelect(SELECT_TAKE)) {
                    popWindow.dissmiss();
                }
            }
        });
        contentView.findViewById(R.id.tv_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener.onSelect(SELECT_PHOTO)) {
                    popWindow.dissmiss();
                }
            }
        });
        //创建并显示popWindow
        return popWindow;
    }

    public static CustomPopWindow getPhotoWindow(NetResponseViewImpl view, @NonNull final OnPhotoFromSelectListener listener) {
        View contentView = LayoutInflater.from(view.getContextActivity()).inflate(R.layout.pop_photo, null);
        final CustomPopWindow popWindow = new CustomPopWindow.PopupWindowBuilder(view.getContextActivity())
                .setView(contentView)
                .sizeAs(null).create();
        contentView.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener.onSelect(CANCEL)) {
                    popWindow.dissmiss();
                }
            }
        });
        contentView.findViewById(R.id.tv_take).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener.onSelect(SELECT_TAKE)) {
                    popWindow.dissmiss();
                }
            }
        });
        contentView.findViewById(R.id.tv_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener.onSelect(SELECT_PHOTO)) {
                    popWindow.dissmiss();
                }
            }
        });
        popWindow.showAtLocation(view.getContextActivity().getWindow().getDecorView(), Gravity.BOTTOM, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return popWindow;
    }

    public static void showImage(Activity view, String path) {
        View contentView = LayoutInflater.from(view).inflate(R.layout.pop_image, null);
        final CustomPopWindow popWindow = new CustomPopWindow.PopupWindowBuilder(view)
                .setView(contentView)
                .sizeAs(null).create();
        ImageView img = contentView.findViewById(R.id.iv_show);
        ImageLoadUtil.display(view, path, img);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dissmiss();
            }
        });
        popWindow.showAtLocation(view.getWindow().getDecorView(), Gravity.CENTER, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
}
