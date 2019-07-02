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

package com.emre.android.weatherapp.database.location;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.emre.android.weatherapp.database.location.LocationDbSchema.LocationTable;
import com.emre.android.weatherapp.datatransferobjects.locationdatatransfer.LocationDTO;

import java.util.UUID;

/**
 * @author Emre Üsküplü
 *
 * Sets location values from database
 */
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
