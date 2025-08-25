package com.example.realestatehhh;

import android.content.DialogInterface;
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
import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvNoData;
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

        setContentView(R.layout.activity_manage_users);

        initViews();
        setupToolbar();
        loadUsers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers(); // Refresh when returning
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_users);
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
            getSupportActionBar().setTitle("Manage Users");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadUsers() {
        progressBar.setVisibility(View.VISIBLE);

        List<User> users = dbHelper.getAllUsers();

        progressBar.setVisibility(View.GONE);

        if (users != null && !users.isEmpty()) {
            adapter = new UserAdapter(users, this, new UserAdapter.OnUserActionListener() {
                @Override
                public void onDeleteUser(User user) {
                    showDeleteConfirmation(user);
                }

                @Override
                public void onViewUserDetails(User user) {
                    showUserDetails(user);
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

    private void showDeleteConfirmation(final User user) {
        // Don't allow deleting admin accounts
        if (user.getRole().equals("admin")) {
            Toast.makeText(this, "Cannot delete admin accounts", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete user '" + user.getFirstName() + " " + user.getLastName() + "'?\n\nThis will also remove all their favorites and reservations.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUser(user);
                    }
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteUser(User user) {
        if (dbHelper.deleteUser(user.getEmail())) {
            Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show();
            loadUsers(); // Refresh list
        } else {
            Toast.makeText(this, "Failed to delete user", Toast.LENGTH_SHORT).show();
        }
    }

    private void showUserDetails(User user) {
        String details = "Email: " + user.getEmail() + "\n" +
                "Name: " + user.getFirstName() + " " + user.getLastName() + "\n" +
                "Phone: " + user.getPhone() + "\n" +
                "Location: " + user.getCity() + ", " + user.getCountry() + "\n" +
                "Gender: " + user.getGender() + "\n" +
                "Role: " + user.getRole();

        new AlertDialog.Builder(this)
                .setTitle("User Details")
                .setMessage(details)
                .setPositiveButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
}