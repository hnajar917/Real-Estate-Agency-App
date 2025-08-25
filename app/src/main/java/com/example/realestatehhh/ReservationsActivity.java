package com.example.realestatehhh;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReservationsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PropertyAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvNoData;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);

        initViews();
        setupToolbar();
        loadReservations();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh reservations when returning to this activity
        loadReservations();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_reservations);
        progressBar = findViewById(R.id.progress_bar);
        tvNoData = findViewById(R.id.tv_no_data);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("RealEstatePrefs", MODE_PRIVATE);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Reservations");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadReservations() {
        progressBar.setVisibility(View.VISIBLE);

        String userEmail = sharedPreferences.getString("user_email", "");
        List<Property> reservations = dbHelper.getUserReservations(userEmail);

        progressBar.setVisibility(View.GONE);

        if (reservations != null && !reservations.isEmpty()) {
            adapter = new PropertyAdapter(reservations, this);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
            tvNoData.setText("No reserved properties yet.\nStart reserving properties you're interested in!");
        }
    }
}