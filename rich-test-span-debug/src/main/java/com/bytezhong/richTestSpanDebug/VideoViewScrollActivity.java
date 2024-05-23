package com.bytezhong.richTestSpanDebug;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bytezhong.richTextSpan.RichTextSpan;
import com.bytezhong.richTextSpan.TextViewWrapper;
import com.bytezhong.richTextSpan.ViewAdapter;

import java.util.Collections;

public class VideoViewScrollActivity extends AppCompatActivity {

    private static final String TAG = "VideoViewScrollActivity";

    private String videoPath = "https://www.w3schools.com/html/mov_bbb.mp4";
    private int width = 320; // 视频宽度
    private int height = 176; // 视频高度
    private int textLength = 1000;
    private int videoNumber = 1;

    private String text;
    private int[] startIndices;
    private int[] endIndices;
    public static int viewCount = 0;

    class ViewAdapterImpl implements ViewAdapter {


        @Override
        public Class getViewClass() {
            return VideoView.class;
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public void onViewCreateCompleted(View view) {
            viewCount++;
            System.out.println(TAG + " onViewCreateCompleted viewCount: " + viewCount);
            VideoView videoView = (VideoView) view;
            videoView.setVideoPath(videoPath);
            videoView.setLayoutParams(new FrameLayout.LayoutParams(width, height));
            videoView.start();
        }

        @Override
        public void onFullyScrollIn(View view) {
            VideoView videoView = (VideoView) view;
            videoView.start();
        }

        @Override
        public void onPartiallyScrollOut(View view) {
            VideoView videoView = (VideoView) view;
            videoView.pause();
        }
    }

    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_view_scroll);
        initText(videoNumber);
        TextViewWrapper textViewWrapper = findViewById(R.id.textViewWrapper);
        SpannableString spannableString = new SpannableString(text);
        for (int i = 0; i < videoNumber; ++i) {
            ViewAdapterImpl viewAdapter = new ViewAdapterImpl();
            RichTextSpan richTextSpan = new RichTextSpan(viewAdapter);
            textViewWrapper.addRichTextSpan(richTextSpan);
            spannableString.setSpan(richTextSpan, startIndices[i], endIndices[i], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textViewWrapper.getTextView().setText(spannableString);
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
            stringBuilder.append(baseText).append("video: ").append("[video").append(i).append("]");
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
        text = text + getString(R.string.long_text2);
    }
}