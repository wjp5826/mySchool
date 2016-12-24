package com.baidu.wjp.jvtc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 作者：吴建平
 * 时间：2016/11/16.
 * 作用：
 */

public class AboutActivity extends Activity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        loadWebView();
    }

    private void loadWebView() {
        //初始化webview
        mWebView = (WebView) findViewById(R.id.about_webview);
        //加载网页
        mWebView.loadUrl("http://jvtc5826.kuaizhan.com/fp/page/display/582bdc1b8340eaad13baad82");
        //覆盖webview默认打开方式
        mWebView.setWebViewClient(mWebViewClient);
        //获取设置对象
        WebSettings settings = mWebView.getSettings();
        //支持js
        settings.setJavaScriptEnabled(true);
        //优先使用缓存
//        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }
    WebViewClient mWebViewClient = new WebViewClient(){
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.contains("582641604")){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }else{
                view.loadUrl(url);
            }
            return true;
        }
    };

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
}
