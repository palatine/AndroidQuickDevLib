package com.yzh.androidquickdevlib.app.exceptionhandler;


import com.yzh.androidquickdevlib.R;
import com.yzh.androidquickdevlib.app.BaseApplication;

import static com.yzh.androidquickdevlib.app.BaseApplication.AppContext.RES_MAIN_ACTIVITY_CLASS;

class CommonExceptionHandler implements CrashHandler.ExceptionHandler {

    @Override
    public boolean handleException(Thread t, Throwable ex) {
        if (ex == null || BaseApplication.instance() == null) {
            return false;
        }

        try {
            final Class<?> restartClass = (Class<?>) BaseApplication.instance()
                    .getAppResource(RES_MAIN_ACTIVITY_CLASS);
            if (restartClass == null) {
                return false;
            }

            RestartAppTask.doTask(BaseApplication.instance()
                    .getResources()
                    .getString(R.string.app_error_tips), 2500);
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

}
