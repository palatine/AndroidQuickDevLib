package com.yzh.androidquickdevlib.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.yzh.androidquickdevlib.app.BaseApplication;
import com.yzh.androidquickdevlib.net.interfaces.AbsHttpClient;
import com.yzh.androidquickdevlib.net.interfaces.HttpResponse;
import com.yzh.androidquickdevlib.net.interfaces.HttpResponseHandler;
import com.yzh.androidquickdevlib.net.interfaces.JsonResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;

public class DefaultHttpClient extends AbsHttpClient {

    private PersistentCookieStore mCookieStore = null;

    public DefaultHttpClient() {
        try {
            mCookieStore = new PersistentCookieStore(BaseApplication.instance());
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void get(String url, Map<String, Object> params, Header[] headers, JsonResponseHandler responseHandler, boolean block) {
        JsonHttpResponseHandlerAdapter adpter = new JsonHttpResponseHandlerAdapter(responseHandler);
        RequestParams requestParams = wrapperRequestParamsMap(params);
        this.getImpl(block)
                .get(null, createUrl(url), headers, requestParams, adpter);
    }

    @Override
    public void get(String url, Map<String, Object> params, Header[] headers, HttpResponseHandler responseHandler, boolean block) {
        HttpResponseHandlerAdapter adpter = new HttpResponseHandlerAdapter(responseHandler);
        RequestParams requestParams = wrapperRequestParamsMap(params);
        this.getImpl(block)
                .get(null, createUrl(url), headers, requestParams, adpter);
    }


    public void post(String url, Map<String, Object> params, Header[] headers, JsonResponseHandler responseHandler, boolean block) {
        JsonHttpResponseHandlerAdapter adpter = new JsonHttpResponseHandlerAdapter(responseHandler);
        RequestParams requestParams = new RequestParams(params);
        this.getImpl(block)
                .post(null, createUrl(url), headers, requestParams, null, adpter);
    }

    // synch functions
    public JSONObject postJSONObject(String url, Map<String, Object> params, Header[] headers) throws Exception {
        JsonHttpResponseHandlerResult result = new JsonHttpResponseHandlerResult();
        RequestParams requestParams = null;
        Logger.getLogger("TEST")
                .log(Level.INFO, "request url=" + url);
        if (params != null) {
            requestParams = new RequestParams();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                requestParams.put(entry.getKey(), entry.getValue());
                // 为了支持List类型的参数, 去掉toString(). (requestParams本身支持List的解析)
                // requestParams.put(entry.getKey(), entry.getValue().toString());
                Logger.getLogger("TEST")
                        .log(Level.INFO, "key=" + entry.getKey() + " value=" + String.valueOf(entry.getValue()));
            }
        }
        else {
            requestParams = new RequestParams();
        }

        this.getImpl(true)
                .post(null, createUrl(url), getWrappedHeaders(headers), requestParams, null, result);
        return result.mObject;
    }

    @Override
    public JSONObject postJSONObject(String url, JSONObject params, Header[] headers) throws Exception {
        JsonHttpResponseHandlerResult result = new JsonHttpResponseHandlerResult();
        HttpEntity entity = new ByteArrayEntity(params.toString()
                .getBytes("UTF-8"));
        this.getImpl(true)
                .post(null, createUrl(url), getWrappedHeaders(headers), entity, "application/json", result);
        return result.mObject;
    }

    // synch functions
    public JSONObject getJSONObject(String url, Map<String, Object> params, Header[] headers) throws Exception {
        JsonHttpResponseHandlerResult result = new JsonHttpResponseHandlerResult();
        RequestParams requestParams = null;
        Logger.getLogger("TEST")
                .log(Level.INFO, "request url=" + url);
        if (params != null) {
            requestParams = new RequestParams();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                requestParams.put(entry.getKey(), entry.getValue());
                // 为了支持List类型的参数, 去掉toString(). (requestParams本身支持List的解析)
                // requestParams.put(entry.getKey(), entry.getValue().toString());
                Logger.getLogger("TEST")
                        .log(Level.INFO, "key=" + entry.getKey() + " value=" + String.valueOf(entry.getValue()));
            }
        }
        else {
            requestParams = new RequestParams();
        }

        this.getImpl(true)
                .get(null, createUrl(url), getWrappedHeaders(headers), requestParams, result);
        return result.mObject;
    }

