package com.yzh.androidquickdevlib.gui.listview;

import android.widget.BaseAdapter;

import com.yzh.androidquickdevlib.pageloader.PageLoader;
import com.yzh.androidquickdevlib.pageloader.PageLoaderListener;

public abstract class RefreshableAdapter extends BaseAdapter implements PageLoaderListener, RefreshableListView.RefreshableListViewListener {

    protected PageLoader mPageLoader = null;
    protected boolean mIsLoading = false;

    public RefreshableAdapter(PageLoader pageLoader) {
        mPageLoader = pageLoader;
    }

    public void refresh() {
        refresh(false);
    }

    /**
     * 刷新数据
     *
     * @param showProgress 是否显示进度
     */
    public void refresh(boolean showProgress) {
        mPageLoader.refresh(this, showProgress);
    }

    /**
     * 根据指定的position刷新相应的页面数据(position计算从0开始, 只位于listview内的位置)
     *
     * @param position
     * @param refreshOrDelete true : 删除了该项需要刷新, false : 仅更新内容没有删除
     */
    public void refreshPageByPos(int position, boolean refreshOrDelete) {
        if (position < 0 || position > mPageLoader.getLoadedCount()) {
            return;
        }

        if (mPageLoader.getPageSize() > 0) {
            refreshPage(position / mPageLoader.getPageSize(), refreshOrDelete);
        }
    }

    /**
     * 刷新指定的页面数据(必须是已经loaded的)
     *
     * @param pageIndex
     * @param refreshFromNext 是否需要刷新本页之后的所有页面
     */
    public void refreshPage(int pageIndex, boolean refreshFromNext) {
        if (pageIndex < 0 || pageIndex > mPageLoader.getLoadedPageCount()) {
            return;
        }

        if (!refreshFromNext) {
            mPageLoader.loadPage(this, pageIndex);
        }
        else {
            MultiPageLoaderListener multiPageLoaderListener = new MultiPageLoaderListener(this);
            final int count = mPageLoader.getLoadedPageCount();
            for (int i = pageIndex; i < count; i++) {
                mPageLoader.loadPage(multiPageLoaderListener.addAndGetNewDelegatePageLoaderListener(), i);
            }
        }
    }

    @Override
    public boolean onRefreshOrMore(RefreshableListView RefreshableListView, boolean isRefresh) {
        mIsLoading = true;
        if (!isRefresh) {
            mPageLoader.loadNextPage(this);
        }
        else {
            mPageLoader.refresh(this);
        }
        return false;
    }

    @Override
    public boolean onQueryFinished(boolean isRefresh) {
        return !this.mIsLoading;
    }

    @Override
    public void onFailed() {
        this.mIsLoading = false;
    }


    @Override
    public void onLoaded(int newLoadedCount) {
        this.mIsLoading = false;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mPageLoader.getLoadedCount();
    }


    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
