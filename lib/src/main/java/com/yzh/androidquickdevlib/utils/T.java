package com.yzh.androidquickdevlib.utils;

import android.widget.Toast;

import com.yzh.androidquickdevlib.app.BaseApplication;
import com.yzh.androidquickdevlib.task.ThreadUtility;


/**
 * Created by yzh on 2017/9/26.
 */

public class T {
    private T() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    /**
     * 短时间显示Toast
     *
     * @param message
     */
    public static void showShort(CharSequence message) {
        ThreadUtility.postOnUiThreadReuse(() -> Toast.makeText(BaseApplication.instance(), message, Toast.LENGTH_SHORT)
                .show());
    }


    /**
     * 长时间显示Toast
     *
     * @param message
     */
    public static void showLong(CharSequence message) {
        ThreadUtility.postOnUiThreadReuse(() -> Toast.makeText(BaseApplication.instance(), message, Toast.LENGTH_LONG)
                .show());
    }

}
