package com.yzh.androidquickdevlib.task;

import android.os.AsyncTask;

import com.yzh.androidquickdevlib.app.BaseApplication;
import com.yzh.androidquickdevlib.task.listener.TaskListener;
import com.yzh.androidquickdevlib.utils.ProgressWindowHelper;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    protected ArrayList<TaskRunner> mWaitingTasks = new ArrayList<TaskRunner>();
    protected ArrayList<TaskRunner> mRunningTasks = new ArrayList<TaskRunner>();
    protected int mMaxThreadCount = 5;

    public static TaskManager sTheOne = new TaskManager();

    protected TaskManager() {
    }


    final public synchronized void startTask(Object task, String taskMethod, boolean showProgressBar) {
        this.startTask(task, taskMethod, null, null, null, showProgressBar, false);
    }


    final public synchronized void startTask(Object task, String taskMethod, Object listener, String listenerMethod) {
        this.startTask(task, taskMethod, null, listener, listenerMethod, false, false);
    }

    final public synchronized void startTask(Object task, String taskMethod, Object listener, String listenerMethod, boolean showProgress, boolean ignoreListenerWhenUiDisposed) {
        this.startTask(task, taskMethod, null, listener, listenerMethod, showProgress, ignoreListenerWhenUiDisposed);
    }

    final public synchronized void startUiSafeTask(Object task, String taskMethod, Object[] params, Object listener, String listenerMethod, boolean showProgress) {
        this.startTask(task, taskMethod, params, listener, listenerMethod, showProgress, true);
    }

    final public synchronized void startUiSafeTask(Object task, String taskMethod, Object listener, String listenerMethod, boolean showProgress) {
        this.startTask(task, taskMethod, null, listener, listenerMethod, showProgress, true);
    }

    final public synchronized void startUiSafeTask(Object task, String taskMethod) {
        this.startTask(task, taskMethod, null, null, null, false, true);
    }

    final public synchronized void startTask(Object task, String taskMethod, Object[] params, TaskListener l, boolean showProgressBar, boolean ignoreListenerWhenUiDisposed) {
        startTask(task, taskMethod, params, l, TaskListener.FUN_onProcessTaskMessage, showProgressBar, ignoreListenerWhenUiDisposed);
    }

    final public synchronized void startUiSafeTask(Object task, String taskMethod, Object[] params, TaskListener l) {
        startTask(task, taskMethod, params, l, TaskListener.FUN_onProcessTaskMessage, true, true);
    }

    final public synchronized void startUiSafeTask(Object task, String taskMethod, Object[] params, TaskListener l, boolean showProgressBar) {
        startTask(task, taskMethod, params, l, TaskListener.FUN_onProcessTaskMessage, showProgressBar, true);
    }

    final public synchronized void startTask(Object task,
            String taskMethod,
            Object[] params,
            Object listener,
            String listenerMethod,
            boolean showProgressBar,
            boolean ignoreListenerWhenUiDisposed) {
        TaskRunner mTask = new TaskRunner();
        mTask.mShowProgressBar = showProgressBar;
        mTask.mParams = params;
        mTask.mTask = task;
        mTask.mTaskListener = listener;
        mTask.mTaskListenerMethod = listenerMethod;
        mTask.mTaskMethod = taskMethod;
        mTask.mIgnoreListenerWhenUiDisposed = ignoreListenerWhenUiDisposed;
        mTask.mTaskManager = this;
        this.mWaitingTasks.add(mTask);

        this.doNextTask();

    }

    final public Object startSyncTask(Object task,
            String taskMethod,
            Object[] params,
            Object listener,
            String listenerMethod,
            boolean showProgressBar,
            boolean ignoreListenerWhenUiDisposed) {
        TaskRunner mTask = new TaskRunner();
        mTask.mShowProgressBar = showProgressBar;
        mTask.mParams = params;
        mTask.mTask = task;
        mTask.mTaskListener = listener;
        mTask.mTaskListenerMethod = listenerMethod;
        mTask.mTaskMethod = taskMethod;
        mTask.mIgnoreListenerWhenUiDisposed = ignoreListenerWhenUiDisposed;
        mTask.mTaskManager = this;
        return mTask.doSynchronized();
    }

    final public synchronized TaskRunner doNextTask() {
        if (this.mRunningTasks.size() < this.mMaxThreadCount && this.mWaitingTasks.size() > 0) {
            TaskRunner mTask = this.mWaitingTasks.remove(0);
            this.mRunningTasks.add(mTask);
            mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mTask.mParams == null ? null : mTask.mParams);
            return mTask;
        }

        return null;
    }

    public synchronized List<TaskRunner> getWaitingTasks() {
        return this.mWaitingTasks;
    }

    public synchronized void setMaxThreadCount(int count) {
        this.mMaxThreadCount = count;
    }

    public synchronized int getMaxThreadCount() {
        return this.mMaxThreadCount;
    }

    final protected synchronized void removeRunningTask(TaskRunner l) {
        for (int i = 0; i < this.mRunningTasks.size(); i++) {
            if (this.mRunningTasks.get(i) == l) {
                this.mRunningTasks.remove(i);
                break;
            }
        }
    }

    protected void onPreTaskExecute(TaskRunner runner) {
        runner.mStartActivity = BaseApplication.getCurrentActivity();
        if (runner.mShowProgressBar) {
            ProgressWindowHelper.showProgressWindow();
        }
    }

    protected void onPostExecute(TaskRunner runner, Object result) {
        removeRunningTask(runner);
        if (runner.mShowProgressBar) {
            ProgressWindowHelper.hideProgressWindow();
        }
        doNextTask();
    }
}
