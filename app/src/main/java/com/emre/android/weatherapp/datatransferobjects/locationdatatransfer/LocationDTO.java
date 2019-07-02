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

package com.emre.android.weatherapp.datatransferobjects.locationdatatransfer;

import java.util.UUID;

/**
 * @author Emre Üsküplü
 *
 * Sets values from database
 */
public class LocationDTO {
    private static final String TAG = LocationDTO.class.getSimpleName();

    private UUID mId;
    private double mLatitude;
    private double mLongitude;

    public UUID getId() {
        return mId;
    }

    public void setId(UUID Id) {
        mId = Id;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }
}
