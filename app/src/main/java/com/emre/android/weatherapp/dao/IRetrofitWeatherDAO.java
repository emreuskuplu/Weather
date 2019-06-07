package com.emre.android.weatherapp.dao;

import com.emre.android.weatherapp.dto.WeatherDTO;
import com.emre.android.weatherapp.dto.weather_json_schema.ForecastBody;
import com.emre.android.weatherapp.dto.weather_json_schema.WeatherBody;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IRetrofitWeatherDAO {

    @GET("weather")
    Call<WeatherBody> getCurrentWeatherDTO(
            @Query("appId") String appId,
            @Query("units") String units,
            @Query("lat") Double lat,
            @Query("lon") Double lon
    );

    @GET("forecast")
    Call<ForecastBody> getForecastWeatherDTO(
            @Query("appId") String appId,
            @Query("units") String units,
            @Query("lat") Double lat,
            @Query("lon") Double lon
    );
}
