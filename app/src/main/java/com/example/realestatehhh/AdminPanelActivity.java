package com.example.realestatehhh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

public class AdminPanelActivity extends AppCompatActivity {
    private TextView tvAdminName, tvTotalUsers, tvTotalProperties, tvTotalReservations, tvTotalFavorites;
    private CardView cardManageProperties, cardManageUsers, cardViewAnalytics, cardAddProperty;
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

        setContentView(R.layout.activity_admin_panel);

        initViews();
        setupToolbar();
        loadAdminData();
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAdminData(); // Refresh data when returning
    }

    private void initViews() {
        tvAdminName = findViewById(R.id.tv_admin_name);
        tvTotalUsers = findViewById(R.id.tv_total_users);
        tvTotalProperties = findViewById(R.id.tv_total_properties);
        tvTotalReservations = findViewById(R.id.tv_total_reservations);
        tvTotalFavorites = findViewById(R.id.tv_total_favorites);

        cardManageProperties = findViewById(R.id.card_manage_properties);
        cardManageUsers = findViewById(R.id.card_manage_users);
        cardViewAnalytics = findViewById(R.id.card_view_analytics);
        cardAddProperty = findViewById(R.id.card_add_property);

        dbHelper = new DatabaseHelper(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Admin Panel");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadAdminData() {
        // Display admin name
        String firstName = sharedPreferences.getString("user_first_name", "Admin");
        String lastName = sharedPreferences.getString("user_last_name", "User");
        tvAdminName.setText("Welcome, " + firstName + " " + lastName);

        // Load statistics
        int totalUsers = dbHelper.getTotalUsersCount();
        int totalProperties = dbHelper.getAllProperties().size();
        int totalReservations = dbHelper.getTotalReservationsCount();
        int totalFavorites = dbHelper.getTotalFavoritesCount();

        tvTotalUsers.setText(String.valueOf(totalUsers));
        tvTotalProperties.setText(String.valueOf(totalProperties));
        tvTotalReservations.setText(String.valueOf(totalReservations));
        tvTotalFavorites.setText(String.valueOf(totalFavorites));
    }

    private void setupClickListeners() {
        cardManageProperties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminPanelActivity.this, ManagePropertiesActivity.class));
            }
        });

        cardManageUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminPanelActivity.this, ManageUsersActivity.class));
            }
        });

        cardViewAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminPanelActivity.this, AnalyticsActivity.class));
            }
        });

        cardAddProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminPanelActivity.this, AddPropertyActivity.class));
            }
        });
    }
}