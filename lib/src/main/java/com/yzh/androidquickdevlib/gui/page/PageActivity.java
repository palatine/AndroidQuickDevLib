package com.yzh.androidquickdevlib.gui.page;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransactionBugFixHack;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.yzh.androidquickdevlib.R;
import com.yzh.androidquickdevlib.app.BaseApplication;
import com.yzh.androidquickdevlib.app.exceptionhandler.RestartAppTask;
import com.yzh.androidquickdevlib.task.ThreadUtility;
import com.yzh.androidquickdevlib.utils.ResUtils;
import com.yzh.androidquickdevlib.utils.T;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.SupportActivity;

abstract public class PageActivity extends SupportActivity {
    /**
     * the tag
     */
    public final static String TAG = "PageActivity";

    /**
     * 记录当前的page activity
     */
    protected static PageActivity sCurrentPageActivity = null;

    /**
     * inputer
     */
    private InputMethodManager mInputMethodManager;

    /**
     * pending stack
     */
    private final List<Runnable> mPopBackStackRunnable = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawable(null);
        // 记录当前的page activity
        sCurrentPageActivity = this;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 当前的page activity被执行onDestroy
        if (sCurrentPageActivity == this) {
            sCurrentPageActivity = null;
        }
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序处于后台时, 内存不足时会导致程序部分资源被释放, 而出现UI不正常的情况
        if (level > TRIM_MEMORY_COMPLETE) {
            // the process is around the middle of the background LRU list;
            // freeing memory can help the system keep other processes running
            // later in the list for better overall performance.
            Log.i("PageActivity", "App exit due to less of memory!");
            BaseApplication.exitForcely();
        }
        else if (level >= TRIM_MEMORY_BACKGROUND) {
            // the process is around the middle of the background LRU list;
            // freeing memory can help the system keep other processes running
            // later in the list for better overall performance.
            Log.i("PageActivity", "App free memory due to less of memory!");
            System.gc();
        }

