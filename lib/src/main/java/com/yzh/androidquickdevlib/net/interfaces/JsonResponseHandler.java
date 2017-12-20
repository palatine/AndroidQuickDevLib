package com.yzh.androidquickdevlib.net.interfaces;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public interface JsonResponseHandler
{
	void onSuccess(int statusCode, Header[] headers, JSONObject response);
    void onSuccess(int statusCode, Header[] headers, JSONArray timeline);
    void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error);
}
