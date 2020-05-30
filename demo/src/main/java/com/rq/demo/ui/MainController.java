package com.rq.demo.ui;

import android.view.View;
import android.widget.ImageView;

import com.rq.ctr.common_util.ToastUtil;
import com.rq.ctr.common_util.image.ImageLoadUtil;
import com.rq.ctr.controller_part.BaseController;
import com.rq.ctr.ui.PopWindowManager;
import com.rq.demo.R;
import com.rq.demo.bean.WeatherBean;
import com.rq.demo.contact.EaseContactList;
import com.rq.demo.contact.EaseUser;
import com.rq.demo.contact.PingYingUtil;

import java.util.ArrayList;
import java.util.List;

public class MainController extends BaseController {
    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    WeatherBean passBean;

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        passBean = (WeatherBean) getPass(0);
        ToastUtil.show(passBean.getData().getForecast().get(0).toToast());
        EaseContactList list = getView(R.id.show);
        setOnClickListener(R.id.click);
        List<EaseUser> contactList = new ArrayList<>();
        contactList.add(new DebugUser("点击跳转数据传递"));
        contactList.add(new DebugUser("李四"));
        contactList.add(new DebugUser("王五"));
        contactList.add(new DebugUser("赵柳"));
        contactList.add(new DebugUser("孙琦"));
        contactList.add(new DebugUser("张三2"));
        contactList.add(new DebugUser("李四2"));
        contactList.add(new DebugUser("王五2"));
        contactList.add(new DebugUser("赵柳2"));
        contactList.add(new DebugUser("孙琦2"));
        contactList.add(new DebugUser("张三3"));
        contactList.add(new DebugUser("李四3"));
        contactList.add(new DebugUser("王五3"));
        contactList.add(new DebugUser("赵柳3"));
        contactList.add(new DebugUser("孙琦3"));
        contactList.add(new DebugUser("张三.05"));
        contactList.add(new DebugUser("李四.05"));
        contactList.add(new DebugUser("王五.05"));
        contactList.add(new DebugUser("赵柳.05"));
        contactList.add(new DebugUser("孙琦.05"));
        list.init(contactList);
        list.autoSort(true);
        ImageLoadUtil.display(getContextActivity(), "", 0, new ImageView(getContextActivity()), 0, 0, 0, 6);
    }

    String url = "https://img2.3lian.com/2014/f6/173/d/51.jpg";

    @Override
    public void onClick(View v) {
        super.onClick(v);
        PopWindowManager.showImage(getActivity(), url);
//        open(DataPassExampleCon.class);
    }

    class DebugUser implements EaseUser {
        int position;
        String name = "";

        public DebugUser(int position) {
            this.position = position;
        }

        public DebugUser(String name) {
            this.name = name;
        }

        @Override
        public String getUsername() {
            return name + position;
        }

        @Override
        public String getInitialLetter() {
            return PingYingUtil.getFirstLetter(name + position);
        }

        @Override
        public boolean match(String str) {
            return false;
        }
    }
}
