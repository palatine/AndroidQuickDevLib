package com.yzh.androidquickdevlib.gui.page.toolbarpage.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

/**
 * Created by yzh on 2017/9/4.
 */

public class FullScreenSoftInputResizer {
    private Activity activity;
    private View decorView;
    private View contentView;

    /**
     * FullScreenSoftInputResizer
     *
     * @param activity
     * @param contentView 界面容器，activity中一般是R.id.content，也可能是Fragment的容器，根据个人需要传递
     */
    public FullScreenSoftInputResizer(Activity activity, View contentView) {
        this.activity = activity;
        this.decorView = activity.getWindow()
                .getDecorView();
        this.contentView = contentView;
        if (this.activity == null || this.decorView == null || this.contentView == null) {
            throw new IllegalArgumentException("activity, contentView should not be null, BTW, should be called after #setContentView");
        }
    }

    /**
     * 开始监听layout变化
     */
    public void start() {
        this.activity.getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.decorView.getViewTreeObserver()
                .addOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    /**
     * 停止监听
     */
    public void stop() {
        this.activity.getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        this.decorView.getViewTreeObserver()
                .removeOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Rect r = new Rect();
            FullScreenSoftInputResizer.this.decorView.getWindowVisibleDisplayFrame(r);
            int height = decorView.getContext()
                    .getResources()
                    .getDisplayMetrics().heightPixels;
            int diff = height - r.bottom;

            if (FullScreenSoftInputResizer.this.contentView != null) {
                if (diff != 0) {
                    if (FullScreenSoftInputResizer.this.contentView.getPaddingBottom() != diff) {
                        FullScreenSoftInputResizer.this.contentView.setPadding(0, 0, 0, diff);
                    }
                }
                else {
                    if (FullScreenSoftInputResizer.this.contentView.getPaddingBottom() != 0) {
                        FullScreenSoftInputResizer.this.contentView.setPadding(0, 0, 0, 0);
                    }
                }
            }
        }
    };
}
