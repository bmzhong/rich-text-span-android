package com.bytezhong.richTextSpan.old;

import android.graphics.Rect;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.bytezhong.richTextSpan.old.OnViewSizeChangedListener;

public interface RichTextViewAdapter {

    int getViewWidth();

    int getViewHeight();

    void setViewSizeChangedListener(OnViewSizeChangedListener onViewSizeChangedListener);

    void startPlay();

    void pausePlay();

    void resumePlay();

    void setLayoutParams(ViewGroup.LayoutParams layoutParams);

    ViewGroup.LayoutParams getLayoutParams();
    void requestLayout();
    void layout(int l, int t, int r, int b);
    boolean getGlobalVisibleRect(Rect r);
    ViewTreeObserver getViewTreeObserver();
}
