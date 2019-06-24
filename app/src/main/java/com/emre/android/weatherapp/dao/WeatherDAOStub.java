package com.emre.android.weatherapp.dao;

import android.location.Location;

import com.emre.android.weatherapp.dto.WeatherDTO;

import java.util.ArrayList;
import java.util.List;

public class WeatherDAOStub implements IWeatherDAO {

    @Override
    public WeatherDTO getUserWeather(Location location) {
        WeatherDTO weatherDTO = new WeatherDTO();
        weatherDTO.setLocationName("İstanbul");
        weatherDTO.setMainDescription("Clear");
        weatherDTO.setTempDegree("20");
        weatherDTO.setDescription("clear sky");

        return weatherDTO;
    }

    @Override
    public List<WeatherDTO> getBookmarkWeatherList(List<WeatherDTO> weatherDTOList) {
        String[] locationNames =
                {"İstanbul", "Kağıthane", "Amsterdam", "Schipol", "Germany", "Berlin", "", "", "", ""};
        String[] mainDescriptions =
                {"Clear", "Clouds", "Drizzle", "Rain", "Thunderstorm", "Snow", "", "", "", ""};
        String[] tempDegrees =
                {"10", "25", "6", "100", "2000", "0", "10000", "10", "10", "0"};
        String[] descriptions =
                {"clear sky", "few clouds", "light rain", "snow", "", "", "", "", "", ""};

        for (int i = 0; i < 10; i++) {
            WeatherDTO weatherDTO = new WeatherDTO();
            weatherDTO.setLocationName(locationNames[i]);
            weatherDTO.setMainDescription(mainDescriptions[i]);
            weatherDTO.setTempDegree(tempDegrees[i]);
            weatherDTO.setDescription(descriptions[i]);

            weatherDTOList.add(weatherDTO);
        }

        return weatherDTOList;
    }

    @Override
    public List<WeatherDTO> getForecastDetailedWeatherList(Location location) {
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

        List<WeatherDTO> weatherDTOList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            WeatherDTO weatherDTO = new WeatherDTO();
            weatherDTO.setLocationName(locationName);
            weatherDTO.setMainDescription(mainDescriptions[i]);
            weatherDTO.setTempDegree(tempDegrees[i]);
            weatherDTO.setDescription(descriptions[i]);
            weatherDTO.setHumidity(humidities[i]);
            weatherDTO.setWindVolume(windVolumes[i]);
            weatherDTO.setRainVolume(rainVolumes[i]);
            weatherDTO.setSnowVolume(snowVolumes[i]);
            weatherDTO.setDate(dates[i]);

            weatherDTOList.add(weatherDTO);
        }

        return weatherDTOList;
    }
}
