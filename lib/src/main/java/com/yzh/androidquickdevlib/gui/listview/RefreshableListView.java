package com.yzh.androidquickdevlib.gui.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yzh.androidquickdevlib.R;


/**
 * 动态刷新和加载数据ListView
 *
 * @author RobinTang
 */
public class RefreshableListView extends ListView implements OnScrollListener {

    /**
     * 监听器 监听控件的刷新或者加载更多事件 所有的条目事件都会有一个偏移量，也就是position应该减1才是你适配器中的条目
     *
     * @author RobinTang
     */
    public interface RefreshableListViewListener {
        /**
         * @param RefreshableListView
         * @param isRefresh           为true的时候代表的是刷新，为false的时候代表的是加载更多
         * @return true:刷新或者加载更多动作完成，刷新或者加载更多的动画自动消失
         * false:刷新或者加载更多为完成，需要在数据加载完成之后去调用控件的doneRefresh
         * ()或者doneMore()方法
         */
        public boolean onRefreshOrMore(RefreshableListView RefreshableListView, boolean isRefresh);

        /*
         * @return true refreshing/loading task done,and doneRefresh or doneMore will be called.
         */
        public boolean onQueryFinished(boolean isRefresh);
    }

    /**
     * 状态控件（StatusView，列表头上和底端的）的状态枚举
     *
     * @author RobinTang
     */
    enum RefreshStatus {
        none, normal, willrefresh, refreshing
    }

    /**
     * 状态控件
     *
     * @author RobinTang
     */
    class StatusView extends LinearLayout {
        public int height;
        public int width;
        private ProgressBar progressBar = null;
        private TextView textView = null;
        private RefreshStatus refreshStatus = RefreshStatus.none;
        private String normalString = getContext().getString(R.string.pull_to_refresh);
        private String willrefreshString = getContext().getString(R.string.release_to_start_refresh);
        private String refreshingString = getContext().getString(R.string.is_refreshing);

        public StatusView(Context context, AttributeSet attrs) {
            super(context, attrs);
            initThis(context);
        }

        public StatusView(Context context) {
            super(context);
            initThis(context);
        }

        private void initThis(Context context) {
            this.setOrientation(LinearLayout.HORIZONTAL);
            this.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

            progressBar = new ProgressBar(context);
            progressBar.setLayoutParams(new LinearLayout.LayoutParams(60, 60));
            textView = new TextView(context);
            textView.setPadding(5, 0, 0, 0);

            this.addView(progressBar);
            this.addView(textView);

            int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            this.measure(w, h);

            height = this.getMeasuredHeight();
            width = this.getMeasuredWidth();

            this.setRefreshStatus(RefreshStatus.normal);
        }

        public RefreshStatus getRefreshStatus() {
            return refreshStatus;
        }

        public void setRefreshStatus(RefreshStatus refreshStatus) {
            if (this.refreshStatus != refreshStatus) {
                this.refreshStatus = refreshStatus;
                if (refreshStatus == RefreshStatus.refreshing) {
                    this.progressBar.setVisibility(View.VISIBLE);
                }
                else {
                    this.progressBar.setVisibility(View.GONE);
                }
                refreshStatusString();
                this.invalidate();
            }
        }

        private void refreshStatusString() {
            switch (refreshStatus) {
                case normal:
                    textView.setText(normalString);
                    progressBar.setProgress(0);
                    break;
                case willrefresh:
                    textView.setText(willrefreshString);
                    break;
                case refreshing:
                    textView.setText(refreshingString);
                    break;
                default:
                    break;
            }
        }

        /**
         * 设置状态字符串
         *
         * @param normalString      平时的字符串
         * @param willrefreshString 松开后刷新（或加载）的字符串
         * @param refreshingString  正在刷新（或加载）的字符串
         */
        public void setStatusStrings(String normalString, String willrefreshString, String refreshingString) {
            this.normalString = normalString;
            this.willrefreshString = willrefreshString;
            this.refreshingString = refreshingString;
            this.refreshStatusString();
        }
    }

    private StatusView refreshView;
    private StatusView moreView;
    private int itemFlag = -1;
    private boolean isRecorded = false;
    private float downY = -1;
    private int indexOfFirstItemWhenScroll = 0;
    private final float minTimesToRefresh = 2.5f;
    private final static int ITEM_FLAG_FIRST = 1;
    private final static int ITEM_FLAG_NONE = 0;
    private final static int ITEM_FLAG_LAST = -1;
    private final static int ITEM_FLAG_PENDING = 2;

    // 两个监听器
    private RefreshableListViewListener onRefreshListener;
    private RefreshableListViewListener onMoreListener;
    // 滚动到低端的时候是否自动加载更多
    private boolean doMoreWhenBottom = true;

    /**
     * 滑动时候的listener
     */
    private OnScrollListener onScrollListener;

