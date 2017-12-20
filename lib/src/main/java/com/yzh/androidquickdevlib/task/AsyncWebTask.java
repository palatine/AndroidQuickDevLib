package com.yzh.androidquickdevlib.task;


import com.yzh.androidquickdevlib.task.listener.TaskListener;
import com.yzh.androidquickdevlib.task.listener.WebTaskListener;

/**
 * Created by yzh on 2017/6/27.
 */

public class AsyncWebTask implements IAsyncTaskRunner {
    private Object task;
    private String taskMethod;
    private Object[] params;
    private Object listener;
    private String listenerMethod;
    private boolean showProgress;
    private boolean ignoreListenerWhenUiDisposed;
    private long cacheLife = DataCachedTaskManager.NO_CACHE_TIME;

    /**
     * a default async task can be execute
     *
     * @param task
     * @param taskMethod
     * @param params
     * @param listener
     * @param listenerMethod
     * @param showProgress
     * @param ignoreListenerWhenUiDisposed
     */
    private AsyncWebTask(Object task,
            String taskMethod,
            Object[] params,
            Object listener,
            String listenerMethod,
            long cacheLife,
            boolean showProgress,
            boolean ignoreListenerWhenUiDisposed) {
        this.task = task;
        this.taskMethod = taskMethod;
        this.params = params;
        this.listener = listener;
        this.listenerMethod = listenerMethod;
        this.showProgress = showProgress;
        this.ignoreListenerWhenUiDisposed = ignoreListenerWhenUiDisposed;
        this.cacheLife = cacheLife;
    }

    @Override
    public void startTask() {
        new DefaultTaskRunnerImpl().startTask();
    }

    /**
     * build a AsyncTask
     */
    public static class Builder {
        private Object task;
        private String taskMethod;
        private Object[] params = null;
        private Object listener = WebTaskListener.sDefault;
        private String listenerMethod = TaskListener.FUN_onProcessTaskMessage;
        private boolean showProgress = true;
        private boolean ignoreListenerWhenUiDisposed = false;
        private long cacheLife = DataCachedTaskManager.NO_CACHE_TIME;

        public Builder setTask(Object task) {
            this.task = task;
            return this;
        }

        public Builder setTaskMethod(String taskMethod) {
            this.taskMethod = taskMethod;
            return this;
        }

        public Builder setParams(Object... params) {
            this.params = params;
            return this;
        }

        public Builder setListener(Object listener) {
            this.listener = listener;
            return this;
        }

        public Builder setListenerMethod(String listenerMethod) {
            this.listenerMethod = listenerMethod;
            return this;
        }

        public Builder setShowProgress(boolean showProgress) {
            this.showProgress = showProgress;
            return this;
        }

        public Builder cacheLife(long cacheLife) {
            this.cacheLife = cacheLife;
            return this;
        }

        public Builder setIgnoreListenerWhenUiDisposed(boolean ignoreListenerWhenUiDisposed) {
            this.ignoreListenerWhenUiDisposed = ignoreListenerWhenUiDisposed;
            return this;
        }

        public AsyncWebTask build() {
            return new AsyncWebTask(task, taskMethod, params, listener, listenerMethod, cacheLife, showProgress, ignoreListenerWhenUiDisposed);
        }
    }

    /**
     * default task runner
     */
    private class DefaultTaskRunnerImpl implements IAsyncTaskRunner {

        @Override
        public void startTask() {
            DataCachedTaskManager.sTheOne.ayncCall(task, taskMethod, params, listener, listenerMethod, cacheLife, showProgress, ignoreListenerWhenUiDisposed);
        }
    }
}
