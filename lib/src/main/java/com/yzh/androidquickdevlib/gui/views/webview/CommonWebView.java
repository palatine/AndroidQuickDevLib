package com.yzh.androidquickdevlib.gui.views.webview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yzh.androidquickdevlib.BuildConfig;
import com.yzh.androidquickdevlib.utils.ProgressWindowHelper;
import com.yzh.androidquickdevlib.utils.T;

/**
 * Created by yzh on 2016/9/14.
 */
public class CommonWebView extends WebView {
    /**
     * 显示默认的进度
     */
    private boolean showDefaultProgress = true;
    private OnWebViewLoadStatusListener onWebViewLoadStatusListener;

    /**
     * 加载url的监听
     */
    public interface OnWebViewLoadStatusListener {
        /**
         * 开始加载
         */
        void onLoadStart();

        /**
         * 加载完成
         */
        void onLoadEnd();

        /**
         * 加载错误
         *
         * @param s
         */
        void onLoadError(String s);

        /**
         * 加载进度更新
         *
         * @param progress
         */
        void onLoadProgress(float progress);
    }

    /**
     * default download listener
     * // just deliver it to default web browser of system
     */
    static class DefaultDownloadListener implements DownloadListener {
        Context context;

        public DefaultDownloadListener(Context context) {
            this.context = context;
        }

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            try {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public CommonWebView(Context context) {
        super(context);
        setup();
    }

    public CommonWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public CommonWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup();
    }

    /**
     * 设置WebView的相关参数
     */
    protected void setup() {
        setupDefaultWebSetting();
        setupDefaultWebViewClient();
        setupDefaultWebChromeClient();
    }

    /**
     * 进行基础的设置
     */
    protected void setupDefaultWebSetting() {
        final WebSettings settings = getSettings();
        // 设置缓存模式为不缓存
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 设置启用Dom Storage
        settings.setDomStorageEnabled(true);
        // 设置远程debug
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        }
        // setup default download listener
        setDownloadListener(new DefaultDownloadListener(getContext()));
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        super.setWebViewClient(new WebViewClientWrapper(client));
    }

    @Override
    public void setWebChromeClient(WebChromeClient client) {
        super.setWebChromeClient(new WebChromeClientWrapper(client));
    }

    class WebViewClientWrapper extends WebViewClient {
        WebViewClient client;

        public WebViewClientWrapper(WebViewClient client) {
            this.client = client;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return this.client != null && this.client.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (this.client != null) {
                this.client.onPageStarted(view, url, favicon);
            }
            if (getOnWebViewLoadStatusListener() != null) {
                getOnWebViewLoadStatusListener().onLoadStart();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (this.client != null) {
                this.client.onPageFinished(view, url);
            }
            if (getOnWebViewLoadStatusListener() != null) {
                getOnWebViewLoadStatusListener().onLoadEnd();
            }
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            if (this.client != null) {
                this.client.onLoadResource(view, url);
            }
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onPageCommitVisible(WebView view, String url) {
            if (this.client != null) {
                this.client.onPageCommitVisible(view, url);
            }
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            return this.client != null ? this.client.shouldInterceptRequest(view, url) : null;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return this.client != null ? this.client.shouldInterceptRequest(view, request) : null;
        }

        @Override
        public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
            if (this.client != null) {
                this.client.onTooManyRedirects(view, cancelMsg, continueMsg);
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if (this.client != null) {
                this.client.onReceivedError(view, errorCode, description, failingUrl);
            }
            if (getOnWebViewLoadStatusListener() != null) {
                getOnWebViewLoadStatusListener().onLoadError(errorCode + description);
            }
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (this.client != null) {
                this.client.onReceivedError(view, request, error);
            }
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            if (this.client != null) {
                this.client.onReceivedHttpError(view, request, errorResponse);
            }
        }

        @Override
        public void onFormResubmission(WebView view, Message dontResend, Message resend) {
            if (this.client != null) {
                this.client.onFormResubmission(view, dontResend, resend);
            }
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            if (this.client != null) {
                this.client.doUpdateVisitedHistory(view, url, isReload);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            if (this.client != null) {
                this.client.onReceivedSslError(view, handler, error);
            }
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
            if (this.client != null) {
                this.client.onReceivedClientCertRequest(view, request);
            }
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            if (this.client != null) {
                this.client.onReceivedHttpAuthRequest(view, handler, host, realm);
            }
        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return this.client != null && this.client.shouldOverrideKeyEvent(view, event);
        }

        @Override
        public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
            if (this.client != null) {
                this.client.onUnhandledKeyEvent(view, event);
            }
        }

        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            if (this.client != null) {
                this.client.onScaleChanged(view, oldScale, newScale);
            }
        }

        @Override
        public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
            if (this.client != null) {
                this.client.onReceivedLoginRequest(view, realm, account, args);
            }
        }
    }

    class WebChromeClientWrapper extends WebChromeClient {
        WebChromeClient chromeClient;

