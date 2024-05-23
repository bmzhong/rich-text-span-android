package com.bytezhong.richTextSpan.old.video;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Surface;

import com.bytezhong.richTextSpan.old.listener.OnBufferingUpdateListener;
import com.bytezhong.richTextSpan.old.listener.OnCompletionListener;
import com.bytezhong.richTextSpan.old.listener.OnErrorListener;
import com.bytezhong.richTextSpan.old.listener.OnInfoListener;
import com.bytezhong.richTextSpan.old.listener.OnPreparedListener;
import com.bytezhong.richTextSpan.old.listener.OnVideoSizeChangedListener;

import java.io.IOException;
import java.util.Map;

public class DefaultMediaPlayer implements MediaPlayerAdapter {

    private final MediaPlayer mMediaPlayer;

    private OnPreparedListener mOnPreparedListener;

    private OnVideoSizeChangedListener mOnVideoSizeChangedListener;

    private OnBufferingUpdateListener mOnBufferingUpdateListener;

    private OnCompletionListener mOnCompletionListener;

    private OnErrorListener mOnErrorListener;

    private OnInfoListener mOnInfoListener;

    public DefaultMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(preparedListener);
        mMediaPlayer.setOnVideoSizeChangedListener(videoSizeChangedListener);
        mMediaPlayer.setOnBufferingUpdateListener(bufferingUpdateListener);
        mMediaPlayer.setOnCompletionListener(completionListener);
        mMediaPlayer.setOnErrorListener(errorListener);
        mMediaPlayer.setOnInfoListener(infoListener);
    }

    @Override
    public void setSurface(Surface surface) {
        mMediaPlayer.setSurface(surface);
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException {
        mMediaPlayer.setDataSource(context, uri, headers);
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void prepare() throws IOException, IllegalStateException {
        mMediaPlayer.prepare();
    }


    @Override
    public void stop() throws IllegalStateException {
        mMediaPlayer.stop();
    }

    @Override
    public void release() {
        mMediaPlayer.release();
    }

    @Override
    public void reset() {
        mMediaPlayer.reset();
    }

    @Override
    public void start() throws IllegalStateException {
        mMediaPlayer.start();
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public void pause() throws IllegalStateException {
        mMediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int millisecond) throws IllegalStateException {
        mMediaPlayer.seekTo(millisecond);
    }

    @Override
    public int getVideoWidth() {
        return mMediaPlayer.getVideoWidth();
    }

    @Override
    public int getVideoHeight() {
        return mMediaPlayer.getVideoHeight();
    }

    MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(DefaultMediaPlayer.this);
            }
        }
    };

    MediaPlayer.OnVideoSizeChangedListener videoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            if (mOnVideoSizeChangedListener != null) {
                mOnVideoSizeChangedListener.onVideoSizeChanged(DefaultMediaPlayer.this, width, height);
            }
        }
    };

    MediaPlayer.OnBufferingUpdateListener bufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (mOnBufferingUpdateListener != null) {
                mOnBufferingUpdateListener.onBufferingUpdate(DefaultMediaPlayer.this, percent);
            }
        }
    };

    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion(DefaultMediaPlayer.this);
            }
        }
    };

    MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            if (mOnErrorListener != null) {
                return mOnErrorListener.onError(DefaultMediaPlayer.this, what, extra);
            }
            return false;
        }
    };

    MediaPlayer.OnInfoListener infoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (mOnInfoListener != null) {
                return mOnInfoListener.onInfo(DefaultMediaPlayer.this, what, extra);
            }
            return false;
        }
    };

    @Override
    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        mOnPreparedListener = onPreparedListener;
    }

    @Override
    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener onVideoSizeChangedListener) {
        mOnVideoSizeChangedListener = onVideoSizeChangedListener;
    }

    @Override
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener onBufferingUpdateListener) {
        mOnBufferingUpdateListener = onBufferingUpdateListener;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener onCompletionListener) {
        mOnCompletionListener = onCompletionListener;
    }

    @Override
    public void setOnErrorListener(OnErrorListener onErrorListener) {
        mOnErrorListener = onErrorListener;
    }

    @Override
    public void setOnInfoListener(OnInfoListener onInfoListener) {
        mOnInfoListener = onInfoListener;
    }
}
