package com.bytezhong.richTextSpan.old;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;

public class CustomLottieAnimationView extends LottieAnimationView implements RichTextViewAdapter {

    private int mWidth;
    private int mHeight;

    private OnViewSizeChangedListener mOnViewSizeChangedListener;

    public CustomLottieAnimationView(Context context) {
        this(context, null);
    }

    public CustomLottieAnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomLottieAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mWidth = 0;
        mHeight = 0;
    }

    public void setLottieSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    @Override
    public int getViewWidth() {
        return mWidth;
    }

    @Override
    public int getViewHeight() {
        return mHeight;
    }

    @Override
    public void setViewSizeChangedListener(OnViewSizeChangedListener onViewSizeChangedListener) {
        mOnViewSizeChangedListener = onViewSizeChangedListener;
    }

    @Override
    public void startPlay() {
        playAnimation();
    }

    @Override
    public void pausePlay() {
        pauseAnimation();
    }

    @Override
    public void resumePlay() {
        resumeAnimation();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

/*    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        mWidth = params.width;
        mHeight = params.height;
        if (mOnViewSizeChangedListener != null) {
            mOnViewSizeChangedListener.viewSizeChanged(mWidth, mHeight);
        }
    }*/

    public void changeSize(int width, int height) {
        mWidth = width;
        mHeight = height;
        setLayoutParams(new FrameLayout.LayoutParams(width, height));
        if (mOnViewSizeChangedListener != null) {
            mOnViewSizeChangedListener.viewSizeChanged(width, height);
        }
    }
}
