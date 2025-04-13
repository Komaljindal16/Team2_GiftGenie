package com.example.team2;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextInputEditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Initialize views
        TextInputLayout searchBarLayout = findViewById(R.id.search_bar);
        searchBar = searchBarLayout != null && searchBarLayout.getEditText() instanceof TextInputEditText
                ? (TextInputEditText) searchBarLayout.getEditText()
                : null;
        ImageView appLogo = findViewById(R.id.app_logo);
        ImageView loginIcon = findViewById(R.id.login_icon);

        // Clear focus and hide keyboard when clicking outside search bar
        findViewById(R.id.fragment_container).setOnClickListener(v -> clearSearchBarFocus());

        // App Logo click -> Navigate to HomeFragment
        if (appLogo != null) {
            appLogo.setOnClickListener(v -> {
                Log.d(TAG, "App logo clicked");
                clearSearchBarFocus();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
            });
        }

        // Login Icon click -> Navigate to ProfileFragment
        if (loginIcon != null) {
            loginIcon.setOnClickListener(v -> {
                Log.d(TAG, "Login icon clicked");
                clearSearchBarFocus();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment())
                        .commit();
            });
        }

        // Search Bar listener
        if (searchBar != null) {
            searchBar.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.d(TAG, "Search query: " + s.toString());
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (currentFragment instanceof HomeFragment) {
                        Log.d(TAG, "Calling filterGifts on HomeFragment");
                        ((HomeFragment) currentFragment).filterGifts(s.toString());
                    } else if (currentFragment instanceof BrowseFragment) {
                        Log.d(TAG, "Calling filterGifts on BrowseFragment");
                        ((BrowseFragment) currentFragment).filterGifts(s.toString());
                    } else {
                        Log.d(TAG, "No matching fragment: " + (currentFragment != null ? currentFragment.getClass().getSimpleName() : "null"));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            // Optional: Ensure clear button works (redundant if endIconMode works)
            searchBarLayout.setEndIconOnClickListener(v -> {
                searchBar.setText("");
                Log.d(TAG, "Clear button clicked");
            });
        } else {
            Log.e(TAG, "Search bar TextInputEditText not found");
        }

        // Set up Bottom Navigation
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
                clearSearchBarFocus();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Load HomeFragment by default
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }

    public void updateCartBadge() {
        int cartSize = GiftData.getInstance().getCart().size();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.getOrCreateBadge(R.id.nav_cart).setNumber(cartSize);
    }

    private void clearSearchBarFocus() {
        if (searchBar != null && searchBar.hasFocus()) {
            searchBar.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
            Log.d(TAG, "Search bar focus cleared");
        }
    }
}