package com.yzh.androidquickdevlib.gui.page;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import com.yzh.androidquickdevlib.R;
import com.yzh.androidquickdevlib.utils.ResUtils;

import me.yokeyword.fragmentation.SupportActivity;

public abstract class ActionBarPage extends PageFragmentation implements IFragmentMultiChildController, IActionBarPage {
    protected int mContainerLayoutResId = R.layout.fm_container_with_actionbar;
    protected int mActionBarLayoutId = R.layout.action_bar_common;
    protected int mRightImageButtonResourceId = 0;
    protected int mLeftImageButtonResourceId = R.drawable.action_bar_left_image;
    protected int mTitleResId = 0;

    private TextView mTitleView;
    private ImageButton rightImageButton;
    private ImageButton mLeftImageButton;
    private boolean mLayoutInflated = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(mContainerLayoutResId, container, false);
        // 设置fm container
        final ViewGroup fmContainer = (ViewGroup) rootView.findViewById(R.id.fragmmentContainer);
        fmContainer.addView(onCreatePageView(inflater, container, savedInstanceState));
        // 设置默认的action bar
        final View actionBarContainer = rootView.findViewById(R.id.actionBarContainer);
        onSetuptActionBar((ViewGroup) actionBarContainer);
        mLayoutInflated = true;
        return rootView;
    }

    @Override
    public void onSetuptActionBar(ViewGroup actionBarView) {
        if (actionBarView != null) {
            // 移除所有已添加的布局
            actionBarView.removeAllViews();
            final LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(mActionBarLayoutId, actionBarView, true);

            // 设置左上角点击事件
            this.mLeftImageButton = (ImageButton) view.findViewById(R.id.action_bar_left_image_button);
            if (this.mLeftImageButton != null) {
                if (this.mLeftImageButtonResourceId != 0) {
                    this.mLeftImageButton.setImageResource(this.mLeftImageButtonResourceId);
                    this.mLeftImageButton.setOnClickListener(v -> onActionBarLeftButtonClicked(v));
                    this.mLeftImageButton.setVisibility(View.VISIBLE);
                }
                else {
                    mLeftImageButton.setVisibility(View.INVISIBLE);
                }
            }

            // 设置右上角Image按钮点击事件
            this.rightImageButton = (ImageButton) view.findViewById(R.id.action_bar_right_image_button);
            if (this.rightImageButton != null) {
                if (this.mRightImageButtonResourceId != 0) {
                    this.rightImageButton.setImageResource(this.mRightImageButtonResourceId);
                    this.rightImageButton.setOnClickListener(v -> onAciontBarRightButtonClicked(v));
                    this.rightImageButton.setVisibility(View.VISIBLE);
                }
                else {
                    rightImageButton.setVisibility(View.INVISIBLE);
                }
            }

            // 设置标题
            this.mTitleView = (TextView) view.findViewById(R.id.action_bar_title);
            if (this.mTitleView != null) {
                this.mTitleView.setText(onSetupPageTitle());
                // only for test
                if (sShowFragmentStackWhenClickActionBar) {
                    this.mTitleView.setOnClickListener(v -> ((SupportActivity) getActivity()).getSupportDelegate()
                            .showFragmentStackHierarchyView());
                }
            }
        }
    }

    @Override
    public String onSetupPageTitle() {
        return getResById(this.mTitleResId, "");
    }

    @Override
    public void updatePageTitle(CharSequence title) {
        if (this.mTitleView != null) {
            this.mTitleView.setText(title);
        }
    }

    @Override
    public void updatePageTitle(int strResId) {
        if (this.mTitleView != null) {
            this.mTitleView.setText(strResId);
        }
    }

    protected ImageButton getRightImageButton() {
        return rightImageButton;
    }

    protected ImageButton getLeftImageButton() {
        return mLeftImageButton;
    }

    protected void onAciontBarRightButtonClicked(View v) { }

    protected void onActionBarLeftButtonClicked(View v) {
        _mActivity.onBackPressed();
    }

    @Override
    public void setPreCheckedIndex(int index) { }

    /**
     * 目前只支持Drawable, String资源获取
     */
    @Override
    public <T> T getResById(int resId, T defVal) {
        try {
            if (defVal instanceof String) {
                defVal = (T) ResUtils.getString(getActivity(), resId);
            }
            else if (defVal instanceof Drawable) {
                defVal = (T) ResUtils.getDrawable(getActivity(), resId);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return defVal;
    }

    @Override
    public boolean isLayoutInflated() {
        return this.mLayoutInflated;
    }

    /**
     * 点击title是否显示fragment stack view, only for test
     */
    public static boolean sShowFragmentStackWhenClickActionBar = false;
}
