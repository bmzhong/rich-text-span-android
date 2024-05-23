package com.bytezhong.richTestSpanDebug;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bytezhong.richTextSpan.RichTextSpan;
import com.bytezhong.richTextSpan.TextViewWrapper;
import com.bytezhong.richTextSpan.ViewAdapter;

public class MainActivity extends AppCompatActivity {
    private String videoPath = "https://www.w3schools.com/html/mov_bbb.mp4";
    private int width = 320; // 视频宽度
    private int height = 176; // 视频高度
    private String text = "Hello Hello Hello Hello Hello Hello Hello Hello Hello" +
            "Hello Hello Hello Hello Hello Hello: [video1]Hello Hello Hello" +
            "Hello";

    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        TextViewWrapper textViewWrapper = findViewById(R.id.textViewWrapper);
        SpannableString spannableString = new SpannableString(text);
        String placeHolderText = "[video1]";
        int start = text.indexOf(placeHolderText);
        int end = start + placeHolderText.length();

        ViewAdapter viewAdapter = new ViewAdapter() {
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
                VideoView videoView = (VideoView) view;
                videoView.setVideoPath(videoPath);
                videoView.setLayoutParams(new FrameLayout.LayoutParams(width, height));
                videoView.start();
            }
        };
        RichTextSpan richTextSpan = new RichTextSpan(viewAdapter);
        textViewWrapper.addRichTextSpan(richTextSpan);
        spannableString.setSpan(richTextSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewWrapper.getTextView().setText(spannableString, TextView.BufferType.SPANNABLE);

        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoViewScrollActivity.class);
                startActivity(intent);
            }
        });
        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoSizeChangeActivity.class);
                startActivity(intent);
            }
        });

        Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("hello");
                Intent intent = new Intent(MainActivity.this, DemoActivity1.class);
                startActivity(intent);
            }
        });
    }
}