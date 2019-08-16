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
package com.emre.android.weatherapp.webservice.weather;

import android.location.Location;
import android.util.Log;

import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.BookmarkWeatherDTO;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.DetailedWeatherDTO;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.UserWeatherDTO;

import com.emre.android.weatherapp.webservice.weather.deserializer.BookmarkWeatherDeserializer;
import com.emre.android.weatherapp.webservice.weather.deserializer.DetailedWeatherDeserializer;
import com.emre.android.weatherapp.webservice.weather.deserializer.UserWeatherDeserializer;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * @author Emre Üsküplü
 *
 * Fetchs user weather, bookmark weather and detailed weather
 */
public class WeatherRestAPI {
    private static final String TAG = WeatherRestAPI.class.getSimpleName();
    private static final String API_KEY = "2cd12ee50d586f06c24dbd1dd2cd8eca";

    private Location mLocation;
    private String mUnits;
    private String mLang;

    public WeatherRestAPI(Location location, String units, String lang) {
        mLocation = location;
        mUnits = units;
        mLang = lang;
    }

    public UserWeatherDTO fetchUserWeather()  {
        IWeatherRetrofitService iWeatherRetrofitService = WeatherClient
                .getWeatherRetrofit(UserWeatherDTO.class, new UserWeatherDeserializer())
                .create(IWeatherRetrofitService.class);

        Call<UserWeatherDTO> userWeatherDTOCall = iWeatherRetrofitService.getCurrentWeather(
                API_KEY,
                mUnits,
                mLang,
                mLocation.getLatitude(),
                mLocation.getLongitude()
        );

        Log.i(TAG, "User weather api url = " + userWeatherDTOCall.request().url().toString());

        try {
            if (userWeatherDTOCall.execute().isSuccessful()) {
                return userWeatherDTOCall.clone().execute().body();
            } else {
                Log.e(TAG, "" + userWeatherDTOCall.clone().execute().code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new UserWeatherDTO();
    }

    public BookmarkWeatherDTO fetchBookmarkWeather(BookmarkWeatherDTO bookmarkWeatherDTO) {
        IWeatherRetrofitService iWeatherRetrofitService = WeatherClient
                .getWeatherRetrofit(BookmarkWeatherDTO.class, new BookmarkWeatherDeserializer(bookmarkWeatherDTO))
                .create(IWeatherRetrofitService.class);

        Call<BookmarkWeatherDTO> bookmarkWeatherDTOCall = iWeatherRetrofitService.getBookmarkWeather(
                API_KEY,
                mUnits,
                mLang,
                mLocation.getLatitude(),
                mLocation.getLongitude()
        );

        Log.i(TAG, "Bookmark weather api url = " + bookmarkWeatherDTOCall.request().url().toString());

        try {
            if (bookmarkWeatherDTOCall.execute().isSuccessful()) {
                return bookmarkWeatherDTOCall.clone().execute().body();
            } else {
                Log.e(TAG, "" + bookmarkWeatherDTOCall.clone().execute().code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bookmarkWeatherDTO;
    }

    public List<DetailedWeatherDTO> fetchDetailedWeather() {
        IWeatherRetrofitService iWeatherRetrofitService = WeatherClient
                .getWeatherRetrofit(new TypeToken<List<DetailedWeatherDTO>>() {}.getType(), new DetailedWeatherDeserializer())
                .create(IWeatherRetrofitService.class);

        Call<List<DetailedWeatherDTO>> detailedWeatherDTOListCall = iWeatherRetrofitService.getDetailedWeather(
                API_KEY,
                mUnits,
                mLang,
                mLocation.getLatitude(),
                mLocation.getLongitude()
        );

        Log.i(TAG, "Detailed weather api url = " + detailedWeatherDTOListCall.request().url().toString());

        try {
            if (detailedWeatherDTOListCall.execute().isSuccessful()) {
                return detailedWeatherDTOListCall.clone().execute().body();
            } else {
                Log.e(TAG, "" + detailedWeatherDTOListCall.clone().execute().code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }
}
