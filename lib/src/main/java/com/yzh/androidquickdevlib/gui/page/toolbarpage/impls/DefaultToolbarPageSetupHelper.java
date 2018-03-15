package com.yzh.androidquickdevlib.gui.page.toolbarpage.impls;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.yzh.androidquickdevlib.R;
import com.yzh.androidquickdevlib.gui.page.toolbarpage.interfaces.BarsColor;
import com.yzh.androidquickdevlib.gui.page.toolbarpage.interfaces.CollapsingLayoutParams;
import com.yzh.androidquickdevlib.gui.page.toolbarpage.interfaces.ICollapsingToolBarPage;
import com.yzh.androidquickdevlib.gui.page.toolbarpage.interfaces.IToolbarPage;
import com.yzh.androidquickdevlib.gui.page.toolbarpage.interfaces.IToolbarPageSetup;


/**
 * Created by yzh on 2017/8/29.
 */

public class DefaultToolbarPageSetupHelper implements IToolbarPageSetup {
    // common ids
    final static int ID_ROOT = R.id.rootLayout;
    final static int ID_FRAGMENT_CONTAINER = R.id.fragmentContainer;
    final static int ID_TOOLBAR = R.id.toolbar;
    final static int ID_TOOLBAR_CONTAINER = R.id.toolbarContainer;
    // for collapsing toolbars
    final static int ID_APP_BAR = R.id.appBarLayout;
    final static int ID_COLLAPSING_LAYOUT = R.id.collapsingToolbarLayout;
    final static int ID_COLLAPSING_VIEW_CONTAINER = R.id.collapsingViewContainer;
    // for layouts
    final static int FULLSCREEN_LAYOUT_NORMAL_RES_ID = R.layout.fm_fullscreen_container_with_normal_toolbar;
    final static int FULLSCREEN_LAYOUT_IMMERSION_RES_ID = R.layout.fm_fullscreen_container_with_immersion_toolbar;
    final static int FULLSCREEN_LAYOUT_IMMERSION_COLLAPS_RES_ID = R.layout.fm_fullscreen_container_with_immersion_collasp_toolbar;

    IToolbarPage iToolbarPage;
    OnSetupPageContentListener onSetupPageContentListener;
    OnCollapsingToolBarStatusChangingListener onCollapsingToolBarStatusChangingListener;

    @Override
    public View setupThis(IToolbarPage toolbarPage,
            OnSetupPageContentListener onSetupPageContentListener,
            OnCollapsingToolBarStatusChangingListener onCollapsingToolBarStatusChangingListener) {
        View rootView = null;
        if (toolbarPage != null) {
            this.iToolbarPage = toolbarPage;
            this.onSetupPageContentListener = onSetupPageContentListener;
            this.onCollapsingToolBarStatusChangingListener = onCollapsingToolBarStatusChangingListener;
            setupFullScreenMode();
            rootView = setupRootLayout();
        }
        return rootView;
    }

    /**
     * setup to full screen mode
     */
    protected void setupFullScreenMode() {
        final Activity activity = this.iToolbarPage.getSafeActivity();
        final View decorView = activity.getWindow()
                .getDecorView();
        int old = decorView.getSystemUiVisibility();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | old;
        decorView.setSystemUiVisibility(option);
    }

    protected View setupRootLayout() {
        boolean supportCollapsing = this.iToolbarPage.supportCollapsingView() && this.iToolbarPage.getCollapsingView() != null;
        // if support collapsing, of course it should support immersion mode
        boolean supportImmersion = this.iToolbarPage.supportImmersiveMode() || supportCollapsing;
        int rootLayoutResId = supportImmersion ? supportCollapsing ? FULLSCREEN_LAYOUT_IMMERSION_COLLAPS_RES_ID : FULLSCREEN_LAYOUT_IMMERSION_RES_ID : FULLSCREEN_LAYOUT_NORMAL_RES_ID;
        ViewGroup rootView = (ViewGroup) LayoutInflater.from(this.iToolbarPage.getSafeActivity())
                .inflate(rootLayoutResId, null);

        if (supportCollapsing) {
            setupCollapsingToolBarView(rootView);
            setupAppBarLayoutOnCollapsingChangingListener(rootView);
        }

        setupFragmentContent(rootView);
        setupToolbar(rootView);
        return rootView;
    }

