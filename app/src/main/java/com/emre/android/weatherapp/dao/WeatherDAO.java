package com.emre.android.weatherapp.dao;

import android.location.Location;
import android.util.Log;

import com.emre.android.weatherapp.dto.LocationDTO;
import com.emre.android.weatherapp.dto.WeatherForecastDTO;
import com.emre.android.weatherapp.dto.WeatherDTO;
import com.emre.android.weatherapp.dto.weather_json_schema.City;
import com.emre.android.weatherapp.dto.weather_json_schema.ForecastBody;
import com.emre.android.weatherapp.dto.weather_json_schema.ForecastDayBody;
import com.emre.android.weatherapp.dto.weather_json_schema.Main;
import com.emre.android.weatherapp.dto.weather_json_schema.Weather;
import com.emre.android.weatherapp.dto.weather_json_schema.WeatherBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherDAO implements IWeatherDAO {
    private static final String TAG = WeatherDAO.class.getSimpleName();

    private static final String ENDPOINT_WEATHER = "http://api.openweathermap.org/data/2.5/";
    private static final String ENDPOINT_FORECAST = "http://api.openweathermap.org/data/2.5/";
    private static final String API_KEY = "2cd12ee50d586f06c24dbd1dd2cd8eca";
    private static final String UNITS = "metric";

    @Override
    public WeatherDTO getUserWeather(Location userLocation) {
        WeatherDTO weatherDTO = new WeatherDTO();
        Retrofit retrofit = buildBaseUrlCurrentWeather();
        IRetrofitWeatherDAO retrofitWeatherDAO = retrofit.create(IRetrofitWeatherDAO.class);

        Call<WeatherBody> weatherBodyCall = retrofitWeatherDAO.getCurrentWeatherDTO(
                API_KEY,
                UNITS,
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
    public List<WeatherDTO> getWeatherList(List<LocationDTO> locationDTOList) {
        List<WeatherDTO> weatherDTOList = new ArrayList<>();
        Retrofit retrofit = buildBaseUrlCurrentWeather();
        IRetrofitWeatherDAO iRetrofitWeatherDAO = retrofit.create(IRetrofitWeatherDAO.class);

        for (LocationDTO locationDTO : locationDTOList) {
            Call<WeatherBody> weatherBodyCall = iRetrofitWeatherDAO.getCurrentWeatherDTO(
                    API_KEY,
                    UNITS,
                    locationDTO.getLatitude(),
                    locationDTO.getLongitude()
            );

            Log.i(TAG, "List item weather api url = " + weatherBodyCall.request().url().toString());

            WeatherDTO weatherDTO = new WeatherDTO();

            try {
                parseCurrentWeatherResponse(weatherBodyCall, weatherDTO);
                weatherDTOList.add(weatherDTO);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return weatherDTOList;
    }

    @Override
    public WeatherForecastDTO getDetailedWeather(Location location) {
        Retrofit retrofit = buildBaseUrlDetailedWeather();
        IRetrofitWeatherDAO iRetrofitWeatherDAO = retrofit.create(IRetrofitWeatherDAO.class);

        Call<ForecastBody> forecastBodyCall = iRetrofitWeatherDAO.getForecastWeatherDTO(
                API_KEY,
                UNITS,
                location.getLatitude(),
                location.getLongitude()
        );

        Log.i(TAG, "Detailed weather api url = " + forecastBodyCall.request().url().toString());

        WeatherForecastDTO weatherForecastDTO = new WeatherForecastDTO();

        try {
            parseDetailedWeatherResponse(forecastBodyCall, weatherForecastDTO);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return weatherForecastDTO;
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
                                              WeatherForecastDTO weatherForecastDTO) throws IOException {
        List<WeatherDTO> weatherDTOList = new ArrayList<>();

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

        weatherForecastDTO.setWeatherDTOList(weatherDTOList);
    }

    private Retrofit buildBaseUrlCurrentWeather() {
        return new Retrofit.Builder()
                .baseUrl(ENDPOINT_WEATHER)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private Retrofit buildBaseUrlDetailedWeather() {
        return new Retrofit.Builder()
                .baseUrl(ENDPOINT_FORECAST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
