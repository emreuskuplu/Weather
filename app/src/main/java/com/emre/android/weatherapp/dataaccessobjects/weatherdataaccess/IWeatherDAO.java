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

import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.BookmarkWeatherDTO;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.DetailedWeatherDTO;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.UserWeatherDTO;

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
    UserWeatherDTO getUserWeather(Location location);

    /**
     * Gets bookmark list weather from api
     * @param bookmarkWeatherDTOList of bookmark list
     * @return weather of bookmark list
     */
    List<BookmarkWeatherDTO> getBookmarkListWeather(List<BookmarkWeatherDTO> bookmarkWeatherDTOList);

    /**
     * Gets detailed weather of forecast days from api
     * @param location of user or bookmark list
     * @return detailed weather of forecast days
     */
    List<DetailedWeatherDTO> getDetailedWeatherList(Location location);
}