    /**
     * setup the collaspsing tool bar
     *
     * @param rootLayout
     * @return whether support collapsing child view
     */
    private void setupCollapsingToolBarView(ViewGroup rootLayout) {
        Toolbar toolbar = (Toolbar) rootLayout.findViewById(ID_TOOLBAR);
        ViewGroup collapsingContainer = (ViewGroup) rootLayout.findViewById(ID_COLLAPSING_VIEW_CONTAINER);
        CollapsingToolbarLayout collapsinLayout = (CollapsingToolbarLayout) rootLayout.findViewById(ID_COLLAPSING_LAYOUT);
        final View collapsingView = this.iToolbarPage.getCollapsingView();
        CollapsingToolbarLayout.LayoutParams clp = new CollapsingToolbarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        collapsingContainer.addView(collapsingView, clp);

        // setup by collapsingLayoutParams
        CollapsingLayoutParams collapsingLayoutParams = this.iToolbarPage.getCollapsingLayoutParams();
        if (collapsingLayoutParams != null) {
            setupCollapsingToolbarScrollFlags(collapsinLayout, collapsingLayoutParams.getCollapsingLayoutScrollFlags());
            setupCollapsingToolbarChildScrollMode(toolbar, collapsingLayoutParams.getToolbarScrollMode());
            setupCollapsingToolbarChildScrollMode(collapsingView, collapsingLayoutParams.getCollapsingViewScrollMode());
            setupCollapsingToolbarScrimDrawable(collapsinLayout, collapsingLayoutParams.getStatusBarScrimDrawable(), collapsingLayoutParams.getContentScrimDrawable());
            setupCollapsingToolbarScrimTrigger(collapsinLayout, collapsingLayoutParams.getScrimTriggerHeight());
        }
    }

    /**
     * setup the listener when collapsing tool bar status is changing
     *
     * @param rootView
     */
    private void setupAppBarLayoutOnCollapsingChangingListener(ViewGroup rootView) {
        AppBarLayout appBarLayout = (AppBarLayout) rootView.findViewById(ID_APP_BAR);
        if (appBarLayout != null && this.onCollapsingToolBarStatusChangingListener != null) {
            appBarLayout.addOnOffsetChangedListener(new CollapsingToolBarOffsetChangedResolver() {
                @Override
                public void onCollapsingToolBarStatusChanging(ICollapsingToolBarPage.State state, float percentage, float oldPercentage) {
                    if (onCollapsingToolBarStatusChangingListener != null) {
                        onCollapsingToolBarStatusChangingListener.onCollapsingToolBarStatusChanging(state, percentage, oldPercentage);
                    }
                }
            });
        }
    }

    /**
     * setup the tool bar
     *
     * @param rootLayout
     */
    protected void setupToolbar(ViewGroup rootLayout) {
        BarsColor barsColor = this.iToolbarPage.getBarsColor();
        Toolbar toolbar = (Toolbar) rootLayout.findViewById(ID_TOOLBAR);
        toolbar.setBackgroundColor(barsColor == null ? Color.TRANSPARENT : barsColor.getToolBarColor());

        if (this.onSetupPageContentListener != null) {
            this.onSetupPageContentListener.onSetupActionBar(findActionBarContainer(rootLayout));
        }
    }

    /**
     * setup the fragment content
     *
     * @param rootLayout
     */
    protected void setupFragmentContent(ViewGroup rootLayout) {
        if (this.onSetupPageContentListener != null) {
            this.onSetupPageContentListener.onSetupFragment(findFragmentContainer(rootLayout));
        }
    }

    @Override
    public void updateGlobalStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                BarsColor barsColor = this.iToolbarPage.getBarsColor();
                Window window = this.iToolbarPage.getSafeActivity()
                        .getWindow();
                window.setStatusBarColor(barsColor == null ? Color.TRANSPARENT : barsColor.getStatusBarColor());

