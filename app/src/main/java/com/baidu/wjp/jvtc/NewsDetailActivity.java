
package com.baidu.wjp.jvtc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import adapter.CommentListViewAdapter;
import utils.HttpUtil;
import utils.SPUtils;
import utils.ThreadManager;
import utils.util;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import domain.Comment;


public class NewsDetailActivity extends AppCompatActivity {

    public static final String TO_COMMENT = "comment_list";
    private FrameLayout mLayout;
    private WebView mWebView;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String mHtml = (String) msg.obj;
                    mWebView.loadDataWithBaseURL(HttpUtil.baseUrl, mHtml, "text/html", "gb2312", null);
                    break;
            }
            return false;
        }
    });
    private ListView mCommentLV;
    private EditText mCommentET;
    private TextView mCommentSendTV;
    private View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        final String url = getIntent().getStringExtra("url");
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String htmlContent = HttpUtil.getNewsDerail2(HttpUtil.baseUrl + url);
                    Message msg = mHandler.obtainMessage(1, htmlContent);
                    mHandler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mLayout = (FrameLayout) findViewById(R.id.news_content);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView = new WebView(getApplicationContext());
        mWebView.setLayoutParams(params);
        mLayout.addView(mWebView);

        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setSupportZoom(true);
//        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setDefaultTextEncodingName("utf-8");
        mWebSettings.setLoadsImagesAutomatically(true);

        //调用JS方法.安卓版本大于17,加上注解 @JavascriptInterface
        mWebSettings.setJavaScriptEnabled(true);

        saveData(mWebSettings);

        newWin(mWebSettings);

        mWebView.setWebChromeClient(webChromeClient);
        mWebView.setWebViewClient(webViewClient);

        initView();

        requestComment(url);

        mCommentSendTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputSend = mCommentET.getText().toString();
                //判断网络
                util.checkNetWorkConnect(NewsDetailActivity.this);

                if(!SPUtils.getLoginState()){
                    util.sureDialog(NewsDetailActivity.this);
                }else{
                    if (TextUtils.isEmpty(inputSend)) {
                        util.showMsg("输入为空");
                        return;
                    }
                    Comment comment = new Comment();
                    comment.setContent(inputSend);
                    String currentTime = util.timeChange(System.currentTimeMillis());
                    comment.setTime(currentTime);
                    comment.setUrl(url);
                    String userName = SPUtils.getUserName();
                    comment.setUserName(userName);

                    comment.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                util.showMsg("评论成功");
                                mCommentET.setText("");
                            }
                        }
                    });
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    requestComment(url);
                }


            }
        });

        mCommentLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NewsDetailActivity.this,CommentActivity.class);
                intent.putExtra(TO_COMMENT,url);
                startActivity(intent);
            }
        });

    }

    private void requestComment(String url) {
        BmobQuery<Comment> queryComment = new BmobQuery<>();
        queryComment.addWhereEqualTo("url", url);
        queryComment.setLimit(6);
        queryComment.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                CommentListViewAdapter adapter = new CommentListViewAdapter(list);
                adapter.notifyDataSetChanged();
                mCommentLV.setAdapter(adapter);
                mCommentLV.setEmptyView(mEmptyView);
            }
        });
    }

    private void initView() {
        mCommentLV = (ListView) findViewById(R.id.news_comment);
        mCommentET = (EditText) findViewById(R.id.news_comment_edit);
        mCommentSendTV = (TextView) findViewById(R.id.news_comment_send);
        mEmptyView = findViewById(R.id.news_comment_empty);
    }

    /**
     * 多窗口的问题
     */
    private void newWin(WebSettings mWebSettings) {
        //html中的_bank标签就是新建窗口打开，有时会打不开，需要加以下
        //然后 复写 WebChromeClient的onCreateWindow方法
        mWebSettings.setSupportMultipleWindows(false);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    }


    /**
     * HTML5数据存储
     */
    private void saveData(WebSettings mWebSettings) {
        //有时候网页需要自己保存一些关键数据,Android WebView 需要自己设置
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setAppCacheEnabled(true);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        mWebSettings.setAppCachePath(appCachePath);
    }

    WebViewClient webViewClient = new WebViewClient() {

        /**
         * 多页面在同一个WebView中打开，就是不新建activity或者调用系统浏览器打开
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

    };

    WebChromeClient webChromeClient = new WebChromeClient() {

        //=========HTML5定位==========================================================
        //需要先加入权限
        //<uses-permission android:name="android.permission.INTERNET"/>
        //<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
        //<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }

        @Override
        public void onGeolocationPermissionsHidePrompt() {
            super.onGeolocationPermissionsHidePrompt();
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);//注意个函数，第二个参数就是是否同意定位权限，第三个是是否希望内核记住
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
        //=========HTML5定位==========================================================


        //=========多窗口的问题==========================================================
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(view);
            resultMsg.sendToTarget();
            return true;
        }
        //=========多窗口的问题==========================================================
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

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