package com.yzh.androidquickdevlib.task;

import android.app.Activity;
import android.os.AsyncTask;

import com.yzh.androidquickdevlib.utils.L;


public class TaskRunner extends AsyncTask<Object, Integer, Object> {
    private static final String TAG = TaskRunner.class.getCanonicalName();
    private boolean mFailed = true;

    protected boolean mShowProgressBar = false;

    protected Object mTaskListener = null;
    protected String mTaskListenerMethod = null;

    protected Object[] mParams = null;
    protected Object mTask = null;
    protected String mTaskMethod = null;
    protected Activity mStartActivity = null;
    protected boolean mIgnoreListenerWhenUiDisposed = false;
    protected TaskManager mTaskManager = null;

    public Object doSynchronized() {
        return this.doInBackground(mParams);
    }

    protected Object doInBackground(Object... objects) {
        boolean retry = false;
        Object ret = null;
        int retryTimes = 0;
        do {
            try {
                mFailed = true;
                ret = Utility.callObjectMethod(mTask, mTaskMethod, mParams);
                retry = false;
                mFailed = false;
            }
            catch (Throwable e) {
                if (mTaskListener != null) {
                    if (!mIgnoreListenerWhenUiDisposed || Utility.isRunningUi(mTaskListener)) {
                        TaskMessage l = new TaskMessage();
                        l.mMsgType = TaskMessage.MSG_EXCEPTION;
                        l.mMessage = e;
                        l.mSender = mTask;
                        l.mRetryTimes = retryTimes;
                        Object messageResult = ThreadUtility.runObjectOnUiThreadBlocked(mTaskListener, mTaskListenerMethod, l);
                        if (messageResult != null && messageResult instanceof Boolean) {
                            retry = (Boolean) messageResult;
                            retryTimes++;
                            continue;
                        }

                    }
                }

                L.e(TAG, e.getMessage(), e);

                if (Utility.isRunningUi(mTaskListener)) {
                    throw new RuntimeException(e);
                }
            }
        } while (retry);
        return ret;
    }

    protected void onPostExecute(Object result) {
        if (mTaskListener != null && (!mIgnoreListenerWhenUiDisposed || Utility.isRunningUi(mTaskListener))) {
            TaskMessage l = new TaskMessage();
            l.mSender = mTask;
            l.mMessage = result;
            l.mMsgType = mFailed ? TaskMessage.MSG_TASK_FAILED : TaskMessage.MSG_TASK_END;
            try {
                Utility.callObjectMethod(mTaskListener, mTaskListenerMethod, new Object[]{l});
            }
            catch (Throwable e) {
                L.e(TAG,
                        mTaskListener.getClass()
                                .getName(),
                        e);
                throw new RuntimeException(e);
            }
        }
        mTaskManager.onPostExecute(this, result);
    }


    protected void onPreExecute() {
        this.mTaskManager.onPreTaskExecute(this);
        if (mTaskListener != null && (!mIgnoreListenerWhenUiDisposed || Utility.isRunningUi(mTaskListener))) {
            TaskMessage l = new TaskMessage();
            l.mSender = mTask;
            l.mMessage = mParams;
            l.mMsgType = TaskMessage.MSG_TASK_START;
            try {
                Utility.callObjectMethod(mTaskListener, mTaskListenerMethod, new Object[]{l});
            }
            catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

}
