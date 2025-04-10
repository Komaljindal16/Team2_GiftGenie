package com.example.team2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        findViewById(R.id.continue_button).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // Animate sparkles
        animateSparkle(findViewById(R.id.sparkle1), 0, 2000);
        animateSparkle(findViewById(R.id.sparkle2), 500, 2500);
        animateSparkle(findViewById(R.id.sparkle3), 1000, 3000);
    }

    private void animateSparkle(ImageView sparkle, long delay, long duration) {
        sparkle.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(sparkle, "translationY", 0f, 800f);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.start();

        ObjectAnimator fade = ObjectAnimator.ofFloat(sparkle, "alpha", 0f, 1f, 0f);
        fade.setDuration(duration);
        fade.setStartDelay(delay);
        fade.setRepeatCount(ValueAnimator.INFINITE);
        fade.start();
    }
}