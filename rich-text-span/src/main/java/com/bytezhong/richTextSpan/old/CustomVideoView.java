package com.bytezhong.richTextSpan.old;

import android.content.Context;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomVideoView extends VideoView implements RichTextViewAdapter {

    private int mWidth;
    private int mHeight;

    private OnViewSizeChangedListener mOnViewSizeChangedListener;

    public CustomVideoView(@NonNull Context context) {
        super(context);
    }

    public CustomVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setVideoSize(int width, int height) {
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
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        mWidth = params.width;
        mHeight = params.height;
    }

    @Override
    public void setViewSizeChangedListener(OnViewSizeChangedListener onViewSizeChangedListener) {
        mOnViewSizeChangedListener = onViewSizeChangedListener;
    }

    @Override
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        super.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (l != null) {
                    l.onPrepared(mp);
                }
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        if (mOnViewSizeChangedListener != null) {
                            mOnViewSizeChangedListener.viewSizeChanged(width, height);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void startPlay() {
        start();
    }

    @Override
    public void pausePlay() {
        pause();
    }

    @Override
    public void resumePlay() {
        start();
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
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    public void changeSize(int width, int height) {
        mWidth = width;
        mHeight = height;
        setLayoutParams(new FrameLayout.LayoutParams(width, height));
        if (mOnViewSizeChangedListener != null) {
            mOnViewSizeChangedListener.viewSizeChanged(width, height);
        }
    }

}
