package com.bytezhong.richTextSpan.old.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bytezhong.richTextSpan.old.OnViewSizeChangedListener;
import com.bytezhong.richTextSpan.old.RichTextViewAdapter;
import com.bytezhong.richTextSpan.old.listener.OnBufferingUpdateListener;
import com.bytezhong.richTextSpan.old.listener.OnCompletionListener;
import com.bytezhong.richTextSpan.old.listener.OnErrorListener;
import com.bytezhong.richTextSpan.old.listener.OnInfoListener;
import com.bytezhong.richTextSpan.old.listener.OnPreparedListener;
import com.bytezhong.richTextSpan.old.listener.OnVideoSizeChangedListener;

import java.io.IOException;
import java.util.Map;

public class VideoView extends FrameLayout implements MediaPlayerControl, RichTextViewAdapter {

    private static final String TAG = "VideoView";

    // all possible internal states
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;
    private TextureView mTextureView;
    private ProgressBar progressBar;
    private float mProgressBarRelativeSize;
    private SurfaceTexture mSurfaceTexture;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private Uri mUri;
    private Map<String, String> mHeaders;
    private int mCurrentState;
    private int mTargetState;
    private int mSeekWhenPrepared;
    private int mCurrentBufferPercentage;
    private MediaPlayerAdapter mMediaPlayerAdapter;
    private MediaControllerAdapter mMediaControllerAdapter;
    private OnCompletionListener mOnCompletionListener;
    private OnErrorListener mOnErrorListener;
    private OnInfoListener mOnInfoListener;
    private OnPreparedListener mOnPreparedListener;
    private OnVideoSizeChangedListener mOnVideoSizeChangedListener;
    private OnViewSizeChangedListener mOnViewSizeChangeListener;

    public VideoView(@NonNull Context context) {
        this(context, null);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        // unchecked
        mVideoWidth = 0;
        mVideoHeight = 0;
        mProgressBarRelativeSize = 0.25F;
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();

        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;

        ViewGroup.LayoutParams currentLayoutParams = getLayoutParams();

        if (currentLayoutParams == null) {
            currentLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        setLayoutParams(currentLayoutParams);
        setBackgroundColor(Color.BLACK);

        mTextureView = new TextureView(getContext());
        FrameLayout.LayoutParams textureViewLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        addView(mTextureView, textureViewLayoutParams);
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        FrameLayout.LayoutParams progressBarLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        progressBar = new ProgressBar(getContext());

        addView(progressBar, progressBarLayoutParams);
    }

    public void setVideoSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;

//        setLayoutParams(new ViewGroup.LayoutParams(width, height));
    }

