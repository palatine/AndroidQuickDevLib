package com.yzh.androidquickdevlib.task.listener;


import android.app.Activity;
import android.app.Dialog;

import com.yzh.androidquickdevlib.R;
import com.yzh.androidquickdevlib.app.BaseApplication;
import com.yzh.androidquickdevlib.gui.page.ActivityPage;
import com.yzh.androidquickdevlib.gui.page.PageActivity;
import com.yzh.androidquickdevlib.gui.views.BlockMessageDialog;
import com.yzh.androidquickdevlib.net.exceptions.OperationFailedException;
import com.yzh.androidquickdevlib.net.exceptions.SessionExpiredException;
import com.yzh.androidquickdevlib.task.DataCachedTaskManager;
import com.yzh.androidquickdevlib.task.TaskMessage;
import com.yzh.androidquickdevlib.utils.T;
import com.yzh.modaldialog.dialog.interfaces.IModalDialog;

import java.net.ConnectException;

public class WebTaskListener implements TaskListener {
    public static final WebTaskListener sDefault = new WebTaskListener();

    /**
     * 获取默认的webtask listener chain
     *
     * @return
     */
    public static TaskListenerChain getDefaultTaskListenerChain() {
        return new TaskListenerChain().addListener(sDefault);
    }

    @Override
    public Object onProcessTaskMessage(TaskMessage message) {
        if (message.getMessageType() == TaskMessage.MSG_EXCEPTION) {
            if (message.getMessage() instanceof java.net.ConnectException) {
                // 当retrytimes > 0 时, 每重试3次才弹连接失败对话框, 否则自动返回true以重试
                if (message.getRetryTimes() % 4 != 3) {
                    return true;
                }

                // show retry connect dlg if have
                final Activity activity = BaseApplication.getCurrentActivity();
                if (BaseApplication.isActivityRunning(activity)) {
                    final String msg = ((ConnectException) message.getMessage()).getMessage();
                    T.showLong(msg);

                    final IModalDialog dlg = new BlockMessageDialog(activity, R.string.query_reconnect, true);
                    if (dlg.doModal() == Dialog.BUTTON_POSITIVE) {
                        return true;
                    }
                }

                return false;
            }
            else if (message.getMessage() instanceof SessionExpiredException) {
                // 清除data cache manager数据
                DataCachedTaskManager.sTheOne.clear();

                Class<?> pageClass = (Class<?>) BaseApplication.instance()
                        .getAppResource(BaseApplication.AppContext.RES_LOGIN_PAGE);

                if (pageClass == null) {
                    return null;
                }

                Activity a = BaseApplication.getCurrentActivity();
                if (!(a instanceof PageActivity)) {
                    return null;
                }

                try {
                    PageActivity.setPage((ActivityPage) pageClass.newInstance(), true, true);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }

                return false;
            }
            else if (message.getMessage() instanceof OperationFailedException) {
                try {
                    final OperationFailedException exception = (OperationFailedException) message.getMessage();
                    T.showLong(exception.getMessage());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }

        }
        return null;
    }

}
