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

package com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer;

import androidx.annotation.NonNull;

import java.util.UUID;

/**
 * @author Emre Üsküplü
 *
 * Sets values from locationdto and weather api
 */
public class BookmarkWeatherDTO {
    private static final String TAG = BookmarkWeatherDTO.class.getSimpleName();

    // Location values
    private UUID mLocationDTOId;
    private double mLocationDTOLatitude;
    private double mLocationDTOLongitude;
    // Weather values
    private String mLocationName = "";
    private String mMainDescription = "";
    private String mTempDegree = "";
    private String mDescription = "";

    public BookmarkWeatherDTO(UUID locationDTOId, double locationDTOLatitude, double locationDTOLongitude) {
        mLocationDTOId = locationDTOId;
        mLocationDTOLatitude = locationDTOLatitude;
        mLocationDTOLongitude = locationDTOLongitude;
    }

    public UUID getLocationDTOId() {
        return mLocationDTOId;
    }

    public double getLocationDTOLatitude() {
        return mLocationDTOLatitude;
    }

    public double getLocationDTOLongitude() {
        return mLocationDTOLongitude;
    }

    public String getLocationName() {
        return mLocationName;
    }

    public void setLocationName(String locationName) {
        mLocationName = locationName;
    }

    public String getMainDescription() {
        return mMainDescription;
    }

    public void setMainDescription(String mainDescription) {
        mMainDescription = mainDescription;
    }

    public String getTempDegree() {
        return mTempDegree;
    }

    public void setTempDegree(String tempDegree) {
        float fTempDegree = Float.parseFloat(tempDegree);
        int iTempDegree = Math.round(fTempDegree);
        mTempDegree = Integer.toString(iTempDegree);
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    @NonNull
    @Override
    public String toString() {
        return mLocationDTOId + " " + mLocationDTOLatitude + " " + mLocationDTOLongitude + " "
                + mLocationName + " " + mMainDescription + " " + mTempDegree + " " + mDescription;
    }
}
