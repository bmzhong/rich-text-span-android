package com.bytezhong.richTextSpanTest;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.bytezhong.richTextSpan.RichTextSpan;
import com.bytezhong.richTextSpan.TextViewWrapper;
import com.bytezhong.richTextSpan.ViewAdapter;

import java.util.Collections;

public class VideoLoadTimeTestActivity extends AppCompatActivity {

    private static final String TAG = "VideoActivity1";

    //    private String videoPath = "https://vfx.mtime.cn/Video/2019/07/12/mp4/190712140656051701.mp4";
    private String videoPath = "https://www.w3schools.com/html/mov_bbb.mp4";
    private int videoWidth = 320; // 视频宽度
    private int videoHeight = 176; // 视频高度
    private int textLength = 1000;
    private int videoNumber = 100;
    private String text;
    private int[] startIndices;
    private int[] endIndices;
    private long preDrawTime;
    private long beforeNewViewTime;
    private long newVideoAdapter;
    private long newRichTextSpanTime;


    @Override
    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_load_time_test);
        initText(videoNumber);
        TextViewWrapper textViewWrapper = findViewById(R.id.textViewWrapper1);
        SpannableString spannableString = new SpannableString(text);
        System.out.println(TAG + " text length: " + spannableString.length());
        beforeNewViewTime = System.currentTimeMillis();


        for (int i = 0; i < videoNumber; ++i) {

            long before = System.currentTimeMillis();
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


            newVideoAdapter += System.currentTimeMillis() - before;

            before = System.currentTimeMillis();
            RichTextSpan richTextSpan = new RichTextSpan(viewAdapter);
            textViewWrapper.addRichTextSpan(richTextSpan);
            newRichTextSpanTime += System.currentTimeMillis() - before;
            spannableString.setSpan(richTextSpan, startIndices[i], endIndices[i], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textViewWrapper.getTextView().setText(spannableString);
        textViewWrapper.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                preDrawTime = System.currentTimeMillis();
                long duration = preDrawTime - beforeNewViewTime;
                Log.e(TAG, "Construct, Measure, Layout Time " + duration + " ms");
                System.out.println(TAG + " Construct, Measure, Layout Time " + duration + " ms");
                textViewWrapper.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
        System.out.println(TAG + " newVideoAdapter: " + newVideoAdapter + " ms");
        System.out.println(TAG + " newRichTextSpanTime: " + newRichTextSpanTime + " ms");
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