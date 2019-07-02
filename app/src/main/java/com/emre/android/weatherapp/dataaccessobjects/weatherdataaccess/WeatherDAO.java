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
import android.util.Log;

import com.emre.android.weatherapp.dataaccessobjects.settingsdataaccess.SettingsDAO;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.WeatherDTO;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.weatherjsonschema.City;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.weatherjsonschema.ForecastBody;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.weatherjsonschema.ForecastDayBody;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.weatherjsonschema.Main;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.weatherjsonschema.Weather;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.weatherjsonschema.WeatherBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Emre Üsküplü
 *
 * Sends weather of api
 */
public class WeatherDAO implements IWeatherDAO {
    private static final String TAG = WeatherDAO.class.getSimpleName();

    private static final String API_URL = "http://api.openweathermap.org/data/2.5/";
    private static final String API_KEY = "2cd12ee50d586f06c24dbd1dd2cd8eca";

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
    public WeatherDTO getUserWeather(Location userLocation) {
        WeatherDTO weatherDTO = new WeatherDTO();
        Retrofit retrofit = buildBaseUrl();
        IRetrofitWeatherDAO retrofitWeatherDAO = retrofit.create(IRetrofitWeatherDAO.class);

        Call<WeatherBody> weatherBodyCall = retrofitWeatherDAO.getCurrentWeatherDTO(
                API_KEY,
                mUnits,
                mLang,
                userLocation.getLatitude(),
                userLocation.getLongitude());

        Log.i(TAG, "User weather api url = " + weatherBodyCall.request().url().toString());

        try {
            parseCurrentWeatherResponse(weatherBodyCall, weatherDTO);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return weatherDTO;
    }

    @Override
    public List<WeatherDTO> getBookmarkListWeather(List<WeatherDTO> weatherDTOList) {
        Retrofit retrofit = buildBaseUrl();
        IRetrofitWeatherDAO iRetrofitWeatherDAO = retrofit.create(IRetrofitWeatherDAO.class);

        for (WeatherDTO weatherDTO : weatherDTOList) {
            Call<WeatherBody> weatherBodyCall = iRetrofitWeatherDAO.getCurrentWeatherDTO(
                    API_KEY,
                    mUnits,
                    mLang,
                    weatherDTO.getLocationDTOLatitude(),
                    weatherDTO.getLocationDTOLongitude()
            );

            Log.i(TAG, "List item weather api url = " + weatherBodyCall.request().url().toString());

            try {
                parseCurrentWeatherResponse(weatherBodyCall, weatherDTO);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return weatherDTOList;
    }

    @Override
    public List<WeatherDTO> getForecastDetailedWeatherList(Location location) {
        Retrofit retrofit = buildBaseUrl();
        IRetrofitWeatherDAO iRetrofitWeatherDAO = retrofit.create(IRetrofitWeatherDAO.class);

        Call<ForecastBody> forecastBodyCall = iRetrofitWeatherDAO.getForecastWeatherDTO(
                API_KEY,
                mUnits,
                mLang,
                location.getLatitude(),
                location.getLongitude()
        );

        Log.i(TAG, "Forecast weather api url = " + forecastBodyCall.request().url().toString());

        List<WeatherDTO> weatherDTOList = new ArrayList<>();

        try {
            parseDetailedWeatherResponse(forecastBodyCall, weatherDTOList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return weatherDTOList;
    }

    private Retrofit buildBaseUrl() {
        return new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void parseCurrentWeatherResponse(Call<WeatherBody> weatherBodyCall,
                                             WeatherDTO weatherDTO) throws IOException {
        if (weatherBodyCall.execute().isSuccessful()) {
            WeatherBody weatherBody = weatherBodyCall.clone().execute().body();
            if (weatherBody != null) {

                if (weatherBody.getName() != null) {
                    weatherDTO.setLocationName(weatherBody.getName());
                }

                if (weatherBody.getWeather() != null) {

                    if (weatherBody.getWeather().get(0) != null) {

                        if (weatherBody.getWeather().get(0).getMain() != null) {
                            weatherDTO.setMainDescription(weatherBody.getWeather().get(0).getMain());
                        }

                        if (weatherBody.getWeather().get(0).getDescription() != null) {
                            weatherDTO.setDescription(weatherBody.getWeather().get(0).getDescription());
                        }

                        if (weatherBody.getMain() != null) {
                            weatherDTO.setTempDegree(Double.toString(weatherBody.getMain().getTemp()));
                        }
                    }
                }
            }

        } else {
            Log.e(TAG, "" + weatherBodyCall.clone().execute().code());
        }
     }

    private void parseDetailedWeatherResponse(Call<ForecastBody> forecastBodyCall,
                                              List<WeatherDTO> weatherDTOList) throws IOException {
        if (forecastBodyCall.execute().isSuccessful()) {
            ForecastBody forecastBody = forecastBodyCall.clone().execute().body();

            if (forecastBody != null) {
                List<ForecastDayBody> forecastDayBodyList = forecastBody.getList();
                City city = forecastBody.getCity();

                int indexPosition = 0;

                for (int i = 0; i < 5; i++) {
                    WeatherDTO weatherDTO = new WeatherDTO();

                    if (city.getName() != null) {
                        weatherDTO.setLocationName(city.getName());
                    }

                    if (forecastDayBodyList.get(indexPosition) != null) {
                        ForecastDayBody forecastDayBody =  forecastDayBodyList.get(indexPosition);

                        if (forecastDayBody.getMain() != null) {
                            Main main = forecastDayBody.getMain();
                            weatherDTO.setTempDegree(Double.toString(main.getTemp()));
                            weatherDTO.setHumidity(Double.toString(main.getHumidity()));
                        }

                        if (forecastDayBody.getWeather().get(0) != null) {
                            Weather weather = forecastDayBody.getWeather().get(0);

                            if (weather.getMain() != null) {
                                weatherDTO.setMainDescription(weather.getMain());
                            }

                            if (weather.getDescription() != null) {
                                weatherDTO.setDescription(weather.getDescription());
                            }
                        }

                        if (forecastDayBody.getWind() != null) {
                            weatherDTO.setWindVolume(Double.toString(forecastDayBody.getWind().getSpeed()));
                        }

                        if (forecastDayBody.getRain() != null) {
                            weatherDTO.setRainVolume(Double.toString(forecastDayBody.getRain().getThreeH()));
                        }

                        if (forecastDayBody.getSnow() != null) {
                            weatherDTO.setSnowVolume(Double.toString(forecastDayBody.getSnow().getThreeH()));
                        }

                        if (forecastDayBody.getDt_txt() != null) {
                            weatherDTO.setDate(forecastDayBody.getDt_txt());
                        }
                    }

                    weatherDTOList.add(weatherDTO);
                    indexPosition += 8;
                }
            }
        }
    }
}
