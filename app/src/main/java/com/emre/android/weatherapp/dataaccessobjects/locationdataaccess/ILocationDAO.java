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

import com.emre.android.weatherapp.datatransferobjects.locationdatatransfer.LocationDTO;

import java.util.List;
import java.util.UUID;

/**
 * @author Emre Üsküplü
 *
 * Manages values of database
 */
public interface ILocationDAO {

    /**
     * Insert location data
     * @param id generated random
     * @param latitude generated on map
     * @param longitude generated on map
     */
    void locationDbInserting(UUID id, Double latitude, Double longitude);

    /**
     * Extract location data list
     * @return locations from database
     */
    List<LocationDTO> locationDbExtract();

    /**
     * Deletes a location data
     * @param uuid gets id
     */
    void locationDbDeleteLocationData(UUID uuid);

    /**
     * Deletes all location data
     */
    void locationDbDeleteAllLocationData();
}
