package com.rq.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.rq.ctr.controller_part.BaseActivity;

public class CompareStartActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        findViewById(R.id.btn_4).setOnClickListener(this);
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
        if (time > 0 && time <= 1000) {
            if (time == 1) {
                startTime = System.currentTimeMillis();
                max = getCurrentMemoryInfo();
                startMemory = getCurrentMemoryInfo();
                showRes = getAllMemoryInfo() + "\n" + "getCurrentMemoryInfo = " + startMemory + " M\n";
            }
            if (time == 1000) {
                showRes += ("最后一次打开--> " + System.currentTimeMillis() +
                        "\n 最后信息：" + getAllMemoryInfo() +
                        "\n\n" + name +
                        "\n 一千次耗时:" + (System.currentTimeMillis() - startTime)
                        + "\n始/终/Max  --> "+startMemory+"/"+getCurrentMemoryInfo()+"/"+max+" MB");
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

    private void setData2View(int tv_result, String showRes) {
        ((TextView) findViewById(tv_result)).setText(showRes);
    }

    private View getView(int clickId) {
        return findViewById(clickId);
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
        float freeMemory = (float) (Runtime.getRuntime().freeMemory() * 1.0 / (1024 * 1024));
        return totalMemory - freeMemory;
    }


    @Override
    public void onClick(View v) {
        clickId = v.getId();
        time = 1;
        skip(v);
    }

    private void skip(View v) {
        if (v.getId() == R.id.btn_1) {
            name = "按钮1";
            BaseActivity.open(this, CompareController.class);
        } else if (v.getId() == R.id.btn_2) {
        } else if (v.getId() == R.id.btn_3) {
            name = "按钮3";
            startActivity(new Intent(this, CompareActivity.class));
        } else if (v.getId() == R.id.btn_4) {
            name = "按钮4";
            startActivity(new Intent(this, CompareNormalActivity.class));
        }
    }

}
