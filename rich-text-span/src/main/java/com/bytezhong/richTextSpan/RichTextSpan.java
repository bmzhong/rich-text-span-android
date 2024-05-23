package com.bytezhong.richTextSpan;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.style.ReplacementSpan;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RichTextSpan extends ReplacementSpan {

    interface OnInitialSpanDrawListener {
        void onInitialSpanDraw(RichTextSpan richTextSpan);
    }

    interface OnSpanViewSizeChangedListener {
        void onSpanViewSizeChanged(RichTextSpan richTextSpan);
    }


    private static final String TAG = "RichTextSpan";
    /**
     * A constant indicating that the bottom of this span should be aligned
     * with the bottom of the surrounding text, i.e., at the same level as the
     * lowest descender in the text.
     */
    public static final int ALIGN_BOTTOM = 0;

    /**
     * A constant indicating that the bottom of this span should be aligned
     * with the baseline of the surrounding text.
     */
    public static final int ALIGN_BASELINE = 1;

    /**
     * A constant indicating that this span should be vertically centered between
     * the top and the lowest descender.
     */
    public static final int ALIGN_CENTER = 2;

    public static final int NOT_VISIBLE = 0;

    public static final int PARTIALLY_VISIBLE = 1;

    public static final int FULLY_VISIBLE = 2;

    private boolean firstDraw = false;
    private final ViewAdapter mViewAdapter;
    private Rect mDrawRect;
    private View mView;
    private Rect mRect;
    private boolean mSizeChanged;
    private int mVerticalAlignment;
    private int mVisibility;

    private OnInitialSpanDrawListener mOnInitialSpanDrawListener;
    private OnSpanViewSizeChangedListener mOnSpanViewSizeChangedListener;


    public RichTextSpan(ViewAdapter textViewAdapter) {
        mViewAdapter = textViewAdapter;
        mSizeChanged = true;
        mVerticalAlignment = ALIGN_BOTTOM;

    }

    public Class getViewClass() {
        return mViewAdapter.getViewClass();
    }

    public Rect getDrawRect() {
        return mDrawRect;
    }

    public void setView(View view) {
        mView = view;
        mViewAdapter.registerViewSizeChangeListener(view, mOnViewSizeChangedListener);
    }

    public View getView() {
        return mView;
    }

    public ViewAdapter getViewAdapter() {
        return mViewAdapter;
    }

    public int getVerticalAlignment() {
        return mVerticalAlignment;
    }

    public void setVerticalAlignment(int verticalAlignment) {
        mVerticalAlignment = verticalAlignment;
    }

    public int getVisibility() {
        return mVisibility;
    }

    public void setVisibility(int visibility) {
        mVisibility = visibility;
    }

    public void setOnInitialSpanDrawListener(OnInitialSpanDrawListener OnInitialSpanDrawListener) {
        mOnInitialSpanDrawListener = OnInitialSpanDrawListener;
    }

    public void setOnSpanViewSizeChangedListener(OnSpanViewSizeChangedListener onSpanViewSizeChangedListener) {
        mOnSpanViewSizeChangedListener = onSpanViewSizeChangedListener;
    }

    public void onViewCreateCompleted() {
        mViewAdapter.onViewCreateCompleted(mView);
    }

    public void onPartiallyScrollIn() {
        mViewAdapter.onPartiallyScrollIn(mView);
    }

    public void onFullyScrollIn() {
        mViewAdapter.onFullyScrollIn(mView);
    }

    public void onPartiallyScrollOut() {
        mViewAdapter.onPartiallyScrollOut(mView);
    }

    public void onFullyScrollOut() {
        mViewAdapter.onFullyScrollOut(mView);
    }


    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        if (mRect == null || mSizeChanged) {
            mRect = new Rect(0, 0, mViewAdapter.getWidth(), mViewAdapter.getHeight());
        }
        if (fm != null) {
            fm.ascent = -mRect.bottom;
            fm.descent = 0;

            fm.top = fm.ascent;
            fm.bottom = 0;
        }
        return mRect.right;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        if (mRect == null || mSizeChanged) {
            mRect = new Rect(0, 0, mViewAdapter.getWidth(), mViewAdapter.getHeight());
        }
        final Rect rect = mRect;
        int transY = bottom - rect.bottom;
        if (mVerticalAlignment == ALIGN_BASELINE) {
            transY -= paint.getFontMetricsInt().descent;
        } else if (mVerticalAlignment == ALIGN_CENTER) {
            transY = top + (bottom - top) / 2 - rect.bottom / 2;
        }
        if (mDrawRect == null) {
            mDrawRect = new Rect();
        }
        mDrawRect.left = (int) x;
        mDrawRect.top = transY;
        mDrawRect.right = (int) x + rect.width();
        mDrawRect.bottom = transY + rect.height();
        if (!firstDraw && mOnInitialSpanDrawListener != null) {
            mOnInitialSpanDrawListener.onInitialSpanDraw(this);
            firstDraw = false;
        }


        if (mSizeChanged && mView != null) {
            FrameLayout.LayoutParams currentParams = (FrameLayout.LayoutParams) mView.getLayoutParams();
            FrameLayout.LayoutParams newParams = generateLayoutParams(new Point((int) x, transY), rect.width(), rect.height());
            if (areLayoutParamsDifferent(currentParams, newParams)) {
                mView.layout((int) x, transY, (int) (x + rect.width()), transY + rect.height());
                mView.setLayoutParams(newParams);
                mView.requestLayout();
            }
            mSizeChanged = false;
        }
    }

    public static boolean areLayoutParamsDifferent(FrameLayout.LayoutParams params1, FrameLayout.LayoutParams params2) {
        if (params1 == null && params2 == null) return false;
        if (params1 == null) return true;
        if (params2 == null) return true;
        if (params1.height != params2.height) return true;
        if (params1.width != params2.width) return true;
        if (params1.leftMargin != params2.leftMargin) return true;
        return params1.topMargin != params2.topMargin;
    }

    public static FrameLayout.LayoutParams generateLayoutParams(Point position, int width, int height) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        params.leftMargin = position.x;
        params.topMargin = position.y;
        return params;
    }

    OnViewSizeChangedListener mOnViewSizeChangedListener = new OnViewSizeChangedListener() {
        @Override
        public void viewSizeChanged(int width, int height) {
            mSizeChanged = true;
            mOnSpanViewSizeChangedListener.onSpanViewSizeChanged(RichTextSpan.this);
        }
    };
}
