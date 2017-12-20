package com.yzh.androidquickdevlib.task.listener;


import com.yzh.androidquickdevlib.task.TaskMessage;

public interface TaskListener {

    String FUN_onProcessTaskMessage = "onProcessTaskMessage";

    Object onProcessTaskMessage(TaskMessage message);
}
