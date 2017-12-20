package com.yzh.androidquickdevlib.net.interfaces;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import com.yzh.androidquickdevlib.app.BaseApplication;
import com.yzh.androidquickdevlib.net.DefaultUrlCreator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.CookieStore;

abstract public class AbsHttpClient {
    private static AbsHttpClient sClient = null;
    private static HttpUrlCreator sCreator = new DefaultUrlCreator();

    public static AbsHttpClient instance() {
        if (sClient != null) {
            return sClient;
        }

        sClient = (AbsHttpClient) BaseApplication.instance()
                .getAppResource(BaseApplication.AppContext.RES_HTTP_CLIENT);
        return sClient;
    }

    abstract public void get(String url, Map<String, Object> params, Header[] headers, JsonResponseHandler responseHandler, boolean block);

    abstract public void get(String url, Map<String, Object> params, Header[] headers, HttpResponseHandler responseHandler, boolean block);


    //synch functions
    abstract public JSONObject getJSONObject(String url, Map<String, Object> params, Header[] headers) throws Exception;

    abstract public JSONArray getJSONArray(String url, Map<String, Object> params, Header[] headers) throws Exception;

    abstract public JSONObject postJSONObject(String url, Map<String, Object> params, Header[] headers) throws Exception;

    abstract public JSONObject postJSONObject(String url, JSONObject params, Header[] headers) throws Exception;

    abstract public HttpResponse getBytes(String url, Map<String, Object> params, Header[] headers);


    /**
     * 上传文件流到指定url
     *
     * @param url
     * @param fileStream  需要上传的文件stream
     * @param fileName    文件名
     * @param contentType 文件类型, 默认是 octec-stream
     * @param autoClose   是否在上传完成后自动关闭流
     * @param params      附加参数
     * @param headers     附加headers
     * @return
     * @throws Exception
     */
    abstract public JSONObject postFileStream(@NonNull String url,
            @NonNull String key,
            @NonNull InputStream fileStream,
            @Nullable String fileName,
            @Nullable String contentType,
            @Nullable boolean autoClose,
            @Nullable Map<String, Object> params,
            @Nullable Header[] headers) throws Exception;

    abstract public CookieStore getCookieStore();

    public static String createUrl(String url) {
        final HttpUrlCreator clientUrlCreator = (HttpUrlCreator) BaseApplication.instance()
                .getAppResource(BaseApplication.AppContext.RES_HTTP_CLIENT_URL_CREATOR);
        if (clientUrlCreator != null) {
            return clientUrlCreator.createUrl(url);
        }

        return sCreator.createUrl(url);
    }

}
