package com.yzh.androidquickdevlib.gui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.makeramen.roundedimageview.RoundedImageView;
import com.yzh.androidquickdevlib.R;

public class FitSizeRoundImageView extends RoundedImageView {
    int mWidthFactor = 4;
    int mHeightFactor = 3;

    public FitSizeRoundImageView(Context context) {
        this(context, null, 0);
    }

    public FitSizeRoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FitSizeRoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // 不需要边框
        setBorderWidth((float) 0);
        setBorderColor(0xFFFFFFFF);

        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FitSizeImageView);
            try {
                mWidthFactor = array.getInteger(R.styleable.FitSizeImageView_widthFactor, mWidthFactor);
                mHeightFactor = array.getInteger(R.styleable.FitSizeImageView_heightFactor, mHeightFactor);
            }
            finally {
                array.recycle();
            }
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
        int height = this.getMeasuredHeight();
        if (mWidthFactor > 0 && mHeightFactor > 0) {
            height = width * mHeightFactor / mWidthFactor;
        }
        this.setMeasuredDimension(width, height);
    }

}
