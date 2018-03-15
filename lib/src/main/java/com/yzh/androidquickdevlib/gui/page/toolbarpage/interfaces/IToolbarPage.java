package com.yzh.androidquickdevlib.gui.page.toolbarpage.interfaces;

import android.view.View;

import com.yzh.androidquickdevlib.gui.page.IActionBarPage;


/**
 * Created by yzh on 2017/8/28.
 */

public interface IToolbarPage extends IActionBarPage {
    /**
     * whether is a immersive mode toolbar
     *
     * @return
     */
    boolean supportImmersiveMode();

    /**
     * whether is support collapsing toolbar
     *
     * @return
     */
    boolean supportCollapsingView();

    /**
     * get the collapsing view
     *
     * @return
     */
    View getCollapsingView();

    /**
     * get the colors of status/toolbar
     *
     * @return
     */
    BarsColor getBarsColor();

    /**
     * get the collapsing layout's params
     *
     * @return
     */
    CollapsingLayoutParams getCollapsingLayoutParams();
}
