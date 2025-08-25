package com.example.realestatehhh;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsActivity extends AppCompatActivity {
    private TextView tvTotalUsers, tvTotalProperties, tvTotalReservations, tvTotalFavorites;
    private TextView tvMaleCount, tvFemaleCount, tvMalePercentage, tvFemalePercentage;
    private TextView tvTopCountryReservations, tvTopCountryName;
    private TextView tvMostReservedProperty, tvPropertyTitle;
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

        setContentView(R.layout.activity_analytics);

        initViews();
        setupToolbar();
        loadAnalytics();
    }

    private void initViews() {
        tvTotalUsers = findViewById(R.id.tv_total_users);
        tvTotalProperties = findViewById(R.id.tv_total_properties);
        tvTotalReservations = findViewById(R.id.tv_total_reservations);
        tvTotalFavorites = findViewById(R.id.tv_total_favorites);
        tvMaleCount = findViewById(R.id.tv_male_count);
        tvFemaleCount = findViewById(R.id.tv_female_count);
        tvMalePercentage = findViewById(R.id.tv_male_percentage);
        tvFemalePercentage = findViewById(R.id.tv_female_percentage);
        tvTopCountryReservations = findViewById(R.id.tv_top_country_reservations);
        tvTopCountryName = findViewById(R.id.tv_top_country_name);
        tvMostReservedProperty = findViewById(R.id.tv_most_reserved_property);
        tvPropertyTitle = findViewById(R.id.tv_property_title);

        dbHelper = new DatabaseHelper(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Analytics Dashboard");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadAnalytics() {
        // Basic counts
        int totalUsers = dbHelper.getTotalUsersCount();
        int totalProperties = dbHelper.getAllProperties().size();
        int totalReservations = dbHelper.getTotalReservationsCount();
        int totalFavorites = dbHelper.getTotalFavoritesCount();

        tvTotalUsers.setText(String.valueOf(totalUsers));
        tvTotalProperties.setText(String.valueOf(totalProperties));
        tvTotalReservations.setText(String.valueOf(totalReservations));
        tvTotalFavorites.setText(String.valueOf(totalFavorites));

        // Gender statistics
        loadGenderStatistics();

        // Country statistics
        loadCountryStatistics();

        // Most reserved property
        loadMostReservedProperty();
    }

    private void loadGenderStatistics() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Count males
        Cursor maleCursor = db.rawQuery("SELECT COUNT(*) FROM users WHERE gender = 'Male'", null);
        int maleCount = 0;
        if (maleCursor.moveToFirst()) {
            maleCount = maleCursor.getInt(0);
        }
        maleCursor.close();

        // Count females
        Cursor femaleCursor = db.rawQuery("SELECT COUNT(*) FROM users WHERE gender = 'Female'", null);
        int femaleCount = 0;
        if (femaleCursor.moveToFirst()) {
            femaleCount = femaleCursor.getInt(0);
        }
        femaleCursor.close();

        int totalUsers = maleCount + femaleCount;

        if (totalUsers > 0) {
            double malePercentage = (maleCount * 100.0) / totalUsers;
            double femalePercentage = (femaleCount * 100.0) / totalUsers;

            tvMaleCount.setText(String.valueOf(maleCount));
            tvFemaleCount.setText(String.valueOf(femaleCount));
            tvMalePercentage.setText(String.format("%.1f%%", malePercentage));
            tvFemalePercentage.setText(String.format("%.1f%%", femalePercentage));
        } else {
            tvMaleCount.setText("0");
            tvFemaleCount.setText("0");
            tvMalePercentage.setText("0%");
            tvFemalePercentage.setText("0%");
        }
    }

    private void loadCountryStatistics() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT u.country, COUNT(r.id) as reservation_count " +
                "FROM users u " +
                "LEFT JOIN reservations r ON u.email = r.user_email AND r.status = 'active' " +
                "GROUP BY u.country " +
                "ORDER BY reservation_count DESC " +
                "LIMIT 1";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            String topCountry = cursor.getString(0);
            int reservationCount = cursor.getInt(1);

            tvTopCountryName.setText(topCountry != null ? topCountry : "N/A");
            tvTopCountryReservations.setText(String.valueOf(reservationCount));
        } else {
            tvTopCountryName.setText("N/A");
            tvTopCountryReservations.setText("0");
        }

        cursor.close();
    }

    private void loadMostReservedProperty() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT p.title, COUNT(r.id) as reservation_count " +
                "FROM properties p " +
                "LEFT JOIN reservations r ON p.property_id = r.property_id AND r.status = 'active' " +
                "GROUP BY p.property_id, p.title " +
                "ORDER BY reservation_count DESC " +
                "LIMIT 1";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            String propertyTitle = cursor.getString(0);
            int reservationCount = cursor.getInt(1);

            tvPropertyTitle.setText(propertyTitle != null ? propertyTitle : "N/A");
            tvMostReservedProperty.setText(String.valueOf(reservationCount));
        } else {
            tvPropertyTitle.setText("N/A");
            tvMostReservedProperty.setText("0");
        }

        cursor.close();
    }
}