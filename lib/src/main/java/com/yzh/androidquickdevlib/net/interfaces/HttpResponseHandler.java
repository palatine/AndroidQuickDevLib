package com.yzh.androidquickdevlib.net.interfaces;


import cz.msebera.android.httpclient.Header;

public interface HttpResponseHandler
{
	void onSuccess(int statusCode, Header[] headers, byte[] response);
	void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e);
	 
}
