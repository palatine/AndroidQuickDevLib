package com.yzh.androidquickdevlib.gui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.yzh.androidquickdevlib.R;
import com.yzh.androidquickdevlib.gui.page.PageFragmentation;
import com.yzh.androidquickdevlib.gui.views.webview.CommonWebView;
import com.yzh.androidquickdevlib.gui.views.webview.FullscreenHolder;
import com.yzh.androidquickdevlib.gui.views.webview.JsEnabledWebView;
import com.yzh.androidquickdevlib.net.interfaces.AbsHttpClient;
import com.yzh.androidquickdevlib.task.ThreadUtility;

public class CommonWebViewPage extends PageFragmentation {
    public final static String TAG = CommonWebViewPage.class.getCanonicalName();
    protected WebView webView;
    protected ViewGroup container;

    /**
     * 页面的url
     */
    String urlString = "";

    /**
     * 是否支持全屏显示
     */
    boolean supportFullScreen = false;

    @Override
    public View onCreatePageView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        View view = inflater.inflate(R.layout.fragment_web_view_page, null);
        this.webView = (WebView) view.findViewById(R.id.webView);
        this.container = (ViewGroup) view.findViewById(R.id.container);
        return view;
    }

    @Override
    public void onResumePage(boolean firstVisibility) {
        if (firstVisibility) {
            updateContent();
        }
        else {
            // 执行一次waiting任务
            if (this.webView != null) {
                this.webView.onResume();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.webView != null) {
            this.webView.onPause();
        }
    }


    @Override
    public void onRemoveFromStack() {
        super.onRemoveFromStack();
        ThreadUtility.postOnUiThreadNonReuse(this::destroyWebView);
    }

    /**
     * WebView不再需要的时候, 移除他
     */
    protected void destroyWebView() {
        try {
            this.webView.destroy();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            this.webView = null;
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void updateContent() {
        if (this.webView != null) {
            // 添加本地通用基础接口和本地分享接口
            if (this.webView instanceof CommonWebView) {
                ((CommonWebView) this.webView).setShowDefaultProgress(false);
            }
            // 全屏支持
            if (this.supportFullScreen) {
                final WebSettings settings = this.webView.getSettings();
                settings.setUseWideViewPort(true);
                settings.setAllowFileAccess(true);
                settings.setSupportZoom(true); // 支持缩放
                settings.setLoadWithOverviewMode(true);
                this.webView.setWebChromeClient(new FullScreenSupportWebClient(this.container));
            }

            // 初始加载的url
            loadUrl(this.urlString);
        }
    }

    /**
     * 打开指定的url
     *
     * @param url
     * @return 是否成功发起
     */
    protected boolean loadUrl(String url) {
        if (this.webView != null && !TextUtils.isEmpty(url)) {
            final String curUrl = this.webView.getUrl();
            final String settleUrl = AbsHttpClient.createUrl(this.urlString);
            if (TextUtils.isEmpty(curUrl) || !(curUrl.equalsIgnoreCase(settleUrl))) {
                this.webView.loadUrl(settleUrl);
                Log.d(TAG, "load url:" + settleUrl);
            }
            else {
                this.webView.reload();
                Log.d(TAG, "reload");
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onGoPreviousPage() {
        if (this.webView != null && this.webView.canGoBack()) {
            this.webView.goBack();
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 设置页面要打开的url
     *
     * @param urlString
     */
    public void setUrlString(String urlString) {
        if (!TextUtils.isEmpty(urlString) && !loadUrl(urlString)) {
            this.urlString = urlString;
        }
        else {
            this.urlString = "";
        }
    }

    /**
     * 是否支持全屏
     *
     * @param supportFullScreen
     */
    public void setSupportFullScreen(boolean supportFullScreen) {
        this.supportFullScreen = supportFullScreen;
    }

    /**
     * 执行指定的js方法
     *
     * @param funName
     * @param params
     */
    public void callJsFunction(String funName, String... params) {
        if (this.webView != null && this.webView instanceof JsEnabledWebView) {
            ((JsEnabledWebView) this.webView).callJsFunction(funName, params);
        }
    }

    @Override
    public void onSetuptActionBar(ViewGroup actionBarView) {

    }

    public WebView getWebView() {
        return webView;
    }

    /**
     * 全屏支持的WebClient
     */
    private class FullScreenSupportWebClient extends WebChromeClient {
        final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        FullscreenHolder holder;
        CustomViewCallback customViewCallback;
        ViewGroup container;

        public FullScreenSupportWebClient(ViewGroup container) {
            this.container = container;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (getSafeActivity() != null) {
                getSafeActivity().setProgress(newProgress * 1000);
            }
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (getSafeActivity() == null) {
                return;
            }

            if (this.holder != null) {
                callback.onCustomViewHidden();
                return;
            }

            this.holder = new FullscreenHolder(getSafeActivity());
            this.holder.addView(view, COVER_SCREEN_PARAMS);
            this.container.addView(holder, COVER_SCREEN_PARAMS);
            this.customViewCallback = callback;
        }

        @Override
        public void onHideCustomView() {
            if (holder == null || getSafeActivity() == null) {
                return;
            }

            this.container.removeView(holder);
            this.holder = null;
            this.customViewCallback.onCustomViewHidden();
            this.customViewCallback = null;
        }
    }

}
