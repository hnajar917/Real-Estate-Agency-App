package com.example.realestatehhh;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private EditText etSearch, etMinPrice, etMaxPrice;
    private Spinner spinnerType, spinnerLocation;
    private RecyclerView recyclerView;
    private PropertyAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvResults, tvNoResults;
    private DatabaseHelper dbHelper;

    private List<Property> allProperties;
    private List<Property> filteredProperties;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        setupToolbar();
        setupSpinners();
        setupSearchListeners();
        loadAllProperties();
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        etMinPrice = findViewById(R.id.et_min_price);
        etMaxPrice = findViewById(R.id.et_max_price);
        spinnerType = findViewById(R.id.spinner_type);
        spinnerLocation = findViewById(R.id.spinner_location);
        recyclerView = findViewById(R.id.recycler_view_search);
        progressBar = findViewById(R.id.progress_bar);
        tvResults = findViewById(R.id.tv_results);
        tvNoResults = findViewById(R.id.tv_no_results);

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
            getSupportActionBar().setTitle("Search Properties");
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
        String[] types = {"All Types", "Villa", "Apartment", "Land", "House", "Commercial"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        // Locations (you can make this dynamic from database)
        String[] locations = {"All Locations", "Nablus", "Ramallah", "Jerusalem", "Hebron", "Gaza", "Bethlehem"};
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, locations);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(locationAdapter);
    }

    private void setupSearchListeners() {
        // Text search listener
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Price range listeners
        etMinPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        etMaxPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Spinner listeners
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                performSearch();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                performSearch();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadAllProperties() {
        progressBar.setVisibility(View.VISIBLE);
        allProperties = dbHelper.getAllProperties();
        filteredProperties = new ArrayList<>(allProperties);
        updateResults();
        progressBar.setVisibility(View.GONE);
    }

    private void performSearch() {
        if (allProperties == null) return;

        String searchText = etSearch.getText().toString().toLowerCase().trim();
        String selectedType = spinnerType.getSelectedItem().toString();
        String selectedLocation = spinnerLocation.getSelectedItem().toString();

        String minPriceStr = etMinPrice.getText().toString().trim();
        String maxPriceStr = etMaxPrice.getText().toString().trim();

        double minPrice = 0;
        double maxPrice = Double.MAX_VALUE;

        try {
            if (!minPriceStr.isEmpty()) {
                minPrice = Double.parseDouble(minPriceStr);
            }
            if (!maxPriceStr.isEmpty()) {
                maxPrice = Double.parseDouble(maxPriceStr);
            }
        } catch (NumberFormatException e) {
            // Invalid price format, ignore
        }

        filteredProperties.clear();

        for (Property property : allProperties) {
            boolean matches = true;

            // Text search (title, description, location)
            if (!searchText.isEmpty()) {
                boolean textMatch = property.getTitle().toLowerCase().contains(searchText) ||
                        property.getDescription().toLowerCase().contains(searchText) ||
                        property.getLocation().toLowerCase().contains(searchText);
                if (!textMatch) matches = false;
            }

            // Type filter
            if (!selectedType.equals("All Types")) {
                if (!property.getType().equalsIgnoreCase(selectedType)) {
                    matches = false;
                }
            }

            // Location filter
            if (!selectedLocation.equals("All Locations")) {
                if (!property.getLocation().toLowerCase().contains(selectedLocation.toLowerCase())) {
                    matches = false;
                }
            }

            // Price range filter
            if (property.getPrice() < minPrice || property.getPrice() > maxPrice) {
                matches = false;
            }

            if (matches) {
                filteredProperties.add(property);
            }
        }

        updateResults();
    }

    private void updateResults() {
        if (filteredProperties.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvResults.setVisibility(View.GONE);
            tvNoResults.setVisibility(View.VISIBLE);
        } else {
            adapter = new PropertyAdapter(filteredProperties, this);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
            tvResults.setVisibility(View.VISIBLE);
            tvNoResults.setVisibility(View.GONE);

            tvResults.setText(filteredProperties.size() + " properties found");
        }
    }
}