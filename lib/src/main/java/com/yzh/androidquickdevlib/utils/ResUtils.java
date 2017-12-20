package com.yzh.androidquickdevlib.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import com.yzh.androidquickdevlib.app.BaseApplication;


/**
 * Created by yzh on 2016/3/10.
 */
public class ResUtils {

    /**
     * 根据资源id获取对应的string资源
     *
     * @param strResId
     * @return
     */
    public static String getString(int strResId) {
        return getString(null, strResId);
    }

    /**
     * 根据资源id获取对应的string资源
     *
     * @param context
     * @param strResId
     * @return
     */
    public static String getString(Context context, int strResId) {
        try {
            return context == null ? BaseApplication.instance()
                    .getString(strResId) : context.getString(strResId);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 根据资源id获取对应的drawable资源
     *
     * @param resId
     * @return
     */
    public static Drawable getDrawable(int resId) {
        return getDrawable(null, resId);
    }

    /**
     * 根据资源id获取对应的drawable资源
     *
     * @param context
     * @param resId
     * @return
     */
    public static Drawable getDrawable(Context context, int resId) {
        try {
            return context == null ? BaseApplication.instance()
                    .getResources()
                    .getDrawable(resId) : context.getResources()
                    .getDrawable(resId);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 在指定的TypedArray中查找颜色
     *
     * @param array
     * @param colorRefIdOrColor 颜色的resId或者颜色值
     * @param defColor          默认颜色值
     * @return
     */
    public static int getColorByRefIdOrColor(TypedArray array, int colorRefIdOrColor, int defColor) {
        if (array != null) {
            final int resId = array.getResourceId(colorRefIdOrColor, -1);
            return resId < 0 ? array.getColor(colorRefIdOrColor, defColor) : array.getResources()
                    .getColor(resId);
        }
        return defColor;
    }

    /**
     * 在指定的TypedArray中查找尺寸
     *
     * @param array
     * @param dimensionRefIdOrDimension 尺寸的resId或者尺寸
     * @param defDimension              默认尺寸值
     * @return
     */
    public static float getDimensionByRefIdOrDimension(TypedArray array, int dimensionRefIdOrDimension, float defDimension) {
        if (array != null) {
            final int resId = array.getResourceId(dimensionRefIdOrDimension, -1);
            return resId < 0 ? array.getDimension(dimensionRefIdOrDimension, defDimension) : array.getResources()
                    .getDimension(resId);
        }
        return defDimension;
    }

    /**
     * 在指定的TypedArray中查找颜色
     *
     * @param array
     * @param stringRefIdOrColor 字符串的resId
     * @param defStr             默认字符串
     * @return
     */
    public static String getStringByRefIdOrString(TypedArray array, int stringRefIdOrColor, String defStr) {
        if (array != null) {
            final int resId = array.getResourceId(stringRefIdOrColor, -1);
            return resId < 0 ? array.getString(stringRefIdOrColor) : array.getResources()
                    .getString(resId);
        }
        return defStr;
    }
}
