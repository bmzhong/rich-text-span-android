package com.bytezhong.richTextSpanTest;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.Choreographer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.bytezhong.richTextSpan.RichTextSpan;
import com.bytezhong.richTextSpan.TextViewWrapper;
import com.bytezhong.richTextSpan.ViewAdapter;

import java.util.Collections;

public class VideoScrollFPSTestActivity extends AppCompatActivity {
    private static final String TAG = "VideoScrollFPSTestActivity";

    //    private String videoPath = "https://vfx.mtime.cn/Video/2019/07/12/mp4/190712140656051701.mp4";
    private String videoPath = "https://www.w3schools.com/html/mov_bbb.mp4";
    private int videoWidth = 320; // 视频宽度
    private int videoHeight = 176; // 视频高度
    private int textLength = 10000;
    private int videoNumber = 1;
    private String text;
    private int[] startIndices;
    private int[] endIndices;
    private int scrollCount;
    private float meanFPS;
    private long startMoveTime;
    private Choreographer.FrameCallback frameCallback;
    private int frameCount;
    ScrollView scrollView;
    private boolean frameCounting;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_scroll_fps_test);
        initText(videoNumber);
        TextViewWrapper textViewWrapper = findViewById(R.id.textViewWrapper1);
        SpannableString spannableString = new SpannableString(text);
        scrollView = findViewById(R.id.scrollView);
        System.out.println(TAG + " text length: " + spannableString.length());
        for (int i = 0; i < videoNumber; ++i) {

            ViewAdapter viewAdapter = new ViewAdapter() {
                @Override
                public Class getViewClass() {
                    return VideoView.class;
                }

                @Override
                public int getWidth() {
                    return videoWidth;
                }

                @Override
                public int getHeight() {
                    return videoHeight;
                }

                @Override
                public void onViewCreateCompleted(View view) {
                    VideoView videoView = (VideoView) view;
//                    String path = "android.resource://" + getPackageName() + "/" + R.raw.mov_bbb;
//                    videoView.setVideoURI(Uri.parse(path));
                    videoView.setVideoPath(videoPath);
                    videoView.setLayoutParams(new FrameLayout.LayoutParams(videoWidth, videoHeight));
                    videoView.setOnCompletionListener((mp -> mp.start()));
//                    videoView.start();
                }
                @Override
                public void onFullyScrollIn(View view) {
//                    System.out.println("onFullyScrollIn");
                    VideoView videoView = (VideoView) view;
                    videoView.start();
                }

                @Override
                public void onPartiallyScrollOut(View view) {
//                    System.out.println("onPartiallyScrollOut");
                    VideoView videoView = (VideoView) view;
                    videoView.pause();
                }
            };

            RichTextSpan richTextSpan = new RichTextSpan(viewAdapter);
            textViewWrapper.addRichTextSpan(richTextSpan);
            spannableString.setSpan(richTextSpan, startIndices[i], endIndices[i], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textViewWrapper.getTextView().setText(spannableString);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        startTrackingFrameRate();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        stopTrackingFrameRate();
                        break;
                }
                return false;
            }
        });

        frameCallback = new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                frameCount++;
                Choreographer.getInstance().postFrameCallback(this);
            }
        };
    }


    private void startTrackingFrameRate() {
        if (!frameCounting) {
            frameCounting = true;
            frameCount = 0;
            startMoveTime = System.nanoTime();
            Choreographer.getInstance().postFrameCallback(frameCallback);
        }
    }

    private void stopTrackingFrameRate() {
        if (frameCounting) {
            frameCounting = false;
            Choreographer.getInstance().removeFrameCallback(frameCallback);
            long endTime = System.nanoTime();
            long duration = endTime - startMoveTime;
            float fps = (frameCount * 1_000_000_000.0f) / duration;
            meanFPS = meanFPS + fps;
            scrollCount++;
            if (scrollCount == 10) {
                meanFPS = meanFPS / scrollCount;
                Log.e(TAG, "ScrollView scrolling FPS: " + meanFPS);
                System.out.println(TAG + " ScrollView scrolling FPS: " + meanFPS);
                meanFPS = 0F;
                scrollCount = 0;
            }

        }
    }

    public void initText(int videoNumber) {
        startIndices = new int[videoNumber];
        endIndices = new int[videoNumber];

        String baseText = String.join("", Collections.nCopies(videoNumber != 0 ? textLength / videoNumber : textLength, "R"));
        if (videoNumber == 0) {
            text = baseText;
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < videoNumber; ++i) {
            stringBuilder.append(baseText).append("[video").append(i).append("]");
        }
        stringBuilder.append(videoNumber);
        text = stringBuilder.toString();
        for (int i = 0; i < videoNumber; ++i) {
            String placeHolderText = "[video" + i + "]";
            int start = text.indexOf(placeHolderText);
            int end = start + placeHolderText.length();
            startIndices[i] = start;
            endIndices[i] = end;
        }
//        text = text + getString(R.string.long_text);
    }
}