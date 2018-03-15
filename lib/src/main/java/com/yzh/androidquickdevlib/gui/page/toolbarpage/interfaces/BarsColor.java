package com.yzh.androidquickdevlib.gui.page.toolbarpage.interfaces;

import android.graphics.Color;

/**
 * Created by yzh on 2017/8/29.
 */

public class BarsColor {
    /**
     * the transparent bars
     */
    public static final BarsColor TRANSPARENT_BARS = new BarsColor(Color.TRANSPARENT, Color.TRANSPARENT);
    /**
     * the white bars
     */
    public static final BarsColor WHITE_BARS = new BarsColor(Color.WHITE, Color.WHITE);

    /**
     * the white bars
     */
    public static final BarsColor WHITE_BARS_GREEN_STATUS = new BarsColor(0xff1BC380, Color.WHITE);

    int statusBarColor = Color.WHITE;
    int toolBarColor = Color.WHITE;

    public BarsColor(int statusBarColor, int toolBarColor) {
        this.statusBarColor = statusBarColor;
        this.toolBarColor = toolBarColor;
    }

    public int getStatusBarColor() {
        return statusBarColor;
    }

    public int getToolBarColor() {
        return toolBarColor;
    }
}
