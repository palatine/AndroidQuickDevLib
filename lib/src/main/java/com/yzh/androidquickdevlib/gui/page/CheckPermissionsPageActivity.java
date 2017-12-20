package com.yzh.androidquickdevlib.gui.page;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;


import com.yzh.androidquickdevlib.utils.T;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yzh on 2017/11/2.
 */

public class CheckPermissionsPageActivity extends PageActivity {
    private static final int M = Build.VERSION_CODES.M;
    private static final int PERMISSON_REQUESTCODE = 10086;


    /**
     * check all indicated Permissions
     *
     * @param permissions
     */
    public static void checkPermissions(String... permissions) {
        try {
            if (Build.VERSION.SDK_INT >= M && sCurrentPageActivity.getApplicationInfo().targetSdkVersion >= M) {
                List<String> needRequestPermissonList = findDeniedPermissions(permissions);
                if (null != needRequestPermissonList && needRequestPermissonList.size() > 0) {
                    String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
                    Method method = sCurrentPageActivity.getClass()
                            .getMethod("requestPermissions", String[].class, int.class);
                    method.invoke(sCurrentPageActivity, array, PERMISSON_REQUESTCODE);
                }
            }
        }
        catch (Throwable e) {
            T.showLong("checkPermissions failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     */
    private static List<String> findDeniedPermissions(String[] permissions) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List<String> needRequestPermissonList = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= M && sCurrentPageActivity.getApplicationInfo().targetSdkVersion >= M) {
            try {
                for (String perm : permissions) {
                    Method checkSelfMethod = sCurrentPageActivity.getClass()
                            .getMethod("checkSelfPermission", String.class);
                    Method shouldShowRequestPermissionRationaleMethod = sCurrentPageActivity.getClass()
                            .getMethod("shouldShowRequestPermissionRationale", String.class);
                    if ((Integer) checkSelfMethod.invoke(sCurrentPageActivity,
                            perm) != PackageManager.PERMISSION_GRANTED || (Boolean) shouldShowRequestPermissionRationaleMethod.invoke(sCurrentPageActivity, perm)) {
                        needRequestPermissonList.add(perm);
                    }
                }
            }
            catch (Throwable e) {
                e.printStackTrace();
                throw e;
            }
        }
        return needRequestPermissonList;
    }

    /**
     * 检测是否所有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private static boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                showMissingPermissionDialog(permissions);
            }
        }
    }

    /**
     * 显示提示信息
     */
    protected void showMissingPermissionDialog(String... permission) {
    }

    /**
     * 启动应用的设置
     */
    public static void startAppSettings() {
        if (sCurrentPageActivity != null) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + sCurrentPageActivity.getPackageName()));
            sCurrentPageActivity.startActivity(intent);
        }
    }
}
