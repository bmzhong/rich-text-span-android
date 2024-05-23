package com.bytezhong.richTextSpan;

public interface OnViewSizeChangedListener {
    /**
     * 开发者在视图大小变化的地方主动调用此方法
     * @param width 视图新的宽度
     * @param height 视图新的高度
     */
    void viewSizeChanged(int width,int height);
}
