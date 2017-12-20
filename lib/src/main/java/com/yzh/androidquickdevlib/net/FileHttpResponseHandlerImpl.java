package com.yzh.androidquickdevlib.net;



import com.yzh.androidquickdevlib.net.interfaces.HttpResponseHandler;
import com.yzh.androidquickdevlib.utils.FileUtils;

import java.io.File;

import cz.msebera.android.httpclient.Header;

/**
 * Created by yzh on 2017/6/14.
 */

public class FileHttpResponseHandlerImpl implements HttpResponseHandler {
    public String filePath;
    public Exception exception;

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
        FileUtils.writeFile(new File(filePath), response);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
        this.exception = new java.net.ConnectException(e == null ? "UNKNOWN" : e.getMessage());
    }
}
