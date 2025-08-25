package com.example.realestatehhh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> users;
    private Context context;
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onDeleteUser(User user);
        void onViewUserDetails(User user);
    }

    public UserAdapter(List<User> users, Context context, OnUserActionListener listener) {
        this.users = users;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);

        holder.tvName.setText(user.getFirstName() + " " + user.getLastName());
        holder.tvEmail.setText(user.getEmail());
        holder.tvPhone.setText(user.getPhone());
        holder.tvLocation.setText(user.getCity() + ", " + user.getCountry());
        holder.tvRole.setText(user.getRole().toUpperCase());

        // Set role badge color
        if (user.getRole().equals("admin")) {
            holder.tvRole.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvRole.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));
        }

        // Set gender icon
        if (user.getGender().equalsIgnoreCase("male")) {
            holder.ivGender.setImageResource(android.R.drawable.ic_menu_my_calendar);
        } else {
            holder.ivGender.setImageResource(android.R.drawable.ic_menu_today);
        }

        // Set click listeners
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onViewUserDetails(user);
                }
            }
        });

        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDeleteUser(user);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivGender;
        TextView tvName, tvEmail, tvPhone, tvLocation, tvRole, tvDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            ivGender = itemView.findViewById(R.id.iv_gender);
            tvName = itemView.findViewById(R.id.tv_name);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvRole = itemView.findViewById(R.id.tv_role);
            tvDelete = itemView.findViewById(R.id.tv_delete);
        }
    }
}