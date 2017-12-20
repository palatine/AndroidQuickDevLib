package com.yzh.androidquickdevlib.gui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Checkable;

import com.yzh.androidquickdevlib.R;


public class CheckableImageView extends android.support.v7.widget.AppCompatImageView implements Checkable {
    protected boolean mChecked = false;
    protected static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

    public CheckableImageView(Context context) {
        this(context, null);
    }

    public CheckableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initThis(attrs);
    }

    private void initThis(AttributeSet attrs) {
        if (attrs != null) {
            final TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.CheckableView);
            final boolean isChecked = array.getBoolean(R.styleable.CheckableView_checked, false);
            setChecked(isChecked);
            array.recycle();
        }
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        // 获取控件本身所具有的状态，并且我们要另开一个位置存放android.R.attr.state_checked状态，
        // 所以要加1
        final int[] drawStates = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawStates, CHECKED_STATE_SET);
        }
        else {
            for (int i = 0; i < drawStates.length; i++) {
                if (drawStates[i] == CHECKED_STATE_SET[0]) {
                    drawStates[i] = 0;
                }
            }
        }

        return drawStates;
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;

            // / setChecked -> refreshDrawableState ->onCreateDrawableState
            // ->调用selector设置状态
            refreshDrawableState();
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

}
