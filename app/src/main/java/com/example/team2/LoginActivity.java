package com.example.team2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText usernameInput, passwordInput;
    private MaterialButton signInButton;
    private TextView signUpLink, forgetPasswordLink;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

     
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        signInButton = findViewById(R.id.sign_in_button);
        signUpLink = findViewById(R.id.sign_up_link);
        forgetPasswordLink = findViewById(R.id.forget_password);

    
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        signInButton.setOnClickListener(v -> login());
        signUpLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
        forgetPasswordLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
    }

    private void login() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.verifyUser(username, password)) {
            sessionManager.loginUser(username);
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.closeDatabase();
    }
}
