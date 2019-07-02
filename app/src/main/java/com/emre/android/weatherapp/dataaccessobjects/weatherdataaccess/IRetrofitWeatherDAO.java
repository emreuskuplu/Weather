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

import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.weatherjsonschema.ForecastBody;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.weatherjsonschema.WeatherBody;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author Emre Üsküplü
 */
interface IRetrofitWeatherDAO {

    @GET("weather")
    Call<WeatherBody> getCurrentWeatherDTO(
            @Query("appId") String appId,
            @Query("units") String units,
            @Query("lang") String lang,
            @Query("lat") Double lat,
            @Query("lon") Double lon
    );

    @GET("forecast")
    Call<ForecastBody> getForecastWeatherDTO(
            @Query("appId") String appId,
            @Query("units") String units,
            @Query("lang") String lang,
            @Query("lat") Double lat,
            @Query("lon") Double lon
    );
}
