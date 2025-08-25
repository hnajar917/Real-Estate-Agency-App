package com.example.realestatehhh;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AddPropertyActivity extends AppCompatActivity {
    private EditText etTitle, etDescription, etPrice, etArea, etBedrooms, etBathrooms;
    private Spinner spinnerType, spinnerLocation;
    private Button btnAddProperty;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is admin
        sharedPreferences = getSharedPreferences("RealEstatePrefs", MODE_PRIVATE);
        String userRole = sharedPreferences.getString("user_role", "user");

        if (!userRole.equals("admin")) {
            Toast.makeText(this, "Access denied. Admin only.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_add_property);

        initViews();
        setupToolbar();
        setupSpinners();
        setupClickListeners();
    }

    private void initViews() {
        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_description);
        etPrice = findViewById(R.id.et_price);
        etArea = findViewById(R.id.et_area);
        etBedrooms = findViewById(R.id.et_bedrooms);
        etBathrooms = findViewById(R.id.et_bathrooms);
        spinnerType = findViewById(R.id.spinner_type);
        spinnerLocation = findViewById(R.id.spinner_location);
        btnAddProperty = findViewById(R.id.btn_add_property);

        dbHelper = new DatabaseHelper(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add New Property");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupSpinners() {
        // Property types
        String[] types = {"Villa", "Apartment", "Land", "House", "Commercial"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        // Locations
        String[] locations = {"Nablus", "Ramallah", "Jerusalem", "Hebron", "Gaza", "Bethlehem", "Jenin", "Tulkarm"};
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, locations);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(locationAdapter);
    }

    private void setupClickListeners() {
        btnAddProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProperty();
            }
        });
    }

    private void addProperty() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String areaStr = etArea.getText().toString().trim();
        String bedroomsStr = etBedrooms.getText().toString().trim();
        String bathroomsStr = etBathrooms.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString();
        String location = spinnerLocation.getSelectedItem().toString();

        // Validation
        if (TextUtils.isEmpty(title) || title.length() < 3) {
            etTitle.setError("Title must be at least 3 characters");
            etTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description) || description.length() < 10) {
            etDescription.setError("Description must be at least 10 characters");
            etDescription.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(priceStr)) {
            etPrice.setError("Price is required");
            etPrice.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(areaStr)) {
            etArea.setError("Area is required");
            etArea.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(bedroomsStr)) {
            etBedrooms.setError("Number of bedrooms is required");
            etBedrooms.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(bathroomsStr)) {
            etBathrooms.setError("Number of bathrooms is required");
            etBathrooms.requestFocus();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            double area = Double.parseDouble(areaStr);
            int bedrooms = Integer.parseInt(bedroomsStr);
            int bathrooms = Integer.parseInt(bathroomsStr);

            if (price <= 0) {
                etPrice.setError("Price must be greater than 0");
                etPrice.requestFocus();
                return;
            }

            if (area <= 0) {
                etArea.setError("Area must be greater than 0");
                etArea.requestFocus();
                return;
            }

            if (bedrooms < 0 || bedrooms > 20) {
                etBedrooms.setError("Bedrooms must be between 0 and 20");
                etBedrooms.requestFocus();
                return;
            }

            if (bathrooms < 1 || bathrooms > 10) {
                etBathrooms.setError("Bathrooms must be between 1 and 10");
                etBathrooms.requestFocus();
                return;
            }

            // Create property object
            Property property = new Property();
            property.setTitle(title);
            property.setDescription(description);
            property.setPrice(price);
            property.setLocation(location);
            property.setType(type);
            property.setArea(String.valueOf(area));
            property.setBedrooms(bedrooms);
            property.setBathrooms(bathrooms);
            property.setImageUrl(""); // Default empty image URL
            property.setFeatured(false); // Default not featured

            // Add to database
            if (dbHelper.addProperty(property)) {
                Toast.makeText(this, "Property added successfully!", Toast.LENGTH_SHORT).show();

                // Clear form
                clearForm();

                // Go back to admin panel
                finish();
            } else {
                Toast.makeText(this, "Failed to add property. Please try again.", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for price, area, bedrooms, and bathrooms", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        etTitle.setText("");
        etDescription.setText("");
        etPrice.setText("");
        etArea.setText("");
        etBedrooms.setText("");
        etBathrooms.setText("");
        spinnerType.setSelection(0);
        spinnerLocation.setSelection(0);
    }
}