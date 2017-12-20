package com.yzh.androidquickdevlib.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.yzh.androidquickdevlib.R;
import com.yzh.androidquickdevlib.app.BaseApplication;
import com.yzh.androidquickdevlib.app.SimpleActivityLifecycleCallbacks;
import com.yzh.androidquickdevlib.task.ThreadUtility;


public class ProgressWindowHelper {
    /**
     * ProgressWindow的最大显示时间, 目前为10秒
     */
    public final static int PROGRESS_WINDOW_MAX_SHOW_TIME = 1000 * 10;

    private static ProgressWindowHelper singleton = null;

    private static ProgressWindowHelper getInstance() {
        if (singleton == null) {
            synchronized (ProgressWindowHelper.class) {
                if (singleton == null) {
                    singleton = new ProgressWindowHelper();
                }
            }
        }
        return singleton;
    }

    private AlertDialog dialog;
    private int mProgressWindowShowingCount = 0;

    /**
     * private constructor
     */
    private ProgressWindowHelper() {
        BaseApplication.instance()
                .registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityStopped(Activity activity) {
                        if (singleton != null) {
                            singleton.hideProgressWindowInternal();
                        }
                    }
                });
    }


    /**
     * 申请触发隐藏窗口, 并不一定会隐藏
     */
    private void tryToHideProgressWindowInternal() {
        ThreadUtility.postOnUiThreadReuse(() -> {
            if (this.dialog == null) {
                this.mProgressWindowShowingCount = 0;
                return;
            }

            this.mProgressWindowShowingCount--;
            if (this.mProgressWindowShowingCount > 0) {
                return;
            }
            hideProgressWindowInternal();
        });
    }

    /**
     * 隐藏窗口
     */
    private void hideProgressWindowInternal() {
        ThreadUtility.postOnUiThreadReuse(() -> {
            this.mProgressWindowShowingCount = 0;
            if (this.dialog == null) {
                return;
            }

            final DialogInterface pw = dialog;
            this.dialog = null;
            if (pw == null) {
                return;
            }

            ThreadUtility.postOnUiThreadDelayed(() -> {
                try {
                    pw.dismiss();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }, 500);
        });
    }

    /**
     * 显示窗口
     *
     * @param cancelabe
     */
    private void showProgressWindowInternal(final boolean cancelabe) {
        ThreadUtility.postOnUiThreadReuse(() -> {
            if (this.dialog != null) {
                this.mProgressWindowShowingCount++;
                return;
            }

            try {
                this.dialog = new ProgressWindow(BaseApplication.getCurrentActivity(), cancelabe);
                this.dialog.show();
                this.mProgressWindowShowingCount++;
            }
            catch (Exception e) {
                hideProgressWindowInternal();
                e.printStackTrace();
            }
            finally {
                tryToAutoForceHideProgressWindowDelayed();
            }
        });
    }

    /**
     * 以下做法是防止忘记hideProgressBar导致一直显示ProgressWindow
     */
    private void tryToAutoForceHideProgressWindowDelayed() {
        ThreadUtility.postOnUiThreadDelayed(() -> hideProgressWindowInternal(), PROGRESS_WINDOW_MAX_SHOW_TIME);
    }

    /**
     * 显示ProgressWindow
     */
    public static void showProgressWindow() {
        showProgressWindow(false);
    }

    /**
     * 显示ProgressWindow
     *
     * @param cancelabe 是否可以取消
     */
    public static void showProgressWindow(boolean cancelabe) {
        getInstance().showProgressWindowInternal(cancelabe);
    }

    /**
     * 显示ProgressWindow持续指定时长(毫秒单位)
     *
     * @param milisecond (0~ {@link #PROGRESS_WINDOW_MAX_SHOW_TIME})
     */
    public static void showProgressWindow(int milisecond) {
        getInstance().showProgressWindowInternal(true);
        final int duration = milisecond <= 0 ? 1000 : milisecond >= PROGRESS_WINDOW_MAX_SHOW_TIME ? PROGRESS_WINDOW_MAX_SHOW_TIME : milisecond;
        ThreadUtility.postOnUiThreadDelayed(() -> hideProgressWindow(), duration);
    }

    /**
     * 隐藏当前显示的ProgressWindow
     */
    public static void hideProgressWindow() {
        getInstance().tryToHideProgressWindowInternal();
    }

    /**
     * a default progress window
     */
    private class ProgressWindow extends AlertDialog {
        final boolean cancelable;

        ProgressWindow(Context context, boolean cancelable) {
            super(context, R.style.TransparentWindow);
            this.cancelable = cancelable;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.progress_bar);
            setCancelable(this.cancelable);
            setCanceledOnTouchOutside(this.cancelable);
            setOnCancelListener(dialog1 -> hideProgressWindowInternal());
        }
    }
}
