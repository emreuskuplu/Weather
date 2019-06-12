package com.emre.android.weatherapp.dto;

import java.util.ArrayList;
import java.util.List;

public class LocationDTOListBookmark {
    private static final String TAG = LocationDTOListBookmark.class.getSimpleName();

    private List<LocationDTO> mLocationDTOList = new ArrayList<>();

    public List<LocationDTO> getLocationDTOList() {
        return mLocationDTOList;
    }

    public void setLocationDTOList(List<LocationDTO> locationDTOList) {
        mLocationDTOList = locationDTOList;
    }
}