    public JSONArray getJSONArray(String url, Map<String, Object> params, Header[] headers) throws JSONException {
        JsonHttpResponseHandlerResult result = new JsonHttpResponseHandlerResult();
        RequestParams requestParams = null;
        Logger.getLogger("TEST")
                .log(Level.INFO, "request url=" + url);
        if (params != null) {
            requestParams = new RequestParams();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                requestParams.put(entry.getKey(), entry.getValue());
                // 为了支持List类型的参数, 去掉toString(). (requestParams本身支持List的解析)
                // requestParams.put(entry.getKey(), entry.getValue().toString());
                Logger.getLogger("TEST")
                        .log(Level.INFO, "key=" + entry.getKey() + " value=" + String.valueOf(entry.getValue()));
            }
        }
        else {
            requestParams = new RequestParams();
        }

        this.getImpl(true)
                .get(null, createUrl(url), getWrappedHeaders(headers), requestParams, result);
        return result.mArray;
    }

    @Override
    public HttpResponse getBytes(String url, Map<String, Object> params, Header[] headers) {
        HttpResponseHandlerResult result = new HttpResponseHandlerResult();
        RequestParams requestParams = null;
        Logger.getLogger("TEST")
                .log(Level.INFO, "request url=" + url);
        if (params != null) {
            requestParams = new RequestParams();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                requestParams.put(entry.getKey(), entry.getValue());
                // 为了支持List类型的参数, 去掉toString(). (requestParams本身支持List的解析)
                // requestParams.put(entry.getKey(), entry.getValue().toString());
                Logger.getLogger("TEST")
                        .log(Level.INFO, "key=" + entry.getKey() + " value=" + String.valueOf(entry.getValue()));
            }
        }
        else {
            requestParams = new RequestParams();
        }

        this.getImpl(true)
                .get(null, createUrl(url), getWrappedHeaders(headers), requestParams, result);
        return result.mResponse;
    }

    @Override
    public JSONObject postFileStream(@NonNull String url,
            @NonNull String key,
            @NonNull InputStream fileStream,
            @Nullable String fileName,
            @Nullable String contentType,
            @Nullable boolean autoClose,
            @Nullable Map<String, Object> params,
            @Nullable Header[] headers) throws Exception {

        JsonHttpResponseHandlerResult result = new JsonHttpResponseHandlerResult();
        RequestParams requestParams = new RequestParams();
        Logger.getLogger("TEST")
                .log(Level.INFO, "request url=" + url);
        if (TextUtils.isEmpty(key)) {
            throw new IllegalArgumentException("key of file stream should not be empty!");
        }

        if (fileStream == null) {
            throw new IllegalArgumentException("file stream should not be null!");
        }

        // 设置要上传的流参数
        requestParams.put(key, fileStream, fileName, TextUtils.isEmpty(contentType) ? null : contentType, autoClose);
        Logger.getLogger("TEST")
                .log(Level.INFO,
                        "key=" + key + " value=fileStream, fileName=" + String.valueOf(fileName) + ", contentType=" + String.valueOf(contentType) + ", autoClose=" + autoClose);

        // 设置其他参数
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            requestParams.put(entry.getKey(), entry.getValue());
            // 为了支持List类型的参数, 去掉toString(). (requestParams本身支持List的解析)
            // requestParams.put(entry.getKey(), entry.getValue().toString());
            Logger.getLogger("TEST")
                    .log(Level.INFO, "key=" + entry.getKey() + " value=" + String.valueOf(entry.getValue()));
        }

        AsyncHttpClient client = this.getImpl(true);
        client.setTimeout(30 * 1000); // 上传文件等待服务器返回, 等待30s
        client.post(null, createUrl(url), getWrappedHeaders(headers), requestParams, null, result);
        return result.mObject;
    }

    /**
     * 将Map<String, Object>格式的请求参数封装成{@link RequestParams}
     *
     * @param params
     * @return
     */
    static RequestParams wrapperRequestParamsMap(Map<String, Object> params) {
        RequestParams requestParams = null;
        if (params != null) {
            requestParams = new RequestParams();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                requestParams.put(entry.getKey(), entry.getValue());
                // 为了支持List类型的参数, 去掉toString(). (requestParams本身支持List的解析)
                // requestParams.put(entry.getKey(), entry.getValue().toString());
                Logger.getLogger("TEST")
                        .log(Level.INFO, "key=" + entry.getKey() + " value=" + String.valueOf(entry.getValue()));
            }
        }
        else {
            requestParams = new RequestParams();
        }
        return requestParams;
    }

    public static Header[] getWrappedHeaders(Header[] headers) {
        final List<Header> wrappedHeaders = new ArrayList<>();
        final int headerLength = headers == null ? 0 : headers.length;
        for (int i = 0; i < headerLength; i++) {
            wrappedHeaders.add(headers[i]);
        }

        wrappedHeaders.addAll(getDefaultHeaders());
        return (Header[]) wrappedHeaders.toArray(new Header[wrappedHeaders.size()]);
    }

    public static List<Header> getDefaultHeaders() {
        final List<Header> defaultHeaders = new ArrayList<>();
        defaultHeaders.add(new BasicHeader("X-REQUESTED-WITH", "1"));
        defaultHeaders.add(new BasicHeader("platform", "android"));
        return defaultHeaders;
    }

    public CookieStore getCookieStore() {
        return mCookieStore;
    }

    protected AsyncHttpClient getImpl(boolean block) {
        final AsyncHttpClient theAsyImp = block ? new SyncHttpClient() : new AsyncHttpClient();
        theAsyImp.setCookieStore(mCookieStore);
        // loopj AsyncHttpClient v1.4.9 bug : https://github.com/loopj/android-async-http/issues/971
        // Set proxy in system settings won't change the proxy of android async http as well in 1.4.9
        String proxyHost = System.getProperty("http.proxyHost");
        int proPort = Integer.parseInt(System.getProperty("http.proxyPort", "0"));
        if (!TextUtils.isEmpty(proxyHost) && proPort > 0) {
            theAsyImp.setProxy(proxyHost, proPort);
        }

        return theAsyImp;
    }

    // ///Inner class///////////////////////////////
    protected class JsonHttpResponseHandlerResult extends com.loopj.android.http.JsonHttpResponseHandler {
        public JSONObject mObject = null;
        public JSONArray mArray = null;

        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            mObject = response;
        }

        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            mArray = response;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
            Logger.getLogger("DefaultHttpClient")
                    .log(Level.WARNING, "parseJSON failed for:" + responseBody + " statusCode=" + statusCode);
        }
    }

    protected class HttpResponseHandlerResult extends com.loopj.android.http.AsyncHttpResponseHandler {

        public HttpResponse mResponse;

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            // TODO Auto-generated method stub
            mResponse = new HttpResponse(statusCode, headers, responseBody);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            mResponse = new HttpResponse(statusCode, headers, responseBody);
        }

    }

    protected class JsonHttpResponseHandlerAdapter extends com.loopj.android.http.JsonHttpResponseHandler {
        protected JsonResponseHandler mOuterHandler = null;

        public JsonHttpResponseHandlerAdapter(JsonResponseHandler outerHandler) {
            this.mOuterHandler = outerHandler;
        }

        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            if (mOuterHandler == null) {
                super.onSuccess(statusCode, headers, response);
                return;
            }

            mOuterHandler.onSuccess(statusCode, headers, response);
        }

        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            if (mOuterHandler == null) {
                super.onSuccess(statusCode, headers, response);
                return;
            }

            mOuterHandler.onSuccess(statusCode, headers, response);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
            mOuterHandler.onFailure(statusCode, headers, responseBody, error);
        }
    }

    protected class HttpResponseHandlerAdapter extends com.loopj.android.http.AsyncHttpResponseHandler {

        protected HttpResponseHandler mOuterHandler = null;

        public HttpResponseHandlerAdapter(HttpResponseHandler outerHandler) {
            this.mOuterHandler = outerHandler;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            mOuterHandler.onSuccess(statusCode, headers, responseBody);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            mOuterHandler.onFailure(statusCode, headers, responseBody, error);
        }
    }


}
