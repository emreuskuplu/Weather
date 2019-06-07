package com.emre.android.weatherapp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.emre.android.weatherapp.persistence.LocationDbCursorWrapper;
import com.emre.android.weatherapp.persistence.LocationDbHelper;
import com.emre.android.weatherapp.persistence.LocationDbSchema.LocationTable;
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
        List<LocationDTO> locations = new ArrayList<>();

        LocationDbCursorWrapper cursor = queryLocations(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                locations.add(cursor.getLocationData());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return locations;
    }

    @Override
    public void LocationDbDeleteLocationData(UUID uuid) {
        String uuidString = uuid.toString();

        mDatabase.delete(LocationTable.NAME,
                LocationTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private LocationDbCursorWrapper queryLocations(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                LocationTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new LocationDbCursorWrapper(cursor);
    }
}
