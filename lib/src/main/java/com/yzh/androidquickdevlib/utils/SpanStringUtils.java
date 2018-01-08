package com.yzh.androidquickdevlib.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

/**
 * Created by yzh on 2016/4/8.
 */
public class SpanStringUtils {
    /**
     * 将指定字符串的指定位置设置成color的颜色值
     *
     * @param target
     * @param start
     * @param end
     * @param color
     * @return
     */
    public static CharSequence foregroundColorSpan(CharSequence target, int start, int end, int color) {
        if (TextUtils.isEmpty(target)) {
            return "";
        }

        if (start < 0 || start >= target.length() || end <= start || end > target.length()) {
            return target;
        }

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(target);
        ForegroundColorSpan redFroColorSpan = new ForegroundColorSpan(color);
        spannableStringBuilder.setSpan(redFroColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    /**
     * 将指定字符串中包含的key字符串设置成color的颜色值
     *
     * @param target
     * @param key
     * @param color
     * @param ignoreCase
     * @return
     */
    public static CharSequence foregroundColorSpan(CharSequence target, String key, int color, boolean ignoreCase) {
        if (TextUtils.isEmpty(target)) {
            return "";
        }
        else if (TextUtils.isEmpty(key)) {
            return target;
        }

        int indexOfKey = ignoreCase ? target.toString()
                .toUpperCase()
                .indexOf(key.toUpperCase()) : target.toString()
                .indexOf(key);
        if (indexOfKey >= 0) {
            return foregroundColorSpan(target, indexOfKey, indexOfKey + key.length(), color);
        }
        return target;
    }

    /**
     * 将指定字符串的指定位置设置成color的颜色值
     *
     * @param target
     * @param start
     * @param end
     * @param color
     * @return
     */
    public static CharSequence foregroundColorSpan(String target, int start, int end, int color) {
        return foregroundColorSpan((CharSequence) target, start, end, color);
    }

    /**
     * 将指定字符串中包含的key字符串设置成color的颜色值
     *
     * @param target
     * @param key
     * @param color
     * @param ignoreCase
     * @return
     */
    public static CharSequence foregroundColorSpan(String target, String key, int color, boolean ignoreCase) {
        return foregroundColorSpan((CharSequence) target, key, color, ignoreCase);
    }

    /**
     * 将指定字符串的指定位置设置成factor比例大小
     *
     * @param target
     * @param start
     * @param end
     * @param factor
     * @return
     */
    public static CharSequence fontSizeSpan(CharSequence target, int start, int end, float factor) {
        if (TextUtils.isEmpty(target)) {
            return "";
        }

        if (start < 0 || start >= target.length() || end <= start || end > target.length()) {
            return target;
        }

        SpannableString priceSpannableString = new SpannableString(target);
        priceSpannableString.setSpan(new RelativeSizeSpan(factor), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return priceSpannableString;
    }

    /**
     * 将指定字符串的指定位置设置成factor比例大小
     *
     * @param target
     * @param key
     * @param factor
     * @param ignoreCase
     * @return
     */
    public static CharSequence fontSizeSpan(CharSequence target, String key, float factor, boolean ignoreCase) {
        if (TextUtils.isEmpty(target)) {
            return "";
        }
        else if (TextUtils.isEmpty(key)) {
            return target;
        }

        int indexOfKey = ignoreCase ? target.toString()
                .toUpperCase()
                .indexOf(key.toUpperCase()) : target.toString()
                .indexOf(key);
        if (indexOfKey >= 0) {
            return fontSizeSpan(target, indexOfKey, indexOfKey + key.length(), factor);
        }
        return target;
    }

    /**
     * 将指定字符串的指定位置设置成factor比例大小
     *
     * @param target
     * @param start
     * @param end
     * @param color
     * @return
     */
    public static CharSequence fontSizeSpan(String target, int start, int end, int color) {
        return foregroundColorSpan((CharSequence) target, start, end, color);
    }

    /**
     * 将指定字符串的指定位置设置成factor比例大小
     *
     * @param target
     * @param key
     * @param color
     * @param ignoreCase
     * @return
     */
    public static CharSequence fontSizeSpan(String target, String key, int color, boolean ignoreCase) {
        return foregroundColorSpan((CharSequence) target, key, color, ignoreCase);
    }

}
