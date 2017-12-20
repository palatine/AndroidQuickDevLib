package com.yzh.androidquickdevlib.task;

/**
 * Created by yzh on 2017/10/28.
 */
public class TaskMessage {
    public final static int MSG_TASK_START = 1;
    public final static int MSG_TASK_END = 2;

    /*
     * task failed with exception
     */
    public final static int MSG_EXCEPTION = 3;

    /*
     * task failed
     */
    public final static int MSG_TASK_FAILED = 5;

    protected Object mSender;
    protected int mMsgType;
    protected Object mMessage;
    protected int mRetryTimes = 0;

    public TaskMessage() {
        this(null, 0, null, 0);
    }

    public TaskMessage(Object sender, int msgType, Object message) {
        this(sender, msgType, message, 0);
    }

    public TaskMessage(Object sender, int msgType, Object message, int retrytimes) {
        mSender = sender;
        mMsgType = msgType;
        mMessage = message;
        mRetryTimes = retrytimes;
    }

    public Object getSender() {
        return mSender;
    }

    public int getMessageType() {
        return mMsgType;
    }

    public Object getMessage() {
        return mMessage;
    }

    public int getRetryTimes() {
        return mRetryTimes;
    }

}
