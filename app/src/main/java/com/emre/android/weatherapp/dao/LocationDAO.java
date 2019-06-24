package com.emre.android.weatherapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.emre.android.weatherapp.database.LocationDbCursorWrapper;
import com.emre.android.weatherapp.database.LocationDbHelper;
import com.emre.android.weatherapp.database.LocationDbSchema.LocationTable;
import com.emre.android.weatherapp.dto.LocationDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LocationDAO implements ILocationDAO {
    private static final String TAG = LocationDAO.class.getSimpleName();

    private SQLiteDatabase mDatabase;

    public LocationDAO(Context context) {
        mDatabase = new LocationDbHelper(context)
                .getWritableDatabase();
    }

    @Override
    public void LocationDbInserting(UUID id, Double latitude, Double longitude) {
        ContentValues values = new ContentValues();
        values.put(LocationTable.Cols.UUID, id.toString());
        values.put(LocationTable.Cols.LATITUDE, latitude);
        values.put(LocationTable.Cols.LONGITUDE, longitude);

        mDatabase.insert(LocationTable.NAME, null, values);
    }

    @Override
    public List<LocationDTO> LocationDbExtract() {
        List<LocationDTO> locationList = new ArrayList<>();

        LocationDbCursorWrapper cursor = queryLocations();

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                locationList.add(cursor.getLocationData());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return locationList;
    }

    @Override
    public void LocationDbDeleteLocationData(UUID uuid) {
        String uuidString = uuid.toString();

        mDatabase.delete(LocationTable.NAME,
                LocationTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    @Override
    public void LocationDbDeleteAllLocationData() {
        mDatabase.delete(LocationTable.NAME,
                null, null);
    }

    private LocationDbCursorWrapper queryLocations() {
        Cursor cursor = mDatabase.query(
                LocationTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        return new LocationDbCursorWrapper(cursor);
    }
}
