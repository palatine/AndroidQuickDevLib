package com.yzh.androidquickdevlib.task;

import android.app.Activity;
import android.app.Fragment;


import com.yzh.androidquickdevlib.app.BaseApplication;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Utility {
    /*
     * call a method on indicated object
     */
    public static Object callObjectMethod(Object o, String method, Object... params) throws Throwable {
        if (o instanceof Class<?>) {
            return callStaticMethod((Class<?>) o, method, params);
        }

        try {
            Method[] m = o.getClass()
                    .getMethods();
            for (Method i : m) {
                if (i.getName()
                        .equals(method)) {
                    try {

                        return i.invoke(o, params);
                    }
                    catch (IllegalAccessException e) {

                    }
                    catch (IllegalArgumentException r) {

                    }
                }
            }
            throw new NoSuchMethodException(method);
        }
        catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    /*
     * call a static method on a class
     */
    public static Object callStaticMethod(Class<?> classObject, String method, Object... params) throws Throwable {

        try {
            Method[] m = classObject.getMethods();
            for (Method i : m) {
                if (i.getName()
                        .equals(method)) {
                    try {
                        return i.invoke(null, params);
                    }
                    catch (IllegalAccessException e) {

                    }
                    catch (IllegalArgumentException r) {

                    }
                }
            }
            throw new NoSuchMethodException(method);
        }
        catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    /**
     * is a running ui component
     *
     * @param uiObject
     * @return
     */
    public static boolean isRunningUi(Object uiObject) {
        if (uiObject == null) {
            return false;
        }

        Activity a = null;
        if (uiObject instanceof Fragment) {
            Fragment f = (Fragment) uiObject;
            if (f.isDetached() || f.isRemoving() || !f.isResumed()) {
                return false;
            }
            a = f.getActivity();
        }
        else if (uiObject instanceof Activity) {
            a = (Activity) uiObject;
        }

        return BaseApplication.isActivityRunning(a);
    }
}
