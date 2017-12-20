package com.yzh.androidquickdevlib.task;

import android.util.Base64;

import com.yzh.androidquickdevlib.task.listener.TaskListener;
import com.yzh.androidquickdevlib.task.listener.TaskListenerChain;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataCachedTaskManager {
    public final static DataCachedTaskManager sTheOne = new DataCachedTaskManager();
    public final static long ONE_MINUTES = 1000 * 60 * 1;
    public final static long FIVE_MINUTES = 1000 * 60 * 5;
    public final static long MAX_TIME = Long.MAX_VALUE / 3;
    public final static long ONE_DAY = 1000 * 60 * 60 * 24;
    public final static long DEFAULT_TIME = FIVE_MINUTES;
    public final static long NO_CACHE_TIME = Long.MIN_VALUE;

    public static class Result {
        protected long mUpdateTime = 0;
        protected Object mData = null;
        protected long mLifeTime = 0;
        protected boolean mIsExpired = false;

        public long getUpdateTime() {
            return mUpdateTime;
        }


        public Object getData() {
            return mData;
        }

        public long getLifeTime() {
            return mLifeTime;
        }

        public boolean isExpired(long lifeTime) {
            mIsExpired = this.mUpdateTime + lifeTime < System.currentTimeMillis();
            return mIsExpired;
        }

        public boolean isExpiredLastTime() {
            return mIsExpired;
        }

        public long getLivingTime() {
            return System.currentTimeMillis() - mUpdateTime;
        }
    }


    class DataListener extends TaskListenerChain {
        String mKey = null;
        long mLifeTime = 0;

        @Override
        public Object onProcessTaskMessage(TaskMessage message) {
            if (message.getMessageType() == TaskMessage.MSG_TASK_END) {
                Object data = message.getMessage();
                if (data != null) {
                    put(mKey, data, mLifeTime);
                }
            }
            return super.onProcessTaskMessage(message);
        }
    }

    ;

    protected HashMap<String, Result> mData = new HashMap<String, Result>();

    public Result syncCall(Object loader, String loadingFunction, Object[] params, Object receiver, String receiverFunction, long lifeTime, boolean showProgressBar, boolean isUiSafe) {
        String key = genKey(loader, loadingFunction, params);
        Result data = null;
        synchronized (this) {
            data = this.getResult(key);
            if (data != null && !data.isExpired(lifeTime)) {
                return data;
            }
        }

        Object d = TaskManager.sTheOne.startSyncTask(loader, loadingFunction, params, receiver, receiverFunction, false, false);

        if (d == null) {
            return null;
        }

        data = this.put(key, d, lifeTime);
        return data;
    }

    public synchronized Result ayncCall(Object loader,
            String loadingFunction,
            Object[] params,
            Object receiver,
            String receiverFunction,
            long lifeTime,
            boolean showProgressBar,
            boolean isUiSafe) {
        return call(loader, loadingFunction, params, receiver, receiverFunction, lifeTime, showProgressBar, isUiSafe, false);
    }

    public synchronized Result ayncCall(Object loader, String loadingFunction, Object[] params, Object receiver, String receiverFunction, boolean showProgressBar, boolean isUiSafe) {
        return call(loader, loadingFunction, params, receiver, receiverFunction, DEFAULT_TIME, showProgressBar, isUiSafe, false);
    }

    public synchronized Result ayncCall(Object loader, String loadingFunction, Object receiver, String receiverFunction, boolean isUiSafe) {
        return ayncCall(loader, loadingFunction, null, receiver, receiverFunction, DEFAULT_TIME, true, isUiSafe);
    }

    public synchronized Result ayncCall(Object loader, String loadingFunction, Object[] params, TaskListener l, long lifeTime, boolean showProgressBar, boolean isUiSafe) {
        return call(loader, loadingFunction, params, l, TaskListener.FUN_onProcessTaskMessage, lifeTime, showProgressBar, isUiSafe, false);
    }

    public synchronized Result ayncCall(Object loader, String loadingFunction, Object[] params, TaskListener l) {
        return call(loader, loadingFunction, params, l, TaskListener.FUN_onProcessTaskMessage, DataCachedTaskManager.ONE_DAY, true, true, false);
    }

    public synchronized Result call(Object loader,
            String loadingFunction,
            Object[] params,
            Object receiver,
            String receiverFunction,
            long lifeTime,
            boolean showProgressBar,
            boolean isUiSafe,
            boolean sync) {
        String key = genKey(loader, loadingFunction, params);
        Result data = this.getResult(key);
        if (data != null && !data.isExpired(lifeTime)) {
            if (receiver != null) {
                TaskMessage message = new TaskMessage(null, TaskMessage.MSG_TASK_END, data.getData());
                try {
                    Utility.callObjectMethod(receiver, receiverFunction, new Object[]{message});
                }
                catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
            return data;
        }

        DataListener l = new DataListener();
        l.mKey = key;
        l.mLifeTime = lifeTime;
        l.setIgnoreDisPosedUiReceiver(isUiSafe);
        l.addListener(receiver, receiverFunction);

        if (!sync) {
            TaskManager.sTheOne.startTask(loader, loadingFunction, params, l, TaskListener.FUN_onProcessTaskMessage, showProgressBar, false);
        }
        else {
            Object d = TaskManager.sTheOne.startSyncTask(loader, loadingFunction, params, l, TaskListener.FUN_onProcessTaskMessage, showProgressBar, false);

            if (d == null) {
                return null;
            }

            data = this.put(key, d, lifeTime);
        }

        return data;
    }

    public synchronized Result put(String name, Object data, long lifeTime) {
        Result r = new Result();
        r.mUpdateTime = System.currentTimeMillis();
        r.mLifeTime = lifeTime;
        r.mData = data;

        mData.put(name, r);

        return r;
    }


    public synchronized Result put(Object loader, String loadingFunction, Object[] params, Object data, long lifeTime) {
        return put(genKey(loader, loadingFunction, params), data, lifeTime);
    }

    public synchronized Result put(Object loader, String loadingFunction, Object data) {
        return put(genKey(loader, loadingFunction, null), data, DEFAULT_TIME);
    }

    public synchronized Result getResult(String name) {
        return (Result) this.mData.get(name);
    }

    public synchronized Object getData(String key) {
        Result r = this.getResult(key);
        if (r == null) {
            return null;
        }

        return r.mData;
    }

    public synchronized void clearData(String key) {
        this.mData.remove(key);
    }

    public synchronized Result getHttpResult(String url, Map<String, Object> params) {
        return this.getResult(genKey(url, params));
    }

    public synchronized Result getCallResult(Object loader, String loadingFunction, Object[] params) {
        return this.getResult(genKey(loader, loadingFunction, params));
    }

    protected synchronized File getFile(String key) {
        return null;
    }

    protected synchronized void deleteFile(String key) {

    }

    static public String key2FileName(String key) {
        try {
            return Base64.encodeToString(key.getBytes("UTF-8"), Base64.URL_SAFE | Base64.NO_PADDING);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static public String fileName2Key(String fileName) {
        try {
            return new String(Base64.decode(fileName, Base64.URL_SAFE | Base64.NO_PADDING), "UTF-8");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static protected String genKey(String url, Map<String, Object> params) {
        if (params == null || params.size() < 1) {
            return url;
        }
        ArrayList<String> listKeys = new ArrayList<String>(params.keySet());
        Collections.sort(listKeys);

        String key;
        String ret = url;
        for (int i = 0; i < listKeys.size(); i++) {
            key = listKeys.get(i);
            ret += key + params.get(key)
                    .toString();
        }

        return ret;
    }

    public synchronized List<Result> getResults(Object loader, String loadingFunction) {
        ArrayList<Result> ret = new ArrayList<Result>();
        Set<Entry<String, Result>> entries = mData.entrySet();
        String key = genObjKey(loader) + ":" + loadingFunction;
        for (Entry<String, Result> e : entries) {
            if (e.getKey()
                    .startsWith(key)) {
                ret.add(e.getValue());
            }
        }

        return ret;
    }


    public synchronized void clear(Object loader, String loadingFunction, Object[] params) {
        mData.remove(genKey(loader, loadingFunction, params));
    }

    public synchronized void clear() {
        mData.clear();
    }

    public synchronized void clear(Object loader, String loadingFunction) {
        Object[] keys = mData.keySet()
                .toArray();
        String key = genObjKey(loader) + ":" + loadingFunction;
        for (Object e : keys) {
            if (((String) e).startsWith(key)) {
                mData.remove(e);
            }
        }
    }

    public synchronized void clear(Object loader) {
        Object[] keys = mData.keySet()
                .toArray();
        String key = genObjKey(loader) + ":";
        for (Object e : keys) {
            if (((String) e).startsWith(key)) {
                mData.remove(e);
            }
        }
    }

    static protected String genKey(Object o, String function, Object[] params) {
        StringBuffer ret = new StringBuffer();
        ret.append(genObjKey(o))
                .append(":")
                .append(function)
                .append("(");
        Object param;
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                param = params[i];
                if (param == null) {
                    param = "";
                }
                if (i == 0) {
                    ret.append(genParamKey(param));
                }
                else {
                    ret.append(",");
                    ret.append(genParamKey(param));
                }
            }
        }

        ret.append(")");
        Logger.getLogger("TEST")
                .log(Level.INFO, "DataCacheKey=" + ret.toString());
        return ret.toString();
    }

    static protected String genParamKey(Object param) {
        if (param instanceof Calendar) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return df.format(((Calendar) param).getTime());
        }

        return param.toString();
    }

    static protected String genObjKey(Object obj) {
        if (obj instanceof Class) {
            return obj.toString();
        }

        return obj.getClass()
                .toString();
    }
}
