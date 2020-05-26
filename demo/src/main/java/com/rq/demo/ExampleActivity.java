package com.rq.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

public class ExampleActivity extends Activity implements View.OnClickListener {
    TextView tv1, tv2, tv3, tv4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ex_diff);
        tv1 = findViewById(R.id.tv_show1);
        tv2 = findViewById(R.id.tv_show2);
        tv3 = findViewById(R.id.tv_show3);
        tv4 = findViewById(R.id.tv_show4);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        tv4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_show1) {
            CountDownTimer countDownTimer = new CountDownTimer(10 * 1000, 1) {
                @Override
                public void onTick(long millisUntilFinished) {
                    setData2View(R.id.tv_show1, "ExampleActivity-->填充1：" + millisUntilFinished);
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
                    setData2View(R.id.tv_show2, "ExampleActivity-->填充2：" + millisUntilFinished);
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
                    setData2View(R.id.tv_show3, "ExampleActivity-->填充3：" + millisUntilFinished);
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
                    setData2View(R.id.tv_show4, "ExampleActivity-->填充4：" + millisUntilFinished);
                }

                @Override
                public void onFinish() {
                }
            };
            countDownTimer.start();
        }
    }

    private void setData2View(int tv_show1, String s) {
        ((TextView) findViewById(tv_show1)).setText(s);
    }

}
