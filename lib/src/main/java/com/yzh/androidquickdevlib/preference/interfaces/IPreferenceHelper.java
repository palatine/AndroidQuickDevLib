package com.yzh.androidquickdevlib.preference.interfaces;

import java.io.Serializable;

/**
 * Created by yzh on 2017/10/27.
 */

public interface IPreferenceHelper {

    String getString(String key);

    String getString(String key, String defaultValue);

    long getLong(String key);

    long getLong(String key, long defaultValue);

    float getFloat(String key);

    float getFloat(String key, float defaultValue);

    void put(String key, String value);

    void put(String key, long value);

    void put(String key, float value);

    void del(String key);

    void putSerializable(String key, Serializable object);

    Object getSerializable(String key);
}
