package com.yzh.androidquickdevlib.gui.page.toolbarpage.interfaces;

/**
 * Created by yzh on 2017/9/7.
 */

public interface ICollapsingToolBarPage {
    enum State {
        COLLAPSED, CHANGING, EXPANDED
    }

    void onCollapsingToolBarStatusChanging(State newState, float percentage, float oldPercentage);
}
