package com.example.realestatehhh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WelcomeActiviy extends AppCompatActivity {
    private TextView btnConnect;  // Changed from Button to TextView
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        btnConnect = findViewById(R.id.btn_connect);  // Now finds TextView
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToApi();
            }
        });
    }

    private void connectToApi() {
        btnConnect.setText("Connecting...");
        btnConnect.setClickable(false);  // Disable during API call

        Call<ApiResponse> call = apiService.getPropertiesData();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                btnConnect.setText("CONNECT");
                btnConnect.setClickable(true);  // Re-enable

                if (response.isSuccessful() && response.body() != null) {
                    // Save properties to database
                    DatabaseHelper dbHelper = new DatabaseHelper(WelcomeActiviy.this);
                    ApiResponse apiResponse = response.body();

                    if (apiResponse.getProperties() != null && !apiResponse.getProperties().isEmpty()) {
                        dbHelper.savePropertiesFromApi(apiResponse.getProperties());
                        Toast.makeText(WelcomeActiviy.this, "Connection successful! " +
                                apiResponse.getProperties().size() + " properties loaded", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(WelcomeActiviy.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(WelcomeActiviy.this, "No properties found in response", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(WelcomeActiviy.this, "Server error: " + response.code() + " - " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                btnConnect.setText("CONNECT");
                btnConnect.setClickable(true);  // Re-enable
                String errorMsg = "Connection failed: ";
                if (t instanceof java.net.UnknownHostException) {
                    errorMsg += "Check internet connection";
                } else if (t instanceof java.net.SocketTimeoutException) {
                    errorMsg += "Connection timeout";
                } else {
                    errorMsg += t.getMessage();
                }
                Toast.makeText(WelcomeActiviy.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }
}