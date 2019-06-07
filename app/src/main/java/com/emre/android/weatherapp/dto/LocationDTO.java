package com.emre.android.weatherapp.dto;

import java.io.Serializable;
import java.util.UUID;

public class LocationDTO implements Serializable {
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
