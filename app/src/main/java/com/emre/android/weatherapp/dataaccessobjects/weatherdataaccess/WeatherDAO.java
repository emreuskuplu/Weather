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

import android.content.Context;
import android.location.Location;

import com.emre.android.weatherapp.dataaccessobjects.settingsdataaccess.SettingsDAO;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.BookmarkWeatherDTO;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.DetailedWeatherDTO;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.UserWeatherDTO;
import com.emre.android.weatherapp.webservice.weather.WeatherRestAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Emre Üsküplü
 *
 * Sends weather of api
 */
public class WeatherDAO implements IWeatherDAO {
    private static final String TAG = WeatherDAO.class.getSimpleName();

    private String mUnits;
    private String mLang;

    public WeatherDAO(Context context) {
        SettingsDAO settingsDAO = new SettingsDAO();
        mUnits = settingsDAO.getPrefUnitsFormatStorage(context);

        if (Locale.getDefault().getLanguage().equals("tr")) {
            mLang = "tr" ;
        } else {
            mLang = "en";
        }
    }

    @Override
    public UserWeatherDTO getUserWeather(Location userLocation) {
        WeatherRestAPI weatherRestAPI = new WeatherRestAPI(userLocation, mUnits, mLang);

        return weatherRestAPI.fetchUserWeather();
    }

    @Override
    public List<BookmarkWeatherDTO> getBookmarkListWeather(List<BookmarkWeatherDTO> bookmarkWeatherDTOList) {
        List<BookmarkWeatherDTO> fetchedBookmarkWeatherDTO = new ArrayList<>();

        for (BookmarkWeatherDTO bookmarkWeatherDTO : bookmarkWeatherDTOList) {
            Location bookmarkLocation = new Location("");
            bookmarkLocation.setLatitude(bookmarkWeatherDTO.getLocationDTOLatitude());
            bookmarkLocation.setLongitude(bookmarkWeatherDTO.getLocationDTOLongitude());

            WeatherRestAPI weatherRestAPI = new WeatherRestAPI(bookmarkLocation, mUnits, mLang);
            fetchedBookmarkWeatherDTO.add(weatherRestAPI.fetchBookmarkWeather(bookmarkWeatherDTO));
        }

        return fetchedBookmarkWeatherDTO;
    }

    @Override
    public List<DetailedWeatherDTO> getDetailedWeatherList(Location detailedLocation) {
        WeatherRestAPI weatherRestAPI = new WeatherRestAPI(detailedLocation, mUnits, mLang);

        return weatherRestAPI.fetchDetailedWeather();
    }
}
