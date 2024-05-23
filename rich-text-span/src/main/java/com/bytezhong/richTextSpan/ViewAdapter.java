package com.bytezhong.richTextSpan;

import android.view.View;

public interface ViewAdapter {
    /**
     * 获取与RichTextSpan关联的视图类。
     * @return 返回视图类的Class对象。
     */
    Class getViewClass();

    /**
     * 获取视图的宽度。
     * @return 返回视图的宽度（以像素为单位）。
     */
    int getWidth();

    /**
     * 获取视图的高度。
     * @return 返回视图的高度（以像素为单位）。
     */
    int getHeight();

    /**
     * 当视图创建完成后调用此方法。
     * @param view 创建完成的视图实例。
     */
    void onViewCreateCompleted(View view);

    /**
     * 在视图view的尺寸发生变化时，调用监听器onViewSizeChangedListener的void viewSizeChanged(int width,int height)方法。
     * @param view 由容器创建的视图实例。
     * @param onViewSizeChangedListener 尺寸变化监听器。
     */
    default void registerViewSizeChangeListener(View view, OnViewSizeChangedListener onViewSizeChangedListener) {
    }

    /**
     * 当视图部分滚动到屏幕可见区域内时调用此方法。
     * @param view 由容器创建的视图实例。
     */
    default void onPartiallyScrollIn(View view) {
    }

    /**
     * 当视图全部滚动到屏幕可见区域内时调用此方法。
     * @param view 由容器创建的视图实例。
     */
    default void onFullyScrollIn(View view) {
    }

    /**
     * 当视图部分滚动出屏幕可见区域时调用此方法。
     * @param view 由容器创建的视图实例。
     */
    default void onPartiallyScrollOut(View view) {
    }

    /**
     * 当视图全部滚动出屏幕可见区域时调用此方法。
     * @param view 由容器创建的视图实例。
     */
    default void onFullyScrollOut(View view) {
    }
}
