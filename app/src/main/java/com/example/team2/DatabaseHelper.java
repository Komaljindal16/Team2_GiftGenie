package com.example.team2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "team2.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_RESET_CODE = "reset_code";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT NOT NULL, " +
            COLUMN_EMAIL + " TEXT NOT NULL, " +
            COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, " +
            COLUMN_PASSWORD + " TEXT NOT NULL, " +
            COLUMN_PHONE_NUMBER + " TEXT NOT NULL, " +
            COLUMN_RESET_CODE + " TEXT);";

    private static SQLiteDatabase database;
    private static int openCount = 0; // Reference counter to track open instances

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        synchronized (DatabaseHelper.class) {
            if (database == null || !database.isOpen()) {
                database = getWritableDatabase();
            }
            openCount++;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;
        }
    }

    public long addUser(String name, String email, String username, String password, String phoneNumber) {
        synchronized (DatabaseHelper.class) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_EMAIL, email);
            values.put(COLUMN_USERNAME, username);
            values.put(COLUMN_PASSWORD, hashPassword(password));
            values.put(COLUMN_PHONE_NUMBER, phoneNumber);
            return database.insert(TABLE_USERS, null, values);
        }
    }

    public boolean userExists(String username) {
        synchronized (DatabaseHelper.class) {
            Cursor cursor = database.query(TABLE_USERS, new String[]{COLUMN_USERNAME},
                    COLUMN_USERNAME + "=?", new String[]{username},
                    null, null, null);
            boolean exists = cursor.getCount() > 0;
            cursor.close();
            return exists;
        }
    }

    public boolean verifyUser(String username, String password) {
        synchronized (DatabaseHelper.class) {
            Cursor cursor = database.query(TABLE_USERS, new String[]{COLUMN_PASSWORD},
                    COLUMN_USERNAME + "=?", new String[]{username},
                    null, null, null);
            if (cursor.moveToFirst()) {
                String storedHash = cursor.getString(0);
                String inputHash = hashPassword(password);
                boolean valid = storedHash.equals(inputHash);
                cursor.close();
                return valid;
            }
            cursor.close();
            return false;
        }
    }

    public boolean generateResetCode(String username) {
        synchronized (DatabaseHelper.class) {
            if (!userExists(username)) {
                return false;
            }
            ContentValues values = new ContentValues();
            String resetCode = UUID.randomUUID().toString().substring(0, 8);
            values.put(COLUMN_RESET_CODE, resetCode);
            int rows = database.update(TABLE_USERS, values, COLUMN_USERNAME + "=?", new String[]{username});
            return rows > 0;
        }
    }

    public void closeDatabase() {
        synchronized (DatabaseHelper.class) {
            openCount--;
            if (openCount <= 0 && database != null && database.isOpen()) {
                database.close();
                database = null;
                openCount = 0;
            }
        }
    }
}