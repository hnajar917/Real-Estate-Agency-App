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

public class AdminPropertyAdapter extends RecyclerView.Adapter<AdminPropertyAdapter.AdminPropertyViewHolder> {
    private List<Property> properties;
    private Context context;
    private OnPropertyActionListener listener;

    public interface OnPropertyActionListener {
        void onEditProperty(Property property);
        void onDeleteProperty(Property property);
    }

    public AdminPropertyAdapter(List<Property> properties, Context context, OnPropertyActionListener listener) {
        this.properties = properties;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminPropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_property, parent, false);
        return new AdminPropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminPropertyViewHolder holder, int position) {
        Property property = properties.get(position);

        holder.tvTitle.setText(property.getTitle());
        holder.tvPrice.setText(property.getFormattedPrice());
        holder.tvLocation.setText(property.getLocation());
        holder.tvType.setText(property.getType());
        holder.tvDetails.setText(property.getPropertyDetails());
        holder.tvDescription.setText(property.getDescription());
        holder.tvPropertyId.setText("ID: " + property.getId());

        // Set property image based on type
        holder.ivProperty.setImageResource(getPropertyIcon(property.getType()));

        // Set click listeners
        holder.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onEditProperty(property);
                }
            }
        });

        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDeleteProperty(property);
                }
            }
        });
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
            case "house":
                return android.R.drawable.ic_menu_gallery;
            case "commercial":
                return android.R.drawable.ic_menu_view;
            default:
                return android.R.drawable.ic_menu_gallery;
        }
    }

    public static class AdminPropertyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivProperty;
        TextView tvTitle, tvPrice, tvLocation, tvType, tvDetails, tvDescription, tvPropertyId;
        TextView tvEdit, tvDelete;

        public AdminPropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            ivProperty = itemView.findViewById(R.id.iv_property);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvType = itemView.findViewById(R.id.tv_type);
            tvDetails = itemView.findViewById(R.id.tv_details);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvPropertyId = itemView.findViewById(R.id.tv_property_id);
            tvEdit = itemView.findViewById(R.id.tv_edit);
            tvDelete = itemView.findViewById(R.id.tv_delete);
        }
    }
}