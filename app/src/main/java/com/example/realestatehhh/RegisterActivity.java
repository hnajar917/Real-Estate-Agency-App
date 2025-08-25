package com.example.realestatehhh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText etEmail, etFirstName, etLastName, etPassword, etConfirmPassword, etPhone;
    private Spinner spinnerGender, spinnerCountry, spinnerCity;
    private TextView btnRegister;  // Changed from Button to TextView
    private TextView tvLogin;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    private Map<String, String[]> countryCityMap;
    private Map<String, String> countryCodeMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupSpinners();
        setupClickListeners();

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("RealEstatePrefs", MODE_PRIVATE);
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etPhone = findViewById(R.id.et_phone);
        spinnerGender = findViewById(R.id.spinner_gender);
        spinnerCountry = findViewById(R.id.spinner_country);
        spinnerCity = findViewById(R.id.spinner_city);
        btnRegister = findViewById(R.id.btn_register);  // Now finds TextView instead of Button
        tvLogin = findViewById(R.id.tv_login);
    }

    private void setupSpinners() {
        // Gender spinner
        String[] genders = {"Select Gender", "Male", "Female"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        // Country-City mapping
        countryCityMap = new HashMap<>();
        countryCityMap.put("Palestine", new String[]{"Select City", "Nablus", "Ramallah", "Gaza"});
        countryCityMap.put("Jordan", new String[]{"Select City", "Amman", "Irbid", "Zarqa"});
        countryCityMap.put("Lebanon", new String[]{"Select City", "Beirut", "Tripoli", "Sidon"});

        // Country codes
        countryCodeMap = new HashMap<>();
        countryCodeMap.put("Palestine", "+970");
        countryCodeMap.put("Jordan", "+962");
        countryCodeMap.put("Lebanon", "+961");

        // Country spinner
        String[] countries = {"Select Country", "Palestine", "Jordan", "Lebanon"};
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(countryAdapter);

        // Country selection listener
        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCountry = countries[position];
                if (!selectedCountry.equals("Select Country")) {
                    String[] cities = countryCityMap.get(selectedCountry);
                    ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(RegisterActivity.this,
                            android.R.layout.simple_spinner_item, cities);
                    cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCity.setAdapter(cityAdapter);

                    // Update phone number prefix
                    String countryCode = countryCodeMap.get(selectedCountry);
                    if (!etPhone.getText().toString().startsWith(countryCode)) {
                        etPhone.setText(countryCode);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRegistration();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void performRegistration() {
        String email = etEmail.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();
        String country = spinnerCountry.getSelectedItem().toString();
        String city = spinnerCity.getSelectedItem().toString();

        // Basic validation
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(firstName) || firstName.length() < 3) {
            Toast.makeText(this, "First name must be at least 3 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(lastName) || lastName.length() < 3) {
            Toast.makeText(this, "Last name must be at least 3 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPassword(password)) {
            Toast.makeText(this, "Password must be at least 6 characters with 1 letter, 1 number, and 1 special character", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (gender.equals("Select Gender") || country.equals("Select Country") || city.equals("Select City")) {
            Toast.makeText(this, "Please complete all selections", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if email already exists
        if (dbHelper.isEmailExists(email)) {
            Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user and save to database
        User user = new User(email, firstName, lastName, password, gender, country, city, phone, "user");
        long result = dbHelper.registerUser(user);

        if (result > 0) {
            // Save complete user session after successful registration
            saveUserSession(user);

            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();

            // Go directly to MainActivity instead of LoginActivity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
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

    private boolean isValidPassword(String password) {
        if (password.length() < 6) return false;

        boolean hasLetter = Pattern.compile("[a-zA-Z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("\\d").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(password).find();

        return hasLetter && hasDigit && hasSpecial;
    }
}