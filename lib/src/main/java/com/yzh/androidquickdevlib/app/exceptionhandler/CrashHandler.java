package com.yzh.androidquickdevlib.app.exceptionhandler;

import android.support.annotation.Nullable;
import android.util.Log;

public class CrashHandler {
    public static final String TAG = "CrashHandler";
    private static CrashHandler instance;

    public interface ExceptionHandler {
        boolean handleException(Thread t, Throwable ex);
    }

    public static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private ExceptionHandler mExceptionHandler = null;

    private CrashHandler() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
            if (ex != null) {
                Log.d("Uncaught exception", "Uncaught", ex);
            }

            if (this.mExceptionHandler != null && this.mExceptionHandler.handleException(thread, ex)) {
                return;
            }

            // firset use default handler
            CrashHandler.ExceptionHandler handler = ExceptionHandlerFactory.getExceptionHandler(ex);
            if (handler != null && handler.handleException(thread, ex)) {
                return;
            }

            // second use default fatal handler
            handler = ExceptionHandlerFactory.getExceptionHandler(ex, true);
            if (handler != null && handler.handleException(thread, ex)) {
                return;
            }

            if (mDefaultHandler != null) {
                mDefaultHandler.uncaughtException(thread, ex);
            }
            else {
                Log.e(TAG, "error : ", ex);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
    }

    public void setExceptionHandler(@Nullable ExceptionHandler exceptionHandler) {
        this.mExceptionHandler = exceptionHandler;
    }
}
