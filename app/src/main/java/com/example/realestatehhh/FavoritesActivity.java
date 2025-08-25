package com.example.realestatehhh;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PropertyAdapter adapter;
    private LinearLayout layoutLoading, layoutEmpty, layoutError;
    private TextView tvFavoritesCount, btnBrowseProperties, btnRetry;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        initViews();
        setupToolbar();
        setupClickListeners();
        loadFavorites();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh favorites when returning to this activity
        loadFavorites();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_favorites);
        layoutLoading = findViewById(R.id.layout_loading);
        layoutEmpty = findViewById(R.id.layout_empty);
        layoutError = findViewById(R.id.layout_error);
        tvFavoritesCount = findViewById(R.id.tv_favorites_count);
        btnBrowseProperties = findViewById(R.id.btn_browse_properties);
        btnRetry = findViewById(R.id.btn_retry);

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
            getSupportActionBar().setTitle("My Favorites");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupClickListeners() {
        // Browse Properties button
        btnBrowseProperties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonPress(v, new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(FavoritesActivity.this, PropertyListActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

        // Retry button
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonPress(v, new Runnable() {
                    @Override
                    public void run() {
                        loadFavorites();
                    }
                });
            }
        });
    }

    private void loadFavorites() {
        // 🎨 Show loading state with animation
        showLoadingState();

        // Simulate loading delay for smooth animation (you can remove this if you want immediate loading)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String userEmail = sharedPreferences.getString("user_email", "");
                List<Property> favorites = dbHelper.getUserFavorites(userEmail);

                if (favorites != null && !favorites.isEmpty()) {
                    showContentState(favorites);
                } else {
                    showEmptyState();
                }
            }
        }, 800); // 800ms delay for smooth loading animation
    }

    // 🌟 ANIMATION: Show loading state
    private void showLoadingState() {
        // Hide other layouts
        hideAllLayouts();

        // Show loading layout
        layoutLoading.setVisibility(View.VISIBLE);

        // Animate loading state entrance
        layoutLoading.setAlpha(0f);
        layoutLoading.setScaleX(0.8f);
        layoutLoading.setScaleY(0.8f);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(layoutLoading, "alpha", 0f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(layoutLoading, "scaleX", 0.8f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(layoutLoading, "scaleY", 0.8f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeIn, scaleX, scaleY);
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();

        // Update header
        tvFavoritesCount.setText("Loading your favorites...");
    }

    // 🌟 ANIMATION: Show content state with favorites
    private void showContentState(List<Property> favorites) {
        // Hide loading
        animateLayoutExit(layoutLoading, new Runnable() {
            @Override
            public void run() {
                // Setup adapter
                adapter = new PropertyAdapter(favorites, FavoritesActivity.this);
                recyclerView.setAdapter(adapter);

                // Reset adapter animations for fresh load
                if (adapter instanceof PropertyAdapter) {
                    // If your PropertyAdapter has resetAnimations method
                    try {
                        adapter.getClass().getMethod("resetAnimations").invoke(adapter);
                    } catch (Exception e) {
                        // Method doesn't exist, that's fine
                    }
                }

                // Show RecyclerView with animation
                recyclerView.setVisibility(View.VISIBLE);
                animateRecyclerViewEntrance();

                // Update header
                tvFavoritesCount.setText(favorites.size() + " favorite" + (favorites.size() == 1 ? "" : "s"));
                animateHeaderUpdate();
            }
        });
    }

    // 🌟 ANIMATION: Show empty state
    private void showEmptyState() {
        // Hide loading
        animateLayoutExit(layoutLoading, new Runnable() {
            @Override
            public void run() {
                // Show empty layout
                layoutEmpty.setVisibility(View.VISIBLE);
                animateLayoutEntrance(layoutEmpty);

                // Update header
                tvFavoritesCount.setText("No favorites yet");
                animateHeaderUpdate();
            }
        });
    }

    // 🌟 ANIMATION: Generic layout entrance
    private void animateLayoutEntrance(View layout) {
        layout.setAlpha(0f);
        layout.setTranslationY(100f);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(layout, "alpha", 0f, 1f);
        ObjectAnimator slideUp = ObjectAnimator.ofFloat(layout, "translationY", 100f, 0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeIn, slideUp);
        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    // 🌟 ANIMATION: Generic layout exit
    private void animateLayoutExit(View layout, Runnable onComplete) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(layout, "alpha", 1f, 0f);
        ObjectAnimator scaleDown = ObjectAnimator.ofFloat(layout, "scaleX", 1f, 0.8f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(layout, "scaleY", 1f, 0.8f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeOut, scaleDown, scaleDownY);
        animatorSet.setDuration(300);
        animatorSet.start();

        // Hide layout and run callback after animation
        layout.postDelayed(new Runnable() {
            @Override
            public void run() {
                layout.setVisibility(View.GONE);
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        }, 300);
    }

    // 🌟 ANIMATION: RecyclerView entrance
    private void animateRecyclerViewEntrance() {
        recyclerView.setAlpha(0f);
        recyclerView.setTranslationY(50f);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(recyclerView, "alpha", 0f, 1f);
        ObjectAnimator slideUp = ObjectAnimator.ofFloat(recyclerView, "translationY", 50f, 0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeIn, slideUp);
        animatorSet.setDuration(600);
        animatorSet.setStartDelay(200); // Slight delay for better effect
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    // 🌟 ANIMATION: Header update with bounce
    private void animateHeaderUpdate() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(tvFavoritesCount, "scaleX", 1f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(tvFavoritesCount, "scaleY", 1f, 1.1f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new BounceInterpolator());
        animatorSet.start();
    }

    // 🌟 ANIMATION: Button press effect
    private void animateButtonPress(View button, Runnable onComplete) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.9f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.9f);

        scaleDownX.setDuration(100);
        scaleDownY.setDuration(100);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();

        button.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(button, "scaleX", 0.9f, 1f);
                ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(button, "scaleY", 0.9f, 1f);

                scaleUpX.setDuration(100);
                scaleUpY.setDuration(100);

                AnimatorSet scaleUp = new AnimatorSet();
                scaleUp.play(scaleUpX).with(scaleUpY);
                scaleUp.start();

                if (onComplete != null) {
                    onComplete.run();
                }
            }
        }, 100);
    }

    // Helper method to hide all layouts
    private void hideAllLayouts() {
        layoutLoading.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }
}