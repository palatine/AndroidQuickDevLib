package com.yzh.androidquickdevlib.gui.page;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yzh.androidquickdevlib.task.ThreadUtility;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * 继承了旧的ActivityPage的, 将不再用到方法的在此实现了<br>
 * 任何需要转换成新{@link IFragmentationPage}页的都需要继承自本类<br>
 * Created by yzh on 2016/12/21.
 */

public abstract class PageFragmentation extends SupportFragment implements ActivityPage {
    // 是否是第一次展示出来
    boolean isFirstVisibility = true;

    @Override
    public Fragment getRootFragment() {
        return null;
    }

    /**
     * @deprecated use {@link #onSetuptActionBar(ViewGroup)} ()} instead
     */
    @Override
    public void onSetuptActionBar(android.app.ActionBar actionBar) {
    }

    @Override
    public SupportFragment getSupportFragment() {
        return this;
    }

    @Override
    public String getFragmentTag() {
        // support fragment 默认是使用getClass().getName作为tag
        return this.getClass()
                .getName();
    }

    /**
     * please use {@link #onResumePage(boolean)} instead, they have same behavior when firstVisibility == true!
     *
     * @param savedInstanceState
     */
    @Override
    @CallSuper
    protected void onEnterAnimationEnd(Bundle savedInstanceState) {
        // 这个方法仅在第一次入栈的时候调用(动画执行完毕时)
        super.onEnterAnimationEnd(savedInstanceState);
        //执行resumePage更新界面
        onResumePage(true);
        // 第一次入栈动画执行完
        this.isFirstVisibility = false;
    }

    /**
     * please use {@link #onResumePage(boolean)} instead, they have same behavior when firstVisibility == false !
     */
    @Override
    @CallSuper
    public void onSupportVisible() {
        super.onSupportVisible();
        // 如果是第一次UI可见(入栈时), 需要考虑入栈动画正在执行, 为了防止
        // 动画因UI的初始化等耗时操作抖动, 将不再次调用onResumePage
        if (!this.isFirstVisibility) {
            onResumePage(false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = this.onCreatePageView(inflater, container, savedInstanceState);
        return v;
    }

    @Override
    public View onCreatePageView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        return null;
    }

    /**
     * 对应使用{@link PageActivity#setPage(ActivityPage, boolean)}方式添加的页面,
     * 使用本方法判断何时应该懒加载UI. 本方法在第一次专场动画结束完成才开始刷新以保证动画流畅.
     * 第一次专场动画结束后的行为, 等于{@link #onSupportVisible()}
     * <br><br>
     * <b>需要注意的是, 如果不是采用setPage的方式添加的页面, 如使用了{@link #loadMultipleRootFragment(int, int, SupportFragment...)}等,
     * 因为库的限制根fragment没有专场动画效果, 所以在实际page可见之前就会被调用(具体是{@link #onActivityCreated(Bundle)}).
     * 而{@link #onSupportVisible()}则不会有这种情况, 所以在这种情况下请使用{@link #onSupportVisible()}代替!!</b>
     *
     * @param firstVisibility 只有第一次可见的时候为true
     */
    @Override
    public void onResumePage(boolean firstVisibility) { }

    @Override
    public boolean onBackPressedSupport() {
        ThreadUtility.postOnUiThreadNonReuse(() -> {
            if (!onGoPreviousPage()) {
                PageActivity.goPreviousPage(true);
            }
        });
        return true;
    }

    @Override
    public boolean onGoPreviousPage() {
        return false;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onAddedToStack();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onRemoveFromStack();
    }

    @Override
    public void onAddedToStack() { }

    @Override
    public void onRemoveFromStack() { }

    public boolean isFirstVisibility() {
        return isFirstVisibility;
    }

    public Activity getSafeActivity() {
        return _mActivity != null ? _mActivity : PageActivity.getCurrentPageActivity();
    }

}
