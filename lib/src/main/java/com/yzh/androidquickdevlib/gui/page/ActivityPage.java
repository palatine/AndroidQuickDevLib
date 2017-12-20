package com.yzh.androidquickdevlib.gui.page;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface ActivityPage extends IFragmentationPage {
    Fragment getRootFragment();

    void onSetuptActionBar(ActionBar actionBar);

    View onCreatePageView(LayoutInflater inflater, ViewGroup container, Bundle b);

    /**
     * 每次页面可见都会被调用
     *
     * @param firstVisibility 只有第一次可见的时候为true
     */
    void onResumePage(boolean firstVisibility);

    boolean onGoPreviousPage();

    // 以下两个方法是对ActivityPage生命周期变化的的回调

    /**
     * 当本页面被添加到{@link PageActivity}页面栈时候的回调
     */
    void onAddedToStack();

    /**
     * 当本页面被从{@link PageActivity}页面栈中移除时的回调
     */
    void onRemoveFromStack();
}
