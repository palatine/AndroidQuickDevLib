package com.yzh.androidquickdevlib.task.listener;


import com.yzh.androidquickdevlib.task.TaskMessage;

/**
 * Created by yzh on 2017/5/18.
 */

public class DefaultTaskEndListener implements TaskEndListener {
    @Override
    public Object onProcessTaskMessage(TaskMessage message) {
        if (message.getMessageType() == TaskMessage.MSG_TASK_END) {
            onTaskEnd(message.getMessage());
        }
        return null;
    }


    @Override
    public void onTaskEnd(Object message) {

    }
}