    public RefreshableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initThis(context);
    }

    public RefreshableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initThis(context);
    }

    public RefreshableListView(Context context) {
        super(context);
        initThis(context);
    }

    private void initThis(Context context) {
        final String pulllDownString = getContext().getString(R.string.pull_to_refresh);
        final String willrefreshString = getContext().getString(R.string.release_to_start_refresh);
        final String refreshingString = getContext().getString(R.string.is_refreshing);

        final String pulllUpString = getContext().getString(R.string.pull_to_load);
        final String willLoadString = getContext().getString(R.string.release_to_start_load);
        final String loadingString = getContext().getString(R.string.is_loading);

        refreshView = new StatusView(context);
        moreView = new StatusView(context);
        refreshView.setStatusStrings(pulllDownString, willrefreshString, refreshingString);
        moreView.setStatusStrings(pulllUpString, willLoadString, loadingString);

        this.addHeaderView(refreshView, null, false);
        this.addFooterView(moreView, null, false);
        this.setOnScrollListener(this);
        doneRefresh();
        doneMore();
    }

    // 监听器操作
    public RefreshableListViewListener getOnRefreshListener() {
        return onRefreshListener;
    }

    public void setOnRefreshListener(RefreshableListViewListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public RefreshableListViewListener getOnMoreListener() {
        return onMoreListener;
    }

    public void setOnMoreListener(RefreshableListViewListener onMoreListener) {
        this.onMoreListener = onMoreListener;
    }

    // 设置
    public boolean isDoMoreWhenBottom() {
        return doMoreWhenBottom;
    }

    public void setDoMoreWhenBottom(boolean doMoreWhenBottom) {
        this.doMoreWhenBottom = doMoreWhenBottom;
    }

    public void addOnScrollListener(OnScrollListener l) {
        this.onScrollListener = l;
    }

    @Override
    public void onScroll(AbsListView l, int t, int oldl, int count) {
        // log("%d %d %d", t, oldl, count);
        // 如果数量比较多(>=6)的时候, 向下滚到快到末尾(距离3个)就开始加载
        if (count >= 6) {
            if ((t + oldl) == count - 3 && this.indexOfFirstItemWhenScroll < t) {
                if (t != 0) {
                    if (doMoreWhenBottom && onMoreListener != null && moreView.getRefreshStatus() != RefreshStatus.refreshing) {
                        doMore();
                    }
                }
            }
        }

        if ((t + oldl) == count) {
            if (t != 0) {
                itemFlag = ITEM_FLAG_LAST;
                //				if (doMoreWhenBottom
                //						&& onMoreListener != null
                //						&& moreView.getRefreshStatus() != RefreshStatus.refreshing)
                //				{
                //					doMore();
                //				}
            }
            else {
                itemFlag = ITEM_FLAG_PENDING;
            }
        }
        else if (t == 0) {
            itemFlag = ITEM_FLAG_FIRST;
        }
        else {
            itemFlag = ITEM_FLAG_NONE;
            // isRecorded = false;
        }

        // record index as last time
        this.indexOfFirstItemWhenScroll = t;
        if (onScrollListener != null) {
            onScrollListener.onScroll(l, t, oldl, count);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView arg0, int arg1) {
        if (onScrollListener != null) {
            onScrollListener.onScrollStateChanged(arg0, arg1);
        }
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof RefreshableListViewListener) {
            this.setOnRefreshListener((RefreshableListViewListener) adapter);
            this.setOnMoreListener((RefreshableListViewListener) adapter);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isRecorded == false && (((itemFlag == ITEM_FLAG_FIRST || itemFlag == ITEM_FLAG_PENDING) && onRefreshListener != null && refreshView.getRefreshStatus() == RefreshStatus.normal) || ((itemFlag == ITEM_FLAG_LAST || itemFlag == ITEM_FLAG_PENDING) && onMoreListener != null && moreView.getRefreshStatus() == RefreshStatus.normal))) {
                    downY = (int) ev.getY();
                    isRecorded = true;
                    // log("按下，记录：%d flag:%d", downY, itemFlag);
                }
                break;
            case MotionEvent.ACTION_UP: {
                isRecorded = false;
                if (onRefreshListener != null && refreshView.getRefreshStatus() == RefreshStatus.willrefresh) {
                    doRefresh();
                }
                else if (refreshView.getRefreshStatus() == RefreshStatus.normal) {
                    refreshView.setPadding(0, -1 * refreshView.height, 0, 0);
                }

                if (onMoreListener != null && moreView.getRefreshStatus() == RefreshStatus.willrefresh) {
                    doMore();
                }
                else if (moreView.getRefreshStatus() == RefreshStatus.normal) {
                    moreView.setPadding(0, 0, 0, -1 * moreView.height);
                }
                break;
            }
            // 事件被取消(主要是针对父控件消费了ACTION_UP事件)的时候, 防止界面处于中途状态
            case MotionEvent.ACTION_CANCEL: {
                if (!isRecorded) {
                    break;
                }

                isRecorded = false;
                if (onRefreshListener != null && (refreshView.getRefreshStatus() == RefreshStatus.willrefresh || refreshView.getRefreshStatus() == RefreshStatus.normal)) {
                    refreshView.setPadding(0, -1 * refreshView.height, 0, 0);
                    refreshView.setRefreshStatus(RefreshStatus.normal);
                }

                if (onMoreListener != null && (moreView.getRefreshStatus() == RefreshStatus.willrefresh || moreView.getRefreshStatus() == RefreshStatus.normal)) {
                    moreView.setPadding(0, 0, 0, -1 * moreView.height);
                    moreView.setRefreshStatus(RefreshStatus.normal);
                }

                // 默认值, 是需要move时候判断方向
                itemFlag = ITEM_FLAG_PENDING;
                break;
            }
            case MotionEvent.ACTION_MOVE: {

                if (isRecorded == false && (((itemFlag == ITEM_FLAG_FIRST || itemFlag == ITEM_FLAG_PENDING) && onRefreshListener != null && refreshView.getRefreshStatus() == RefreshStatus.normal) || ((itemFlag == ITEM_FLAG_LAST || itemFlag == ITEM_FLAG_PENDING) && onMoreListener != null && moreView.getRefreshStatus() == RefreshStatus.normal))) {
                    downY = ev.getY();
                    isRecorded = true;
                }
                else if (isRecorded) {
                    float nowY = ev.getY();
                    float offset = (nowY - downY) / 2.5f;
                    if (itemFlag == ITEM_FLAG_PENDING) {
                        itemFlag = offset == 0 ? ITEM_FLAG_PENDING : (offset > 0 ? ITEM_FLAG_FIRST : ITEM_FLAG_LAST);
                    }
                    if (offset > 0 && itemFlag == ITEM_FLAG_FIRST && onRefreshListener != null) {
                        // 下拉
                        setSelection(0);
                        if (offset >= (minTimesToRefresh * refreshView.height)) {
                            refreshView.setRefreshStatus(RefreshStatus.willrefresh);
                        }
                        else {
                            refreshView.setRefreshStatus(RefreshStatus.normal);
                        }
                        if (offset < this.getHeight() / 4) {
                            refreshView.setPadding(0, (int) (offset - refreshView.height), 0, 0);
                        }
                    }
                    else if (itemFlag == ITEM_FLAG_LAST && offset < 0 && onMoreListener != null) {
                        // 上拉
                        setSelection(this.getCount());
                        if (offset - (minTimesToRefresh * moreView.height) <= 0) {
                            moreView.setRefreshStatus(RefreshStatus.willrefresh);
                        }
                        else {
                            moreView.setRefreshStatus(RefreshStatus.normal);
                        }

                        if (offset + this.getHeight() / 4 > 0) {
                            moreView.setPadding(0, 0, 0, -(int) (moreView.height + offset));
                        }
                    }
                    // log("位移:%d", offset);
                }
                break;
            }
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    class QueryTimer implements Runnable {
        RefreshableListViewListener mListener = null;
        boolean mRefreshOrLoadMore = false;

        public QueryTimer(RefreshableListViewListener l, boolean refreshOrLoadMore) {
            mListener = l;
            mRefreshOrLoadMore = refreshOrLoadMore;
        }

        @Override
        public void run() {
            if (mListener.onQueryFinished(mRefreshOrLoadMore)) {
                if (mRefreshOrLoadMore) {
                    doneRefresh();
                }
                else {
                    doneMore();
                }

                return;
            }
            postDelayed(this, 500);
        }

    }

    ;

    /**
     * 开始刷新
     */
    protected void doRefresh() {
        // log("开始刷新");
        refreshView.setRefreshStatus(RefreshStatus.refreshing);
        refreshView.setPadding(0, 0, 0, 0);
        if (onRefreshListener.onRefreshOrMore(this, true)) {
            doneRefresh();
        }
        else {
            this.postDelayed(new QueryTimer(onRefreshListener, true), 500);
        }
    }

    /**
     * 开始加载更多
     */
    protected void doMore() {
        // log("加载更多");
        moreView.setRefreshStatus(RefreshStatus.refreshing);
        moreView.setPadding(0, 0, 0, 0);
        if (onMoreListener.onRefreshOrMore(this, false)) {
            doneMore();
        }
        else {
            this.postDelayed(new QueryTimer(onMoreListener, false), 500);
        }
    }

    /**
     * 刷新完成之后调用，用于取消刷新的动画
     */
    public void doneRefresh() {
        // log("刷新完成!");
        refreshView.setRefreshStatus(RefreshStatus.normal);
        refreshView.setPadding(0, -1 * refreshView.height, 0, 0);
    }

    /**
     * 加载更多完成之后调用，用于取消加载更多的动画
     */
    public void doneMore() {
        // log("加载完成!");
        moreView.setRefreshStatus(RefreshStatus.normal);
        moreView.setPadding(0, 0, 0, -1 * moreView.height);
    }

    /**
     * 获取刷新的状态
     *
     * @return 一般 将要刷新 刷新完成
     */
    public RefreshStatus getRefreshStatus() {
        return refreshView.getRefreshStatus();
    }

    /**
     * 获取加载更多的状态
     *
     * @return 一般 将要加载 加载完成
     */
    public RefreshStatus getMoreStatus() {
        return moreView.getRefreshStatus();
    }
}