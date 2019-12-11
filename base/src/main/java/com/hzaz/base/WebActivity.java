package com.hzaz.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hzaz.base.common_util.LOG;

/**
 * 兼容模式 不适用腾讯内核
 */

public class WebActivity extends BaseActivity implements View.OnClickListener {

    WebView mMebView;

    private String webUrl;
    private String webData;
    private String realUrl;

    /**
     * 跳转到WebView界面
     *
     * @param context
     * @param title   标题
     * @param webUrl  url
     */
    public static void start(Context context, String title, String webUrl) {
        Intent intent = new Intent(context, WebActivity.class);
        LOG.e("WebActivity", "webUrl = " + webUrl);
        intent.putExtra("title", title);
        intent.putExtra("webUrl", webUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        initWebView();
        findViewById(R.id.common_left_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.common_title)).setText(getIntent().getStringExtra("title"));
    }

    private void initWebView() {
        final FrameLayout webContainer = findViewById(R.id.web_container);
        mMebView = new WebView(getApplicationContext());
        mMebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mMebView.getSettings().setDomStorageEnabled(true);// 解决对某些标签的不支持出现白屏
        mMebView.getSettings().setJavaScriptEnabled(true);
        mMebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                if (Build.VERSION.SDK_INT < 26) {
                    WebActivity.this.realUrl = url;
                    webView.loadUrl(url);
                    return true;
                }
                return false;
            }

            @Override
            public void onPageStarted(WebView webView, String url, Bitmap bitmap) {
                super.onPageStarted(webView, url, bitmap);
                LOG.e("WebActivity", "onPageStarted.url = " + url);
            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                super.onPageFinished(webView, url);
                webView.getSettings().setBlockNetworkImage(false);
                LOG.e("WebActivity", mMebView.canScrollVertically(10) + "  " + mMebView.canScrollVertically(-10) + "  onPageFinished.url = " + url);
            }
        });
        webContainer.addView(mMebView);
        webUrl = getIntent().getStringExtra("webUrl");
        webData = getIntent().getStringExtra("webData");
        if (!TextUtils.isEmpty(webUrl)) {
            mMebView.loadUrl(webUrl);
        } else if (!TextUtils.isEmpty(webData)) {
            mMebView.loadData(webData, "text/html", "UTF-8");
        }
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMebView != null) {
            mMebView.destroy();
        }
    }

    @Override
    public void onBackPressed() {
        if (mMebView != null) {
            String originalUrl = mMebView.copyBackForwardList().getCurrentItem().getOriginalUrl();
            if (originalUrl != null && mMebView.canGoBack() && !(originalUrl.equals(realUrl) || originalUrl.equals(realUrl + "/index"))) {
                mMebView.goBack();
                return;
            }
        }
        super.onBackPressed();
    }

}
