package com.yzh.androidquickdevlib.app.exceptionhandler;


import java.util.HashSet;

public class ExceptionHandlerFactory {
    private final static HashSet<Class<?>> FATAL_EXCEPTION_SET;

    static {
        FATAL_EXCEPTION_SET = new HashSet<>();
        FATAL_EXCEPTION_SET.add(java.lang.SecurityException.class);
    }

    /**
     * 获取Crash Exception handler
     *
     * @param ex
     * @return
     */
    public static CrashHandler.ExceptionHandler getExceptionHandler(Throwable ex) {
        return getExceptionHandler(ex, false);
    }

    /**
     * 获取Crash Exception handler
     *
     * @param ex
     * @param forceFatal, 如果为true表示强制返回Fatal exception handler
     * @return
     */
    public static CrashHandler.ExceptionHandler getExceptionHandler(Throwable ex, boolean forceFatal) {
        if (ex == null) {
            return null;
        }

        final Class<?> lastCauseClz = Utils.getLastCause(ex)
                .getClass();
        final boolean isFatalErr = FATAL_EXCEPTION_SET.contains(lastCauseClz);
        if (isFatalErr || forceFatal) {
            return new FatalExceptionHandler();
        }

        return new CommonExceptionHandler();
    }

}
