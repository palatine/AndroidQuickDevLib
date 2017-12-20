package com.yzh.androidquickdevlib.gui.views.webview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebSettings;

import com.yzh.androidquickdevlib.task.ThreadUtility;


/**
 * Created by yzh on 2016/9/14.
 */
public class JsEnabledWebView extends CommonWebView {
    public final static String TAG = "JsEnabledWebView";

    /**
     * 本地调用JS接口的默认前缀
     */
    public final static String CALL_JS_PREFIX = "javascript:";

    public JsEnabledWebView(Context context) {
        super(context);
    }

    public JsEnabledWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JsEnabledWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void setup() {
        super.setup();
        setupJsSettings();
    }


    /**
     * 设置Js相关的参数
     */
    protected void setupJsSettings() {
        final WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
    }

    /**
     * 调用指定的js方法
     *
     * @param funName
     * @param params
     */
    protected void callJsFunctionInternal(String funName, String... params) {
        if (!TextUtils.isEmpty(funName)) {
            StringBuilder callerBuilder = new StringBuilder(CALL_JS_PREFIX);
            callerBuilder.append(funName)
                    .append("(");
            for (String param : params) {
                callerBuilder.append("'")
                        .append(param)
                        .append("'")
                        .append(",");
            }
            String caller = callerBuilder.toString();
            if (caller.endsWith(",")) {
                caller = caller.substring(0, caller.length() - 1);
            }
            caller = caller + ")";
            try {
                Log.d(TAG, "callJsFunction:" + caller + "@" + hashCode());
                loadUrl(caller);
            }
            catch (Exception e) {
                Log.d(TAG, "callJsFunction failed:" + e.getMessage());
            }
        }
        else {
            Log.d(TAG, "can not call due to empty function name");
        }
    }

    /**
     * 调用指定的js方法
     *
     * @param funName
     * @param params
     */
    public void callJsFunction(String funName, String... params) {
        // 默认不能再当前loop内执行
        ThreadUtility.postOnUiThreadNonReuse(() -> {
            callJsFunctionInternal(funName, params);
        });
    }
}
