package com.emre.android.weatherapp.dao;

import com.emre.android.weatherapp.dto.LocationDTO;

import java.util.List;
import java.util.UUID;

public interface ILocationDAO {

    void LocationDbInserting(UUID id, Double latitude, Double longitude);
    List<LocationDTO> LocationDbExtract();
    void LocationDbDeleteLocationData(UUID uuid);
    void LocationDbDeleteAllLocationData();
}
