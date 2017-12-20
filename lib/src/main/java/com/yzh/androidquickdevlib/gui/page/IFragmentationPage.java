package com.yzh.androidquickdevlib.gui.page;

import android.view.ViewGroup;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * 为了兼容性继承了旧的{@link ActivityPage}接口
 * Created by yzh on 2016/12/21.
 */

public interface IFragmentationPage {
    /**
     * 新版设置action bar
     *
     * @param actionBarView
     */
    void onSetuptActionBar(ViewGroup actionBarView);

    /**
     * 获取当前实例对应的SupportFragment
     *
     * @return
     */
    SupportFragment getSupportFragment();

    /**
     * 获取fragment tag
     *
     * @return
     */
    String getFragmentTag();
}
