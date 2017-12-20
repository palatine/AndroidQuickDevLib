package com.yzh.androidquickdevlib.net.interfaces;


import cz.msebera.android.httpclient.Header;

public class HttpResponse
{
   
	protected int mStatusCode;
    protected  Header[] mHeaders;
    protected byte []mBody;
    
	public int getStatusCode()
	{
		return mStatusCode;
	}
	public void setStatusCode(int statusCode)
	{
		mStatusCode = statusCode;
	}
	public Header[] getHeaders()
	{
		return mHeaders;
	}
	public void setHeaders(Header[] headers)
	{
		mHeaders = headers;
	}
	public byte[] getBody()
	{
		return mBody;
	}
	public void setBody(byte[] body)
	{
		mBody = body;
	}

	public HttpResponse(int statusCode, Header[] headers, byte[] body)
	{

		mStatusCode = statusCode;
		mHeaders = headers;
		mBody = body;
	}
	
	public HttpResponse()
	{
         this(0,null,null);
	}
	    
}
