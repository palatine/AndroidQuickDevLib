package com.yzh.androidquickdevlib.gui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.LinearLayout;

import com.yzh.androidquickdevlib.R;


public class CheckableLinearLayout extends LinearLayout implements Checkable {
    private boolean mChecked;
    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

    public CheckableLinearLayout(Context context) {
        this(context, null);
    }

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckableLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initThis(attrs);
    }

    private void initThis(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.CheckableView);
            final boolean isChecked = array.getBoolean(R.styleable.CheckableView_checked, false);
            setChecked(isChecked);
            array.recycle();
        }
        setClickable(true);
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshDrawableState();
            setCheckedRecursive(this, checked);
        }
    }

    @Override
    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (child instanceof ViewGroup) {
            setCheckedRecursive((ViewGroup) child, mChecked);
        }
        else if (child instanceof Checkable) {
            ((Checkable) child).setChecked(mChecked);
        }
    }

    private void setCheckedRecursive(ViewGroup parent, boolean checked) {
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = parent.getChildAt(i);
            if (v instanceof Checkable) {
                ((Checkable) v).setChecked(checked);
            }

            if (v instanceof ViewGroup) {
                setCheckedRecursive((ViewGroup) v, checked);
            }
        }
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        Drawable drawable = getBackground();
        if (drawable != null) {
            int[] myDrawableState = getDrawableState();
            drawable.setState(myDrawableState);
            invalidate();
        }
    }
}
