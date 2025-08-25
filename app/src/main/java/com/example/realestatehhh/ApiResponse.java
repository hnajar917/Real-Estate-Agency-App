package com.example.realestatehhh;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiResponse {
    @SerializedName("categories")
    private List<PropertyCategory> categories;

    @SerializedName("properties")
    private List<Property> properties;

    public ApiResponse() {}

    public List<PropertyCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<PropertyCategory> categories) {
        this.categories = categories;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }
}