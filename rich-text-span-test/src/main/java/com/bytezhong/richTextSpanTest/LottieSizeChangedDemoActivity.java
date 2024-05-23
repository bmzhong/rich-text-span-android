package com.bytezhong.richTextSpanTest;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieDrawable;
import com.bytezhong.richTextSpan.old.CustomLottieAnimationView;
import com.bytezhong.richTextSpan.old.RichTextSpan;
import com.bytezhong.richTextSpan.old.TextViewWrapper;

public class LottieSizeChangedDemoActivity extends AppCompatActivity {
    private int width = 200; // 视频宽度
    private int height = 200; // 视频高度

    private String text = "HelloHelloHelloHelloHello" +
            "HelloHelloHelloHelloHelloHello这是一个lottie: [lottie1]HelloHelloHello" +
            "Hello";

    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lottie_size_changed_demo);

        TextViewWrapper textViewWrapper = findViewById(R.id.textViewWrapper);
        SpannableString spannableString = new SpannableString(text);
        String placeHolderText = "[lottie1]";
        int start = text.indexOf(placeHolderText);
        int end = start + placeHolderText.length();

        CustomLottieAnimationView lottieAnimationView = new CustomLottieAnimationView(this);
        lottieAnimationView.setLottieSize(width, height);
        lottieAnimationView.setAnimation(R.raw.bullseye);
        lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);
        RichTextSpan richTextSpan = new RichTextSpan(textViewWrapper, lottieAnimationView);
        spannableString.setSpan(richTextSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewWrapper.setText(spannableString);

        Button button0 = findViewById(R.id.button0);
        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float ratio = 1.1F;
                width = (int) (width * ratio);
                height = (int) (height * ratio);
                lottieAnimationView.changeSize(width,height);
            }
        });
    }
}