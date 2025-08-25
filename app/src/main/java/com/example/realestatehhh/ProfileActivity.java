package com.example.realestatehhh;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 101;
    private static final int REQUEST_CAMERA = 102;
    private static final int REQUEST_GALLERY = 103;

    private TextView tvUserEmail, tvUserRole;
    private EditText etFirstName, etLastName, etPhone, etCountry, etCity;
    private TextView btnEdit, btnSave, btnCancel, btnChangePicture;
    private CircleImageView ivProfilePicture;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private boolean isEditMode = false;
    private User currentUser;
    private String currentProfilePictureBase64 = "";
    private String originalProfilePictureBase64 = ""; // Store original for cancel functionality

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupToolbar();
        loadUserProfile();
        updateUIMode();
        setupProfilePictureClick();
    }

    private void initViews() {
        tvUserEmail = findViewById(R.id.tv_user_email);
        tvUserRole = findViewById(R.id.tv_user_role);
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etPhone = findViewById(R.id.et_phone);
        etCountry = findViewById(R.id.et_country);
        etCity = findViewById(R.id.et_city);
        btnEdit = findViewById(R.id.btn_edit);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        btnChangePicture = findViewById(R.id.btn_change_picture);
        ivProfilePicture = findViewById(R.id.iv_profile_picture);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("RealEstatePrefs", MODE_PRIVATE);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profile Settings");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupProfilePictureClick() {
        btnChangePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("📷 Change Profile Picture");
        builder.setMessage("Choose an option:");

        builder.setPositiveButton("📷 Camera", (dialog, which) -> {
            if (checkCameraPermission()) {
                openCamera();
            } else {
                requestCameraPermission();
            }
        });

        builder.setNegativeButton("🖼️ Gallery", (dialog, which) -> {
            if (checkStoragePermission()) {
                openGallery();
            } else {
                requestStoragePermission();
            }
        });

        builder.setNeutralButton("❌ Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } else {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    Toast.makeText(this, "Storage permission required", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_CAMERA:
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        if (imageBitmap != null) {
                            setProfilePicture(imageBitmap);
                        }
                    }
                    break;
                case REQUEST_GALLERY:
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        try {
                            InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                            if (selectedImage != null) {
                                setProfilePicture(selectedImage);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    }

    private void setProfilePicture(Bitmap bitmap) {
        // Resize bitmap to reasonable size
        Bitmap resizedBitmap = resizeBitmap(bitmap, 300, 300);

        // Set to ImageView
        ivProfilePicture.setImageBitmap(resizedBitmap);

        // Convert to base64 for storage
        currentProfilePictureBase64 = bitmapToBase64(resizedBitmap);

        Toast.makeText(this, "✅ Profile picture updated!", Toast.LENGTH_SHORT).show();
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) maxWidth) / width;
        float scaleHeight = ((float) maxHeight) / height;
        float scale = Math.min(scaleWidth, scaleHeight);

        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private Bitmap base64ToBitmap(String base64String) {
        try {
            if (base64String == null || base64String.isEmpty()) {
                return null;
            }
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadUserProfile() {
        String email = sharedPreferences.getString("user_email", "");
        String firstName = sharedPreferences.getString("user_first_name", "");
        String lastName = sharedPreferences.getString("user_last_name", "");
        String phone = sharedPreferences.getString("user_phone", "");
        String country = sharedPreferences.getString("user_country", "");
        String city = sharedPreferences.getString("user_city", "");
        String gender = sharedPreferences.getString("user_gender", "");
        String role = sharedPreferences.getString("user_role", "user");
        String password = sharedPreferences.getString("user_password", "");

        if (!email.isEmpty()) {
            currentUser = new User(email, firstName, lastName, password, gender, country, city, phone, role);

            // Load profile picture from database
            currentProfilePictureBase64 = dbHelper.getUserProfilePicture(email);
            originalProfilePictureBase64 = currentProfilePictureBase64; // Store original for cancel

            displayUserInfo();
            loadProfilePicture();
        } else {
            Toast.makeText(this, "No user session found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadProfilePicture() {
        if (!currentProfilePictureBase64.isEmpty()) {
            Bitmap bitmap = base64ToBitmap(currentProfilePictureBase64);
            if (bitmap != null) {
                ivProfilePicture.setImageBitmap(bitmap);
            }
        }
    }

    private void displayUserInfo() {
        tvUserEmail.setText(currentUser.getEmail());
        tvUserRole.setText(currentUser.getRole().toUpperCase());
        etFirstName.setText(currentUser.getFirstName());
        etLastName.setText(currentUser.getLastName());
        etPhone.setText(currentUser.getPhone());
        etCountry.setText(currentUser.getCountry());
        etCity.setText(currentUser.getCity());
    }

    private void updateUIMode() {
        if (isEditMode) {
            // Edit mode - enable editing
            etFirstName.setEnabled(true);
            etLastName.setEnabled(true);
            etPhone.setEnabled(true);
            etCountry.setEnabled(true);
            etCity.setEnabled(true);
            btnChangePicture.setVisibility(View.VISIBLE);

            btnEdit.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
        } else {
            // View mode - disable editing
            etFirstName.setEnabled(false);
            etLastName.setEnabled(false);
            etPhone.setEnabled(false);
            etCountry.setEnabled(false);
            etCity.setEnabled(false);
            btnChangePicture.setVisibility(View.GONE);

            btnEdit.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
        }
    }

    public void onEditClick(View view) {
        isEditMode = true;
        updateUIMode();
        Toast.makeText(this, "✏️ Edit mode enabled", Toast.LENGTH_SHORT).show();
    }

    public void onSaveClick(View view) {
        if (validateInput()) {
            saveUserProfile();
        }
    }

    public void onCancelClick(View view) {
        isEditMode = false;
        displayUserInfo(); // Restore original values

        // Restore original profile picture
        currentProfilePictureBase64 = originalProfilePictureBase64;
        loadProfilePicture();

        updateUIMode();
        Toast.makeText(this, "❌ Changes cancelled", Toast.LENGTH_SHORT).show();
    }

    private boolean validateInput() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        String city = etCity.getText().toString().trim();

        if (firstName.isEmpty()) {
            etFirstName.setError("First name is required");
            etFirstName.requestFocus();
            return false;
        }

        if (lastName.isEmpty()) {
            etLastName.setError("Last name is required");
            etLastName.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Phone number is required");
            etPhone.requestFocus();
            return false;
        }

        if (country.isEmpty()) {
            etCountry.setError("Country is required");
            etCountry.requestFocus();
            return false;
        }

        if (city.isEmpty()) {
            etCity.setError("City is required");
            etCity.requestFocus();
            return false;
        }

        return true;
    }

    private void saveUserProfile() {
        // Update user object
        currentUser.setFirstName(etFirstName.getText().toString().trim());
        currentUser.setLastName(etLastName.getText().toString().trim());
        currentUser.setPhone(etPhone.getText().toString().trim());
        currentUser.setCountry(etCountry.getText().toString().trim());
        currentUser.setCity(etCity.getText().toString().trim());

        // Update profile information in database
        boolean profileUpdated = dbHelper.updateUser(currentUser);

        // Update profile picture in database
        boolean pictureUpdated = dbHelper.updateUserProfilePicture(currentUser.getEmail(), currentProfilePictureBase64);

        if (profileUpdated && pictureUpdated) {
            // Update SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user_first_name", currentUser.getFirstName());
            editor.putString("user_last_name", currentUser.getLastName());
            editor.putString("user_phone", currentUser.getPhone());
            editor.putString("user_country", currentUser.getCountry());
            editor.putString("user_city", currentUser.getCity());
            // Note: Profile picture is now stored in database, not SharedPreferences
            editor.apply();

            // Update original profile picture for next cancel operation
            originalProfilePictureBase64 = currentProfilePictureBase64;

            isEditMode = false;
            updateUIMode();
            Toast.makeText(this, "✅ Profile updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "❌ Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }
}