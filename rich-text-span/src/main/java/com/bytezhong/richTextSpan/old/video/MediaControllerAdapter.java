package com.bytezhong.richTextSpan.old.video;

import android.view.View;

public interface MediaControllerAdapter {
    void show();

    void show(int timeout);

    void hide();

    boolean isShowing();

    void setMediaPlayerControl(MediaPlayerControl mediaPlayerControl);

    void setAnchorView(View view);

    void setEnabled(boolean enabled);
}
