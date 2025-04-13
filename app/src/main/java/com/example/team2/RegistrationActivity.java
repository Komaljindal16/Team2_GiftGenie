package com.example.team2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegistrationActivity extends AppCompatActivity {
    private TextInputEditText nameInput, emailInput, usernameInput, passwordInput, phoneInput;
    private MaterialButton signUpButton;
    private TextView signInLink;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_registration);

        // Initialize views
        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        phoneInput = findViewById(R.id.phone_input);
        signUpButton = findViewById(R.id.sign_up_button);
        signInLink = findViewById(R.id.sign_in_link);

        // Initialize helpers
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        // Set listeners
        signUpButton.setOnClickListener(v -> signUp());
        signInLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void signUp() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.userExists(username)) {
            Toast.makeText(this, "Username already registered", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = dbHelper.addUser(name, email, username, password, phone);
        if (result != -1) {
            sessionManager.loginUser(username);
            Toast.makeText(this, "Sign-up successful!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Sign-up failed. Try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.closeDatabase();
    }
}