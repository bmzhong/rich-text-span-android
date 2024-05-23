/*
 * Copyright (c) 2016. Roberto  Prato <https://github.com/robertoprato>
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

package com.bytezhong.richTextSpan.old.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.style.ReplacementSpan;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.bytezhong.richTextSpan.old.util.Size;
import com.bytezhong.richTextSpan.old.video.RichVideoView;

public class VideoPlayerSpan extends ReplacementSpan implements Parcelable, RichVideoView.RichVideoViewListener {

    public static final Creator<VideoPlayerSpan> CREATOR = new Creator<VideoPlayerSpan>() {
        @Override
        public VideoPlayerSpan createFromParcel(Parcel source) {
            String videoPath = source.readString();
            int verticalAlignment = source.readInt();
            Size size = source.readParcelable(Size.class.getClassLoader());
            return new VideoPlayerSpan(videoPath, size.getWidth(), size.getHeight(), verticalAlignment);
        }

        @Override
        public VideoPlayerSpan[] newArray(int size) {
            return new VideoPlayerSpan[size];
        }
    };


    /**
     * A constant indicating that the bottom of this span should be aligned
     * with the bottom of the surrounding text, i.e., at the same level as the
     * lowest descender in the text.
     */
    public static final int ALIGN_BOTTOM = 0;

    /**
     * A constant indicating that the bottom of this span should be aligned
     * with the baseline of the surrounding text.
     */
    public static final int ALIGN_BASELINE = 1;

    /**
     * A constant indicating that this span should be vertically centered between
     * the top and the lowest descender.
     */
    public static final int ALIGN_CENTER = 2;

    protected final int mVerticalAlignment;

    String mVideoPath;
    Size mVideoSize;
    RichVideoView mVideoPlayer;

    boolean mSizeHasChanged;

    private Rect mRect;

    public VideoPlayerSpan() {
        mVerticalAlignment = ALIGN_BOTTOM;
    }

    public VideoPlayerSpan(String video, int imageWidth, int imageHeight) {
        this(video, imageWidth, imageHeight, ALIGN_BOTTOM);
    }

    public VideoPlayerSpan(String videoPath, int imageWidth, int imageHeight, int alignment) {
        super();
        mSizeHasChanged = true;
        mVideoPath = videoPath;
        mVideoSize = new Size(imageWidth, imageHeight);
        mVerticalAlignment = alignment;
    }

    public VideoPlayerSpan(ViewGroup parent, String videoPath, int imageWidth, int imageHeight, int alignment) {
        this(videoPath, imageWidth, imageHeight, alignment);
        ensureParentIsFrameLayout(parent);
        RichVideoView richVideoView = new RichVideoView(parent.getContext());
        richVideoView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        onSpannedSetToView(richVideoView);

        parent.addView(richVideoView);
    }

    public VideoPlayerSpan(ViewGroup parent, String videoPath, int imageWidth, int imageHeight) {
        this(parent, videoPath, imageWidth, imageHeight, ALIGN_BOTTOM);
    }

    public void ensureParentIsFrameLayout(ViewGroup parent){
        if (!(parent instanceof FrameLayout)){
            throw new RuntimeException("The parent view of RichVideoView must be an instance of FrameLayout");
        }
    }

    public void onSpannedSetToView(RichVideoView view) {
        mVideoPlayer = view;
        mVideoPlayer.setData(mVideoPath);
    }


    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mVideoPath);
        dest.writeInt(mVerticalAlignment);
        dest.writeParcelable(mVideoSize, 0);
    }

    private Rect getVideoPlayerBounds() {

        final Size videoSize = mVideoPlayer.getVideoSize();

        if (videoSize == null) {
            return new Rect(0, 0, mVideoSize.getWidth(), mVideoSize.getHeight());
        } else {// we have the size for the video
            return new Rect(0, 0, videoSize.getWidth(), videoSize.getHeight());
        }
    }

    @Override
    public int getSize(Paint paint, CharSequence text,
                       int start, int end,
                       Paint.FontMetricsInt fm) {

        if (mRect == null) {
            mRect = getVideoPlayerBounds();
        }


        if (fm != null) {
            fm.ascent = -mRect.bottom;
            fm.descent = 0;

            fm.top = fm.ascent;
            fm.bottom = 0;
        }

        return mRect.right;
    }


    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x,
                     int top, int y, int bottom, Paint paint) {

        if (mVideoPlayer == null) return;

        final Rect videoPlayerBounds = getVideoPlayerBounds();

        int transY = bottom - videoPlayerBounds.bottom;
        if (mVerticalAlignment == ALIGN_BASELINE) {
            transY -= paint.getFontMetricsInt().descent;
        } else if (mVerticalAlignment == ALIGN_CENTER) {
            transY = top + (bottom - top) / 2 - videoPlayerBounds.bottom / 2;
        }

        if (mSizeHasChanged == true) {

            FrameLayout.LayoutParams current = (FrameLayout.LayoutParams) mVideoPlayer.getLayoutParams();
            FrameLayout.LayoutParams newParams = generateDefaultLayoutParams(new Point((int) x, transY),
                    videoPlayerBounds.width(),
                    videoPlayerBounds.height());

            if (areLayoutParamsDifferent(current, newParams)) {
                mVideoPlayer.setLayoutParams(newParams);
                mVideoPlayer.requestLayout();
            }

            mSizeHasChanged = false;
        }

    }

    public static boolean areLayoutParamsDifferent(FrameLayout.LayoutParams params1, FrameLayout.LayoutParams params2) {

        if (params1 == null && params2 != null) return true;
        if (params2 == null && params1 != null) return true;

        if (params1.height != params2.height) return true;
        if (params1.width != params2.width) return true;

        if (params1.leftMargin != params2.leftMargin) return true;
        if (params1.topMargin != params2.topMargin) return true;

        return false;
    }

    public static FrameLayout.LayoutParams generateDefaultLayoutParams(Point position, int width, int height) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        params.leftMargin = position.x;
        params.topMargin = position.y;

        return params;
    }

    @Override
    public void onVideoReady(RichVideoView videoView) {

    }

    @Override
    public void onVideoSizeAvailable(RichVideoView videoView) {

        mVideoSize = videoView.getVideoSize();
        mSizeHasChanged = true;
        Rect newRect = getVideoPlayerBounds();

        boolean needsLayout = (newRect.equals(mRect) == false);

        if (needsLayout) {
            videoView.requestLayout();
        } else {
            videoView.invalidate();
        }
    }

}
