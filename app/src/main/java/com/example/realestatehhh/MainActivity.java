package com.example.realestatehhh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView tvWelcome, tvPropertyCount;
    private TextView btnSearchProperties;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupNavigationDrawer();
        setupClickListeners();
        loadUserData();
        setupMenuBasedOnUserRole(); // NEW: Setup menu based on user role
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh user data when returning to MainActivity
        loadUserData();
        updateNavigationHeader();
        setupMenuBasedOnUserRole(); // NEW: Update menu when returning to activity
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        tvWelcome = findViewById(R.id.tv_welcome);
        tvPropertyCount = findViewById(R.id.tv_property_count);
        btnSearchProperties = findViewById(R.id.btn_search_properties);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("RealEstatePrefs", MODE_PRIVATE);
    }

    private void setupClickListeners() {
        btnSearchProperties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });
    }

    private void setupNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, findViewById(R.id.toolbar),
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        updateNavigationHeader();
    }

    // NEW: Method to show/hide admin panel based on user role
    private void setupMenuBasedOnUserRole() {
        String userRole = sharedPreferences.getString("user_role", "user");
        Menu navMenu = navigationView.getMenu();
        MenuItem adminPanelItem = navMenu.findItem(R.id.nav_admin_panel);

        if (adminPanelItem != null) {
            // Show admin panel only for admin users
            adminPanelItem.setVisible(userRole.equals("admin"));
        }


        hideAdministrationGroupForNonAdmin(navMenu, userRole);
    }


    private void hideAdministrationGroupForNonAdmin(Menu navMenu, String userRole) {
        // Find the Administration group (the parent menu item)
        for (int i = 0; i < navMenu.size(); i++) {
            MenuItem item = navMenu.getItem(i);
            if (item.getTitle() != null && item.getTitle().toString().equals("Administration")) {
                item.setVisible(userRole.equals("admin"));
                break;
            }
        }
    }

    private void updateNavigationHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.nav_user_name);
        TextView navUserEmail = headerView.findViewById(R.id.nav_user_email);

        String firstName = sharedPreferences.getString("user_first_name", "User");
        String lastName = sharedPreferences.getString("user_last_name", "");
        String email = sharedPreferences.getString("user_email", "");

        navUserName.setText(firstName + " " + lastName);
        navUserEmail.setText(email);
    }

    private void loadUserData() {
        String firstName = sharedPreferences.getString("user_first_name", "User");

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<Property> properties = dbHelper.getAllProperties();

        tvWelcome.setText("Welcome back, " + firstName + "!");

        if (properties != null && !properties.isEmpty()) {
            tvPropertyCount.setText(properties.size() + " properties available");
        } else {
            tvPropertyCount.setText("No properties available");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Already on home
        } else if (id == R.id.nav_properties) {
            startActivity(new Intent(this, PropertyListActivity.class));
        } else if (id == R.id.nav_search) {
            startActivity(new Intent(this, SearchActivity.class));
        } else if (id == R.id.nav_favorites) {
            startActivity(new Intent(this, FavoritesActivity.class));
        } else if (id == R.id.nav_reservations) {
            startActivity(new Intent(this, ReservationsActivity.class));
        } else if (id == R.id.nav_contact_us) {
            startActivity(new Intent(this, ContactUsActivity.class));
        } else if (id == R.id.nav_admin_panel) {
            // This check is now redundant since non-admin users won't see this option
            // But keeping it as a safety measure
            String userRole = sharedPreferences.getString("user_role", "user");
            if (userRole.equals("admin")) {
                startActivity(new Intent(this, AdminPanelActivity.class));
            } else {
                // This should never happen now, but just in case
                Toast.makeText(this, "Access denied. Admin only.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_logout) {
            logout();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, WelcomeActiviy.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void viewAllProperties(View view) {
        startActivity(new Intent(this, PropertyListActivity.class));
    }

    public void searchProperties(View view) {
        startActivity(new Intent(this, SearchActivity.class));
    }
}