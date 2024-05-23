package com.bytezhong.richTextSpan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.Spannable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TextViewWrapper extends FrameLayout {

    private static final String TAG = "TextViewWrapper";

    private TextView mTextView;

    private final List<RichTextSpan> mRichTextSpanList;

    private final Context mContext;


    public TextViewWrapper(@NonNull Context context) {
        this(context, null);
    }

    public TextViewWrapper(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextViewWrapper(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TextViewWrapper(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        mRichTextSpanList = new ArrayList<>();
        mTextView = new TextView(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mTextView, layoutParams);
        getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener);
    }

    public TextView getTextView() {
        return mTextView;
    }

    public void setTextView(TextView textView) {
        mTextView = textView;
    }

    public void addRichTextSpan(RichTextSpan richTextSpan) {
        richTextSpan.setOnSpanViewSizeChangedListener(mOnSpanViewSizeChangedListener);
        richTextSpan.setOnInitialSpanDrawListener(mOnInitialSpanDrawListener);
        richTextSpan.setVisibility(RichTextSpan.NOT_VISIBLE);
        mRichTextSpanList.add(richTextSpan);
    }

    public void removeRichTextSpan(RichTextSpan richTextSpan) {
        richTextSpan.setOnSpanViewSizeChangedListener(null);
        richTextSpan.setOnInitialSpanDrawListener(null);
        mRichTextSpanList.remove(richTextSpan);
        Spannable spannable = (Spannable) mTextView.getText();
        spannable.removeSpan(richTextSpan);
        mTextView.setText(spannable, TextView.BufferType.SPANNABLE);
    }


    ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
        @Override
        public void onScrollChanged() {
//            System.out.println(TAG+" onScrollChanged");
            for (int i = 0; i < mRichTextSpanList.size(); ++i) {
                RichTextSpan richTextSpan = mRichTextSpanList.get(i);
                Rect drawRect = richTextSpan.getDrawRect();
                if (drawRect == null) {
                    continue;
                }
                int currentVisibility = viewVisibility(drawRect);
                // 滑动进入可见区域时创建View.
                if (richTextSpan.getView() == null) {
                    if (currentVisibility != RichTextSpan.NOT_VISIBLE) {
//                        System.out.println(TAG + " Scroll In Create View");
                        View view = createView(richTextSpan);
                        addView(view);
                        richTextSpan.setView(view);
                        richTextSpan.onViewCreateCompleted();
//                        richTextSpan.setVisibility(currentVisibility);
//                    mTextView.setText(mTextView.getText());
                        mTextView.requestLayout();
                    }
                }

                int lastVisibility = richTextSpan.getVisibility();
//                currentVisibility = viewVisibility(drawRect);

                if (lastVisibility == RichTextSpan.NOT_VISIBLE && currentVisibility == RichTextSpan.PARTIALLY_VISIBLE) { // 部分滑入可见区域
                    richTextSpan.onPartiallyScrollIn();
                } else if (lastVisibility == RichTextSpan.PARTIALLY_VISIBLE) {
                    if (currentVisibility == RichTextSpan.NOT_VISIBLE) { // 全部滑出可见区域
                        richTextSpan.onFullyScrollOut();
                    } else if (currentVisibility == RichTextSpan.FULLY_VISIBLE) { // 全部滑入可见区域
                        richTextSpan.onFullyScrollIn();
                    }
                } else if (lastVisibility == RichTextSpan.FULLY_VISIBLE && currentVisibility == RichTextSpan.PARTIALLY_VISIBLE) { // 部分滑出可见区域
                    richTextSpan.onPartiallyScrollOut();
                }
                richTextSpan.setVisibility(currentVisibility);
            }
        }
    };

    RichTextSpan.OnInitialSpanDrawListener mOnInitialSpanDrawListener = new RichTextSpan.OnInitialSpanDrawListener() {
        @Override
        public void onInitialSpanDraw(RichTextSpan richTextSpan) {

            int currentVisibility = viewVisibility(richTextSpan.getDrawRect());
            if (richTextSpan.getView() == null && currentVisibility != RichTextSpan.NOT_VISIBLE) {
                View view = createView(richTextSpan);
                addView(view);
                richTextSpan.setView(view);
                richTextSpan.onViewCreateCompleted();
                richTextSpan.setVisibility(currentVisibility);
                mTextView.requestLayout();

            }
            if (currentVisibility == RichTextSpan.PARTIALLY_VISIBLE) {
                richTextSpan.onPartiallyScrollIn();
            } else if (currentVisibility == RichTextSpan.FULLY_VISIBLE) {
                richTextSpan.onPartiallyScrollIn();
                richTextSpan.onFullyScrollIn();
            }
        }
    };


    private View createView(RichTextSpan richTextSpan) {
        Class viewClass = richTextSpan.getViewClass();
        View view;
        try {
            Constructor constructor = viewClass.getConstructor(Context.class);
            view = (View) constructor.newInstance(mContext);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return view;
    }


    private int viewVisibility(Rect drawRect) {
        if (drawRect == null) {
            return RichTextSpan.NOT_VISIBLE;
        }
        // 获取TextView的全局可见区域
        Rect textViewVisibleRect = new Rect();
        mTextView.getGlobalVisibleRect(textViewVisibleRect);
        // 将ReplacementSpan的坐标转换为全局坐标
        int[] textViewLocation = new int[2];
        mTextView.getLocationOnScreen(textViewLocation);
        Rect spanGlobalRect = new Rect(
                textViewLocation[0] + drawRect.left,
                textViewLocation[1] + drawRect.top,
                textViewLocation[0] + drawRect.right,
                textViewLocation[1] + drawRect.bottom
        );
        // 判断ReplacementSpan的绘制区域是否在TextView的可见区域内
        if (Rect.intersects(textViewVisibleRect, spanGlobalRect)) {
            // ReplacementSpan的绘制区域在TextView的可见区域内
            Rect intersection = new Rect(
                    Math.max(textViewVisibleRect.left, spanGlobalRect.left),
                    Math.max(textViewVisibleRect.top, spanGlobalRect.top),
                    Math.min(textViewVisibleRect.right, spanGlobalRect.right),
                    Math.min(textViewVisibleRect.bottom, spanGlobalRect.bottom));
            if (intersection.width() == drawRect.width() && intersection.height() == drawRect.height()) {
                return RichTextSpan.FULLY_VISIBLE;
            } else {
                return RichTextSpan.PARTIALLY_VISIBLE;
            }
        } else {
            // ReplacementSpan的绘制区域不在TextView的可见区域内
            return RichTextSpan.NOT_VISIBLE;
        }
    }

    RichTextSpan.OnSpanViewSizeChangedListener mOnSpanViewSizeChangedListener = new RichTextSpan.OnSpanViewSizeChangedListener() {
        @Override
        public void onSpanViewSizeChanged(RichTextSpan richTextSpan) {
//            System.out.println(richTextSpan);
            Spannable spannable = (Spannable) mTextView.getText();
            int start = spannable.getSpanStart(richTextSpan);
            int end = spannable.getSpanEnd(richTextSpan);
            int flags = spannable.getSpanFlags(richTextSpan);
            mRichTextSpanList.remove(richTextSpan);
            RichTextSpan newRichTextSpan = new RichTextSpan(richTextSpan.getViewAdapter());
            newRichTextSpan.setView(richTextSpan.getView());
            newRichTextSpan.setVerticalAlignment(richTextSpan.getVerticalAlignment());
            newRichTextSpan.setOnSpanViewSizeChangedListener(this);
            newRichTextSpan.setOnInitialSpanDrawListener(mOnInitialSpanDrawListener);
            newRichTextSpan.setVisibility(richTextSpan.getVisibility());
            mRichTextSpanList.add(richTextSpan);
            // 移除旧的Span
            spannable.removeSpan(richTextSpan);
//            System.out.println(start + " " + end);
            // 添加新的Span
            spannable.setSpan(newRichTextSpan, start, end, flags);
            mTextView.setText(spannable, TextView.BufferType.SPANNABLE);
        }
    };


    private boolean firstOnMeasure = true;
    private boolean firstOnLayout = true;
    private boolean firstDispatchDraw = true;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        long start = System.currentTimeMillis();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        long end = System.currentTimeMillis();
        long duration = end - start;
        if (firstOnMeasure) {
            Log.e(TAG, "onMeasure: " + duration + " ms");
            System.out.println("TextViewWrapper onMeasure: " + duration + " ms");
            firstOnMeasure = false;
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        long start = System.currentTimeMillis();
        super.onLayout(changed, left, top, right, bottom);
        long end = System.currentTimeMillis();
        long duration = end - start;
        if (firstOnLayout) {
            Log.e(TAG, "onLayout: " + duration + " ms");
            System.out.println("TextViewWrapper onLayout: " + duration + " ms");
            firstOnLayout = false;
        }
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        long start = System.currentTimeMillis();
        super.dispatchDraw(canvas);
        long end = System.currentTimeMillis();
        long duration = end - start;
        if (firstDispatchDraw) {
            Log.e(TAG, "TextViewWrapper dispatchDraw: " + duration + " ms");
            System.out.println("TextViewWrapper dispatchDraw: " + duration + " ms");
            firstDispatchDraw = false;
        }
    }
}
