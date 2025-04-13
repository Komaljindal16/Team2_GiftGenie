package com.example.team2;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText searchBar;
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> loginLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        loginLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Log.d(TAG, "Login result: " + result.getResultCode());
            if (result.getResultCode() == RESULT_OK && mAuth.getCurrentUser() != null) {
                Log.d(TAG, "User logged in, navigating to ProfileFragment");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment())
                        .commit();
            } else {
                Log.d(TAG, "Login failed or cancelled");
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView appLogo = findViewById(R.id.app_logo);
        searchBar = findViewById(R.id.search_bar);
        ImageView loginIcon = findViewById(R.id.login_icon);

        appLogo.setOnClickListener(v -> {
            Log.d(TAG, "App logo clicked");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        });

        loginIcon.setOnClickListener(v -> {
            Log.d(TAG, "Login icon clicked");
            if (mAuth.getCurrentUser() == null) {
                Log.d(TAG, "User not logged in, launching LoginActivity");
                Intent intent = new Intent(this, LoginActivity.class);
                loginLauncher.launch(intent);
            } else {
                Log.d(TAG, "User logged in, navigating to ProfileFragment");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment())
                        .commit();
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof HomeFragment) {
                    ((HomeFragment) currentFragment).filterGifts(s.toString());
                } else if (currentFragment instanceof BrowseFragment) {
                    ((BrowseFragment) currentFragment).filterGifts(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_browse) {
                selectedFragment = new BrowseFragment();
            } else if (itemId == R.id.nav_cart) {
                selectedFragment = new CartFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        GiftData.getInstance().setContext(this);
        updateCartBadge();
    }

    public void updateCartBadge() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        int cartSize = 0;
        for (Gift gift : GiftData.getInstance().getCart()) {
            cartSize += gift.getQuantity();
        }
        Log.d(TAG, "Updating badge with cart quantity: " + cartSize);
        bottomNavigationView.getOrCreateBadge(R.id.nav_cart).setNumber(cartSize);
        bottomNavigationView.getOrCreateBadge(R.id.nav_cart).setVisible(cartSize > 0);
    }
}