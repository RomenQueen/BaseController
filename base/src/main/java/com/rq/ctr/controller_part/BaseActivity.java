package com.rq.ctr.controller_part;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import com.gyf.immersionbar.ImmersionBar;
import com.rq.ctr.BASE;
import com.rq.ctr.NetResponseViewImpl;
import com.rq.ctr.R;
import com.rq.ctr.common_util.AppUtil;
import com.rq.ctr.common_util.LOG;
import com.rq.ctr.common_util.ToastUtil;
import com.rq.ctr.net.BaseBean;
import com.rq.ctr.net.RequestType;
import com.rq.ctr.ui.CommonActivity;
import com.rq.ctr.ui.FullScreenActivity;
import com.trello.rxlifecycle2.components.support.RxFragmentActivity;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import static com.rq.ctr.controller_part.BaseController.TAG_NAME;
import static com.rq.ctr.controller_part.BaseController.TAG_PASS;


public class BaseActivity<P extends BaseController> extends RxFragmentActivity implements NetResponseViewImpl {

    public static <P extends BaseController> void open(Context controller, Class<P> clazz, Object... pass) {
        if (controller != null) {
            BaseController target = getController(clazz, controller);
            if (target == null) {
                ToastUtil.show(controller, "启动异常: " + clazz.getSimpleName());
                return;
            }
            Intent intent;
            intent = getInnerIntent(target, clazz, pass);
            controller.startActivity(intent);
        }
    }

    public static <P extends BaseController> void skip(BaseController controller, Class<P> clazz, Object... pass) {
        if (controller != null) {
            open(controller, clazz, pass);
            controller.finish();
        }
    }

    public static String TAG_OPEN_FOR = "open.for";
    public static String TAG_OPEN_CODE = "open.code";

    public static <P extends BaseController> void openFor(BaseController controller, Class<P> clazz, Object... pass) {
        if (controller != null) {
            BaseController target = getController(clazz, controller);
            Intent intent = getInnerIntent(target, clazz, pass);
            intent.putExtra(TAG_OPEN_FOR, true);
            controller.startActivityForResult(intent);
        }
    }

    public static <P extends BaseController> void openWith(BaseController controller, int requestCode, Class<P> clazz, Object... pass) {
        if (controller != null) {
            BaseController target = getController(clazz, controller);
            Intent intent = getInnerIntent(target, clazz, pass);
            intent.putExtra(TAG_OPEN_FOR, true);
            intent.putExtra(TAG_OPEN_CODE, requestCode);
            controller.startActivityForResult(intent, requestCode);
        }
    }

    public static <P extends BaseController> void open(BaseController controller, Class<P> clazz, Object... pass) {
        BaseController target = getController(clazz, controller);
        if (target == null) {
            ToastUtil.show(controller.getContextActivity(), "启动异常: " + clazz.getSimpleName());
            return;
        }
        Intent intent;
        intent = getInnerIntent(target, clazz, pass);
        controller.startActivity(intent);
    }

    /**
     * @param clazz 目标控制器
     * @param pass  传递数据
     * @see BaseController#getPass(int, Intent[])
     */
    @NonNull
    public static <P extends BaseController> Intent getInnerIntent(BaseController target, Class<P> clazz, Object... pass) {
        Intent intent;
        if (target.isFullScreen()) {
            intent = new Intent(BASE.getCxt(), FullScreenActivity.class);
        } else {
            intent = new Intent(BASE.getCxt(), CommonActivity.class);
        }
        intent.putExtra(TAG_NAME, clazz);
        intent.setFlags(target.getActivityFlag());
        if (pass != null) {
            for (int i = 0; i < pass.length; i++) {
                intent.putExtra(TAG_PASS + i, (Serializable) pass[i]);
            }
        }
        return intent;
    }

