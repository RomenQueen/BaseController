package com.rq.demo;

import android.os.CountDownTimer;
import android.view.View;

import com.hzaz.base.BaseController;


public class Example1 extends BaseController {
    @Override
    public int getLayoutId() {
        return R.layout.activity_ex_diff;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        setOnClickListener(R.id.tv_show1, R.id.tv_show2, R.id.tv_show3, R.id.tv_show4);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.tv_show1) {
            CountDownTimer countDownTimer = new CountDownTimer(10 * 1000, 1) {
                @Override
                public void onTick(long millisUntilFinished) {
                    setData2View(R.id.tv_show1, "Example1-->填充1：" + millisUntilFinished);
                }

                @Override
                public void onFinish() {
                }
            };
            countDownTimer.start();
        } else if (v.getId() == R.id.tv_show2) {
            CountDownTimer countDownTimer = new CountDownTimer(10 * 1000, 1) {
                @Override
                public void onTick(long millisUntilFinished) {
                    setData2View(R.id.tv_show2, "Example1-->填充2：" + millisUntilFinished);
                }

                @Override
                public void onFinish() {
                }
            };
            countDownTimer.start();
        } else if (v.getId() == R.id.tv_show3) {
            CountDownTimer countDownTimer = new CountDownTimer(10 * 1000, 1) {
                @Override
                public void onTick(long millisUntilFinished) {
                    setData2View(R.id.tv_show3, "Example1-->填充3：" + millisUntilFinished);
                }

                @Override
                public void onFinish() {
                }
            };
            countDownTimer.start();
        } else if (v.getId() == R.id.tv_show4) {
            CountDownTimer countDownTimer = new CountDownTimer(10 * 1000, 1) {
                @Override
                public void onTick(long millisUntilFinished) {
                    setData2View(R.id.tv_show4, "Example1-->填充4：" + millisUntilFinished);
                }

                @Override
                public void onFinish() {
                }
            };
            countDownTimer.start();
        }
    }
}