package com.yzh.androidquickdevlib.gui.page;

import android.app.Activity;

/**
 * Created by yzh on 2017/3/30.
 */

public interface IActionBarPage {
    /**
     * 第一次显示页面触发设置title
     *
     * @return
     */
    CharSequence onSetupPageTitle();

    /**
     * 主动触发更新title
     *
     * @param title
     */
    void updatePageTitle(CharSequence title);

    /**
     * 主动触发更新title
     *
     * @param strResId
     */
    void updatePageTitle(int strResId);

    /**
     * 获取String字符串等资源
     *
     * @param resId
     * @return
     */
    <T> T getResById(int resId, T defVal);

    /**
     * 安全获取page activity的方式
     *
     * @return
     */
    Activity getSafeActivity();

    /**
     * 是否布局加载完成
     *
     * @return
     */
    boolean isLayoutInflated();
}
