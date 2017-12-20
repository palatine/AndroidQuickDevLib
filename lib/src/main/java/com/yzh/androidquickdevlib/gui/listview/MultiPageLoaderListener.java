package com.yzh.androidquickdevlib.gui.listview;

/**
 * Created by yzh on 2016/11/25.
 */



import com.yzh.androidquickdevlib.pageloader.PageLoaderListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 多页面刷新请求的PageLoaderListener
 */
public class MultiPageLoaderListener implements PageLoaderListener {
    private PageLoaderListener mResultListener;
    private List<DelegatePageLoaderListener> loaderListeners = new ArrayList<>();
    private boolean mIsFinished = false;

    /**
     * 代理监听器
     */
    public class DelegatePageLoaderListener implements PageLoaderListener {
        public static final int LOADED_NONE = 0;
        public static final int LOADED_OK = 1;
        public static final int LOADED_FAILED = 2;
        /**
         * PageLoaderListener的执行状态
         */
        private int mStatus = LOADED_NONE;
        private PageLoaderListener mDelegate;

        public DelegatePageLoaderListener(PageLoaderListener mDelegate) {
            this.mDelegate = mDelegate;
        }

        @Override
        public void onLoaded(int newLoadedCount) {
            this.mStatus = LOADED_OK;
            if (this.mDelegate != null) {
                this.mDelegate.onLoaded(newLoadedCount);
            }
        }

        @Override
        public void onFailed() {
            this.mStatus = LOADED_FAILED;
            if (this.mDelegate != null) {
                this.mDelegate.onFailed();
            }
        }

        public int getStatus() {
            return mStatus;
        }
    }

    /**
     * 多页面刷新请求的PageLoaderListener
     *
     * @param finalResultListener 多PageLoad全部请求完成的回调
     */
    public MultiPageLoaderListener(PageLoaderListener finalResultListener) {
        this.mResultListener = finalResultListener;
    }

    /**
     * 添加一个DelegatePageLoaderListener到队列中
     */
    public DelegatePageLoaderListener addAndGetNewDelegatePageLoaderListener() {
        DelegatePageLoaderListener listener = new DelegatePageLoaderListener(this);
        this.loaderListeners.add(listener);
        return listener;
    }

    /**
     * 获取当前的执行结果
     *
     * @return
     */
    private int getDelegatePageLoaderListenersStatus() {
        final int count = this.loaderListeners.size();
        int i = 0;
        for (; i < count; i++) {
            final int status = this.loaderListeners.get(i)
                    .getStatus();
            if (status == DelegatePageLoaderListener.LOADED_NONE) {
                return DelegatePageLoaderListener.LOADED_NONE;
            }
            else if (status == DelegatePageLoaderListener.LOADED_FAILED) {
                return DelegatePageLoaderListener.LOADED_FAILED;
            }
        }

        return DelegatePageLoaderListener.LOADED_OK;
    }

    @Override
    public void onLoaded(int newLoadedCount) {
        if (!this.mIsFinished && getDelegatePageLoaderListenersStatus() == DelegatePageLoaderListener.LOADED_OK) {
            this.mIsFinished = true;
            if (this.mResultListener != null) {
                this.mResultListener.onLoaded(newLoadedCount);
            }
        }
    }

    @Override
    public void onFailed() {
        if (!this.mIsFinished && this.mResultListener != null) {
            this.mIsFinished = true;
            this.mResultListener.onFailed();
        }
    }
}
