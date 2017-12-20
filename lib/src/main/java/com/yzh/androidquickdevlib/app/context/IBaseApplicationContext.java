package com.yzh.androidquickdevlib.app.context;

import android.support.annotation.NonNull;

/**
 * Created by yzh on 2017/10/27.
 */

public interface IBaseApplicationContext {
    Object getAppResource(String resourceName);

    boolean setAppResource(String resourceName, @NonNull Object object);
}
