package com.rq.demo;

import android.content.Intent;
import android.view.View;

import com.rq.ctr.controller_part.BaseController;
import com.rq.ctr.impl_part.OnClick;


public class GuideController2 extends BaseController {
    @Override
    public int getLayoutId() {
        return R.layout.activity_example;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        setData2View(R.id.show, System.currentTimeMillis() + " -> " + this.getClass().getSimpleName());
    }

    int time = 0;
    int clickId = 0;
    long startTime;
    float startMemory;
    String showRes = "";
    float max = 0;
    String name;

    @Override
    protected void onResume() {
        super.onResume();
        if (time > 0 && time < 1_000) {
            if (time == 1) {
                startTime = System.currentTimeMillis();
                max = getCurrentMemoryInfo();
                startMemory = getCurrentMemoryInfo();
                showRes = getAllMemoryInfo() + "\n" + "getCurrentMemoryInfo = " + max + " M\n";
            }
            if (time == 999) {
                showRes += ("最后一次打开--> " + System.currentTimeMillis() + "\n 最后信息：" + getAllMemoryInfo() + "\n\n" + name + "\n 一千次耗时:" + (System.currentTimeMillis() - startTime)
                        + "\n\nmax = " + max + "\n\n增加：" + (getCurrentMemoryInfo() - startMemory));
                setData2View(R.id.tv_result, showRes);
                startTime = 0;
                max = 0;
            } else {
                max = Math.max(max, getCurrentMemoryInfo());
                setData2View(R.id.tv_result, "重开测试--> " + time);
            }
            time++;
            skip(getView(clickId));
        }
    }

    private String getAllMemoryInfo() {
        //最大分配内存获取方法2
        float maxMemory = (float) (Runtime.getRuntime().maxMemory() * 1.0 / (1024 * 1024));
        //当前分配的总内存
        float totalMemory = (float) (Runtime.getRuntime().totalMemory() * 1.0 / (1024 * 1024));
        //剩余内存
        float freeMemory = (float) (Runtime.getRuntime().freeMemory() * 1.0 / (1024 * 1024));
        String res = "\nmaxMemory:" + maxMemory + "\ntotalMemory:" + totalMemory + "\nfreeMemory:" + freeMemory;
        return res;
    }

    private float getCurrentMemoryInfo() {
        //当前分配的总内存
        float totalMemory = (float) (Runtime.getRuntime().totalMemory() * 1.0 / (1024 * 1024));
        return totalMemory;
    }


    @OnClick({R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4})
    public void onClick(View v) {
        clickId = v.getId();
        time = 1;
        skip(v);
    }

    private void skip(View v) {
        if (v.getId() == R.id.btn_1) {
            name = "按钮一";
            open(CompareController.class);
        } else if (v.getId() == R.id.btn_2) {
        } else if (v.getId() == R.id.btn_3) {
            name = "按钮3";
            getContextActivity().startActivity(new Intent(getContextActivity(), CompareActivity.class));
        } else if (v.getId() == R.id.btn_4) {
            name = "按钮4";
            getContextActivity().startActivity(new Intent(getContextActivity(), CompareNormalActivity.class));
        }
    }
}
