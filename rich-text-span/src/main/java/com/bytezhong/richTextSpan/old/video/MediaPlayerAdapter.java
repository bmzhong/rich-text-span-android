package com.bytezhong.richTextSpan.old.video;

import android.content.Context;
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

public interface MediaPlayerAdapter {

    /**
     * Unspecified media player error.
     *
     * @see android.media.MediaPlayer.OnErrorListener
     */
    public static final int MEDIA_ERROR_UNKNOWN = 1;

    /**
     * Media server died. In this case, the application must release the
     * MediaPlayer object and instantiate a new one.
     *
     * @see android.media.MediaPlayer.OnErrorListener
     */
    public static final int MEDIA_ERROR_SERVER_DIED = 100;

    /**
     * The video is streamed and its container is not valid for progressive
     * playback i.e the video's index (e.g moov atom) is not at the start of the
     * file.
     *
     * @see android.media.MediaPlayer.OnErrorListener
     */
    public static final int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;

    /**
     * File or network related operation errors.
     */
    public static final int MEDIA_ERROR_IO = -1004;
    /**
     * Bitstream is not conforming to the related coding standard or file spec.
     */
    public static final int MEDIA_ERROR_MALFORMED = -1007;
    /**
     * Bitstream is conforming to the related coding standard or file spec, but
     * the media framework does not support the feature.
     */
    public static final int MEDIA_ERROR_UNSUPPORTED = -1010;
    /**
     * Some operation takes too long to complete, usually more than 3-5 seconds.
     */
    public static final int MEDIA_ERROR_TIMED_OUT = -110;


    void setSurface(Surface surface);

    void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException;

    void prepareAsync() throws IllegalStateException;
    void prepare() throws IOException, IllegalStateException;

    void stop() throws IllegalStateException;

    void release();

    void reset();

    void start() throws IllegalStateException;

    boolean isPlaying();

    void pause() throws IllegalStateException;

    int getDuration();

    int getCurrentPosition();

    void seekTo(int millisecond) throws IllegalStateException;

    int getVideoWidth();

    int getVideoHeight();

    void setOnPreparedListener(OnPreparedListener onPreparedListener);

    void setOnVideoSizeChangedListener(OnVideoSizeChangedListener onVideoSizeChangedListener);

    void setOnBufferingUpdateListener(OnBufferingUpdateListener onBufferingUpdateListener);

    void setOnCompletionListener(OnCompletionListener onCompletionListener);

    void setOnErrorListener(OnErrorListener onErrorListener);

    void setOnInfoListener(OnInfoListener onInfoListener);

}
