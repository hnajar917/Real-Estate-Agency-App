package com.example.realestatehhh;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class ManagePropertiesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdminPropertyAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvNoData;
    private FloatingActionButton fabAdd;
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

        setContentView(R.layout.activity_manage_properties);

        initViews();
        setupToolbar();
        loadProperties();
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProperties(); // Refresh when returning
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_properties);
        progressBar = findViewById(R.id.progress_bar);
        tvNoData = findViewById(R.id.tv_no_data);
        fabAdd = findViewById(R.id.fab_add);

        dbHelper = new DatabaseHelper(this);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage Properties");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupClickListeners() {
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManagePropertiesActivity.this, AddPropertyActivity.class));
            }
        });
    }

    private void loadProperties() {
        progressBar.setVisibility(View.VISIBLE);

        List<Property> properties = dbHelper.getAllProperties();

        progressBar.setVisibility(View.GONE);

        if (properties != null && !properties.isEmpty()) {
            adapter = new AdminPropertyAdapter(properties, this, new AdminPropertyAdapter.OnPropertyActionListener() {
                @Override
                public void onEditProperty(Property property) {
                    // TODO: Implement edit property
                    Toast.makeText(ManagePropertiesActivity.this, "Edit property: " + property.getTitle(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDeleteProperty(Property property) {
                    showDeleteConfirmation(property);
                }
            });
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        }
    }

    private void showDeleteConfirmation(final Property property) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Property")
                .setMessage("Are you sure you want to delete '" + property.getTitle() + "'?\n\nThis will also remove all related favorites and reservations.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteProperty(property);
                    }
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteProperty(Property property) {
        if (dbHelper.deleteProperty(property.getId())) {
            Toast.makeText(this, "Property deleted successfully", Toast.LENGTH_SHORT).show();
            loadProperties(); // Refresh list
        } else {
            Toast.makeText(this, "Failed to delete property", Toast.LENGTH_SHORT).show();
        }
    }
}