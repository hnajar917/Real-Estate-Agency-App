package com.example.realestatehhh;

import com.google.gson.annotations.SerializedName;

public class Property {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private double price;

    @SerializedName("location")
    private String location;

    @SerializedName("type")
    private String type;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("area")
    private String area;

    @SerializedName("bedrooms")
    private int bedrooms;

    @SerializedName("bathrooms")
    private int bathrooms;

    private boolean isFeatured;

    public Property() {}

    public Property(int id, String title, String description, double price,
                    String location, String type, String imageUrl, String area,
                    int bedrooms, int bathrooms, boolean isFeatured) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.location = location;
        this.type = type;
        this.imageUrl = imageUrl;
        this.area = area;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.isFeatured = isFeatured;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getLocation() { return location; }
    public String getType() { return type; }
    public String getImageUrl() { return imageUrl; }
    public String getArea() { return area; }
    public int getBedrooms() { return bedrooms; }
    public int getBathrooms() { return bathrooms; }
    public boolean isFeatured() { return isFeatured; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setLocation(String location) { this.location = location; }
    public void setType(String type) { this.type = type; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setArea(String area) { this.area = area; }
    public void setBedrooms(int bedrooms) { this.bedrooms = bedrooms; }
    public void setBathrooms(int bathrooms) { this.bathrooms = bathrooms; }
    public void setFeatured(boolean featured) { isFeatured = featured; }

    public String getFormattedPrice() {
        return String.format("$%.0f", price);
    }

    public String getPropertyDetails() {
        if (bedrooms > 0) {
            return bedrooms + " bed • " + bathrooms + " bath • " + area;
        } else {
            return area; // For land properties
        }
    }
}