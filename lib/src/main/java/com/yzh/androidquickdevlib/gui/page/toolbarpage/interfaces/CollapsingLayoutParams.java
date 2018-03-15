package com.yzh.androidquickdevlib.gui.page.toolbarpage.interfaces;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;

/**
 * Created by yzh on 2017/8/29.
 */

public class CollapsingLayoutParams {
    int collapsingLayoutScrollFlags;
    int toolbarScrollMode;
    int collapsingViewScrollMode;
    Drawable statusBarScrimDrawable;
    Drawable contentScrimDrawable;
    boolean isLightStatusBar;
    int scrimTriggerHeight = 0;

    public CollapsingLayoutParams(int collapsingLayoutScrollFlags,
            int toolbarScrollMode,
            int collapsingViewScrollMode,
            Drawable statusBarScrimDrawable,
            Drawable contentScrimDrawable,
            boolean isLightStatusBar,
            int scrimTriggerHeight) {
        this.collapsingLayoutScrollFlags = collapsingLayoutScrollFlags;
        this.toolbarScrollMode = toolbarScrollMode;
        this.collapsingViewScrollMode = collapsingViewScrollMode;
        this.statusBarScrimDrawable = statusBarScrimDrawable;
        this.contentScrimDrawable = contentScrimDrawable;
        this.isLightStatusBar = isLightStatusBar;
        this.scrimTriggerHeight = scrimTriggerHeight;
    }

    public int getCollapsingLayoutScrollFlags() {
        return collapsingLayoutScrollFlags;
    }

    public int getToolbarScrollMode() {
        return toolbarScrollMode;
    }

    public int getCollapsingViewScrollMode() {
        return collapsingViewScrollMode;
    }

    public Drawable getStatusBarScrimDrawable() {
        return statusBarScrimDrawable;
    }

    public Drawable getContentScrimDrawable() {
        return contentScrimDrawable;
    }

    public boolean isLightStatusBar() {
        return isLightStatusBar;
    }

    public int getScrimTriggerHeight() {
        return scrimTriggerHeight;
    }

    /**
     * create a params builder
     *
     * @param context
     * @return
     */
    public static Builder with(Context context) {
        return new Builder(context);
    }

    public static class Builder {
        Context context;
        private int collapsingLayoutScrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL;
        private int toolbarScrollMode = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN;
        private int collapsingViewScrollMode = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX;
        private boolean isLightStatusBar = true;
        private Drawable statusBarScrimDrawable;
        private Drawable contentScrimDrawable;
        private int scrimTriggerHeight = 0;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setCollapsingLayoutScrollFlags(int collapsingLayoutScrollFlags) {
            this.collapsingLayoutScrollFlags = collapsingLayoutScrollFlags;
            return this;
        }

        public Builder setToolbarScrollMode(int toolbarScrollMode) {
            this.toolbarScrollMode = toolbarScrollMode;
            return this;
        }

        public Builder setCollapsingViewScrollMode(int collapsingViewScrollMode) {
            this.collapsingViewScrollMode = collapsingViewScrollMode;
            return this;
        }

        public Builder setStatusBarScrimDrawable(Drawable statusBarScrimDrawable) {
            this.statusBarScrimDrawable = statusBarScrimDrawable;
            return this;
        }

        public Builder setStatusBarScrimDrawableResId(int statusBarScrimDrawableResId) {
            this.statusBarScrimDrawable = ContextCompat.getDrawable(this.context, statusBarScrimDrawableResId);
            return this;
        }

        public Builder setStatusBarScrimColor(int statusBarScrimColor) {
            this.statusBarScrimDrawable = new ColorDrawable(statusBarScrimColor);
            return this;
        }

        public Builder setContentScrimDrawable(Drawable contentScrimDrawable) {
            this.contentScrimDrawable = contentScrimDrawable;
            return this;
        }

        public Builder setContentScrimDrawableResId(int contentScrimDrawableResId) {
            this.contentScrimDrawable = ContextCompat.getDrawable(this.context, contentScrimDrawableResId);
            return this;
        }

        public Builder setContentScrimColor(int contentBarScrimColor) {
            this.contentScrimDrawable = new ColorDrawable(contentBarScrimColor);
            return this;
        }

        public Builder isLightStatusBar(boolean isLightStatusBar) {
            this.isLightStatusBar = isLightStatusBar;
            return this;
        }

        public Builder scrimTriggerHeight(int scrimTriggerHeight) {
            this.scrimTriggerHeight = scrimTriggerHeight;
            return this;
        }

        public CollapsingLayoutParams build() {
            return new CollapsingLayoutParams(collapsingLayoutScrollFlags,
                    toolbarScrollMode,
                    collapsingViewScrollMode,
                    statusBarScrimDrawable,
                    contentScrimDrawable,
                    isLightStatusBar,
                    scrimTriggerHeight);
        }
    }

}
