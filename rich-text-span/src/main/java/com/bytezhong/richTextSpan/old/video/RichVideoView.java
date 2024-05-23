/*
 * Copyright (c) 2015. Roberto  Prato <https://github.com/robertoprato>
 *
 *  *
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package com.bytezhong.richTextSpan.old.video;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.bytezhong.richTextSpan.R;
import com.bytezhong.richTextSpan.old.RichTextViewAdapter;
import com.bytezhong.richTextSpan.old.OnViewSizeChangedListener;
import com.bytezhong.richTextSpan.old.util.Size;

/**
 * Created by roberto on 12/10/15.
 */
public class RichVideoView extends FrameLayout implements RichMediaPlayer.FirstFrameAvailableListener,
        MediaPlayer.OnBufferingUpdateListener,
        RichMediaPlayer.OnCompletionListener,
        RichMediaPlayer.OnVideoSizeListener,
        RichTextViewAdapter {

    int width;
    int height;

    private OnViewSizeChangedListener mOnViewSizeChangedListener;

    public void setSurface(SurfaceTexture surface) {
        mMediaPlayer.setSurfaceTexture(surface);
    }

    public RichMediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public void setVideoSize(int width, int hegiht) {
        this.width = width;
        this.height = hegiht;
//        setLayoutParams(new ViewGroup.LayoutParams(this.width, this.height));
    }

    @Override
    public int getViewWidth() {
        return width;
    }

    @Override
    public int getViewHeight() {
        return height;
    }


    @Override
    public void setViewSizeChangedListener(OnViewSizeChangedListener onViewSizeChangedListener) {
        mOnViewSizeChangedListener = onViewSizeChangedListener;
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

    public interface RichVideoViewListener {

        void onVideoReady(RichVideoView videoView);

        void onVideoSizeAvailable(RichVideoView videoView);

    }


    private RichVideoViewListener mRichVideoViewListener;

    private RichMediaPlayer.OnCompletionListener mOnCompletionListener;

    private VideoControls mControlsContainer;
    private TextureView mTextureView;
    private RichMediaPlayer mMediaPlayer;

    private SurfaceTexture mSurface;
    private int mSurfaceWidth;
    private int mSurfaceHeight;


    private ProgressBar mLoadingProgress;
    private String mCurrentUri;


    public RichVideoView(Context context) {
        super(context);
        init();
    }

    public RichVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RichVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public RichVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    public void initMediaPlayer() {

        if (isInEditMode() == true) {
            return;
        }
        if (mMediaPlayer == null) {
            mMediaPlayer = new RichMediaPlayer(getContext());
        }

        mMediaPlayer.setFirstFrameAvailableListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnVideoSizeListener(this);

        if (mSurface != null) {
            mMediaPlayer.setSurfaceTexture(mSurface);
        }

    }

    public void init() {

        initMediaPlayer();

        LayoutInflater.from(getContext())
                .inflate(R.layout.internal_richtext_video_display,
                        this, true);

        mTextureView = (TextureView) findViewById(R.id.internal_texture_view);
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);

        if (mTextureView.isAvailable() == true) {
            mSurfaceTextureListener.onSurfaceTextureAvailable(mTextureView.getSurfaceTexture(),
                    mTextureView.getWidth()
                    , mTextureView.getHeight());
        }

        mControlsContainer = new VideoControls(getContext(), this);


        ViewGroup.LayoutParams currentLayoutParams = getLayoutParams();

        if (currentLayoutParams == null) {
            currentLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        currentLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        setLayoutParams(currentLayoutParams);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

        //setup the progress bar
        mLoadingProgress = (ProgressBar) findViewById(R.id.internal_progress);
        mLoadingProgress.setIndeterminate(true);

    }

    public void handover(RichVideoView destination) {

        destination.mMediaPlayer = mMediaPlayer;

        if (destination.mTextureView.isAvailable() == true) {

            destination.mSurfaceTextureListener.onSurfaceTextureAvailable(destination.mTextureView.getSurfaceTexture(),
                    destination.mTextureView.getWidth(),
                    destination.mTextureView.getHeight());
        }

        destination.initMediaPlayer();
        destination.mMediaPlayer.syncMediaState();


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
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        if(mMediaPlayer != null) {
//            mMediaPlayer.pause();
//            mMediaPlayer = null;
//       }
    }

    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            mSurface = surface;
            mSurfaceWidth = width;
            mSurfaceHeight = height;
            mMediaPlayer.setSurfaceTexture(mSurface);
            mMediaPlayer.setData(mCurrentUri);

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            mSurface = surface;
            mSurfaceWidth = width;
            mSurfaceHeight = height;
            mMediaPlayer.setSurfaceTexture(mSurface);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (mSurface == surface) {
                mSurface = null;
            }
            mMediaPlayer.onSurfaceTextureDestroyed(surface);
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    public boolean isPlaying() {

        if (mMediaPlayer == null) {
            return false;
        }

        return mMediaPlayer.isPlaying();
    }

    public void pause() {

        if (mMediaPlayer == null) {
            return;
        }

        mMediaPlayer.pause();
    }

    public void start() {

        if (mMediaPlayer == null ||
                mMediaPlayer.isPlaying()) {
            return;
        }

        mMediaPlayer.start();
    }

    public void setData(String videoUri) {
        if (TextUtils.equals(videoUri, mCurrentUri) == false) {
            mLoadingProgress.setVisibility(View.VISIBLE);
            mCurrentUri = videoUri;
            initMediaPlayer();
            mMediaPlayer.setData(videoUri);
        }
    }

    public void setRichVideoViewListener(RichVideoViewListener listener) {
        mRichVideoViewListener = listener;
    }

    public void setOnCompletionListener(RichMediaPlayer.OnCompletionListener onCompletionListener) {
        mOnCompletionListener = onCompletionListener;
    }

    @Override
    public void onFirstFrameAvailable(RichMediaPlayer player) {

        mLoadingProgress.setVisibility(GONE);

        if (mControlsContainer != null) {
            mControlsContainer.updateControls();
            mControlsContainer.showControls();
            mControlsContainer.setFullScreenButtonVisible(true);
        }

        invalidate();
        requestLayout();
        if (mRichVideoViewListener != null) {
            mRichVideoViewListener.onVideoReady(this);
        }
    }


    public Size getVideoSize() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getVideoSize();
        }
        return null;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }


    @Override
    public void onCompletion(RichMediaPlayer mp) {
        if (mControlsContainer != null) {
            mControlsContainer.updateControls();
        }
        if (mOnCompletionListener != null) {
            mp.setOnCompletionListener(mOnCompletionListener);
        }
    }

    @Override
    public void onVideoSizeChanged(RichMediaPlayer mp) {

        requestLayout();
        if (mRichVideoViewListener != null) {
            mRichVideoViewListener.onVideoSizeAvailable(this);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mControlsContainer != null) {
            mControlsContainer.showControls();
        }
        return super.onTouchEvent(event);
    }

    public void release() {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
            }
        } catch (Exception e) {
        }
    }


    public boolean toggleFullScreen(boolean fullscreen) {

        final Context context = getContext();

        if (!(context instanceof Activity)) {
            return false;
        }

        Activity activity = (Activity) context;

        // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
        if (fullscreen) {
            FullScreenVideoFragment fullScreenVideoFragment = new FullScreenVideoFragment();
            fullScreenVideoFragment.presentVideoFullScreen(activity, this);
        } else {
            //mSurface
            mSurfaceTextureListener.onSurfaceTextureAvailable(mSurface, mSurfaceWidth, mSurfaceHeight);
            if (mControlsContainer != null) {
                mControlsContainer.updateControls();
            }
        }

        return true;
    }

    public void setVideoPath(String videoUri) {
        if (TextUtils.equals(videoUri, mCurrentUri) == false) {
            mLoadingProgress.setVisibility(View.VISIBLE);
            mCurrentUri = videoUri;
            initMediaPlayer();
            mMediaPlayer.setData(videoUri);
        }
    }

    public void changeSize(int width, int height) {
        if (mTextureView != null) {
            this.width = width;
            this.height = height;
            setLayoutParams(new FrameLayout.LayoutParams(width, height));
            mTextureView.setLayoutParams(new FrameLayout.LayoutParams(width, height, Gravity.CENTER));
            requestLayout();
        }
        if (mOnViewSizeChangedListener != null) {
            mOnViewSizeChangedListener.viewSizeChanged(width, height);
        }

    }
}
