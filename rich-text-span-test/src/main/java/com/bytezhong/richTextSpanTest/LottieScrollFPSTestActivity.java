package com.bytezhong.richTextSpanTest;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.Choreographer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.bytezhong.richTextSpan.RichTextSpan;
import com.bytezhong.richTextSpan.TextViewWrapper;
import com.bytezhong.richTextSpan.ViewAdapter;

import java.util.Collections;

public class LottieScrollFPSTestActivity extends AppCompatActivity {

    private static final String TAG = "LottieScrollFPSTestActivity";
    private int width = 200;
    private int height = 200;
    private int textLength = 10000;
    private int lottieNumber = 1;
    private int[] startIndices;
    private int[] endIndices;
    private int[] lottieIndices;
    private int[] lottieResources = {R.raw.bullseye, R.raw.hamburger_arrow, R.raw.heart, R.raw.lottielogo, R.raw.walkthrough};
    private String text;
    private long startMoveTime;
    private Choreographer.FrameCallback frameCallback;
    private int frameCount;
    ScrollView scrollView;
    private boolean frameCounting;
    private int scrollCount;
    private float meanFPS;

    private static int playCount = 0;

    private static int scrollIn = 0;
    private static int scrollOut = 0;


    @Override
    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        initText(lottieNumber);
        setContentView(R.layout.activity_lottie_scroll_fps_test);
        TextViewWrapper textViewWrapper = findViewById(R.id.textViewWrapper2);
        SpannableString spannableString = new SpannableString(text);
        System.out.println(TAG + " text length: " + spannableString.length());
        scrollView = findViewById(R.id.scrollView);

        for (int i = 0; i < lottieNumber; ++i) {
            int a = i;
            ViewAdapter viewAdapter = new ViewAdapter() {

                int index = a;

                boolean start = false;

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
//                    start = true;
//                    playCount++;
//                    System.out.println("onViewCreateCompleted playCount: "+playCount+" "+index);
                }

                @Override
                public void onPartiallyScrollIn(View view) {
                    LottieAnimationView lottieAnimationView = (LottieAnimationView) view;
//                    System.out.println(lottieAnimationView.isAnimating());
                    scrollIn++;
//                    if (!start) {
                        playCount++;
//                        start = true;
                        lottieAnimationView.resumeAnimation();
//                        System.out.println("onPartiallyScrollIn playCount: " + playCount + " scrollIn" + scrollIn);
//                    }
//                    System.out.println("onPartiallyScrollIn playCount: " + playCount + " scrollIn " + scrollIn);
                }

                @Override
                public void onFullyScrollOut(View view) {
                    LottieAnimationView lottieAnimationView = (LottieAnimationView) view;
                    scrollOut++;
//                    if (start) {
                        start = false;
                        lottieAnimationView.pauseAnimation();
                        playCount--;
//                        System.out.println("onFullyScrollOut playCount: " + playCount + " scrollOut" + scrollOut);
//                    }
//                    System.out.println("onFullyScrollOut playCount: " + playCount + " scrollOut " + scrollOut);
                }
            };

            RichTextSpan richTextSpan = new RichTextSpan(viewAdapter);
            textViewWrapper.addRichTextSpan(richTextSpan);
            spannableString.setSpan(richTextSpan, startIndices[i], endIndices[i], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textViewWrapper.getTextView().setText(spannableString);

        frameCallback = new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                frameCount++;
                Choreographer.getInstance().postFrameCallback(this);
            }
        };

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

    public void initText(int lottieNumber) {
        startIndices = new int[lottieNumber];
        endIndices = new int[lottieNumber];
        lottieIndices = new int[lottieNumber];
        String baseText = String.join("", Collections.nCopies(lottieNumber != 0 ? textLength / lottieNumber : textLength, "R"));
        if (lottieNumber == 0) {
            text = baseText;
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < lottieNumber; ++i) {
            stringBuilder.append(baseText).append(i).append("：[lottie").append(i).append("]");
        }
        stringBuilder.append(lottieNumber);

        text = stringBuilder.toString();
//        text = getString(R.string.long_text2) + text;
        for (int i = 0; i < lottieNumber; ++i) {
            String placeHolderText = "[lottie" + i + "]";
            int start = text.indexOf(placeHolderText);
            int end = start + placeHolderText.length();
            startIndices[i] = start;
            endIndices[i] = end;
            lottieIndices[i] = lottieResources[i % lottieResources.length];
        }
//        text = text + getString(R.string.long_text);
    }
}