                // also need to setup the status bar text color mode
                boolean isLightStatusBar = this.iToolbarPage.getCollapsingLayoutParams()
                        .isLightStatusBar();
                View decorView = window.getDecorView();
                int option = decorView.getSystemUiVisibility();
                if (isLightStatusBar) {
                    option |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                else {
                    option &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decorView.setSystemUiVisibility(option);

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * setup collapsing tool bar layout's scroll flags
     *
     * @param collapsingToolbarLayout
     * @param flag
     */
    static void setupCollapsingToolbarScrollFlags(CollapsingToolbarLayout collapsingToolbarLayout, int flag) {
        ViewGroup.LayoutParams layoutParams = collapsingToolbarLayout.getLayoutParams();
        if (layoutParams instanceof AppBarLayout.LayoutParams) {
            final int oldFlag = ((AppBarLayout.LayoutParams) layoutParams).getScrollFlags();
            final int newFlag = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | flag | oldFlag;
            ((AppBarLayout.LayoutParams) layoutParams).setScrollFlags(newFlag);
        }
    }

    /**
     * setup the scroll mode for collapsingtoolbarlayout's children
     *
     * @param child
     * @param mode
     */
    static void setupCollapsingToolbarChildScrollMode(View child, int mode) {
        ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
        if (layoutParams instanceof CollapsingToolbarLayout.LayoutParams) {
            ((CollapsingToolbarLayout.LayoutParams) layoutParams).setCollapseMode(mode);
        }
    }

    /**
     * setup the scrim drawables for collapsingtoolbar layout
     *
     * @param collapsingToolbarLayout
     * @param statusScrimDrawable
     * @param contentScrimDrawable
     */
    static void setupCollapsingToolbarScrimDrawable(CollapsingToolbarLayout collapsingToolbarLayout, Drawable statusScrimDrawable, Drawable contentScrimDrawable) {
        if (collapsingToolbarLayout != null) {
            if (statusScrimDrawable != null) {
                collapsingToolbarLayout.setStatusBarScrim(statusScrimDrawable);
            }
            if (contentScrimDrawable != null) {
                collapsingToolbarLayout.setContentScrim(contentScrimDrawable);
            }
        }
    }

    /**
     * set up the height when scrim visibility start to change
     *
     * @param collapsinLayout
     * @param scrimTriggerHeight
     */
    static void setupCollapsingToolbarScrimTrigger(CollapsingToolbarLayout collapsinLayout, int scrimTriggerHeight) {
        if (collapsinLayout != null && scrimTriggerHeight > 0) {
            collapsinLayout.setScrimVisibleHeightTrigger(scrimTriggerHeight);
        }
    }

    /**
     * find the actionbar container
     *
     * @param rootLayout
     * @return
     */
    static ViewGroup findActionBarContainer(View rootLayout) {
        return (ViewGroup) rootLayout.findViewById(ID_TOOLBAR_CONTAINER);
    }

    /**
     * find the fragment container
     *
     * @param rootLayout
     * @return
     */
    static ViewGroup findFragmentContainer(View rootLayout) {
        return (ViewGroup) rootLayout.findViewById(ID_FRAGMENT_CONTAINER);
    }

    /**
     * collapsing tool bar offset changed listener
     */
    class CollapsingToolBarOffsetChangedResolver implements AppBarLayout.OnOffsetChangedListener, IToolbarPageSetup.OnCollapsingToolBarStatusChangingListener {
        private ICollapsingToolBarPage.State state = ICollapsingToolBarPage.State.COLLAPSED;
        private float percentage;

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            float oldPercentage = this.percentage;
            verticalOffset = Math.abs(verticalOffset);
            ICollapsingToolBarPage.State oldState = this.state;
            final int totalRange = appBarLayout.getTotalScrollRange();
            if (verticalOffset == 0) {
                this.percentage = 1f;
                this.state = ICollapsingToolBarPage.State.EXPANDED;
            }
            else if (verticalOffset >= totalRange) {
                this.percentage = 0f;
                this.state = ICollapsingToolBarPage.State.COLLAPSED;
            }
            else {
                this.percentage = totalRange == 0 ? 0 : 1 - verticalOffset * 1f / totalRange;
                this.state = ICollapsingToolBarPage.State.CHANGING;
            }
            onCollapsingToolBarStatusChanging(this.state, this.percentage, oldPercentage);
        }

        @Override
        public void onCollapsingToolBarStatusChanging(ICollapsingToolBarPage.State state, float percentage, float oldPercentage) {
        }
    }
}
