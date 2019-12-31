package com.hzaz.base;

public class ExampleController extends BaseController {
    @Override
    public int getLayoutId() {
        return R.layout.activity_example;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        setData2View(R.id.show, System.currentTimeMillis() + " -> " + this.getClass().getSimpleName());
    }
}
