package com.example.realestatehhh;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    // Use the exact endpoint path from your Mocki.io URL
    @GET("864ce399-3f76-4c3a-b9f2-3102f57d804f")
    Call<ApiResponse> getPropertiesData();
}
