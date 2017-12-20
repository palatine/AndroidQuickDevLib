package com.yzh.androidquickdevlib.pageloader;

public interface PageLoaderListener
{
	void onLoaded(int newLoadedCount);
	void onFailed();
}
