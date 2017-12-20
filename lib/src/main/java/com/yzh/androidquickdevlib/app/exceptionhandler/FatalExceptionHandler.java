package com.yzh.androidquickdevlib.app.exceptionhandler;

import android.content.Context;
import android.content.Intent;

import com.yzh.androidquickdevlib.R;
import com.yzh.androidquickdevlib.app.BaseApplication;
import com.yzh.androidquickdevlib.gui.DialogActivity;


class FatalExceptionHandler implements CrashHandler.ExceptionHandler {
    @Override
    public boolean handleException(Thread t, Throwable ex) {
        if (ex == null || BaseApplication.instance() == null) {
            return false;
        }

        try {
            //            if (!AnalyticsUtils.getInstance()
            //                    .errReportEnabled()) {
            //                SubmitErrorTask task = new SubmitErrorTask();
            //                task.setException(ex);
            //                task.submit();
            //            }

            Context context = BaseApplication.instance();
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(context, DialogActivity.class);
            if (Utils.getLastCause(ex) instanceof java.lang.SecurityException) {
                intent.putExtra(DialogActivity.MESSAGE_TEXT, context.getString(R.string.app_error_due_to_permission));
            }
            else {
                intent.putExtra(DialogActivity.MESSAGE_TEXT, context.getString(R.string.app_crashed_tips));
            }

            intent.putExtra(DialogActivity.BUTTONS_TEXT, new String[]{context.getString(R.string.confirm)});

            try {
                context.startActivity(intent);
            }
            catch (Exception ignore) {
            }

            // 强制停止程序
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(3500);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    BaseApplication.exitForcely();
                }
            }.start();

        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

}
