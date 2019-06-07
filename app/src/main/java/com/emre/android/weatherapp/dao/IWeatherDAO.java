package com.emre.android.weatherapp.dao;

import android.location.Location;

import com.emre.android.weatherapp.dto.LocationDTO;
import com.emre.android.weatherapp.dto.WeatherDTO;
import com.emre.android.weatherapp.dto.WeatherForecastDTO;

import java.util.List;

public interface IWeatherDAO {
    WeatherDTO getUserWeather(Location location);
    List<WeatherDTO> getWeatherList(List<LocationDTO> locationDTOList);
    WeatherForecastDTO getDetailedWeather(Location location);
}
