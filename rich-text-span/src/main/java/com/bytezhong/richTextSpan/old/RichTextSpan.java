package com.bytezhong.richTextSpan.old;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.style.ReplacementSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RichTextSpan extends ReplacementSpan {

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

    public static final int PLAY_WHEN_PARTIALLY_VISIBLE = 0;
    public static final int PLAY_WHEN_FULLY_VISIBLE = 1;


    private static final int IDlE = 0;

    private static final int PLAYING = 1;

    private static final int PAUSE = 2;

    private final RichTextViewAdapter mRichTextViewAdapter;

    private final TextViewWrapper mTextViewWrapper;

    private Rect mRect;

    private int mPlayCondition;

    private int mPlayStatus;

    private int mVerticalAlignment;

    private boolean mSizeChanged;

    private boolean mRichTextViewVisible;

    private static int debugCountPlaying = 0;

    private Rect lastDrawnRect = new Rect();

    public RichTextSpan(TextViewWrapper textViewWrapper, RichTextViewAdapter richTextViewAdapter) {
        this(textViewWrapper, richTextViewAdapter, ALIGN_BOTTOM);
    }

    public RichTextSpan(TextViewWrapper textViewWrapper, RichTextViewAdapter richTextViewAdapter, int verticalAlignment) {
        mTextViewWrapper = textViewWrapper;
        mRichTextViewAdapter = richTextViewAdapter;
        mVerticalAlignment = verticalAlignment;
        mPlayCondition = PLAY_WHEN_PARTIALLY_VISIBLE;
        mSizeChanged = true;
        mRichTextViewVisible = false;
        mPlayStatus = IDlE;
        mRichTextViewAdapter.setViewSizeChangedListener(mOnViewSizeChangeListener);

        mTextViewWrapper.addView((View) mRichTextViewAdapter);

        mRichTextViewAdapter.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);

        mRichTextViewAdapter.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener);
    }

    public void setPlayTime(int playTime) {
        this.mPlayCondition = playTime;
    }


    public void removeGlobalLayoutListener() {
        if (mOnGlobalLayoutListener != null) {
            mTextViewWrapper.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
            mOnGlobalLayoutListener = null;
        }
    }

    public void removeScrollChangedListener() {
        if (mOnScrollChangedListener != null) {
            mRichTextViewAdapter.getViewTreeObserver().removeOnScrollChangedListener(mOnScrollChangedListener);
            mOnScrollChangedListener = null;
        }
    }

    public void setVerticalAlignment(int verticalAlignment) {
        mVerticalAlignment = verticalAlignment;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        if (mRect == null || mSizeChanged) {
            mRect = new Rect(0, 0, mRichTextViewAdapter.getViewWidth(), mRichTextViewAdapter.getViewHeight());
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

//        // 保存绘制区域的坐标
//        lastDrawnRect.left = (int) x;
//        lastDrawnRect.top = top;
//        lastDrawnRect.right = (int) x + (int) paint.measureText(text, start, end);
//        lastDrawnRect.bottom = bottom;


        if (mRect == null || mSizeChanged) {
            mRect = new Rect(0, 0, mRichTextViewAdapter.getViewWidth(), mRichTextViewAdapter.getViewHeight());
        }
        final Rect rect = mRect;
        int transY = bottom - rect.bottom;
        if (mVerticalAlignment == ALIGN_BASELINE) {
            transY -= paint.getFontMetricsInt().descent;
        } else if (mVerticalAlignment == ALIGN_CENTER) {
            transY = top + (bottom - top) / 2 - rect.bottom / 2;
        }

        lastDrawnRect.left = (int) x;
        lastDrawnRect.top = transY;
        lastDrawnRect.right = (int) x + rect.width();
        lastDrawnRect.bottom = transY+rect.height();

        if (mSizeChanged) {
            FrameLayout.LayoutParams currentParams = (FrameLayout.LayoutParams) mRichTextViewAdapter.getLayoutParams();
            FrameLayout.LayoutParams newParams = generateLayoutParams(new Point((int) x, transY), rect.width(), rect.height());
            if (areLayoutParamsDifferent(currentParams, newParams)) {
                mRichTextViewAdapter.layout((int) x, transY, (int) (x + rect.width()), transY + rect.height());
                mRichTextViewAdapter.setLayoutParams(newParams);
                mRichTextViewAdapter.requestLayout();
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

    OnViewSizeChangedListener mOnViewSizeChangeListener = new OnViewSizeChangedListener() {
        @Override
        public void viewSizeChanged(int width, int height) {
            mSizeChanged = true;
            CharSequence text = mTextViewWrapper.getText();
            mTextViewWrapper.setText(text);
            mTextViewWrapper.getTextView().requestLayout();
        }
    };

    ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (isRichTextViewVisible()) {
                mRichTextViewVisible = true;
                if (mPlayStatus == IDlE) {
                    mRichTextViewAdapter.startPlay();
                    mPlayStatus = PLAYING;
                } else if (mPlayStatus == PAUSE) {
                    mRichTextViewAdapter.resumePlay();
                    mPlayStatus = PLAYING;
                }
            } else {
                mRichTextViewVisible = false;
                if (mPlayStatus == PLAYING) {
                    mRichTextViewAdapter.pausePlay();
                    mPlayStatus = PAUSE;
                }
            }
        }
    };

    ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
        @Override
        public void onScrollChanged() {
            // 获取TextView的全局可见区域
            Rect visibleRect = new Rect();
            mTextViewWrapper.getTextView().getGlobalVisibleRect(visibleRect);
//            System.out.println(visibleRect);
            System.out.println("lastDrawnRect: "+lastDrawnRect);
            // 将ReplacementSpan的坐标转换为全局坐标
            int[] textViewLocation = new int[2];
            mTextViewWrapper.getTextView().getLocationOnScreen(textViewLocation);
            System.out.println(textViewLocation[0]+" "+textViewLocation[1]);
            Rect spanGlobalRect = new Rect(
                    textViewLocation[0] + lastDrawnRect.left,
                    textViewLocation[1] + lastDrawnRect.top,
                    textViewLocation[0] + lastDrawnRect.right,
                    textViewLocation[1] + lastDrawnRect.bottom
            );

            // 判断ReplacementSpan的绘制区域是否在TextView的可见区域内
            if (Rect.intersects(visibleRect, spanGlobalRect)) {
                // ReplacementSpan的绘制区域在TextView的可见区域内
                System.out.println("In");
            } else {
                // ReplacementSpan的绘制区域不在TextView的可见区域内
                System.out.println("Out");
            }

            if (isRichTextViewVisible()) {
                if (!mRichTextViewVisible) {
                    if (mPlayStatus == IDlE) {
                        mRichTextViewAdapter.startPlay();
                        mPlayStatus = PLAYING;
                    } else if (mPlayStatus == PAUSE) {
                        mRichTextViewAdapter.resumePlay();
                        mPlayStatus = PLAYING;
                    }
                    mRichTextViewVisible = true;
                }
            } else {
                if (mPlayStatus == PLAYING) {
                    mRichTextViewAdapter.pausePlay();
                    mPlayStatus = PAUSE;
                }
                mRichTextViewVisible = false;
            }
        }
    };


    private boolean isRichTextViewVisible() {
        Rect richTextViewRect = new Rect();
        boolean visible = mRichTextViewAdapter.getGlobalVisibleRect(richTextViewRect);
//        System.out.println(richTextViewRect);
        if (mPlayCondition == PLAY_WHEN_FULLY_VISIBLE) {
            visible = richTextViewRect.width() == mRichTextViewAdapter.getViewWidth() && richTextViewRect.height() == mRichTextViewAdapter.getViewHeight();
        }
        return visible;
    }
}
