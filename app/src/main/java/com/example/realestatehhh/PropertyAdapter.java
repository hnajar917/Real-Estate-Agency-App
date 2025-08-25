package com.example.realestatehhh;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {
    private List<Property> properties;
    private Context context;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    // 🎨 NEW: Animation tracking
    private int lastPosition = -1;

    public PropertyAdapter(List<Property> properties, Context context) {
        this.properties = properties;
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
        this.sharedPreferences = context.getSharedPreferences("RealEstatePrefs", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_property, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = properties.get(position);
        String userEmail = sharedPreferences.getString("user_email", "");

        holder.tvTitle.setText(property.getTitle());
        holder.tvPrice.setText(property.getFormattedPrice());
        holder.tvLocation.setText(property.getLocation());
        holder.tvType.setText(property.getType());
        holder.tvDetails.setText(property.getPropertyDetails());
        holder.tvDescription.setText(property.getDescription());

        // Set property image (placeholder for now)
        holder.ivProperty.setImageResource(getPropertyIcon(property.getType()));

        // Update favorite button based on current status
        updateFavoriteButton(holder.tvFavorite, userEmail, property.getId());

        // Update reserve button based on current status
        updateReserveButton(holder.tvReserve, userEmail, property.getId());

        // 🎨 NEW: Add smooth slide-in animation
        animateItemSlideIn(holder.itemView, position);

        // Set click listener for property card with animation
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateCardPress(v, new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(context, PropertyDetailActivity.class);
                        intent.putExtra("property_id", property.getId());
                        context.startActivity(intent);
                    }
                });
            }
        });


        setupTouchFeedback(holder.cardView);

        // Set favorite button click with animation
        holder.tvFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonPress(v, new Runnable() {
                    @Override
                    public void run() {
                        toggleFavorite(userEmail, property, holder.tvFavorite);
                    }
                });
            }
        });

        // Set reserve button click with animation
        holder.tvReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonPress(v, new Runnable() {
                    @Override
                    public void run() {
                        toggleReservation(userEmail, property, holder.tvReserve);
                    }
                });
            }
        });
    }

    // 🌟 NEW: Smooth slide-in animation for cards
    private void animateItemSlideIn(View view, int position) {
        if (position > lastPosition) {
            view.setTranslationX(300f);
            view.setAlpha(0f);

            ObjectAnimator slideIn = ObjectAnimator.ofFloat(view, "translationX", 300f, 0f);
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(slideIn, fadeIn);
            animatorSet.setDuration(400);
            animatorSet.setStartDelay(position * 60); // Stagger effect
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.start();

            lastPosition = position;
        }
    }


    private void animateCardPress(View view, Runnable onComplete) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.96f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.96f);

        scaleDownX.setDuration(120);
        scaleDownY.setDuration(120);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 0.96f, 1f);
                ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.96f, 1f);

                scaleUpX.setDuration(120);
                scaleUpY.setDuration(120);

                AnimatorSet scaleUp = new AnimatorSet();
                scaleUp.play(scaleUpX).with(scaleUpY);
                scaleUp.start();

                if (onComplete != null) {
                    onComplete.run();
                }
            }
        }, 120);
    }

    // 🌟 NEW: Button press animation
    private void animateButtonPress(View view, Runnable onComplete) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f);

        scaleDownX.setDuration(80);
        scaleDownY.setDuration(80);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f);
                ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f);

                scaleUpX.setDuration(80);
                scaleUpY.setDuration(80);

                AnimatorSet scaleUp = new AnimatorSet();
                scaleUp.play(scaleUpX).with(scaleUpY);
                scaleUp.start();

                if (onComplete != null) {
                    onComplete.run();
                }
            }
        }, 80);
    }

    // 🌟 NEW: Subtle touch feedback
    private void setupTouchFeedback(CardView cardView) {
        cardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ObjectAnimator elevationUp = ObjectAnimator.ofFloat(v, "elevation",
                                v.getElevation(), v.getElevation() + 6f);
                        elevationUp.setDuration(100);
                        elevationUp.start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        ObjectAnimator elevationDown = ObjectAnimator.ofFloat(v, "elevation",
                                v.getElevation(), v.getElevation() - 6f);
                        elevationDown.setDuration(100);
                        elevationDown.start();
                        break;
                }
                return false; // Let click listener handle the click
            }
        });
    }

    // 🔄 NEW: Method to reset animations when refreshing data
    public void resetAnimations() {
        lastPosition = -1;
    }

    // YOUR EXISTING METHODS - UNCHANGED
    private void updateFavoriteButton(TextView tvFavorite, String userEmail, int propertyId) {
        if (dbHelper.isPropertyInFavorites(userEmail, propertyId)) {
            tvFavorite.setText("❤ Favorited");
            tvFavorite.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
            tvFavorite.setTextColor(context.getResources().getColor(android.R.color.white));
        } else {
            tvFavorite.setText("♡ Favorite");
            tvFavorite.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            tvFavorite.setTextColor(context.getResources().getColor(R.color.primary_blue));
        }
    }

    private void updateReserveButton(TextView tvReserve, String userEmail, int propertyId) {
        if (dbHelper.isPropertyReservedByUser(userEmail, propertyId)) {
            tvReserve.setText("Reserved");
            tvReserve.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
            tvReserve.setTextColor(context.getResources().getColor(android.R.color.white));
        } else {
            tvReserve.setText("Reserve");
            tvReserve.setBackgroundColor(context.getResources().getColor(R.color.primary_blue));
            tvReserve.setTextColor(context.getResources().getColor(android.R.color.white));
        }
    }

    private void toggleFavorite(String userEmail, Property property, TextView tvFavorite) {
        if (dbHelper.isPropertyInFavorites(userEmail, property.getId())) {
            // Remove from favorites
            if (dbHelper.removeFromFavorites(userEmail, property.getId())) {
                updateFavoriteButton(tvFavorite, userEmail, property.getId());
                Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to remove from favorites", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Add to favorites
            if (dbHelper.addToFavorites(userEmail, property.getId())) {
                updateFavoriteButton(tvFavorite, userEmail, property.getId());
                Toast.makeText(context, "Added to favorites!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to add to favorites", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void toggleReservation(String userEmail, Property property, TextView tvReserve) {
        if (dbHelper.isPropertyReservedByUser(userEmail, property.getId())) {
            // Cancel reservation
            if (dbHelper.cancelReservation(userEmail, property.getId())) {
                updateReserveButton(tvReserve, userEmail, property.getId());
                Toast.makeText(context, "Reservation cancelled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to cancel reservation", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Make reservation
            if (dbHelper.reserveProperty(userEmail, property.getId(), "Reserved via app")) {
                updateReserveButton(tvReserve, userEmail, property.getId());
                Toast.makeText(context, "Property reserved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to reserve property", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    private int getPropertyIcon(String type) {
        switch (type.toLowerCase()) {
            case "villa":
                return android.R.drawable.ic_menu_gallery;
            case "apartment":
                return android.R.drawable.ic_menu_view;
            case "land":
                return android.R.drawable.ic_menu_mapmode;
            default:
                return android.R.drawable.ic_menu_gallery;
        }
    }

    public static class PropertyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivProperty;
        TextView tvTitle, tvPrice, tvLocation, tvType, tvDetails, tvDescription;
        TextView tvFavorite, tvReserve;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            ivProperty = itemView.findViewById(R.id.iv_property);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvType = itemView.findViewById(R.id.tv_type);
            tvDetails = itemView.findViewById(R.id.tv_details);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvFavorite = itemView.findViewById(R.id.tv_favorite);
            tvReserve = itemView.findViewById(R.id.tv_reserve);
        }
    }
}