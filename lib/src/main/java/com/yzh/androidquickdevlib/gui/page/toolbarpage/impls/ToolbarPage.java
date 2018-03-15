package com.yzh.androidquickdevlib.gui.page.toolbarpage.impls;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yzh.androidquickdevlib.gui.page.ActionBarPage;
import com.yzh.androidquickdevlib.gui.page.toolbarpage.interfaces.BarsColor;
import com.yzh.androidquickdevlib.gui.page.toolbarpage.interfaces.CollapsingLayoutParams;
import com.yzh.androidquickdevlib.gui.page.toolbarpage.interfaces.ICollapsingToolBarPage;
import com.yzh.androidquickdevlib.gui.page.toolbarpage.interfaces.IToolbarPage;
import com.yzh.androidquickdevlib.gui.page.toolbarpage.interfaces.IToolbarPageSetup;
import com.yzh.androidquickdevlib.utils.L;

/**
 * Created by yzh on 2017/8/28.
 */

public class ToolbarPage extends ActionBarPage implements IToolbarPage, ICollapsingToolBarPage {
    private boolean isInflated = false;
    // toolbar page setup helper
    IToolbarPageSetup iToolbarPageSetup = ToolbarPageSetupHelperFactory.getDefault();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = iToolbarPageSetup.setupThis(this, new IToolbarPageSetup.OnSetupPageContentListener() {
            @Override
            public void onSetupActionBar(ViewGroup actionBarContainer) {
                ToolbarPage.this.onSetuptActionBar(actionBarContainer);
            }

            @Override
            public void onSetupFragment(ViewGroup fragmentContainer) {
                final View fragmentView = onCreatePageView(inflater, fragmentContainer, savedInstanceState);
                if (fragmentView != null && fragmentView.getParent() == null
                        // if call inflater.inflate(resId, fragmentContainer, true) inside #onCreatePageView
                        // the the fragmentContainer itself will be returned
                        && fragmentView != fragmentContainer) {
                    fragmentContainer.addView(fragmentView);
                }
            }
        }, (state, percentage, oldPercentage) -> onCollapsingToolBarStatusChanging(state, percentage, oldPercentage));
        this.isInflated = true;
        return rootView;
    }


    @Override
    public boolean supportImmersiveMode() {
        return false;
    }

    @Override
    public boolean supportCollapsingView() {
        return false;
    }

    @Override
    public View getCollapsingView() {
        return null;
    }

    @Override
    public BarsColor getBarsColor() {
        return BarsColor.WHITE_BARS_GREEN_STATUS;
    }

    @Override
    public CollapsingLayoutParams getCollapsingLayoutParams() {
        return CollapsingLayoutParams.with(getSafeActivity())
                .isLightStatusBar(false)
                .build();
    }

    @Override
    public boolean isLayoutInflated() {
        return this.isInflated;
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        this.iToolbarPageSetup.updateGlobalStatusBarColor();
    }

    @Override
    public void onCollapsingToolBarStatusChanging(State state, float percentage, float oldPercentage) {
        L.d("111", "state:" + state.ordinal() + ", percentage:" + percentage + ",oldPercentage:" + oldPercentage);
    }
}
