package com.example.realestatehhh;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "RealEstateDB";
    private static final int DATABASE_VERSION = 3; // Updated version for profile picture column

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String COL_EMAIL = "email";
    private static final String COL_FIRST_NAME = "first_name";
    private static final String COL_LAST_NAME = "last_name";
    private static final String COL_PASSWORD = "password";
    private static final String COL_GENDER = "gender";
    private static final String COL_COUNTRY = "country";
    private static final String COL_CITY = "city";
    private static final String COL_PHONE = "phone";
    private static final String COL_ROLE = "role";
    private static final String COL_PROFILE_PICTURE = "profile_picture"; // NEW COLUMN

    // Properties table
    private static final String TABLE_PROPERTIES = "properties";
    private static final String COL_PROPERTY_ID = "property_id";
    private static final String COL_TITLE = "title";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_PRICE = "price";
    private static final String COL_LOCATION = "location";
    private static final String COL_TYPE = "type";
    private static final String COL_IMAGE_URL = "image_url";
    private static final String COL_AREA = "area";
    private static final String COL_BEDROOMS = "bedrooms";
    private static final String COL_BATHROOMS = "bathrooms";
    private static final String COL_IS_FEATURED = "is_featured";

    // NEW: Favorites and Reservations tables
    private static final String TABLE_FAVORITES = "favorites";
    private static final String TABLE_RESERVATIONS = "reservations";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table with profile_picture column
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + COL_EMAIL + " TEXT PRIMARY KEY,"
                + COL_FIRST_NAME + " TEXT,"
                + COL_LAST_NAME + " TEXT,"
                + COL_PASSWORD + " TEXT,"
                + COL_GENDER + " TEXT,"
                + COL_COUNTRY + " TEXT,"
                + COL_CITY + " TEXT,"
                + COL_PHONE + " TEXT,"
                + COL_ROLE + " TEXT,"
                + COL_PROFILE_PICTURE + " TEXT)"; // NEW COLUMN
        db.execSQL(createUsersTable);

        // Create properties table
        String createPropertiesTable = "CREATE TABLE " + TABLE_PROPERTIES + "("
                + COL_PROPERTY_ID + " INTEGER PRIMARY KEY,"
                + COL_TITLE + " TEXT,"
                + COL_DESCRIPTION + " TEXT,"
                + COL_PRICE + " REAL,"
                + COL_LOCATION + " TEXT,"
                + COL_TYPE + " TEXT,"
                + COL_IMAGE_URL + " TEXT,"
                + COL_AREA + " TEXT,"
                + COL_BEDROOMS + " INTEGER,"
                + COL_BATHROOMS + " INTEGER,"
                + COL_IS_FEATURED + " INTEGER DEFAULT 0)";
        db.execSQL(createPropertiesTable);

        // Create favorites table
        String CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user_email TEXT,"
                + "property_id INTEGER,"
                + "date_added TEXT,"
                + "FOREIGN KEY(property_id) REFERENCES " + TABLE_PROPERTIES + "(property_id)"
                + ")";
        db.execSQL(CREATE_FAVORITES_TABLE);

        // Create reservations table
        String CREATE_RESERVATIONS_TABLE = "CREATE TABLE " + TABLE_RESERVATIONS + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user_email TEXT,"
                + "property_id INTEGER,"
                + "reservation_date TEXT,"
                + "status TEXT DEFAULT 'active',"
                + "notes TEXT,"
                + "FOREIGN KEY(property_id) REFERENCES " + TABLE_PROPERTIES + "(property_id)"
                + ")";
        db.execSQL(CREATE_RESERVATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add favorites table
            String CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "user_email TEXT,"
                    + "property_id INTEGER,"
                    + "date_added TEXT,"
                    + "FOREIGN KEY(property_id) REFERENCES " + TABLE_PROPERTIES + "(property_id)"
                    + ")";
            db.execSQL(CREATE_FAVORITES_TABLE);

            // Add reservations table
            String CREATE_RESERVATIONS_TABLE = "CREATE TABLE " + TABLE_RESERVATIONS + "("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "user_email TEXT,"
                    + "property_id INTEGER,"
                    + "reservation_date TEXT,"
                    + "status TEXT DEFAULT 'active',"
                    + "notes TEXT,"
                    + "FOREIGN KEY(property_id) REFERENCES " + TABLE_PROPERTIES + "(property_id)"
                    + ")";
            db.execSQL(CREATE_RESERVATIONS_TABLE);
        }

        if (oldVersion < 3) {
            // Add profile_picture column to users table
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COL_PROFILE_PICTURE + " TEXT");
        }
    }

    // User operations
    public long registerUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EMAIL, user.getEmail());
        values.put(COL_FIRST_NAME, user.getFirstName());
        values.put(COL_LAST_NAME, user.getLastName());
        values.put(COL_PASSWORD, user.getPassword());
        values.put(COL_GENDER, user.getGender());
        values.put(COL_COUNTRY, user.getCountry());
        values.put(COL_CITY, user.getCity());
        values.put(COL_PHONE, user.getPhone());
        values.put(COL_ROLE, user.getRole());
        values.put(COL_PROFILE_PICTURE, ""); // Initialize with empty profile picture

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result;
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COL_EMAIL + "=? AND " + COL_PASSWORD + "=?",
                new String[]{email, password}, null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_FIRST_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_LAST_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_GENDER)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_COUNTRY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE))
            );
        }
        cursor.close();
        db.close();
        return user;
    }

    // NEW: Get user profile picture
    public String getUserProfilePicture(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_PROFILE_PICTURE},
                COL_EMAIL + "=?",
                new String[]{email}, null, null, null);

        String profilePicture = "";
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(COL_PROFILE_PICTURE);
            if (columnIndex != -1) {
                profilePicture = cursor.getString(columnIndex);
                if (profilePicture == null) {
                    profilePicture = "";
                }
            }
        }
        cursor.close();
        db.close();
        return profilePicture;
    }

    // NEW: Update user profile picture
    public boolean updateUserProfilePicture(String email, String profilePictureBase64) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PROFILE_PICTURE, profilePictureBase64);

        int result = db.update(TABLE_USERS, values, COL_EMAIL + " = ?", new String[]{email});
        db.close();
        return result > 0;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COL_EMAIL + "=?",
                new String[]{email}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Property operations
    public void savePropertiesFromApi(List<Property> properties) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROPERTIES, null, null); // Clear existing data

        for (Property property : properties) {
            ContentValues values = new ContentValues();
            values.put(COL_PROPERTY_ID, property.getId());
            values.put(COL_TITLE, property.getTitle());
            values.put(COL_DESCRIPTION, property.getDescription());
            values.put(COL_PRICE, property.getPrice());
            values.put(COL_LOCATION, property.getLocation());
            values.put(COL_TYPE, property.getType());
            values.put(COL_IMAGE_URL, property.getImageUrl());
            values.put(COL_AREA, property.getArea());
            values.put(COL_BEDROOMS, property.getBedrooms());
            values.put(COL_BATHROOMS, property.getBathrooms());
            values.put(COL_IS_FEATURED, 0); // Will be set by admin later

            db.insertWithOnConflict(TABLE_PROPERTIES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
        db.close();
    }

    public List<Property> getAllProperties() {
        List<Property> properties = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROPERTIES, null);

        if (cursor.moveToFirst()) {
            do {
                Property property = new Property(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROPERTY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_AREA)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_BEDROOMS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_BATHROOMS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_FEATURED)) == 1
                );
                properties.add(property);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return properties;
    }

    // FAVORITES METHODS
    public boolean addToFavorites(String userEmail, int propertyId) {
        // Check if already in favorites first
        if (isPropertyInFavorites(userEmail, propertyId)) {
            return false; // Already exists
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_email", userEmail);
        values.put("property_id", propertyId);
        values.put("date_added", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

        long result = db.insert(TABLE_FAVORITES, null, values);
        return result != -1;
    }

    public boolean removeFromFavorites(String userEmail, int propertyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_FAVORITES,
                "user_email = ? AND property_id = ?",
                new String[]{userEmail, String.valueOf(propertyId)});
        return result > 0;
    }

    public boolean isPropertyInFavorites(String userEmail, int propertyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVORITES,
                new String[]{"id"},
                "user_email = ? AND property_id = ?",
                new String[]{userEmail, String.valueOf(propertyId)},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public List<Property> getUserFavorites(String userEmail) {
        List<Property> favorites = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT p.* FROM " + TABLE_PROPERTIES + " p " +
                "INNER JOIN " + TABLE_FAVORITES + " f ON p.property_id = f.property_id " +
                "WHERE f.user_email = ? ORDER BY f.date_added DESC";

        Cursor cursor = db.rawQuery(query, new String[]{userEmail});

        if (cursor.moveToFirst()) {
            do {
                Property property = new Property(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROPERTY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_AREA)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_BEDROOMS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_BATHROOMS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_FEATURED)) == 1
                );
                favorites.add(property);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return favorites;
    }

    // RESERVATIONS METHODS
    public boolean reserveProperty(String userEmail, int propertyId, String notes) {
        // Check if already reserved by this user first
        if (isPropertyReservedByUser(userEmail, propertyId)) {
            return false; // Already reserved
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_email", userEmail);
        values.put("property_id", propertyId);
        values.put("reservation_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        values.put("status", "active");
        values.put("notes", notes);

        long result = db.insert(TABLE_RESERVATIONS, null, values);
        return result != -1;
    }

    public boolean cancelReservation(String userEmail, int propertyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", "cancelled");

        int result = db.update(TABLE_RESERVATIONS, values,
                "user_email = ? AND property_id = ? AND status = 'active'",
                new String[]{userEmail, String.valueOf(propertyId)});
        return result > 0;
    }

    public boolean isPropertyReservedByUser(String userEmail, int propertyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RESERVATIONS,
                new String[]{"id"},
                "user_email = ? AND property_id = ? AND status = 'active'",
                new String[]{userEmail, String.valueOf(propertyId)},
                null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public List<Property> getUserReservations(String userEmail) {
        List<Property> reservations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT p.* FROM " + TABLE_PROPERTIES + " p " +
                "INNER JOIN " + TABLE_RESERVATIONS + " r ON p.property_id = r.property_id " +
                "WHERE r.user_email = ? AND r.status = 'active' ORDER BY r.reservation_date DESC";

        Cursor cursor = db.rawQuery(query, new String[]{userEmail});

        if (cursor.moveToFirst()) {
            do {
                Property property = new Property(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROPERTY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_AREA)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_BEDROOMS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_BATHROOMS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_FEATURED)) == 1
                );
                reservations.add(property);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return reservations;
    }

    // UPDATED: updateUser method to include profile picture
    public boolean updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_FIRST_NAME, user.getFirstName());
        values.put(COL_LAST_NAME, user.getLastName());
        values.put(COL_PHONE, user.getPhone());
        values.put(COL_COUNTRY, user.getCountry());
        values.put(COL_CITY, user.getCity());
        values.put(COL_GENDER, user.getGender());
        // Note: We don't update email and password here for security

        int result = db.update(TABLE_USERS, values, COL_EMAIL + " = ?",
                new String[]{user.getEmail()});

        return result > 0;
    }

    // ADMIN STATISTICS METHODS
    public int getTotalUsersCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getTotalReservationsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RESERVATIONS + " WHERE status = 'active'", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getTotalFavoritesCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_FAVORITES, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User(
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_FIRST_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_LAST_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_GENDER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_COUNTRY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE))
                );
                users.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return users;
    }

    public boolean deleteUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Also delete user's favorites and reservations
        db.delete(TABLE_FAVORITES, "user_email = ?", new String[]{email});
        db.delete(TABLE_RESERVATIONS, "user_email = ?", new String[]{email});

        int result = db.delete(TABLE_USERS, COL_EMAIL + " = ?", new String[]{email});
        return result > 0;
    }

    public boolean deleteProperty(int propertyId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Also delete related favorites and reservations
        db.delete(TABLE_FAVORITES, "property_id = ?", new String[]{String.valueOf(propertyId)});
        db.delete(TABLE_RESERVATIONS, "property_id = ?", new String[]{String.valueOf(propertyId)});

        int result = db.delete(TABLE_PROPERTIES, COL_PROPERTY_ID + " = ?", new String[]{String.valueOf(propertyId)});
        return result > 0;
    }

    public boolean addProperty(Property property) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_TITLE, property.getTitle());
        values.put(COL_DESCRIPTION, property.getDescription());
        values.put(COL_PRICE, property.getPrice());
        values.put(COL_LOCATION, property.getLocation());
        values.put(COL_TYPE, property.getType());
        values.put(COL_IMAGE_URL, property.getImageUrl());
        values.put(COL_AREA, property.getArea());
        values.put(COL_BEDROOMS, property.getBedrooms());
        values.put(COL_BATHROOMS, property.getBathrooms());
        values.put(COL_IS_FEATURED, property.isFeatured() ? 1 : 0);

        long result = db.insert(TABLE_PROPERTIES, null, values);
        return result != -1;
    }
}