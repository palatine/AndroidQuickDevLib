package com.yzh.androidquickdevlib.gui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.yzh.androidquickdevlib.R;


public class FitSizeImageView extends ImageView {
    int mWidthFactor = 4;
    int mHeightFactor = 3;

    public FitSizeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initUI(context, attrs);
    }

    public FitSizeImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FitSizeImageView(Context context) {
        this(context, null, 0);
    }

    private void initUI(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FitSizeImageView);
            mWidthFactor = array.getInteger(R.styleable.FitSizeImageView_widthFactor, mWidthFactor);
            mHeightFactor = array.getInteger(R.styleable.FitSizeImageView_heightFactor, mHeightFactor);
            array.recycle();
        }
    }

    public void setFactor(int width, int height) {
        mWidthFactor = width;
        mHeightFactor = height;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = this.getMeasuredWidth();
        int height = width * mHeightFactor / mWidthFactor;
        this.setMeasuredDimension(width, height);
    }

}