    private void openVideo() {
        if (mUri == null || mSurfaceTexture == null) {
            return;
        }
        release(false);
        initMediaPlayer();
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        mMediaPlayerAdapter.setSurface(new Surface(mSurfaceTexture));
        mCurrentBufferPercentage = 0;

        try {
            mMediaPlayerAdapter.setDataSource(getContext(), mUri, mHeaders);
            mMediaPlayerAdapter.prepareAsync();
            mCurrentState = STATE_PREPARING;
        } catch (IOException | IllegalStateException exception) {
            Log.w(TAG, "Unable to open content: " + mUri, exception);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayerAdapter, MediaPlayerAdapter.MEDIA_ERROR_UNKNOWN, 0);
        }
    }

    private void initMediaPlayer() {
        if (mMediaPlayerAdapter == null) {
            mMediaPlayerAdapter = new DefaultMediaPlayer();
        }
        if (mMediaControllerAdapter == null) {
            mMediaControllerAdapter = new DefaultMediaPlayerController(getContext());
        }
        if (progressBar != null) {
            progressBar.setLayoutParams(new FrameLayout.LayoutParams((int) (mVideoHeight * mProgressBarRelativeSize), (int) (mVideoWidth * mProgressBarRelativeSize), Gravity.CENTER));
        }
        attachMediaController();
        mMediaPlayerAdapter.setOnPreparedListener(mPreparedListener);
        mMediaPlayerAdapter.setOnVideoSizeChangedListener(mVideoSizeChangedListener);
        mMediaPlayerAdapter.setOnCompletionListener(mCompletionListener);
        mMediaPlayerAdapter.setOnErrorListener(mErrorListener);
        mMediaPlayerAdapter.setOnInfoListener(mInfoListener);
        mMediaPlayerAdapter.setOnBufferingUpdateListener(mBufferingUpdateListener);
    }

    public void setMediaPlayerAdapter(MediaPlayerAdapter mediaPlayerAdapter) {
        mMediaPlayerAdapter = mediaPlayerAdapter;
        initMediaPlayer();
    }

    public void setMediaControllerAdapter(MediaControllerAdapter mediaControllerAdapter) {
        if (mediaControllerAdapter != null) {
            mMediaControllerAdapter.hide();
        }
        mMediaControllerAdapter = mediaControllerAdapter;
        attachMediaController();
    }

    public float getProgressBarRelativeSize() {
        return mProgressBarRelativeSize;
    }

    public void setProgressBarRelativeSize(float progressBarRelativeSize) {
        mProgressBarRelativeSize = progressBarRelativeSize;
    }

    private void attachMediaController() {
        if (mMediaControllerAdapter != null) {
            mMediaControllerAdapter.setMediaPlayerControl(this);
            mMediaControllerAdapter.setAnchorView(this);
            mMediaControllerAdapter.setEnabled(isInPlaybackState());
        }
    }

    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        setVideoURI(uri, null);
    }

    public void setVideoURI(Uri uri, Map<String, String> headers) {
        mUri = uri;
        mHeaders = headers;
        mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return VideoView.class.getName();
    }

    public void stopPlayback() {
        if (mMediaPlayerAdapter != null) {
            mMediaPlayerAdapter.stop();
            mMediaPlayerAdapter.release();
            mMediaPlayerAdapter = null;
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
        }
    }


    private boolean isInPlaybackState() {
        return (mMediaPlayerAdapter != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }

    private void release(boolean isClearTargetState) {
        if (mMediaPlayerAdapter != null) {
            mMediaPlayerAdapter.reset();
            mMediaPlayerAdapter.release();
            mMediaPlayerAdapter = null;
            mCurrentState = STATE_IDLE;
            if (isClearTargetState) {
                mTargetState = STATE_IDLE;
            }
        }
    }

    public void setOnCompletionListener(OnCompletionListener onCompletionListener) {
        mOnCompletionListener = onCompletionListener;
    }

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        mOnErrorListener = onErrorListener;
    }

    public void setOnInfoListener(OnInfoListener onInfoListener) {
        mOnInfoListener = onInfoListener;
    }

    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        mOnPreparedListener = onPreparedListener;
    }

    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener onVideoSizeChangedListener) {
        mOnVideoSizeChangedListener = onVideoSizeChangedListener;
    }

    @Override
    public void setViewSizeChangedListener(OnViewSizeChangedListener onViewSizeChangedListener) {
        mOnViewSizeChangeListener = onViewSizeChangedListener;
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

    private void toggleMediaControlVisibility() {
        if (mMediaControllerAdapter.isShowing()) {
            mMediaControllerAdapter.hide();
        } else {
            mMediaControllerAdapter.show();
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && isInPlaybackState() && mMediaControllerAdapter != null) {
            toggleMediaControlVisibility();
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && isInPlaybackState() && mMediaControllerAdapter != null) {
            toggleMediaControlVisibility();
        }
        return super.onTrackballEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK &&
                keyCode != KeyEvent.KEYCODE_VOLUME_UP &&
                keyCode != KeyEvent.KEYCODE_VOLUME_DOWN &&
                keyCode != KeyEvent.KEYCODE_VOLUME_MUTE &&
                keyCode != KeyEvent.KEYCODE_MENU &&
                keyCode != KeyEvent.KEYCODE_CALL &&
                keyCode != KeyEvent.KEYCODE_ENDCALL;
        if (isInPlaybackState() && isKeyCodeSupported && mMediaControllerAdapter != null) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK ||
                    keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                if (mMediaPlayerAdapter.isPlaying()) {
                    pause();
                    mMediaControllerAdapter.show();
                } else {
                    start();
                    mMediaControllerAdapter.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                if (!mMediaPlayerAdapter.isPlaying()) {
                    start();
                    mMediaControllerAdapter.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                    || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                if (mMediaPlayerAdapter.isPlaying()) {
                    pause();
                    mMediaControllerAdapter.show();
                }
                return true;
            } else {
                toggleMediaControlVisibility();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
            mMediaPlayerAdapter.start();
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayerAdapter.isPlaying()) {
                mMediaPlayerAdapter.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;
    }

    public void suspend() {
        release(false);
    }

    public void resume() {
        openVideo();
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            return mMediaPlayerAdapter.getDuration();
        }
        return -1;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return mMediaPlayerAdapter.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int millisecond) {
        if (isInPlaybackState()) {
            mMediaPlayerAdapter.seekTo(millisecond);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = millisecond;
        }
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayerAdapter.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        if (mMediaPlayerAdapter != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            mSurfaceTexture = surface;
            mSurfaceWidth = width;
            mSurfaceHeight = height;
            openVideo();
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
            mSurfaceWidth = width;
            mSurfaceHeight = height;
            if (surface != mSurfaceTexture) {
                mSurfaceTexture = surface;
                mMediaPlayerAdapter.setSurface(new Surface(surface));
            }

            boolean isValidState = (mTargetState == STATE_PLAYING);

            if (mMediaPlayerAdapter != null && isValidState) {
                if (mSeekWhenPrepared != 0) {
                    seekTo(mSeekWhenPrepared);
                }
                start();
            }
            if (mOnViewSizeChangeListener != null) {
                mOnViewSizeChangeListener.viewSizeChanged(getWidth(), getHeight());
            }
        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            mSurfaceTexture = null;
            if (mMediaControllerAdapter != null) {
                mMediaControllerAdapter.hide();
            }
            release(true);
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

        }
    };

    OnVideoSizeChangedListener mVideoSizeChangedListener = new OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayerAdapter mediaPlayerAdapter, int width, int height) {
            if (mOnVideoSizeChangedListener != null) {
                mOnVideoSizeChangedListener.onVideoSizeChanged(mediaPlayerAdapter, width, height);
            }
        }
    };

    OnPreparedListener mPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayerAdapter mediaPlayerAdapter) {
//            mOnViewSizeChangeListener.viewSizeChanged(mVideoWidth*2,mVideoHeight*2);
//            mVideoWidth = mVideoWidth*2;
//            mVideoHeight = mVideoHeight*2;

            mCurrentState = STATE_PREPARED;
            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mediaPlayerAdapter);
            }
            if (mMediaControllerAdapter != null) {
                mMediaControllerAdapter.setEnabled(true);
            }
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            if (mMediaControllerAdapter != null) {
                mMediaControllerAdapter.show();
            }
            int videoWidth = mediaPlayerAdapter.getVideoWidth();
            int videoHeight = mediaPlayerAdapter.getVideoHeight();
            int seekToPosition = mSeekWhenPrepared;
            if (seekToPosition >= 0) {
                seekTo(seekToPosition);
            }
            if (videoWidth != 0 && videoHeight != 0) {
                // unchecked
                if (mVideoWidth == 0 && mVideoHeight == 0) {
                    mVideoWidth = videoWidth;
                    mVideoHeight = videoHeight;
                }
                mTextureView.setLayoutParams(new FrameLayout.LayoutParams(mVideoWidth, mVideoHeight, Gravity.CENTER));
                mTextureView.requestLayout();

                if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
                    if (mTargetState == STATE_PLAYING) {
                        start();
                    } else if (!isPlaying() && (seekToPosition != 0 || getCurrentPosition() > 0)) {
                        if (mMediaControllerAdapter != null) {
                            mMediaControllerAdapter.show(0);
                        }
                    }
                }
            } else {
                if (mTargetState == STATE_PLAYING) {
                    start();
                }
            }
        }
    };

    OnCompletionListener mCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayerAdapter mediaPlayerAdapter) {
            mCurrentState = STATE_PLAYBACK_COMPLETED;
            mTargetState = STATE_PLAYBACK_COMPLETED;
            if (mMediaControllerAdapter != null) {
                mMediaControllerAdapter.show();
            }
            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion(mediaPlayerAdapter);
            }
        }
    };

    OnInfoListener mInfoListener = new OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayerAdapter mediaPlayerAdapter, int what, int extra) {
            if (mOnInfoListener != null) {
                mOnInfoListener.onInfo(mediaPlayerAdapter, what, extra);
            }
            return true;
        }
    };

    OnErrorListener mErrorListener = new OnErrorListener() {
        @Override
        public boolean onError(MediaPlayerAdapter mediaPlayerAdapter, int what, int extra) {
            Log.d(TAG, "Error: " + what + "," + extra);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            if (mMediaControllerAdapter != null) {
                mMediaControllerAdapter.hide();
            }
            if (mOnErrorListener != null) {
                if (mOnErrorListener.onError(mMediaPlayerAdapter, what, extra)) {
                    return true;
                }
            }
            return true;
        }
    };

    OnBufferingUpdateListener mBufferingUpdateListener = new OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayerAdapter mediaPlayerAdapter, int percent) {
            mCurrentBufferPercentage = percent;
        }
    };


    @Override
    public int getViewWidth() {
        return mVideoWidth;
    }

    @Override
    public int getViewHeight() {
        return mVideoHeight;
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
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
    }

    public void changeSize(int width, int height) {
        if (mTextureView != null) {
            mTextureView.setLayoutParams(new FrameLayout.LayoutParams(width, height, Gravity.CENTER));
        }
        requestLayout();
        mVideoWidth = width;
        mVideoHeight = height;
        if (mOnViewSizeChangeListener != null) {
            mOnViewSizeChangeListener.viewSizeChanged(mVideoWidth, mVideoHeight);
        }
    }

}
