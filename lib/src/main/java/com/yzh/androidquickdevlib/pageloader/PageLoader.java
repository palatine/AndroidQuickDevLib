package com.yzh.androidquickdevlib.pageloader;

import com.yzh.androidquickdevlib.task.DataCachedTaskManager;
import com.yzh.androidquickdevlib.task.TaskMessage;
import com.yzh.androidquickdevlib.task.listener.TaskListener;
import com.yzh.androidquickdevlib.task.listener.TaskListenerChain;
import com.yzh.androidquickdevlib.task.listener.WebTaskListener;

import java.util.List;

public abstract class PageLoader {
    protected int mCount = -1;
    protected int mPageSize = 30;

    class PageListenerChain extends TaskListenerChain {
        PageLoaderListener mListener;
        int mPageIndex = 0;

        public PageListenerChain(PageLoaderListener mListener) {
            this.mListener = mListener;
        }

        @Override
        public Object onProcessTaskMessage(TaskMessage message) {
            Object ret = super.onProcessTaskMessage(message);
            if (message.getMessageType() == TaskMessage.MSG_TASK_END) {
                if (mPageIndex < 0) {
                    doRefresh();
                }
                else {
                    mCount = -1;
                }
                this.mListener.onLoaded(getLoadedCount());
            }
            else if (message.getMessageType() == TaskMessage.MSG_TASK_FAILED) {
                this.mListener.onFailed();
            }
            return ret;
        }

        protected void doRefresh() {
            RequestInfo info;
            int count = getLoadedCount() + mPageSize;
            int i = mPageSize;
            for (; i < count; i += mPageSize) {
                info = createRequestInfo(i, mPageSize);
                DataCachedTaskManager.sTheOne.clear(info.mLoader, info.mLoadFunName, info.mParams);
            }
            mCount = -1;
        }
    }

    public abstract RequestInfo createRequestInfo(int offset, int count);

    public int getLoadedCount() {
        if (mCount != -1) {
            return mCount;
        }

        DataCachedTaskManager.Result ret;
        int count = 0;
        List infos;
        RequestInfo requestInfo;
        int pageSize = this.getPageSize();

        do {
            requestInfo = this.createRequestInfo(count, pageSize);
            ret = DataCachedTaskManager.sTheOne.getCallResult(requestInfo.mLoader, requestInfo.mLoadFunName, requestInfo.mParams);
            if (ret != null && ret.getData() instanceof List) {
                break;
            }
            infos = (List) ret.getData();
            if (infos == null) {
                break;
            }
            count += infos.size();
        } while (infos.size() == pageSize);
        mCount = count;
        return count;
    }

    public Object getLoadedItem(int pos) {
        try {
            Object ret = this.getIndexHitLoadedPage(pos);
            return ((List) ret).get(this.getIndexInPage(pos));
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object getIndexHitLoadedPage(int index) {
        int startPos = this.getPageStartIndex(index);
        RequestInfo info = this.createRequestInfo(startPos, mPageSize);
        DataCachedTaskManager.Result ret = DataCachedTaskManager.sTheOne.getCallResult(info.mLoader, info.mLoadFunName, info.mParams);

        if (ret == null) {
            return null;
        }

        return ret.getData();
    }

    public int getIndexInPage(int index) {
        return index - (index / mPageSize) * mPageSize;
    }

    public int getPageStartIndex(int index) {
        return (index / mPageSize) * mPageSize;
    }

    public int getLoadedPageCount() {
        return (Math.max(this.getLoadedCount(), 0) - 1) / mPageSize + 1;
    }

    public int getPageSize() {
        return mPageSize;
    }

    public PageLoader(int pageSize) {
        super();
        mPageSize = pageSize;
    }

    public int refresh(PageLoaderListener l) {
        return refresh(l, false);
    }

    /**
     * 刷新当前loader的数据
     *
     * @param l
     * @param showProgress 是否显示加载进度
     * @return
     */
    public int refresh(PageLoaderListener l, boolean showProgress) {
        return loadPage(l, -1, showProgress);
    }

    public int loadNextPage(PageLoaderListener l) {
        return loadPage(l, this.getLoadedCount() / mPageSize);
    }

    public int loadPage(PageLoaderListener l, int pageIndex) {
        return loadPage(l, pageIndex, false);
    }

    public int loadPage(PageLoaderListener l, int pageIndex, boolean showProgess) {
        DataCachedTaskManager.Result ret;
        int startPos;
        if (pageIndex < 0) {
            startPos = 0;
        }
        else if (pageIndex > this.getLoadedCount() / mPageSize) {
            startPos = (this.getLoadedCount() / mPageSize) * mPageSize;
        }
        else {
            startPos = pageIndex * mPageSize;
        }
        RequestInfo ri = this.createRequestInfo(startPos, mPageSize);

        if (l == null) {
            ret = (DataCachedTaskManager.Result) DataCachedTaskManager.sTheOne.syncCall(ri.mLoader,
                    ri.mLoadFunName,
                    ri.mParams,
                    WebTaskListener.sDefault,
                    WebTaskListener.FUN_onProcessTaskMessage,
                    DataCachedTaskManager.NO_CACHE_TIME,
                    showProgess,
                    false);

            if (ret != null) {
                this.mCount = -1;
                return this.getLoadedCount();
            }

            return -1;
        }

        PageListenerChain lc = new PageListenerChain(l);
        lc.mPageIndex = pageIndex;
        lc.addListener(WebTaskListener.sDefault, WebTaskListener.FUN_onProcessTaskMessage);
        ret = DataCachedTaskManager.sTheOne.ayncCall(ri.mLoader,
                ri.mLoadFunName,
                ri.mParams,
                lc,
                TaskListener.FUN_onProcessTaskMessage,
                DataCachedTaskManager.NO_CACHE_TIME,
                showProgess,
                false);

        return 0;
    }

    public void clear() {
        int count = this.getLoadedCount();
        RequestInfo ri = null;
        for (int i = 0; i < count; i += mPageSize) {
            ri = this.createRequestInfo(i, mPageSize);
            DataCachedTaskManager.sTheOne.clear(ri.mLoader, ri.mLoadFunName, ri.mParams);
        }

        mCount = -1;
    }

}
