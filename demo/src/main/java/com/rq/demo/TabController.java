package com.rq.demo;

import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.rq.ctr.R;
import com.rq.ctr.common_util.ToastUtil;
import com.rq.ctr.controller_part.BaseController;
import com.rq.ctr.controller_part.BaseFragment;
import com.rq.ctr.ui.FragmentSaveTabHost;

public class TabController extends BaseController implements TabHost.OnTabChangeListener, FragmentSaveTabHost.OnChangeInterceptor, View.OnClickListener {

    @Override
    public boolean needOutScroll() {
        return false;
    }

    @Override
    public boolean underStatusBar() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_default_tab;
    }

    private String texts[] = {"首页", "资讯", "", "直播", "商城"};
    private int imageButton[] = {
            R.drawable.selector_home_pic_demo,
            R.drawable.selector_home_pic_demo,
            0,
            R.drawable.selector_home_pic_demo,
            R.drawable.selector_home_pic_demo,
    };
    private Class fragmentArray[] = {
            GuideController.class,//首页
            GuideController2.class,//团队
            null,//空
            null,//直播
            null//空
    };

    @Override
    public boolean changeAble(String tabId, int position) {
        boolean canOpen = fragmentArray[position] != null;
        if (!canOpen) {
            ToastUtil.show("暂未开通");
        }
        return canOpen;
    }

    @Override
    public final void onViewCreated() {
        super.onViewCreated();
        FragmentSaveTabHost tabhost = getView(android.R.id.tabhost);
        tabhost.setup(getActivity(), getActivity().getSupportFragmentManager(), R.id.main_content);
        for (int i = 0; i < texts.length; i++) {
            TabHost.TabSpec spec = tabhost.newTabSpec(texts[i]).setIndicator(getItemView(i));
            tabhost.addTab(spec, fragmentArray[i], null, i);//R.layout.activity_main_tab
        }
        tabhost.getTabWidget().setDividerDrawable(null);
        tabhost.setOnTabChangedListener(this);
        tabhost.setChangeInterceptor(this);
        setOnClickListener(this, R.id.iv_main_center, R.id.iv_main_center_bottom);
    }

    private View getItemView(int i) {
        //取得布局实例
        View view = View.inflate(getActivity(), R.layout.activity_default_tab_item, null);

        //取得布局对象
        ImageView imageView = view.findViewById(R.id.image);
        TextView textView = view.findViewById(R.id.text);
        //设置图标
        imageView.setImageResource(imageButton[i]);
        //设置标题
        textView.setText(texts[i]);
        return view;
    }

    String fraTag;

    @Override
    public void onTabChanged(String tabId) {
        this.fraTag = tabId;
//        setImmersion(!"我的".equals(tabId), R.color.colorPrimary_ff2a3b);
    }

    @Override
    public boolean interruptBack() {
        BaseFragment fragment = (BaseFragment) getActivity().getSupportFragmentManager().findFragmentByTag(fraTag);
        if (fragment != null) {
            return fragment.onBreakPress();
        }
        return false;
    }

    @Override
    public void onClick(View v) {

    }
}
