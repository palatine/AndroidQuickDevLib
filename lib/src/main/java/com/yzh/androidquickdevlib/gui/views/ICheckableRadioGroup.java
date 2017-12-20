package com.yzh.androidquickdevlib.gui.views;

/**
 * Created by yzh on 2016/5/12.
 */
public interface ICheckableRadioGroup
{
    /**
     * check 指定id的子view
     *
     * @param id
     */
    void check(int id);

    /**
     * check 指定index的子view
     *
     * @param index
     */
    void checkByIndex(int index);

    /**
     * 获取当前处于选中状态下的view的id
     *
     * @return
     */
    int getCheckedId();

    /**
     * 获取当前处于选中状态下的view的index(从0开始)
     *
     * @return
     */
    int getCheckedIndex();

    /**
     * 取消所有选中的项目
     */
    void clearCheck();
}
