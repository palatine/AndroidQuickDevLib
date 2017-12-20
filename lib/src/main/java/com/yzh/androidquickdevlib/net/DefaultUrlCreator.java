package com.yzh.androidquickdevlib.net;


import com.yzh.androidquickdevlib.app.BaseApplication;
import com.yzh.androidquickdevlib.net.interfaces.HttpScheme;
import com.yzh.androidquickdevlib.net.interfaces.HttpUrlCreator;

import cz.msebera.android.httpclient.util.TextUtils;

/**
 * Created by yzh on 2017/10/28.
 */

public class DefaultUrlCreator implements HttpUrlCreator {
    @Override
    public String createUrl(String url) {
        if (!TextUtils.isEmpty(url) && (url.startsWith(HttpScheme.HTTP_SCHEME) || url.startsWith(HttpScheme.HTTPS_SCHEME))) {
            return url;
        }
        final String hostAddr = (String) BaseApplication.instance()
                .getAppResource(BaseApplication.AppContext.RES_HOST_ADDR);
        return HttpScheme.HTTP_SCHEME + hostAddr + (hostAddr.endsWith("/") ? "" : "/") + url;
    }
}
