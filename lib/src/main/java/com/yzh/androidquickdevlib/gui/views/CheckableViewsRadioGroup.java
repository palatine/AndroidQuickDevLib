package com.yzh.androidquickdevlib.gui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.LinearLayout;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class CheckableViewsRadioGroup extends LinearLayout implements ICheckableRadioGroup {
    /**
     * 未选中时的index
     */
    public static final int UNCHECKED_ID = -1;
    /**
     * 记录当前被选中的子view id
     */
    private int mCheckedId = UNCHECKED_ID;

    /**
     * 添加到当前RadioGroup的子view list
     */
    private final Map<Integer, Checkable> mChildrens = new LinkedHashMap<>();

    /**
     * 统一处理onclick事件
     */
    private final OnClickListener mDelegateOnClickListener = new OnChildClickedListener();

    /**
     * 设置check change事件监听器
     */
    private OnCheckedChangeListener mOnCheckedChangeListener;

    /**
     * <p>
     * Interface definition for a callback to be invoked when the checked radio
     * button changed in this group.
     * </p>
     */
    public interface OnCheckedChangeListener {
        /**
         * <p>
         * Called when the checked radio button has changed. When the selection
         * is cleared, checkedId is UNCHECKED_ID.
         * </p>
         *
         * @param group     the group in which the checked radio button has changed
         * @param checkedId the unique identifier of the newly checked radio button
         */
        void onCheckedChanged(CheckableViewsRadioGroup group, int checkedId);
    }

    /**
     * 统一处理子view的OnClicked事件
     */
    private class OnChildClickedListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            // 如果非enable的时候, 则什么也不做
            if (isEnabled()) {
                check(getViewId(v));
            }
        }
    }

    public CheckableViewsRadioGroup(Context context) {
        super(context);
    }

    public CheckableViewsRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // checks the appropriate radio button as requested in the XML file
        if (this.mCheckedId != UNCHECKED_ID) {
            setCheckedIdInternal(this.mCheckedId);
        }
    }

    /**
     * 选中指定id的子view
     *
     * @param id
     */
    private void setCheckedIdInternal(int id) {
        // uncheck 当前选中的项目
        if (this.mCheckedId != UNCHECKED_ID) {
            setCheckedStateForView(this.mCheckedId, false);
        }

        // 选中指定的id
        setCheckedStateForView(id, true);
        setCheckedId(id);
    }

    /**
     * 设置子view的checked状态
     *
     * @param viewId
     * @param checked
     */
    private void setCheckedStateForView(int viewId, boolean checked) {
        View checkedView = viewId == UNCHECKED_ID ? null : findViewById(viewId);
        if (checkedView != null && checkedView instanceof Checkable) {
            ((Checkable) checkedView).setChecked(checked);
        }
    }

    /**
     * 记录选中的id
     *
     * @param id
     */
    private void setCheckedId(int id) {
        this.mCheckedId = id;
        if (id != UNCHECKED_ID && mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, this.mCheckedId);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        addChildViewRecursively(child);
        super.addView(child, index, params);
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
        this.mChildrens.clear();
    }

    /**
     * 添加子view
     *
     * @param view
     */
    private void addChildViewRecursively(View view) {
        if (view == null) {
            return;
        }

        if (view instanceof View) {
            if (view instanceof Checkable) {
                final int id = getViewId(view);
                final Checkable checkable = (Checkable) view;
                this.mChildrens.put(id, checkable);

                // 如果是选择状态的话, 需要重新设置下
                if (checkable.isChecked()) {
                    setCheckedIdInternal(id);
                }
                // 接管监听事件
                view.setOnClickListener(this.mDelegateOnClickListener);
            }
        }
        else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                addChildViewRecursively(viewGroup.getChildAt(i));
            }
        }
    }

    /**
     * 获取指定view的id, 如果没有设置过的话, 就自动设置一个
     *
     * @param view
     * @return
     */
    private int getViewId(View view) {
        if (view == null) {
            return View.NO_ID;
        }

        int id = view.getId();
        if (id == View.NO_ID) {
            id = view.hashCode();
            view.setId(id);
        }

        return id;
    }

    /**
     * <p>
     * Sets the selection to the radio button whose identifier is passed in
     * parameter. Using UNCHECKED_ID as the selection identifier clears the selection;
     * such an operation is equivalent to invoking {@link #clearCheck()}.
     * </p>
     *
     * @param id the unique id of the radio button to select in this group
     * @see #getCheckedId()
     * @see #clearCheck()
     */
    @Override
    public void check(int id) {
        // don't even bother
        if (id == this.mCheckedId) {
            return;
        }

        setCheckedIdInternal(id);
    }

    @Override
    public void checkByIndex(int index) {
        int count = this.mChildrens.size();
        if (index < 0 || index >= count) {
            return;
        }
        int checkid = (int) this.mChildrens.keySet()
                .toArray()[index];
        check(checkid);
    }


    /**
     * <p>
     * Returns the identifier of the selected radio button in this group. Upon
     * empty selection, the returned value is UNCHECKED_ID.
     * </p>
     *
     * @return the unique id of the selected radio button in this group
     * @see #check(int)
     * @see #clearCheck()
     */
    @Override
    public int getCheckedId() {
        return this.mCheckedId;
    }

    @Override
    public int getCheckedIndex() {
        final int checkid = getCheckedId();
        if (!this.mChildrens.containsKey(checkid)) {
            return UNCHECKED_ID;
        }

        int index = 0;
        Iterator<Map.Entry<Integer, Checkable>> iterator = this.mChildrens.entrySet()
                .iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Checkable> entry = iterator.next();
            if (entry.getKey() == checkid) {
                break;
            }
            index++;
        }

        return index;
    }


    /**
     * <p>
     * Clears the selection. When the selection is cleared, no radio button in
     * this group is selected and {@link #getCheckedId()} returns
     * null.
     * </p>
     *
     * @see #check(int)
     * @see #getCheckedId()
     */
    public void clearCheck() {
        check(UNCHECKED_ID);
    }

    /**
     * <p>
     * Register a callback to be invoked when the checked radio button changes
     * in this group.
     * </p>
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.mOnCheckedChangeListener = listener;
    }

    /**
     * 返回当前Checkable子控件的count
     */
    public int getCheckableChildrenCount() {
        return this.mChildrens.size();
    }
}
