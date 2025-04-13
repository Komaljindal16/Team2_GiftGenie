package com.example.team2;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

public class TestActivity extends AppCompatActivity {
    private static final String TAG = "TestActivity";
    private static final String TEST_IMAGE = "https://picsum.photos/150";  // Reliable image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Log.d(TAG, "onCreate started");

        ImageView imageView = findViewById(R.id.test_image);
        TextView statusText = findViewById(R.id.test_status);

        Log.d(TAG, "Loading local drawable");
        imageView.setImageResource(R.drawable.ic_gift_flaticon);
        statusText.setText("Showing local flaticon");

        imageView.postDelayed(() -> {
            Log.d(TAG, "Loading TEST_IMAGE: " + TEST_IMAGE);
            Picasso.get()
                    .load(TEST_IMAGE)
                    .placeholder(R.drawable.ic_gift_flaticon)
                    .error(R.drawable.ic_gift_flaticon)
                    .into(imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "TEST_IMAGE loaded successfully");
                            statusText.setText("Loaded online image");
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e(TAG, "TEST_IMAGE failed: " + e.getMessage());
                            statusText.setText("Failed: " + e.getMessage());
                        }
                    });
        }, 2000);
    }
}