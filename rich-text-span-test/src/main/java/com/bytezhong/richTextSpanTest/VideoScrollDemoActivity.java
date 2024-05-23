package com.bytezhong.richTextSpanTest;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.bytezhong.richTextSpan.RichTextSpan;
import com.bytezhong.richTextSpan.TextViewWrapper;
import com.bytezhong.richTextSpan.ViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideoScrollDemoActivity extends AppCompatActivity {

    private static final String TAG = "VideoScrollDemoActivity";
    private String videoPath = "https://www.w3schools.com/html/mov_bbb.mp4";
    private int videoWidth = 320; // 视频宽度
    private int videoHeight = 176; // 视频高度
    private int textLength = 10000;
    private int videoNumber = 500;

    private String text;
    private int[] startIndices;
    private int[] endIndices;


    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_scroll_demo);
        initText(videoNumber);
        TextViewWrapper textViewWrapper = findViewById(R.id.textViewWrapper1);
        SpannableString spannableString = new SpannableString(text);
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
                    String path = "android.resource://" + getPackageName() + "/" + R.raw.mov_bbb;
                    videoView.setVideoURI(Uri.parse(path));
//                    videoView.setVideoPath(videoPath);
                    videoView.setLayoutParams(new FrameLayout.LayoutParams(videoWidth, videoHeight));
                    videoView.setOnCompletionListener((mp -> mp.start()));
//                    videoView.start();
                }
                @Override
                public void onPartiallyScrollIn(View view) {
                    VideoView videoView = (VideoView) view;
                    videoView.start();
                }

                @Override
                public void onFullyScrollOut(View view) {
                    VideoView videoView = (VideoView) view;
                    videoView.pause();
                }
            };

            RichTextSpan richTextSpan = new RichTextSpan(viewAdapter);
            textViewWrapper.addRichTextSpan(richTextSpan);
            spannableString.setSpan(richTextSpan, startIndices[i], endIndices[i], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        textViewWrapper.getTextView().setText(spannableString);

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
        text = text + getString(R.string.long_text);
    }
}