package com.emre.android.weatherapp.dto;

import java.util.ArrayList;
import java.util.List;

public class WeatherDTOListBookmark {

    private List<WeatherDTO> mWeatherDTOList = new ArrayList<>();

    public List<WeatherDTO> getWeatherDTOList() {
        return mWeatherDTOList;
    }

    public void setWeatherDTOList(List<WeatherDTO> weatherDTOList) {
        mWeatherDTOList = weatherDTOList;
    }
}
