package com.yzh.androidquickdevlib.app.exceptionhandler;

public class Utils
{

    /**
     * 根据一个Throwable获取最后一次异常
     *
     * @param ex
     * @return
     */
    public static Throwable getLastCause(Throwable ex)
    {
        if (ex == null)
        {
            return null;
        }

        Throwable cause = ex;
        try
        {
            do
            {
                // 防止死循环
                if (cause.getCause() == null || cause == cause.getCause())
                {
                    break;
                }
                cause = cause.getCause();
            }
            while (true);
        } catch (Exception ignore)
        {
        }

        return cause;
    }
}
