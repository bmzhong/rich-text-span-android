package com.bytezhong.richTestSpanDebug;

import android.annotation.SuppressLint;
import android.graphics.Color;
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

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.bytezhong.richTextSpan.RichTextSpan;
import com.bytezhong.richTextSpan.TextViewWrapper;
import com.bytezhong.richTextSpan.ViewAdapter;

public class DemoActivity1 extends AppCompatActivity {

    String text = "Hello Hello Hello Hello Hello Hello Hello Hello Hello" +
            "Hello Hello Hello Hello [video1] Hello Hello Hello" +
            "Hello Hello Hello Hello Hello Hello Hello Hello Hello" +
            "Hello Hello [lottie1] Hello Hello Hello Hello Hello Hello Hello" +
            "Hello Hello Hello Hello Hello Hello Hello Hello Hello" +
            "Hello Hello Hello Hello Hello Hello [audio1] Hello Hello Hello" +
            "Hello Hello Hello Hello Hello Hello [gif1] Hello Hello Hello" +
            "Hello Hello Hello Hello Hello Hello Hello Hello Hello" +
            "Hello Hello Hello Hello Hello Hello Hello Hello Hello";


    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_demo1);
        TextViewWrapper textViewWrapper = findViewById(R.id.textViewWrapper);
        SpannableString spannableString = new SpannableString(text);
        String placeHolderText = "[video1]";
        int start = text.indexOf(placeHolderText);
        int end = start + placeHolderText.length();
        ViewAdapter viewAdapter = new ViewAdapter() {
            String videoPath = "https://www.w3schools.com/html/mov_bbb.mp4";
            int videoWidth = 320*2; // 视频宽度
            int videoHeight = 176*2; // 视频高度
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
                videoView.setVideoPath(videoPath);
                videoView.setLayoutParams(new FrameLayout.LayoutParams(videoWidth, videoHeight));
                videoView.setOnCompletionListener((mp -> mp.start()));
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
        };
        RichTextSpan richTextSpan = new RichTextSpan(viewAdapter);
        textViewWrapper.addRichTextSpan(richTextSpan);
        spannableString.setSpan(richTextSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        placeHolderText = "[lottie1]";
        start = text.indexOf(placeHolderText);
        end = start + placeHolderText.length();
        ViewAdapter viewAdapter2 = new ViewAdapter() {
            int width=300;
            int height=300;
            @Override
            public Class getViewClass() {
                return LottieAnimationView.class;
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
                LottieAnimationView lottieAnimationView = (LottieAnimationView) view;
                lottieAnimationView.setAnimation(R.raw.bullseye);
                lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);
                lottieAnimationView.setLayoutParams(new FrameLayout.LayoutParams(width, height));
//                    lottieAnimationView.playAnimation();
            }

            @Override
            public void onPartiallyScrollIn(View view) {
                LottieAnimationView lottieAnimationView = (LottieAnimationView) view;
                lottieAnimationView.resumeAnimation();
            }

            @Override
            public void onFullyScrollOut(View view) {
                LottieAnimationView lottieAnimationView = (LottieAnimationView) view;
                lottieAnimationView.pauseAnimation();
            }
        };
        RichTextSpan richTextSpan2 = new RichTextSpan(viewAdapter2);
        textViewWrapper.addRichTextSpan(richTextSpan2);
        spannableString.setSpan(richTextSpan2, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        placeHolderText = "[audio1]";
        start = text.indexOf(placeHolderText);
        end = start + placeHolderText.length();
        ViewAdapter viewAdapter3 = new ViewAdapter() {
            int width=200;
            int height=100;
            @Override
            public Class getViewClass() {
                return Button.class;
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
                Button button = (Button) view;
                button.setBackgroundColor(Color.GRAY);
                button.setText("Audio");
                button.setAllCaps(false);
                button.setLayoutParams(new FrameLayout.LayoutParams(width, height));
            }
        };
        RichTextSpan richTextSpan3 = new RichTextSpan(viewAdapter3);
        textViewWrapper.addRichTextSpan(richTextSpan3);
        spannableString.setSpan(richTextSpan3, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        placeHolderText = "[gif1]";
        start = text.indexOf(placeHolderText);
        end = start + placeHolderText.length();
        ViewAdapter viewAdapter4 = new ViewAdapter() {
            String path  = "https://vfx.mtime.cn/Video/2019/07/12/mp4/190712140656051701.mp4";;
            int width=300;
            int height=160;
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
                videoView.setVideoPath(path);
                videoView.setLayoutParams(new FrameLayout.LayoutParams(width, height));
                videoView.setOnCompletionListener((mp -> mp.start()));
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
        };
        RichTextSpan richTextSpan4 = new RichTextSpan(viewAdapter4);
        textViewWrapper.addRichTextSpan(richTextSpan4);
        spannableString.setSpan(richTextSpan4, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewWrapper.getTextView().setText(spannableString, TextView.BufferType.SPANNABLE);

    }
}