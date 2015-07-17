package com.nectar.timeby.gui.util;

/**
 * Created by finalize on 7/17/15.
 *
 * 界面中有抽屉开关的Fragment必须实现此接口
 *
 * 用于显示或隐藏该Fragment中的抽屉开关
 */
public interface OnDrawerStatusChangedListener {
    void onDrawerOpening();

    void onDrawerClosed();
}