        super.onTrimMemory(level);
    }

    /**
     * add一个新页面
     *
     * @param page
     * @param addOrReplace
     */
    private void _setPage(ActivityPage page, boolean addOrReplace) {
        if (isStatedSaved()) {
            addToPopBackStackRunnable(() -> _setPage(page, addOrReplace));
            return;
        }

        if (!addOrReplace) {
            startWithPop(page.getSupportFragment());
        }
        else {
            start(page.getSupportFragment());
        }

        // 跳转页面的时候, 隐藏输入法
        hideCurrentSoftInputMethod();
    }

    /**
     * 回到上一页
     *
     * @param force 当为true的时候, 强制退回上一页无论{@link PageFragmentation#onGoPreviousPage()} 结果如何
     */
    protected void _goPreviousPage(boolean force) {
        if (isStatedSaved()) {
            addToPopBackStackRunnable(() -> _goPreviousPage(force));
            return;
        }

        if (force) {
            onBackPressedSupport();
        }
        else {
            onBackPressed();
        }
        // 跳转页面的时候, 隐藏输入法
        hideCurrentSoftInputMethod();
    }

    /**
     * 获取指定页面是否存在于栈中
     *
     * @param page
     * @return
     */
    private boolean _hasPage(ActivityPage page) {
        return findFragment(page.getFragmentTag()) != null;
    }

    /**
     * 获取指定class的页面是否存在于栈中
     *
     * @param pageClass
     * @return
     */
    private boolean _hasPage(Class<?> pageClass) {
        // support fragment 默认是使用getClass().getName作为tag
        return findFragment(pageClass.getName()) != null;
    }

    /**
     * 加载一个根ActivityPage
     *
     * @param page
     */
    private void _loadRootPage(ActivityPage page) {
        if (isStatedSaved()) {
            addToPopBackStackRunnable(() -> _loadRootPage(page));
            return;
        }

        loadRootFragment(R.id.fm_container, page.getSupportFragment());
    }

    /**
     * 替换的方式加载一个根ActivityPage
     *
     * @param page
     */
    private void _replaceRootPage(ActivityPage page) {
        if (isStatedSaved()) {
            addToPopBackStackRunnable(() -> _replaceRootPage(page));
            return;
        }

        replaceLoadRootFragment(R.id.fm_container, page.getSupportFragment(), true);
    }

    @Override
    public void onBackPressedSupport() {
        // 隐藏输入法
        hideCurrentSoftInputMethod();

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        }
        else {
            new ExitAppTask(this).doTask();
        }
    }

    /**
     * 是否当前状态已经被保存
     *
     * @return
     */
    private boolean isStatedSaved() {
        return FragmentTransactionBugFixHack.isStateSaved(getSupportFragmentManager());
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        executeOnPostResumeTasks();
    }

    /**
     * execute the pending task after post resume
     */
    protected void executeOnPostResumeTasks() {
        for (Runnable runnable : this.mPopBackStackRunnable) {
            try {
                runnable.run();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.mPopBackStackRunnable.clear();
    }

    /**
     * 添加到待执行列表
     *
     * @param runnable
     */
    private void addToPopBackStackRunnable(Runnable runnable) {
        if (runnable != null && !this.mPopBackStackRunnable.contains(runnable)) {
            this.mPopBackStackRunnable.add(runnable);
        }
    }

    /**
     * 获取当前正在显示的fragment
     *
     * @return
     */
    public android.support.v4.app.Fragment _getCurrentPage() {
        return getTopFragment();
    }

    /**
     * 隐藏当前显示在界面上的的输入法
     */
    public void hideCurrentSoftInputMethod() {
        if (mInputMethodManager == null) {
            mInputMethodManager = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        }

        final View focusedView = this.getCurrentFocus();
        if (focusedView != null && focusedView instanceof EditText) {
            mInputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 获取当前展示的页面
     *
     * @return
     */
    public static android.support.v4.app.Fragment getCurrentPage() {
        android.support.v4.app.Fragment page = null;
        try {
            page = sCurrentPageActivity._getCurrentPage();
        }
        catch (Exception e) {
        }
        return page;
    }

    /**
     * 程序出错时要显示的msg
     */
    private final static String APP_ERR_MSG = "App seems abnormal";

    /**
     * 执行自动重启的msg
     */
    private final static String APP_ERR_OPERATION_RESTART_AUTO_MSG = ", trying to restart!";

    /**
     * 执行手动重启的msg
     */
    private final static String APP_ERR_OPERATION_RESTART_MANUAL_MSG = ", please kill it and restart manually!";

    /**
     * 切换到指定页面
     *
     * @param page
     * @param addOrReplace 是否新增一个页面还是替换当前页
     */
    public static void setPage(ActivityPage page, boolean addOrReplace) {
        setPage(page, addOrReplace, false);
    }

    /**
     * 切换到指定页面
     *
     * @param page
     * @param addOrReplace 是否新增一个页面还是替换当前页
     * @param popIfExist   当该页面存在的情况下, 先弹出, 再切换
     */
    public static void setPage(ActivityPage page, boolean addOrReplace, boolean popIfExist) {
        try {
            if (popIfExist && hasPage(page)) {
                popToPage(page, true, () ->
                        // 如果执行到这儿, 当前页肯定被弹出了, 所以addOrReplace=false已经无意义
                        sCurrentPageActivity._setPage(page, true));
            }
            else {
                sCurrentPageActivity._setPage(page, addOrReplace);
            }
        }
        catch (Exception e) {
            reportError(e);
            restart();
        }
    }

    /**
     * 回到上一个页面
     */
    public static void goPreviousPage() {
        goPreviousPage(false);
    }

    /**
     * 回到上一个页面
     *
     * @param force 当为true的时候, 强制退回上一页无论{@link PageFragmentation#onGoPreviousPage()} 结果如何
     */
    public static void goPreviousPage(boolean force) {
        try {
            sCurrentPageActivity._goPreviousPage(force);
        }
        catch (Exception e) {
            reportError(e);
            restart();
        }
    }

    /**
     * 检查是否确实含有当前页面
     *
     * @param page
     * @return
     */
    public static boolean hasPage(ActivityPage page) {
        boolean has = false;
        try {
            has = sCurrentPageActivity._hasPage(page);
        }
        catch (Exception e) {
            reportError(e);
            restart();
        }
        return has;
    }

    /**
     * 检查是否确实含有当前页面
     *
     * @param pageClass
     * @return
     */
    public static boolean hasPage(Class<?> pageClass) {
        boolean has = false;
        try {
            has = sCurrentPageActivity._hasPage(pageClass);
        }
        catch (Exception e) {
            reportError(e);
            restart();
        }
        return has;
    }

    /**
     * 将指定页面弹出到栈顶显示, 如果当前栈中没有该页面存在 则什么也不做
     *
     * @param page
     * @return 是否找到该页面
     */
    public static boolean popToPage(ActivityPage page, boolean includeSelf, Runnable afterTransaction) {
        boolean result = false;
        try {
            result = sCurrentPageActivity._hasPage(page);
            if (result) {
                sCurrentPageActivity.popTo(page.getFragmentTag(), includeSelf, afterTransaction);
            }
        }
        catch (Exception e) {
            reportError(e);
            restart();
        }
        return result;
    }

    /**
     * 将指定class的页面弹出到栈顶显示, 如果当前栈中没有该页面存在 则什么也不做
     *
     * @param pageClass
     * @return 是否找到该页面
     */
    public static boolean popToPage(Class<?> pageClass, boolean includeSelf, Runnable afterTransaction) {
        boolean result = false;
        try {
            result = sCurrentPageActivity._hasPage(pageClass);
            if (result) {
                sCurrentPageActivity.popTo(pageClass, includeSelf, afterTransaction);
            }
        }
        catch (Exception e) {
            reportError(e);
            restart();
        }
        return result;
    }

    /**
     * 加载一个根activity page
     *
     * @param page
     */
    public static void loadRootPage(ActivityPage page) {
        try {
            sCurrentPageActivity._loadRootPage(page);
        }
        catch (Exception e) {
            reportError(e);
            restart();
        }
    }

    /**
     * 替换的方式加载根activity page
     *
     * @param page
     */
    public static void replaceRootPage(ActivityPage page) {
        try {
            sCurrentPageActivity._replaceRootPage(page);
        }
        catch (Exception e) {
            reportError(e);
            restart();
        }
    }

    /**
     * 提交异常信息
     *
     * @param throwable
     */
    protected static void reportError(Throwable throwable) {
        try {
            if (BaseApplication.instance() != null) {
                //                AnalyticsUtils.getInstance()
                //                        .reportError(Application.instance(), throwable);
            }

            // 打印错误堆栈信息
            throwable.printStackTrace();
        }
        catch (Exception ignore) {
        }
    }

    /**
     * 需要执行重启
     */
    protected static void restart() {
        if (!RestartAppTask.doTask(APP_ERR_MSG + APP_ERR_OPERATION_RESTART_AUTO_MSG, 2500)) {
            try {
                T.showLong(APP_ERR_MSG + APP_ERR_OPERATION_RESTART_MANUAL_MSG);
            }
            catch (Exception ignore) {
            }
        }
    }

    static PageActivity getCurrentPageActivity() {
        return sCurrentPageActivity;
    }

    /**
     * exit app task
     */
    static class ExitAppTask {
        /**
         * 等待用户再次点击Back的时间
         */
        protected final static int PENDING_TO_FINISH_WAIT_TIME = 3000;
        /**
         * 是否正在等待下次点击的状态
         */
        protected static boolean isPendingToFinish = false;

        protected Activity activity;

        public ExitAppTask(Activity activity) {
            this.activity = activity;
        }

        public void doTask() {
            if (isPendingToFinish) {
                ThreadUtility.postOnUiThreadNonReuse(() -> {
                    try {
                        activity.finish();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            else {
                isPendingToFinish = true;
                T.showLong(ResUtils.getString(R.string.app_exit_tips));
                ThreadUtility.postOnUiThreadDelayed(() -> isPendingToFinish = false, PENDING_TO_FINISH_WAIT_TIME);
            }
        }
    }
}
