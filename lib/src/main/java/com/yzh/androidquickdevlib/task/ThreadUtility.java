package com.yzh.androidquickdevlib.task;

import android.os.Handler;
import android.os.Looper;

public class ThreadUtility {
    /**
     * the singleton of handler
     */
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    /*
     * @Notice Only object parameters are supported.
	 */
    public static Object runObjectOnUiThreadBlocked(final Object o, final String method, final Object... params) {
        class R implements Runnable {
            Object mRet = null;

            @Override
            public void run() {
                try {
                    mRet = Utility.callObjectMethod(o, method, params);
                }
                catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        }

        R r = new R();
        runOnUiThreadBlocked(r);
        return r.mRet;
    }

    /**
     * 在UI线程上执行runnale, 如果当前不在主线程会post到主线程并且等待执行完成
     *
     * @param runnable
     */
    public static void runOnUiThreadBlocked(Runnable runnable) {
        class SynRun implements Runnable {
            protected Runnable mRunnable = null;

            public SynRun(Runnable runnable) {
                mRunnable = runnable;
            }

            public synchronized void run() {
                try {
                    mRunnable.run();
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
                finally {
                    this.notifyAll();
                }
            }
        }

        SynRun run = new SynRun(runnable);
        synchronized (run) {
            if (isMain()) {
                runnable.run();
                return;
            }

            sHandler.post(run);

            try {
                run.wait();
            }
            catch (java.lang.InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * post 指定的runnable到主线程消息循环
     *
     * @param runnable
     * @param reuse    当reuse为 true, 并且当前是主线程的话, 就立即执行并等待返回,
     */
    public static void postOnUiThread(Runnable runnable, boolean reuse) {
        if (reuse && isMain()) {
            runnable.run();
            return;
        }

        // post 到主线程
        sHandler.post(runnable);
    }

    /**
     * post 指定的runnable到主线程消息循环
     *
     * @param runnable
     */
    public static void postOnUiThreadReuse(Runnable runnable) {
        postOnUiThread(runnable, true);
    }

    /**
     * post 指定的runnable到主线程消息循环
     *
     * @param runnable
     */
    public static void postOnUiThreadNonReuse(Runnable runnable) {
        postOnUiThread(runnable, false);
    }

    /**
     * 将Runnable post 到主线程消息循环并延时执行
     *
     * @param runnable
     * @param delayMillis 延时的时间数
     */
    public static void postOnUiThreadDelayed(Runnable runnable, long delayMillis) {
        sHandler.postDelayed(runnable, delayMillis);
    }

    /**
     * 将主线程上还未执行的Runnable移除
     *
     * @param runnable
     */
    public static void removeRunnableOnUiThread(Runnable runnable) {
        sHandler.removeCallbacks(runnable, null);
    }

    /**
     * 检查当前的执行线程是否在主线程
     *
     * @return
     */
    public static boolean isMain() {
        return Looper.getMainLooper()
                .getThread() == Thread.currentThread();
    }
}
