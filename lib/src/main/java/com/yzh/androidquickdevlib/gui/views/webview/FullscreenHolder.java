package com.yzh.androidquickdevlib.gui.views.webview;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.yzh.androidquickdevlib.R;

/**
 * Created by yzh on 2017/3/20.
 */

public class FullscreenHolder extends FrameLayout {
    public FullscreenHolder(@NonNull Context context) {
        this(context, null);
    }

    public FullscreenHolder(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FullscreenHolder(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundResource(R.color.black);
    }

    @Override
    public boolean onTouchEvent(MotionEvent evt) {
        return true;
    }
}
