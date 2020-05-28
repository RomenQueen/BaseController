package com.rq.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

public class CompareActivity extends Activity implements View.OnClickListener, OnRefreshLoadMoreListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ex_diff);
        findViewById(R.id.tv_show1).setOnClickListener(this);
        findViewById(R.id.tv_show2).setOnClickListener(this);
        findViewById(R.id.tv_show3).setOnClickListener(this);
        findViewById(R.id.tv_show4).setOnClickListener(this);
        ((SmartRefreshLayout) findViewById(R.id.refuse_layout)).setOnRefreshLoadMoreListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_show1) {
            setData2View(R.id.tv_show1, "CompareActivity-->填充1：" + System.currentTimeMillis());
        } else if (v.getId() == R.id.tv_show2) {
            setData2View(R.id.tv_show2, "CompareActivity-->填充2：" + System.currentTimeMillis());
        } else if (v.getId() == R.id.tv_show3) {
            setData2View(R.id.tv_show3, "CompareActivity-->填充3：" + System.currentTimeMillis());
        } else if (v.getId() == R.id.tv_show4) {
            setData2View(R.id.tv_show4, "CompareActivity-->填充4：" + System.currentTimeMillis());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        finish();
    }

    int page = 1;

    private void setData2View(int tv_show1, String s) {
        ((TextView) findViewById(tv_show1)).setText(s);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mockRequest(++page);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        page = 1;
        mockRequest(1);
    }

    private void mockRequest(final int page) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("CompareNormalActivity", "refuse.over:" + page);
                            setData2View(R.id.tv_refuse_child, "加载完成：" + page);
                            ((SmartRefreshLayout) findViewById(R.id.refuse_layout)).finishLoadMore();
                            ((SmartRefreshLayout) findViewById(R.id.refuse_layout)).finishRefresh();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
