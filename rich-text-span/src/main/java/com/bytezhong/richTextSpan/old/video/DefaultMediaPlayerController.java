package com.bytezhong.richTextSpan.old.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bytezhong.richTextSpan.R;

public class DefaultMediaPlayerController extends FrameLayout implements MediaControllerAdapter {

    private ImageButton mPlayButton;
    private ImageButton mPauseButton;
    private ImageButton mFullScreenButton;
    private MediaPlayerControl mMediaPlayerControl;
    private int mDefaultTimeout;
    private boolean mShowing;
    private float mButtonRelativeSize;
    private ViewGroup mAnchor;

    public DefaultMediaPlayerController(@NonNull Context context) {
        this(context, null);
    }

    public DefaultMediaPlayerController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultMediaPlayerController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
        init();
    }

    public DefaultMediaPlayerController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mDefaultTimeout = 5000;
        mButtonRelativeSize = 0.3F;
        FrameLayout controlContainer = (FrameLayout) LayoutInflater.from(getContext())
                .inflate(R.layout.internal_richtext_video_controller,
                        this, false);
        mPlayButton = controlContainer.findViewById(R.id.play);
        mPauseButton = controlContainer.findViewById(R.id.pause);
        mFullScreenButton = controlContainer.findViewById(R.id.full_screen);
        mFullScreenButton.setVisibility(View.GONE);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        setLayoutParams(params);
        setVisibility(View.INVISIBLE);
        controlContainer.removeAllViews();
        addView(mPlayButton);
        addView(mPauseButton);
        addView(mFullScreenButton);
        mPlayButton.setOnClickListener(clickListener);
        mPauseButton.setOnClickListener(clickListener);
        mFullScreenButton.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == mPlayButton.getId()) {
                mMediaPlayerControl.start();
            } else if (v.getId() == mPauseButton.getId()) {
                if (mMediaPlayerControl.isPlaying()) {
                    mMediaPlayerControl.pause();
                }
            } else if (v.getId() == R.id.full_screen) {
                // todo
            }

            updateControls();
        }
    };

    public MediaPlayerControl getMediaPlayerControl() {
        return mMediaPlayerControl;
    }

    public int getDefaultTimeout() {
        return mDefaultTimeout;
    }

    public void setDefaultTimeout(int defaultTimeout) {
        mDefaultTimeout = defaultTimeout;
    }

    public float getButtonRelativeSize() {
        return mButtonRelativeSize;
    }

    public void setButtonRelativeSize(float buttonRelativeSize) {
        mButtonRelativeSize = buttonRelativeSize;
    }

    public void setMediaPlayerControl(MediaPlayerControl mediaPlayerControl) {
        mMediaPlayerControl = mediaPlayerControl;
    }

    @Override
    public void setAnchorView(View view) {
        mAnchor = view instanceof ViewGroup ? (ViewGroup) view : (ViewGroup) view.getParent();
        ViewGroup.LayoutParams layoutParams = mAnchor.getLayoutParams();
        if (layoutParams != null && layoutParams.width > 0 && layoutParams.height > 0) {
            int buttonLength = (int) (Math.min(layoutParams.width, layoutParams.height) * mButtonRelativeSize);
            mPauseButton.setLayoutParams(new FrameLayout.LayoutParams(buttonLength, buttonLength, Gravity.CENTER));
            mPlayButton.setLayoutParams(new FrameLayout.LayoutParams(buttonLength, buttonLength, Gravity.CENTER));
            mFullScreenButton.setLayoutParams(new FrameLayout.LayoutParams(buttonLength, buttonLength, Gravity.RIGHT | Gravity.TOP));
        }
        mAnchor.addView(this);
    }


    protected void updateControls() {
        mPauseButton.setVisibility(mMediaPlayerControl.isPlaying() ? View.VISIBLE : View.GONE);
        mPlayButton.setVisibility(!mMediaPlayerControl.isPlaying() ? View.VISIBLE : View.GONE);
    }

    private final Runnable mFadeOut = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    public void show(int timeout) {
        if (!mShowing) {
            setVisibility(View.VISIBLE);
        }
        updateControls();
        removeCallbacks(mFadeOut);
        postDelayed(mFadeOut, timeout);
        mShowing = true;
    }

    @Override
    public void show() {
        show(mDefaultTimeout);
    }

    @Override
    public void hide() {
        if (mAnchor == null) {
            return;
        }
        if (mShowing) {
            setVisibility(View.INVISIBLE);
            mShowing = false;
        }
    }

    @Override
    public boolean isShowing() {
        return mShowing;
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return DefaultMediaPlayerController.class.getName();
    }
}
