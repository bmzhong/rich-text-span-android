package com.bytezhong.richTextSpanTest;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bytezhong.richTextSpan.old.RichTextSpan;
import com.bytezhong.richTextSpan.old.TextViewWrapper;
import com.bytezhong.richTextSpan.old.video.VideoView;

public class VideoSizeChangeDemoActivity extends AppCompatActivity {
    private String videoPath = "https://www.w3schools.com/html/mov_bbb.mp4";
    private int width = 320; // 视频宽度
    private int height = 176; // 视频高度

    private String text = "HelloHelloHelloHelloHello" +
            "HelloHelloHelloHelloHelloHello这是一个视频: [video1]HelloHelloHello" +
            "Hello";


    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_size_change_demo);
        TextViewWrapper textViewWrapper = findViewById(R.id.textViewWrapper);
        SpannableString spannableString = new SpannableString(text);
        String placeHolderText = "[video1]";
        int start = text.indexOf(placeHolderText);
        int end = start + placeHolderText.length();
        VideoView videoView = new VideoView(this);
        videoView.setVideoSize(width, height);
        videoView.setVideoPath(videoPath);
        videoView.setOnCompletionListener((mp -> mp.start()));

        RichTextSpan richTextSpan = new RichTextSpan(textViewWrapper, videoView);
        spannableString.setSpan(richTextSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewWrapper.setText(spannableString);

        Button button0 = findViewById(R.id.button0);
        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float ratio = 1.1F;
                width = (int) (width * ratio);
                height = (int) (height * ratio);
                videoView.changeSize(width, height);
            }
        });
    }
}