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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emre Üsküplü
 *
 * Sends stub weather
 */
public class WeatherDAOStub implements IWeatherDAO {

    @Override
    public UserWeatherDTO getUserWeather(Location location) {
        UserWeatherDTO userWeatherDTO = new UserWeatherDTO();
        userWeatherDTO.setLocationName("İstanbul");
        userWeatherDTO.setMainDescription("Clear");
        userWeatherDTO.setTempDegree("20");
        userWeatherDTO.setDescription("clear sky");

        return userWeatherDTO;
    }

    @Override
    public List<BookmarkWeatherDTO> getBookmarkListWeather(List<BookmarkWeatherDTO> bookmarkWeatherDTOList) {
        String[] locationNames =
                {"İstanbul", "Kağıthane", "Amsterdam", "Schipol", "Germany", "Berlin", "", "", "", ""};
        String[] mainDescriptions =
                {"Clear", "Clouds", "Drizzle", "Rain", "Thunderstorm", "Snow", "", "", "", ""};
        String[] tempDegrees =
                {"10", "25", "6", "100", "2000", "0", "10000", "10", "10", "0"};
        String[] descriptions =
                {"clear sky", "few clouds", "light rain", "snow", "", "", "", "", "", ""};

        for (int i = 0; i < 10; i++) {
            bookmarkWeatherDTOList.get(i).setLocationName(locationNames[i]);
            bookmarkWeatherDTOList.get(i).setLocationName(locationNames[i]);
            bookmarkWeatherDTOList.get(i).setMainDescription(mainDescriptions[i]);
            bookmarkWeatherDTOList.get(i).setTempDegree(tempDegrees[i]);
            bookmarkWeatherDTOList.get(i).setDescription(descriptions[i]);
        }

        return bookmarkWeatherDTOList;
    }

    @Override
    public List<DetailedWeatherDTO> getDetailedWeatherList(Location location) {
        String locationName = "İstanbul";
        String[] mainDescriptions =
                {"Clear", "Clouds", "Rain", "Thunderstorm", "Snow"};
        String[] tempDegrees =
                {"10", "25", "6", "100", "2000"};
        String[] descriptions =
                {"clear sky", "few clouds", "light rain", "snow", ""};
        String[] humidities =
                {"10", "20", "0", "0", "0"};
        String[] windVolumes =
                {"50", "40", "200", "30", "10"};
        String[] rainVolumes =
                {"10", "10", "10", "10", "10"};
        String[] snowVolumes =
                {"15", "90", "200", "0", "0"};
        String[] dates =
                {"2019-05-01", "2019-05-02", "2019-05-03", "2019-05-04", "2019-05-05"};

        List<DetailedWeatherDTO> detailedWeatherDTOList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            DetailedWeatherDTO detailedWeatherDTO = new DetailedWeatherDTO();
            detailedWeatherDTO.setLocationName(locationName);
            detailedWeatherDTO.setMainDescription(mainDescriptions[i]);
            detailedWeatherDTO.setTempDegree(tempDegrees[i]);
            detailedWeatherDTO.setDescription(descriptions[i]);
            detailedWeatherDTO.setHumidity(humidities[i]);
            detailedWeatherDTO.setWindVolume(windVolumes[i]);
            detailedWeatherDTO.setRainVolume(rainVolumes[i]);
            detailedWeatherDTO.setSnowVolume(snowVolumes[i]);
            detailedWeatherDTO.setDate(dates[i]);

            detailedWeatherDTOList.add(detailedWeatherDTO);
        }

        return detailedWeatherDTOList;
    }
}
