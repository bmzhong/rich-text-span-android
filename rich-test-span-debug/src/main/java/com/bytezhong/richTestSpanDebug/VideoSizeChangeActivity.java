package com.bytezhong.richTestSpanDebug;

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

import com.bytezhong.richTextSpan.OnViewSizeChangedListener;
import com.bytezhong.richTextSpan.RichTextSpan;
import com.bytezhong.richTextSpan.TextViewWrapper;
import com.bytezhong.richTextSpan.ViewAdapter;

public class VideoSizeChangeActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity2";

    private String videoPath = "https://www.w3schools.com/html/mov_bbb.mp4";
    private int width = 320; // 视频宽度
    private int height = 176; // 视频高度
    private String text = "Hello Hello Hello Hello Hello Hello Hello Hello Hello" +
            "Hello Hello Hello Hello Hello Hello: [video1]Hello Hello Hello" +
            "Hello";

    class ViewAdapterImpl implements ViewAdapter {

        public OnViewSizeChangedListener onViewSizeChangedListener;

        public View view;

        @Override
        public Class getViewClass() {
//                System.out.println(TAG + " getViewClass");
            return VideoView.class;
        }

        @Override
        public int getWidth() {
//                System.out.println(TAG + " getWidth");
            return width;
        }

        @Override
        public int getHeight() {
//                System.out.println(TAG + " getHeight");
            return height;
        }

        @Override
        public void onViewCreateCompleted(View view) {
            this.view = view;
            System.out.println(TAG + " onViewCreateCompleted");
            VideoView videoView = (VideoView) view;
            videoView.setVideoPath(videoPath);
            videoView.setLayoutParams(new FrameLayout.LayoutParams(width, height));
            videoView.start();
        }


        @Override
        public void onFullyScrollIn(View view) {
            System.out.println(TAG + " onFullyScrollIn");
            VideoView videoView = (VideoView) view;
            videoView.start();
        }

        @Override
        public void onPartiallyScrollOut(View view) {
            System.out.println(TAG + " onPartiallyScrollOut");
            VideoView videoView = (VideoView) view;
            videoView.pause();
        }

        @Override
        public void registerViewSizeChangeListener(View view, OnViewSizeChangedListener
                onViewSizeChangedListener) {
            this.onViewSizeChangedListener = onViewSizeChangedListener;
        }

        public void changedSize() {
            float ratio = 1.1F;
            width = (int) (width * ratio);
            height = (int) (height * ratio);
            view.setLayoutParams(new FrameLayout.LayoutParams(width, height));
            onViewSizeChangedListener.viewSizeChanged(width, height);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.video_size_changed);
        TextViewWrapper textViewWrapper = findViewById(R.id.textViewWrapper);
        SpannableString spannableString = new SpannableString(text);
        String placeHolderText = "[video1]";
        int start = text.indexOf(placeHolderText);
        int end = start + placeHolderText.length();

        ViewAdapterImpl viewAdapter = new ViewAdapterImpl();
        RichTextSpan richTextSpan = new RichTextSpan(viewAdapter);
        textViewWrapper.addRichTextSpan(richTextSpan);
        spannableString.setSpan(richTextSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewWrapper.getTextView().setText(spannableString, TextView.BufferType.SPANNABLE);


        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAdapter.changedSize();
            }
        });

    }
}