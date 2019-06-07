package com.emre.android.weatherapp.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WeatherForecastDTO implements Serializable {
    private static final String TAG = WeatherForecastDTO.class.getSimpleName();

    private List<WeatherDTO> mWeatherDTOList = new ArrayList<>();

    public List<WeatherDTO> getWeatherDTOList() {
        return mWeatherDTOList;
    }

    public void setWeatherDTOList(List<WeatherDTO> weatherDTOList) {
        mWeatherDTOList = weatherDTOList;
    }
}
