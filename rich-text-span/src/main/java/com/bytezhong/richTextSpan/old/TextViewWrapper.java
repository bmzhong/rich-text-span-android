package com.bytezhong.richTextSpan.old;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TextViewWrapper extends FrameLayout {

    private static final String TAG = "TextViewWrapper";
    private boolean firstOnMeasure = true;
    private boolean firstOnLayout = true;
    private boolean firstDispatchDraw = true;

    private TextView mTextView;

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
        mTextView = new TextView(context);
//        mTextView.setTextIsSelectable(true);
//        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mTextView, layoutParams);
    }

    public CharSequence getText() {
        return mTextView.getText();
    }

    public void setText(CharSequence text) {
        mTextView.setText(text);
    }

    public void setTextView(TextView textView) {
        if (mTextView != null) {
            removeView(mTextView);
        }
        mTextView = textView;
    }

    public TextView getTextView() {
        return mTextView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        long start = System.currentTimeMillis();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        long end = System.currentTimeMillis();
        long duration = end - start;
        if (firstOnMeasure) {
            Log.e(TAG, "onMeasure: " + duration + " ms");
//            System.out.println("TextViewWrapper onMeasure: " + duration + " ms");
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
//            System.out.println("TextViewWrapper onLayout: " + duration + " ms");
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
//            System.out.println("TextViewWrapper dispatchDraw: " + duration + " ms");
            firstDispatchDraw = false;
        }

    }
}
