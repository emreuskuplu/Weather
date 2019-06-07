package com.emre.android.weatherapp.persistence;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.emre.android.weatherapp.persistence.LocationDbSchema.LocationTable;
import com.emre.android.weatherapp.dto.LocationDTO;

import java.util.UUID;

public class LocationDbCursorWrapper extends CursorWrapper {
    private static final String TAG = LocationDbCursorWrapper.class.getSimpleName();

    public LocationDbCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public LocationDTO getLocationData() {
        String uuid = getString(getColumnIndex(LocationTable.Cols.UUID));
        double latitude = getDouble(getColumnIndex(LocationTable.Cols.LATITUDE));
        double longitude = getDouble(getColumnIndex(LocationTable.Cols.LONGITUDE));

        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setId(UUID.fromString(uuid));
        locationDTO.setLatitude(latitude);
        locationDTO.setLongitude(longitude);

        return locationDTO;
    }
}
