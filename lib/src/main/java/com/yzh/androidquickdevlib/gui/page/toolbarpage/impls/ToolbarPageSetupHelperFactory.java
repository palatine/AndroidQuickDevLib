package com.yzh.androidquickdevlib.gui.page.toolbarpage.impls;


import com.yzh.androidquickdevlib.gui.page.toolbarpage.interfaces.IToolbarPageSetup;

/**
 * Created by yzh on 2017/8/31.
 */

public class ToolbarPageSetupHelperFactory {
    /**
     * get a default instance of toolbar page setup helper
     *
     * @return
     */
    public static IToolbarPageSetup getDefault() {
        return new DefaultToolbarPageSetupHelper();
    }
}
