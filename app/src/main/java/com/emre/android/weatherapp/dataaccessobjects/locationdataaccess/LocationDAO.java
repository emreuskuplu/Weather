/*
 * Copyright (c) 2019. Emre Üsküplü
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.emre.android.weatherapp.dataaccessobjects.locationdataaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.emre.android.weatherapp.database.location.LocationDbCursorWrapper;
import com.emre.android.weatherapp.database.location.LocationDbHelper;
import com.emre.android.weatherapp.database.location.LocationDbSchema.LocationTable;
import com.emre.android.weatherapp.datatransferobjects.locationdatatransfer.LocationDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Emre Üsküplü
 *
 * Manages values of database
 *
 * Insert location data
 * Extract location data
 * Delete a location data
 * Delete all location data
 */
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

        try (LocationDbCursorWrapper cursor = queryLocations()) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                locationList.add(cursor.getLocationData());
                cursor.moveToNext();
            }
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
