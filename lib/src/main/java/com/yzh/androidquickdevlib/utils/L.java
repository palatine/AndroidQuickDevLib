package com.yzh.androidquickdevlib.utils;

import android.util.Log;

import com.yzh.androidquickdevlib.BuildConfig;


/**
 * Created by yzh on 2017/4/10.
 */

public class L {
    final static String PREFIX_TAG = "debug_";

    /**
     * send a debug message to log<br>
     * <b>only shows when {@link BuildConfig#DEBUG} is on</b>
     *
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(PREFIX_TAG + tag, msg);
        }
    }

    /**
     * send a exception message to log<br>
     * <b>only shows when {@link BuildConfig#DEBUG} is on</b>
     *
     * @param tag
     * @param msg
     * @param throwable
     */
    public static void e(String tag, String msg, Throwable throwable) {
        if (BuildConfig.DEBUG) {
            Log.e(PREFIX_TAG + tag, msg, throwable);
        }
    }

    /**
     * send a info message to log<br>
     * <b>only shows when {@link BuildConfig#DEBUG} is on</b>
     *
     * @param tag
     * @param msg
     * @param throwable
     */
    public static void i(String tag, String msg, Throwable throwable) {
        if (BuildConfig.DEBUG) {
            Log.i(PREFIX_TAG + tag, msg, throwable);
        }
    }
}
