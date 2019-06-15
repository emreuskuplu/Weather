package com.emre.android.weatherapp.dao;

import android.location.Location;

import com.emre.android.weatherapp.dto.LocationDTO;
import com.emre.android.weatherapp.dto.WeatherDTO;
import com.emre.android.weatherapp.dto.weather_json_schema.Weather;

import java.util.List;

public interface IWeatherDAO {
    WeatherDTO getUserWeather(Location location);
    List<WeatherDTO> getBookmarkWeatherList(List<WeatherDTO> weatherDTOList);
    List<WeatherDTO> getForecastDetailedWeatherList(Location location);
}
