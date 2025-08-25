package com.example.realestatehhh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private LinearLayout layoutRememberMe;  // Custom checkbox layout
    private TextView tvCheckboxIcon;        // Checkbox icon
    private TextView btnLogin, tvRegister;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    private boolean isRememberMeChecked = false;  // Track checkbox state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupClickListeners();
        loadSavedEmail();

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("RealEstatePrefs", MODE_PRIVATE);
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        layoutRememberMe = findViewById(R.id.layout_remember_me);  // Custom checkbox layout
        tvCheckboxIcon = findViewById(R.id.tv_checkbox_icon);     // Checkbox icon
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        // Custom checkbox click listener
        layoutRememberMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRememberMe();
            }
        });
    }

    private void toggleRememberMe() {
        isRememberMeChecked = !isRememberMeChecked;

        if (isRememberMeChecked) {
            // Show checked state
            tvCheckboxIcon.setText("✓");  // Clean checkmark symbol
            tvCheckboxIcon.setTextColor(getResources().getColor(android.R.color.holo_green_light));
            tvCheckboxIcon.setBackgroundResource(R.drawable.checkbox_checked_background);
        } else {
            // Show unchecked state
            tvCheckboxIcon.setText("");  // Empty (no symbol)
            tvCheckboxIcon.setTextColor(getResources().getColor(android.R.color.white));
            tvCheckboxIcon.setBackgroundResource(R.drawable.checkbox_background);
        }
    }

    private void loadSavedEmail() {
        sharedPreferences = getSharedPreferences("RealEstatePrefs", MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString("saved_email", "");
        if (!savedEmail.isEmpty()) {
            etEmail.setText(savedEmail);
            // Set remember me to checked
            isRememberMeChecked = true;
            toggleRememberMe();
        }
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check for admin login
        if (email.equals("admin@admin.com") && password.equals("Admin123!")) {
            saveEmailIfRemembered(email);
            saveAdminSession();
            Toast.makeText(this, "Admin login successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Check user login in database
        User user = dbHelper.loginUser(email, password);
        if (user != null) {
            saveEmailIfRemembered(email);
            saveUserSession(user);
            Toast.makeText(this, "Welcome back, " + user.getFirstName() + "!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveAdminSession() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_logged_in", true);
        editor.putString("user_email", "admin@admin.com");
        editor.putString("user_first_name", "Admin");
        editor.putString("user_last_name", "User");
        editor.putString("user_password", "Admin123!");
        editor.putString("user_phone", "+970-000-000-000");
        editor.putString("user_country", "Palestine");
        editor.putString("user_city", "Nablus");
        editor.putString("user_gender", "Male");
        editor.putString("user_role", "admin");
        editor.apply();
    }

    private void saveUserSession(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_logged_in", true);
        editor.putString("user_email", user.getEmail());
        editor.putString("user_first_name", user.getFirstName());
        editor.putString("user_last_name", user.getLastName());
        editor.putString("user_password", user.getPassword());
        editor.putString("user_phone", user.getPhone());
        editor.putString("user_country", user.getCountry());
        editor.putString("user_city", user.getCity());
        editor.putString("user_gender", user.getGender());
        editor.putString("user_role", user.getRole());
        editor.apply();
    }

    private void saveEmailIfRemembered(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isRememberMeChecked) {  // Use custom boolean instead of CheckBox
            editor.putString("saved_email", email);
        } else {
            editor.remove("saved_email");
        }
        editor.apply();
    }
}