    public static <P extends BaseController> P getController(Class<P> clazz, Object... objects) {
        if (clazz == null) return null;
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            LOG.showUserWhere(objects);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            LOG.showUserWhere(objects);
            e.printStackTrace();
        } catch (ClassCastException e) {
            LOG.showUserWhere(objects);
            e.printStackTrace();
        } catch (NullPointerException e) {
            LOG.showUserWhere(objects);
            e.printStackTrace();
        }
        return null;
    }

    protected P mPresenter;
    View root;
    boolean isFirstOnResume = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Class<P> clazz = (Class<P>) getIntent().getSerializableExtra(TAG_NAME);
        mPresenter = getController(clazz, this);
        if (mPresenter == null) {
            mPresenter = getController(this, 0);
        }
        if (mPresenter != null) {
            mPresenter.setActivity(this);
            if (BASE.useCommonLayout) {
                final LayoutInflater inflater = LayoutInflater.from(this);
                if (mPresenter.needOutScroll()) {
                    root = inflater.inflate(BASE.commonLayoutId, null);
                    final ViewGroup container = root.findViewById(R.id.base_container);
                    final View content = inflater.inflate(mPresenter.getLayoutId(), null);
                    container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onGlobalLayout() {
                            int height = ((View) container.getParent()).getHeight();
                            int height2 = container.getHeight();
                            if (height2 < height && height2 != 0) {
                                ScrollView.LayoutParams Params = (ScrollView.LayoutParams) container.getLayoutParams();
                                Params.height = height;
                                container.setLayoutParams(Params);
                                content.setLayoutParams(Params);
                            }
                            container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });
                    container.addView(content);
                } else {
                    root = inflater.inflate(R.layout.activity_base_no_scroller, null);
                    final ViewGroup container = root.findViewById(R.id.base_container);
                    final View content = inflater.inflate(mPresenter.getLayoutId(), null);
                    container.addView(content);
                }
                setContentView(root);
                if (findViewById(R.id.common_status_bar) != null) {
                    ViewGroup.LayoutParams params = findViewById(R.id.common_status_bar).getLayoutParams();
                    params.height = AppUtil.getStatusBarHeight();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    findViewById(R.id.common_status_bar).setLayoutParams(params);
                }
                if (mPresenter != null) {
                    if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M || mPresenter.needPermissions() == null)) {
                        mPresenter.onViewCreated();
                        mPresenter.findView();
                    }
                    if (mPresenter.underStatusBar()) {
                        ImmersionBar.with(this).fitsSystemWindows(false).fullScreen(false).init();
                    } else {
                        ImmersionBar.with(this).fitsSystemWindows(true).statusBarColor(BASE.statusColorId).fullScreen(true).init();
                    }
                    if (root.findViewById(R.id.common_title_pan) != null) {
                        root.findViewById(R.id.common_title_pan).setVisibility(mPresenter.underStatusBar() || mPresenter.isFullScreen() ? View.GONE : View.VISIBLE);
                    }
                }
            } else {
                root = LayoutInflater.from(this).inflate(mPresenter.getLayoutId(), null);
                setContentView(root);
                if (findViewById(R.id.common_status_bar) != null) {
                    ViewGroup.LayoutParams params = findViewById(R.id.common_status_bar).getLayoutParams();
                    params.height = AppUtil.getStatusBarHeight();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    findViewById(R.id.common_status_bar).setLayoutParams(params);
                }
                if (mPresenter != null && (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || mPresenter.needPermissions() == null)) {
                    mPresenter.onViewCreated();
                    mPresenter.findView();
                }
            }
            if (mPresenter != null) {
                if (mPresenter.isTranslucent()) {
                    this.getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.transparent_pop));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mPresenter.needPermissions() != null && mPresenter.needPermissions().length > 0) {
                    boolean hasAllGet = false;
                    for (String per : mPresenter.needPermissions()) {
                        hasAllGet = ActivityCompat.checkSelfPermission(this, per) == PackageManager.PERMISSION_GRANTED;
                        if (!hasAllGet) break;
                    }
                    if (!hasAllGet) {
                        ActivityCompat.requestPermissions(this, this.mPresenter.needPermissions(), TAG_PERMISSION_GTE);
                    } else {
                        mPresenter.onViewCreated();
                        mPresenter.findView();
                    }
                }
            }
        }
    }

    public static final int TAG_PERMISSION_GTE = 102;

//    int x1;
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                x1 = (int) event.getX();
//                LOG.e("BaseActivity", "onTouchEvent.187:");
//                return true;
//            case MotionEvent.ACTION_UP:
//                if (x1 - event.getX() < -120) {
//                    finish();
//                }
//                x1 = 0;
//                LOG.e("BaseActivity", "onTouchEvent.194:");
//                return true;
//        }
//        return super.onTouchEvent(event);
//    }

    public View getRootView() {
        return root;
    }

    public Object getPass(int position) {
        return getIntent().getSerializableExtra(TAG_PASS + position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mPresenter != null) {
            if (mPresenter.onActivityResult(requestCode, resultCode, data)) return;
        }
        try {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment fragment : fragments) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception e) {

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onControllerResume();
            mPresenter.onActivityResume();
            ControllerWatcher.get().addActivityController(mPresenter);
            LOG.i("BaseActivity", mPresenter.getClass().getSimpleName() + " LINE:onResume");
        }
        LOG.i("BaseActivity", "isFirstOnResume = " + isFirstOnResume);
        if (isFirstOnResume) {
            isFirstOnResume = false;
        } else {
            ControllerWatcher.get().notifyFragmentOnResume();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPresenter != null) {
            LOG.i("BaseActivity", mPresenter.getClass().getSimpleName() + " LINE:onControllerPause");
//            ControllerWatcher.get().removerActivityController(mPresenter);
            mPresenter.onControllerPause();
            mPresenter.onActivityPause();
            ControllerWatcher.get().removerActivityController(mPresenter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onActivityDestroy();
        }
    }

    //反射获取当前Presenter对象
    protected P getController(Object o, int i) {
        try {
            P back = getController((Class<P>) ((ParameterizedType) (o.getClass().getGenericSuperclass())).getActualTypeArguments()[i], this);
            back.setActivity(BaseActivity.this);
            return back;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        if (mPresenter != null && mPresenter.interruptBack()) {
            return;
        }
        super.onBackPressed();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mPresenter != null) {
            if (mPresenter.onKeyDown(keyCode, event))
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void handleFailResponse(BaseBean baseBean) {

    }

    protected ProgressDialog progressDialog;

    @Override
    public void showLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.show();
    }

    @Override
    public void dismissLoading() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public final Activity getContextActivity() {
        return this;
    }

    @Override
    public <T extends Serializable> void onResponseSucceed(@NonNull RequestType type, T data) {

    }

    @Override
    public void onResponseError(@NonNull RequestType type) {

    }

}
