package com.yzh.androidquickdevlib.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.yzh.androidquickdevlib.app.context.BaseApplicationContextFactory;
import com.yzh.androidquickdevlib.app.context.IBaseApplicationContext;
import com.yzh.androidquickdevlib.app.exceptionhandler.CrashHandler;
import com.yzh.androidquickdevlib.net.DefaultHttpClient;

import static com.yzh.androidquickdevlib.app.BaseApplication.AppContext.RES_HTTP_CLIENT;


public class BaseApplication extends android.app.Application implements IBaseApplicationContext {
    /**
     * for app context
     */
    public static class AppContext {
        public static final String RES_LOGIN_PAGE = "AppContext_login_page";
        public static final String RES_HOST_ADDR = "AppContext_host_addr";
        public static final String RES_MAIN_ACTIVITY_CLASS = "AppContext_main_activity";
        public static final String RES_HTTP_CLIENT = "AppContext_http_client";
        public static final String RES_HTTP_CLIENT_URL_CREATOR = "AppContext_http_url_creator";
    }


    protected static BaseApplication singleTon = null;
    protected static Activity sCurrentActivity = null;

    public static BaseApplication instance() {
        return singleTon;
    }

    public BaseApplication() {
        singleTon = this;
    }

    public static Activity getCurrentActivity() {
        return sCurrentActivity;
    }

    public static boolean isCurrentActivityRunning() {
        return isActivityRunning(getCurrentActivity());
    }

    public static boolean isActivityRunning(Context a) {
        if (a == null || !(a instanceof Activity)) {
            return false;
        }

        return isActivityRunning((Activity) a);
    }

    public static boolean isActivityRunning(Activity a) {
        if (sCurrentActivity == null) {
            return false;
        }

        if (a != sCurrentActivity && a != null) {
            return false;
        }

        if (sCurrentActivity.isFinishing()) {
            return false;
        }

        return true;
    }

    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance()
                .setExceptionHandler(null);
        registerActivityLiftRecycleCallbacks();
        setupDefaultAppContext();

    }

    private void setupDefaultAppContext() {
        setAppResource(RES_HTTP_CLIENT, new DefaultHttpClient());
    }

    private void registerActivityLiftRecycleCallbacks() {
        registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                sCurrentActivity = activity;
            }

            @Override
            public void onActivityStarted(Activity activity) {
                sCurrentActivity = activity;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                sCurrentActivity = activity;
            }

            @Override
            public void onActivityStopped(Activity activity) {
                if (activity == sCurrentActivity) {
                    sCurrentActivity = null;
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                if (activity == sCurrentActivity) {
                    sCurrentActivity = null;
                }
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (activity == sCurrentActivity) {
                    sCurrentActivity = null;
                }
            }

        });
    }


    public static void exit() {
        Activity a = getCurrentActivity();
        if (a != null) {
            a.finish();
        }
    }

    public static void exitForcely() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    @Override
    public Object getAppResource(String resourceName) {
        return BaseApplicationContextFactory.getDefaultImpl()
                .getAppResource(resourceName);
    }

    @Override
    public boolean setAppResource(String resourceName, @NonNull Object object) {
        return BaseApplicationContextFactory.getDefaultImpl()
                .setAppResource(resourceName, object);
    }
}
