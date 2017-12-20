package com.yzh.androidquickdevlib.gui.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import com.yzh.androidquickdevlib.pageloader.RequestInfo;
import com.yzh.androidquickdevlib.task.DataCachedTaskManager;
import com.yzh.androidquickdevlib.task.TaskMessage;
import com.yzh.androidquickdevlib.task.listener.TaskListener;
import com.yzh.androidquickdevlib.task.listener.WebTaskListener;


public abstract class HttpBaseAapter extends BaseAdapter implements TaskListener {

    public abstract RequestInfo createRequestInfo();

    public abstract int getLoadedCount();

    public abstract Object getLoadedItem(int index);

    public abstract void onLoaded(Object data);


    /**
     * 数据加载完成的监听接口
     */
    public interface OnDataLoadedListener {
        void onDataLoaded();
    }

    protected int mCount = -1;
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected boolean showProgressbar = true;
    protected OnDataLoadedListener onDataloadedListener;

    public HttpBaseAapter(Context context) {
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void load(boolean refresh) {
        load(refresh, null);
    }

    public void load(boolean refresh, OnDataLoadedListener listener) {
        if (refresh) {
            cleanUp();
        }

        this.onDataloadedListener = listener;
        startAsyncCall();
    }

    private void startAsyncCall() {
        mCount = -1;
        RequestInfo info = this.createRequestInfo();
        if (info != null) {
            DataCachedTaskManager.sTheOne.ayncCall(info.mLoader, info.mLoadFunName, info.mParams, HttpBaseAapter.this, DataCachedTaskManager.DEFAULT_TIME, showProgressbar, true);
        }
    }

    @Override
    final public Object onProcessTaskMessage(TaskMessage message) {
        if (message.getMessageType() == TaskMessage.MSG_TASK_END) {
            onLoaded(message.getMessage());

            //通知数据集变化
            notifyDataSetChanged();
            notifyDataLoaded();

            return null;
        }

        return WebTaskListener.sDefault.onProcessTaskMessage(message);
    }

    /**
     * 通知结果已经取回
     */
    private void notifyDataLoaded() {
        if (this.onDataloadedListener != null) {
            this.onDataloadedListener.onDataLoaded();
        }
    }

    protected DataCachedTaskManager.Result getCallResult() {
        final RequestInfo info = this.createRequestInfo();
        if (info != null) {
            return DataCachedTaskManager.sTheOne.getCallResult(info.mLoader, info.mLoadFunName, info.mParams);
        }

        return null;
    }

    @Override
    public Object getItem(int position) {
        return getLoadedItem(position);
    }

    @Override
    public int getCount() {
        return this.getLoadedCount();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void cleanUp() {
        final RequestInfo info = this.createRequestInfo();
        if (info != null) {
            DataCachedTaskManager.sTheOne.clear(info.mLoader, info.mLoadFunName, info.mParams);
        }
    }

    public void setShowProgressbar(boolean showProgressbar) {
        this.showProgressbar = showProgressbar;
    }
}
