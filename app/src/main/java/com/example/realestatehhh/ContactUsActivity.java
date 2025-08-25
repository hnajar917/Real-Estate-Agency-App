package com.example.realestatehhh;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ContactUsActivity extends AppCompatActivity {

    private TextView btnCallUs, btnLocateUs, btnEmailUs;

    // Agency contact information
    private static final String AGENCY_PHONE = "+970599000000";
    private static final String AGENCY_EMAIL = "RealEstateHub@agency.com";
    private static final String AGENCY_LATITUDE = "32.2278"; // Nablus coordinates
    private static final String AGENCY_LONGITUDE = "35.2542";
    private static final String AGENCY_NAME = "Real Estate Hub Agency";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        setupToolbar();
        initViews();
        setupClickListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Contact Us");
    }

    private void initViews() {
        btnCallUs = findViewById(R.id.btn_call_us);
        btnLocateUs = findViewById(R.id.btn_locate_us);
        btnEmailUs = findViewById(R.id.btn_email_us);
    }

    private void setupClickListeners() {
        btnCallUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });

        btnLocateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps();
            }
        });

        btnEmailUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }

    private void makePhoneCall() {
        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + AGENCY_PHONE));
            startActivity(callIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to make phone call", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGoogleMaps() {
        try {
            // Create Google Maps URI with coordinates and label
            String mapUri = "geo:" + AGENCY_LATITUDE + "," + AGENCY_LONGITUDE +
                    "?q=" + AGENCY_LATITUDE + "," + AGENCY_LONGITUDE +
                    "(" + Uri.encode(AGENCY_NAME) + ")";

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUri));
            mapIntent.setPackage("com.google.android.apps.maps");

            // Check if Google Maps is installed
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // Fallback to web browser if Google Maps is not installed
                String webMapUri = "https://www.google.com/maps?q=" + AGENCY_LATITUDE + "," + AGENCY_LONGITUDE;
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webMapUri));
                startActivity(webIntent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open maps", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmail() {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + AGENCY_EMAIL));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry from Real Estate Hub App");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello,\n\nI am interested in your real estate services.\n\nBest regards,");

            // Check if email app is available
            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(emailIntent, "Send Email"));
            } else {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Unable to send email", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}