        public WebChromeClientWrapper(WebChromeClient chromeClient) {
            this.chromeClient = chromeClient;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (this.chromeClient != null) {
                this.chromeClient.onProgressChanged(view, newProgress);
            }
            if (getOnWebViewLoadStatusListener() != null) {
                getOnWebViewLoadStatusListener().onLoadProgress(newProgress);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (this.chromeClient != null) {
                this.chromeClient.onReceivedTitle(view, title);
            }
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            if (this.chromeClient != null) {
                this.chromeClient.onReceivedIcon(view, icon);
            }
        }

        @Override
        public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
            if (this.chromeClient != null) {
                this.chromeClient.onReceivedTouchIconUrl(view, url, precomposed);
            }
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (this.chromeClient != null) {
                this.chromeClient.onShowCustomView(view, callback);
            }
        }

        @Override
        public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
            if (this.chromeClient != null) {
                this.chromeClient.onShowCustomView(view, requestedOrientation, callback);
            }
        }

        @Override
        public void onHideCustomView() {
            if (this.chromeClient != null) {
                this.chromeClient.onHideCustomView();
            }
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            return this.chromeClient != null && this.chromeClient.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }

        @Override
        public void onRequestFocus(WebView view) {
            if (this.chromeClient != null) {
                this.chromeClient.onRequestFocus(view);
            }
        }

        @Override
        public void onCloseWindow(WebView window) {
            if (this.chromeClient != null) {
                this.chromeClient.onCloseWindow(window);
            }
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return this.chromeClient != null && this.chromeClient.onJsAlert(view, url, message, result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            return this.chromeClient != null && this.chromeClient.onJsConfirm(view, url, message, result);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return this.chromeClient != null && this.chromeClient.onJsPrompt(view, url, message, defaultValue, result);
        }

        @Override
        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
            return this.chromeClient != null && this.chromeClient.onJsBeforeUnload(view, url, message, result);
        }

        @Override
        public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
            if (this.chromeClient != null) {
                this.chromeClient.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
            }
        }

        @Override
        public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
            if (this.chromeClient != null) {
                this.chromeClient.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
            }
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            if (this.chromeClient != null) {
                this.chromeClient.onGeolocationPermissionsShowPrompt(origin, callback);
            }
        }

        @Override
        public void onGeolocationPermissionsHidePrompt() {
            if (this.chromeClient != null) {
                this.chromeClient.onGeolocationPermissionsHidePrompt();
            }
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onPermissionRequest(PermissionRequest request) {
            if (this.chromeClient != null) {
                this.chromeClient.onPermissionRequest(request);
            }
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onPermissionRequestCanceled(PermissionRequest request) {
            if (this.chromeClient != null) {
                this.chromeClient.onPermissionRequestCanceled(request);
            }
        }

        @Override
        public boolean onJsTimeout() {
            return this.chromeClient != null && this.chromeClient.onJsTimeout();
        }

        @Override
        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
            if (this.chromeClient != null) {
                this.chromeClient.onConsoleMessage(message, lineNumber, sourceID);
            }
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            return this.chromeClient != null && this.chromeClient != null && this.chromeClient.onConsoleMessage(consoleMessage);
        }

        @Override
        public Bitmap getDefaultVideoPoster() {
            return this.chromeClient != null ? this.chromeClient.getDefaultVideoPoster() : null;
        }

        @Override
        public View getVideoLoadingProgressView() {
            return this.chromeClient != null ? this.chromeClient.getVideoLoadingProgressView() : null;
        }

        @Override
        public void getVisitedHistory(ValueCallback<String[]> callback) {
            if (this.chromeClient != null) {
                this.chromeClient.getVisitedHistory(callback);
            }
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            return this.chromeClient != null && this.chromeClient.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        }
    }

    /**
     * 设置默认的WebChromeClient
     */
    protected void setupDefaultWebChromeClient() {
        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (getContext() != null && getContext() instanceof Activity) {
                    ((Activity) (getContext())).setProgress(newProgress * 1000);
                }
            }
        });
    }

    /**
     * 设置默认的WebViewClient
     */
    protected void setupDefaultWebViewClient() {
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (isShowDefaultProgress()) {
                    ProgressWindowHelper.showProgressWindow(true);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                if (isShowDefaultProgress()) {
                    ProgressWindowHelper.hideProgressWindow();
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (isShowDefaultProgress()) {
                    ProgressWindowHelper.hideProgressWindow();
                }
                T.showLong(description);
            }
        });
    }

    /**
     * 获取加载进度监听器
     *
     * @return
     */
    OnWebViewLoadStatusListener getOnWebViewLoadStatusListener() {
        return onWebViewLoadStatusListener;
    }

    /**
     * 设置加载进度监听
     *
     * @param onWebViewLoadStatusListener
     */
    public void setOnWebViewLoadStatusListener(OnWebViewLoadStatusListener onWebViewLoadStatusListener) {
        this.onWebViewLoadStatusListener = onWebViewLoadStatusListener;
    }

    /**
     * 是否显示默认的进度条
     *
     * @return
     */
    public boolean isShowDefaultProgress() {
        return showDefaultProgress;
    }

    /**
     * 设置显示默认的进度条
     *
     * @param showDefaultProgress
     */
    public void setShowDefaultProgress(boolean showDefaultProgress) {
        this.showDefaultProgress = showDefaultProgress;
    }
}
