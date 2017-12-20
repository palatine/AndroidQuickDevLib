package com.yzh.androidquickdevlib.task.listener;

import android.text.TextUtils;


import com.yzh.androidquickdevlib.task.TaskMessage;
import com.yzh.androidquickdevlib.task.Utility;
import com.yzh.androidquickdevlib.utils.L;

import java.util.ArrayList;

public class TaskListenerChain implements TaskListener {

    private static final String TAG = TaskListenerChain.class.getCanonicalName();
    protected ArrayList<Object> mObjectChain = new ArrayList<Object>();
    protected ArrayList<String> mMethodChain = new ArrayList<String>();
    protected boolean mIgnoreDisposedUiReceiver = false;

    @Override
    public Object onProcessTaskMessage(TaskMessage message) {
        Object ret = null;
        for (int i = 0; i < mObjectChain.size() && ret == null; i++) {
            if (this.mIgnoreDisposedUiReceiver && !Utility.isRunningUi(mObjectChain.get(i))) {
                continue;
            }

            try {
                ret = Utility.callObjectMethod(mObjectChain.get(i), mMethodChain.get(i), message);
            }
            catch (Throwable e) {
                L.e(TAG,
                        mObjectChain.get(i)
                                .getClass()
                                .getName() + "->" + mMethodChain.get(i),
                        e);
                throw new RuntimeException(e);
            }
        }

        return ret;
    }

    public TaskListenerChain addListener(TaskListener l) {
        return addListener(l, TaskListener.FUN_onProcessTaskMessage);
    }

    public TaskListenerChain addListener(Object receiver, String method) {
        if (receiver != null && !TextUtils.isEmpty(method)) {
            mObjectChain.add(receiver);
            mMethodChain.add(method);
        }
        return this;
    }

    public TaskListenerChain addListener(int index, Object receiver, String method) {
        if (receiver != null && !TextUtils.isEmpty(method)) {
            mObjectChain.add(index, receiver);
            mMethodChain.add(index, method);
        }
        return this;
    }

    public TaskListenerChain removeListener(int index) {
        mObjectChain.remove(index);
        mMethodChain.remove(index);
        return this;
    }

    public TaskListenerChain setIgnoreDisPosedUiReceiver(boolean ignore) {
        this.mIgnoreDisposedUiReceiver = ignore;
        return this;
    }
}
