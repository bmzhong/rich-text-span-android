package com.bytezhong.richTextSpan.old.video;

public interface MediaPlayerControl {
    void start();

    void pause();

    int getDuration();

    int getCurrentPosition();

    void seekTo(int millisecond);

    boolean isPlaying();

    int getBufferPercentage();

}
