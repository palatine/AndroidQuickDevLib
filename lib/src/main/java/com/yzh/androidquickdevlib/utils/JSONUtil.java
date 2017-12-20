package com.yzh.androidquickdevlib.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JSONUtil extends JSONObject {

    public static Object getRecursive(JSONObject ret, String name) {
        if (ret == null) {
            return null;
        }

        if (ret.has(name)) {
            return ret.opt(name);
        }

        JSONArray names = ret.names();
        Object o;
        for (int i = 0; names != null && i < names.length(); i++) {
            o = ret.opt(names.optString(i));
            if (o instanceof JSONObject) {
                o = getRecursive((JSONObject) o, name);
            }
            else if (o instanceof JSONArray) {
                o = getRecursive((JSONArray) o, name);
            }
            else {
                continue;
            }
            if (o != null) {
                return o;
            }
        }

        return null;
    }

    public static Object getRecursive(JSONArray ret, String name) {
        if (ret == null) {
            return null;
        }

        Object o = null;
        for (int i = 0; i < ret.length(); i++) {
            o = ret.opt(i);
            if (o instanceof JSONArray) {
                o = getRecursive((JSONArray) o, name);
                if (o != null) {
                    return o;
                }
            }
            else if (o instanceof JSONObject) {
                o = getRecursive((JSONObject) o, name);
                if (o != null) {
                    return o;
                }
            }
        }

        return null;
    }

    public static int getIntRecursive(JSONObject ret, String name) {
        return getIntRecursive(ret, name, 0);
    }

    public static String getStringRecursive(JSONObject ret, String name) {
        return getStringRecursive(ret, name, "");
    }

    public static Calendar getTimeRecursive(JSONObject ret, String name) {
        return getTimeRecursive(ret, name, Calendar.getInstance());
    }

    public static double getDoubleRecursive(JSONObject ret, String name) {
        return getDoubleRecursive(ret, name, 0d);
    }

    public static int getIntRecursive(JSONObject ret, String name, int defaultV) {
        Object o = getRecursive(ret, name);
        if (o == null || o.toString()
                .length() < 1) {
            return defaultV;
        }

        try {
            return Integer.parseInt(o.toString());
        }
        catch (Exception e) {
            Logger.getLogger("TEST")
                    .log(Level.WARNING, e.getMessage());
            return defaultV;
        }
    }

    public static String getStringRecursive(JSONObject ret, String name, String defaultV) {
        Object o = getRecursive(ret, name);
        if (o == null) {
            return defaultV;
        }

        return replaceNullString(o.toString(), defaultV);
    }

    public static Calendar getTimeRecursive(JSONObject ret, String name, Calendar defaultV) {
        Object o = getRecursive(ret, name);
        if (o == null) {
            return defaultV;
        }

        try {
            return DateUtility.parseTimeFromServerFormat(o.toString());
        }
        catch (Exception e) {
            Logger.getLogger("TEST")
                    .log(Level.WARNING, e.getMessage());
            return defaultV;
        }
    }

    public static double getDoubleRecursive(JSONObject ret, String name, double defaultV) {
        Object o = getRecursive(ret, name);
        if (o == null || o.toString()
                .length() < 1) {
            return defaultV;
        }

        try {
            return Double.parseDouble(o.toString());
        }
        catch (Exception e) {
            Logger.getLogger("TEST")
                    .log(Level.WARNING, e.getMessage());
            return defaultV;
        }
    }

    /**
     * 去除json解析中, 将null解析为:"null"字符串的情况
     *
     * @param oldString
     * @param defaultV
     * @return
     */
    private static String replaceNullString(String oldString, String defaultV) {
        if (oldString == null) {
            return defaultV;
        }

        String oldStrTrimed = oldString.trim();
        if (oldStrTrimed.length() > 0 && oldStrTrimed.equalsIgnoreCase("null")) {
            return defaultV;
        }

        return oldString;
    }

    /**
     * 解析一个对象
     *
     * @param jsonObject * @param name
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> T parseJSONObject(JSONObject jsonObject, String name, Class<T> clz) {
        try {
            return parseJSONObject((JSONObject) getRecursive(jsonObject, name), clz);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析一个对象
     *
     * @param jsonObject
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> T parseJSONObject(JSONObject jsonObject, Class<T> clz) {
        return JSON.parseObject(jsonObject == null ? "{}" : jsonObject.toString(), clz, Feature.InitStringFieldAsEmpty);
    }

    /**
     * 解析一个对象数组
     *
     * @param jsonObject
     * @param name
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> List<T> parseJSONArray(JSONObject jsonObject, String name, Class<T> clz) {
        try {
            return parseJSONArray((JSONArray) getRecursive(jsonObject, name), clz);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 解析一个对象数组
     *
     * @param jsonArray
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> List<T> parseJSONArray(JSONArray jsonArray, Class<T> clz) {
        return JSON.parseArray(jsonArray == null ? "[]" : jsonArray.toString(), clz);
    }
}
