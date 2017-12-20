package com.yzh.androidquickdevlib.preference.impls;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;


import com.yzh.androidquickdevlib.app.BaseApplication;
import com.yzh.androidquickdevlib.preference.interfaces.IPreferenceHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class PreferenceUtil {
    private static IPreferenceHelper helper = null;

    public static IPreferenceHelper getHelper() {
        if (helper == null) {
            synchronized (PreferenceUtil.class) {
                final Context context = BaseApplication.instance();
                if (helper == null && context != null) {
                    helper = new DefaultPerferenceHelper(context.getSharedPreferences(context.getApplicationInfo().name, Context.MODE_PRIVATE));
                }
            }
        }
        return helper;
    }


    static class DefaultPerferenceHelper implements IPreferenceHelper {

        private SharedPreferences mImp = null;

        public DefaultPerferenceHelper(SharedPreferences share) {
            this.mImp = share;
            if (share == null) {
                throw new RuntimeException("sare preference should not be null!");
            }
        }

        @Override
        public String getString(String key) {
            return getString(key, "");
        }

        public String getString(String key, String defaultValue) {
            return mImp.getString(key, defaultValue);
        }

        @Override
        public long getLong(String key) {
            return getLong(key, 0L);
        }

        @Override
        public long getLong(String key, long defaultValue) {
            return mImp.getLong(key, defaultValue);
        }

        @Override
        public float getFloat(String key) {
            return getFloat(key, 0f);
        }

        @Override
        public float getFloat(String key, float defaultValue) {
            return mImp.getFloat(key, defaultValue);
        }

        @Override
        public void put(String key, String value) {
            mImp.edit()
                    .putString(key, value)
                    .apply();
        }

        @Override
        public void put(String key, long value) {
            mImp.edit()
                    .putLong(key, value)
                    .apply();
        }

        @Override
        public void put(String key, float value) {
            mImp.edit()
                    .putFloat(key, value)
                    .apply();
        }

        @Override
        public void del(String key) {
            mImp.edit()
                    .remove(key)
                    .apply();
        }


        @Override
        public void putSerializable(String key, Serializable object) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = null;
            try {
                out = new ObjectOutputStream(baos);
                out.writeObject(object);
                String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
                put(key, objectVal);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                try {
                    baos.close();
                    if (out != null) {
                        out.close();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public Object getSerializable(String key) {
            String objStr = getString(key, "");
            if (!TextUtils.isEmpty(objStr)) {
                byte[] buffer = Base64.decode(objStr, Base64.DEFAULT);
                ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                ObjectInputStream ois = null;
                try {
                    ois = new ObjectInputStream(bais);
                    return ois.readObject();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        bais.close();
                        if (ois != null) {
                            ois.close();
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }

}
