package com.example.realestatehhh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PropertyDetailActivity extends AppCompatActivity {
    private ImageView ivProperty;
    private TextView tvTitle, tvPrice, tvLocation, tvType, tvDescription;
    private TextView tvBedrooms, tvBathrooms, tvArea, tvPropertyId;
    private Button btnFavorite, btnReserve, btnContact;

    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private Property property;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_detail);

        initViews();
        setupToolbar();
        getPropertyData();
        displayPropertyDetails();
        updateButtonStates();
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateButtonStates();
    }

    private void initViews() {
        ivProperty = findViewById(R.id.iv_property);
        tvTitle = findViewById(R.id.tv_title);
        tvPrice = findViewById(R.id.tv_price);
        tvLocation = findViewById(R.id.tv_location);
        tvType = findViewById(R.id.tv_type);
        tvDescription = findViewById(R.id.tv_description);
        tvBedrooms = findViewById(R.id.tv_bedrooms);
        tvBathrooms = findViewById(R.id.tv_bathrooms);
        tvArea = findViewById(R.id.tv_area);
        tvPropertyId = findViewById(R.id.tv_property_id);
        btnFavorite = findViewById(R.id.btn_favorite);
        btnReserve = findViewById(R.id.btn_reserve);
        btnContact = findViewById(R.id.btn_contact);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("RealEstatePrefs", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("user_email", "");
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Property Details");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getPropertyData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("property_id")) {
            int propertyId = intent.getIntExtra("property_id", -1);

            // Get property from database
            for (Property p : dbHelper.getAllProperties()) {
                if (p.getId() == propertyId) {
                    property = p;
                    break;
                }
            }
        }

        if (property == null) {
            Toast.makeText(this, "Property not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayPropertyDetails() {
        if (property == null) return;

        tvTitle.setText(property.getTitle());
        tvPrice.setText(property.getFormattedPrice());
        tvLocation.setText(property.getLocation());
        tvType.setText(property.getType());
        tvDescription.setText(property.getDescription());
        tvBedrooms.setText(String.valueOf(property.getBedrooms()));
        tvBathrooms.setText(String.valueOf(property.getBathrooms()));
        tvArea.setText(property.getArea() + " sqm");
        tvPropertyId.setText("ID: " + property.getId());

        // Set property image based on type
        ivProperty.setImageResource(getPropertyIcon(property.getType()));
    }

    private void updateButtonStates() {
        if (property == null) return;

        // Update favorite button
        if (dbHelper.isPropertyInFavorites(userEmail, property.getId())) {
            btnFavorite.setText("❤ Remove from Favorites");
            btnFavorite.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            btnFavorite.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            btnFavorite.setText("♡ Add to Favorites");
            btnFavorite.setBackgroundColor(getResources().getColor(android.R.color.white));
            btnFavorite.setTextColor(getResources().getColor(R.color.primary_blue));
        }

        // Update reserve button
        if (dbHelper.isPropertyReservedByUser(userEmail, property.getId())) {
            btnReserve.setText("Cancel Reservation");
            btnReserve.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
            btnReserve.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            btnReserve.setText("Reserve Property");
            btnReserve.setBackgroundColor(getResources().getColor(R.color.primary_blue));
            btnReserve.setTextColor(getResources().getColor(android.R.color.white));
        }
    }

    private void setupClickListeners() {
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite();
            }
        });

        btnReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleReservation();
            }
        });

        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactOwner();
            }
        });
    }

    private void toggleFavorite() {
        if (dbHelper.isPropertyInFavorites(userEmail, property.getId())) {
            // Remove from favorites
            if (dbHelper.removeFromFavorites(userEmail, property.getId())) {
                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                updateButtonStates();
            } else {
                Toast.makeText(this, "Failed to remove from favorites", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Add to favorites
            if (dbHelper.addToFavorites(userEmail, property.getId())) {
                Toast.makeText(this, "Added to favorites!", Toast.LENGTH_SHORT).show();
                updateButtonStates();
            } else {
                Toast.makeText(this, "Failed to add to favorites", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void toggleReservation() {
        if (dbHelper.isPropertyReservedByUser(userEmail, property.getId())) {
            // Cancel reservation
            if (dbHelper.cancelReservation(userEmail, property.getId())) {
                Toast.makeText(this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
                updateButtonStates();
            } else {
                Toast.makeText(this, "Failed to cancel reservation", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Make reservation
            if (dbHelper.reserveProperty(userEmail, property.getId(), "Reserved from property details")) {
                Toast.makeText(this, "Property reserved successfully!", Toast.LENGTH_SHORT).show();
                updateButtonStates();
            } else {
                Toast.makeText(this, "Failed to reserve property", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void contactOwner() {
        // Create contact intent (email, phone, etc.)
        String subject = "Inquiry about: " + property.getTitle();
        String body = "Hello,\n\nI'm interested in the property:\n" +
                "Title: " + property.getTitle() + "\n" +
                "Location: " + property.getLocation() + "\n" +
                "Price: " + property.getFormattedPrice() + "\n\n" +
                "Please provide more information.\n\nThank you.";

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@realestate.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        try {
            startActivity(Intent.createChooser(emailIntent, "Contact Property Owner"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
        }
    }

    private int getPropertyIcon(String type) {
        switch (type.toLowerCase()) {
            case "villa":
                return android.R.drawable.ic_menu_gallery;
            case "apartment":
                return android.R.drawable.ic_menu_view;
            case "land":
                return android.R.drawable.ic_menu_mapmode;
            case "house":
                return android.R.drawable.ic_menu_gallery;
            case "commercial":
                return android.R.drawable.ic_menu_view;
            default:
                return android.R.drawable.ic_menu_gallery;
        }
    }
}