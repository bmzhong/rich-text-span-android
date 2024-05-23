package com.bytezhong.richTextSpanTest;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.bytezhong.richTextSpan.RichTextSpan;
import com.bytezhong.richTextSpan.TextViewWrapper;
import com.bytezhong.richTextSpan.ViewAdapter;

import java.util.Collections;

public class LottieLoadTimeTestActivity extends AppCompatActivity {


    private static final String TAG = "LottieLoadTimeTestActivity";
    private int width = 200;
    private int height = 200;
    private int textLength = 1000;
    private int lottieNumber = 500;
    private String text;
    private int[] startIndices;
    private int[] endIndices;
    private int[] lottieIndices;
    private int[] lottieResources = {R.raw.bullseye, R.raw.hamburger_arrow, R.raw.heart, R.raw.lottielogo, R.raw.walkthrough};
    private long preDrawTime;
    private long beforeNewViewTime;
    private long newViewAdapter;
    private long newRichTextSpanTime;

    @Override
    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        initText(lottieNumber);
        setContentView(R.layout.activity_lottie_load_time_test);
        TextViewWrapper textViewWrapper = findViewById(R.id.textViewWrapper2);
        SpannableString spannableString = new SpannableString(text);
        System.out.println(TAG + " text length: " + spannableString.length());
        beforeNewViewTime = System.currentTimeMillis();

        for (int i = 0; i < lottieNumber; ++i) {
            long before = System.currentTimeMillis();

            ViewAdapter viewAdapter = new ViewAdapter() {
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

            newViewAdapter += System.currentTimeMillis() - before;
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

        System.out.println(TAG + " newViewAdapter: " + newViewAdapter + " ms");
        System.out.println(TAG + " newRichTextSpanTime: " + newRichTextSpanTime + " ms");

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