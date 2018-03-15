package com.yzh.androidquickdevlib.gui.page.toolbarpage.interfaces;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yzh on 2017/8/29.
 */

public interface IToolbarPageSetup {
    /**
     * 需要加注布局子View的时候回调
     */
    interface OnSetupPageContentListener {
        void onSetupActionBar(ViewGroup actionBarContainer);

        void onSetupFragment(ViewGroup fragmentContainer);
    }

    /**
     * called when collapsing tool bar is changing
     */
    interface OnCollapsingToolBarStatusChangingListener {
        void onCollapsingToolBarStatusChanging(ICollapsingToolBarPage.State state, float percentage, float oldPercentage);
    }

    /**
     * setup indicated toolbar page
     *
     * @param toolbarPage                               the page need to be setup
     * @param listener                                  a listener when setup page content called
     * @param onCollapsingToolBarStatusChangingListener a listener to get callback when collapsing status is changing
     * @return
     */
    View setupThis(IToolbarPage toolbarPage, OnSetupPageContentListener listener, OnCollapsingToolBarStatusChangingListener onCollapsingToolBarStatusChangingListener);

    /**
     * update the status bar color based on current page setting
     */
    void updateGlobalStatusBarColor();
}
