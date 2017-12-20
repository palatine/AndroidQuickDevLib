package com.yzh.androidquickdevlib.app.context;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yzh on 2017/10/27.
 */

public class BaseApplicationContextFactory {
    static IBaseApplicationContext baseApplicationContext = new BaseAppContextImpl();

    public static IBaseApplicationContext getDefaultImpl() {
        return baseApplicationContext;
    }

    static class BaseAppContextImpl implements IBaseApplicationContext {
        private Map<String, Object> mResourceMap = new HashMap<String, Object>();


        @Override
        public java.lang.Object getAppResource(String resName) {
            try {
                return this.mResourceMap.get(resName);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public boolean setAppResource(String resourceName, @NonNull Object object) {
            try {
                this.mResourceMap.put(resourceName, object);
                return true;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }


    }
}
