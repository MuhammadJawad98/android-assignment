package com.example.pc.mytravelapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "locationsTable";
    private static final String TABLE_LOCATIONS = "locations";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LNG = "longitude";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_NOTE = "note";
    private static final String KEY_CURRENT_DATE = "date";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOCATIONS_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_LAT + " TEXT," + KEY_LNG + " TEXT," + KEY_ADDRESS + " TEXT," + KEY_NOTE + " TEXT," + KEY_CURRENT_DATE + " TEXT" + ")";
        db.execSQL(CREATE_LOCATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        onCreate(db);
    }

    void addLocation(LocationData locationData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, locationData.getLocationName());
        values.put(KEY_LAT, locationData.getLocationName());
        values.put(KEY_LNG, locationData.getLocationName());
        values.put(KEY_ADDRESS, locationData.getLocationName());
        values.put(KEY_NOTE, locationData.getLocationName());
        values.put(KEY_CURRENT_DATE, locationData.getLocationName());


        db.insert(TABLE_LOCATIONS, null, values);
        db.close();
    }


    LocationData getLocationData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_LOCATIONS, new String[]{KEY_ID,
                        KEY_NAME, KEY_LAT, KEY_LNG, KEY_NOTE, KEY_ADDRESS, KEY_CURRENT_DATE}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        LocationData locationData = new LocationData(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3),
                cursor.getString(4), cursor.getString(5), cursor.getString(6));
        return locationData;
    }

    public List<LocationData> getAllLocations() {
        List<LocationData> locationsList = new ArrayList<LocationData>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOCATIONS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                LocationData locationData = new LocationData();
                locationData.setId(Integer.parseInt(cursor.getString(0)));
                locationData.setLocationName(cursor.getString(1));
                locationData.setLat(cursor.getString(2));
                locationData.setLng(cursor.getString(3));
                locationData.setNote(cursor.getString(4));
                locationData.setAddress(cursor.getString(5));
                locationData.setCurrentDate(cursor.getString(6));
                locationsList.add(locationData);
            } while (cursor.moveToNext());
        }

        return locationsList;
    }

    public int updateLocationData(LocationData locationData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, locationData.getLocationName());
        values.put(KEY_LAT, locationData.getLat());
        values.put(KEY_LNG, locationData.getLng());
        values.put(KEY_ADDRESS, locationData.getAddress());
        values.put(KEY_NOTE, locationData.getNote());
        values.put(KEY_CURRENT_DATE, locationData.getCurrentDate());

        return db.update(TABLE_LOCATIONS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(locationData.getId())});
    }

    public void deleteLocation(LocationData locationData) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCATIONS, KEY_ID + " = ?",
                new String[]{String.valueOf(locationData.getId())});
        db.close();
    }

    public int getLocationDataCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOCATIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

}