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

package com.emre.android.weatherapp.dataaccessobjects.weatherdataaccess;

import android.location.Location;

import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.WeatherDTO;

import java.util.List;

/**
 * @author Emre Üsküplü
 *
 * Sends weather of api
 */
public interface IWeatherDAO {

    /**
     * Gets weather of user from api
     * @param location of device
     * @return weather of user
     */
    WeatherDTO getUserWeather(Location location);

    /**
     * Gets bookmark list weather from api
     * @param weatherDTOList of bookmark list
     * @return weather of bookmark list
     */
    List<WeatherDTO> getBookmarkListWeather(List<WeatherDTO> weatherDTOList);

    /**
     * Gets forecast detailed weather list from api
     * @param location of user or bookmark list
     * @return forecast detailed weather list
     */
    List<WeatherDTO> getForecastDetailedWeatherList(Location location);
}
