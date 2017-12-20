package com.yzh.androidquickdevlib.app.exceptionhandler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

import com.yzh.androidquickdevlib.app.BaseApplication;
import com.yzh.androidquickdevlib.preference.impls.PreferenceUtil;

import static com.yzh.androidquickdevlib.app.BaseApplication.AppContext.RES_MAIN_ACTIVITY_CLASS;


public class RestartAppTask {
    /**
     * 最短的重启延时
     */
    private final static int MIN_RESTART_DELAY_IN_MILISECONDS = 2000;
    private static final String LAST_RESTART = "RestartAppTask_lastRestartTime";

    /**
     * 强制使App重启
     *
     * @param msg                       重启是要显示的msg
     * @param restartDelayInMiliseconds 开始重启的延时, 最小为 2000 ms
     * @return 是否能够执行重启
     */
    public static boolean doTask(final String msg, final int restartDelayInMiliseconds) {
        try {
            if (System.currentTimeMillis() - PreferenceUtil.getHelper()
                    .getLong(LAST_RESTART) < 10 * MIN_RESTART_DELAY_IN_MILISECONDS) {
                BaseApplication.exitForcely();
                return true;
            }

            // 没有设置重启的Activity.class
            final Class<?> restartClass = (Class<?>) BaseApplication.instance()
                    .getAppResource(RES_MAIN_ACTIVITY_CLASS);
            if (restartClass == null) {
                return false;
            }

            PreferenceUtil.getHelper()
                    .put(LAST_RESTART, System.currentTimeMillis());
            final Context context = BaseApplication.instance();
            int restartDelay = Math.max(restartDelayInMiliseconds, MIN_RESTART_DELAY_IN_MILISECONDS);
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(context, restartClass);
            PendingIntent restartIntent = PendingIntent.getActivity(context, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + restartDelay, restartIntent);

            // 提示信息
            Thread displayMsgThread = new Thread() {
                public void run() {
                    Looper.prepare();
                    try {
                        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                    catch (Exception ignore) {
                    }
                    Looper.loop();
                }
            };
            displayMsgThread.setDaemon(true);
            displayMsgThread.start();

            // 强制退出当前程序
            new Thread(() -> {
                try {
                    Thread.sleep(restartDelay - 300);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                BaseApplication.exitForcely();
            }).start();

        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

}
