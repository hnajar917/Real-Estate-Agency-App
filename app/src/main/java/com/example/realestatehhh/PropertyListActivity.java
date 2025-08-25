package com.example.realestatehhh;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PropertyListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PropertyAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvNoData;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_list);

        initViews();
        setupToolbar();
        loadProperties();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_properties);
        progressBar = findViewById(R.id.progress_bar);
        tvNoData = findViewById(R.id.tv_no_data);

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
            getSupportActionBar().setTitle("All Properties");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadProperties() {
        progressBar.setVisibility(View.VISIBLE);

        // Load properties from database
        List<Property> properties = dbHelper.getAllProperties();

        progressBar.setVisibility(View.GONE);

        if (properties != null && !properties.isEmpty()) {
            adapter = new PropertyAdapter(properties, this);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
            tvNoData.setText("No properties found");
        }
    }
}