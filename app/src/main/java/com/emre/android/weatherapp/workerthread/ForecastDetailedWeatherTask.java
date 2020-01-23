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

package com.emre.android.weatherapp.workerthread;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.emre.android.weatherapp.dataaccessobjects.weatherdataaccess.WeatherDAO;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.DetailedWeatherDTO;
import com.emre.android.weatherapp.scenes.detailedweather.DetailedWeatherFragment;
import com.emre.android.weatherapp.scenes.detailedweather.IUpdateDetailedWeather;
import com.emre.android.weatherapp.scenes.detailedweather.IUpdateForecastDayListWeather;

import java.util.List;

/**
 * @author Emre Üsküplü
 *
 * Executes task for fetch weather of forecast days from api
 */
public class ForecastDetailedWeatherTask extends AsyncTask<Location, Void, List<DetailedWeatherDTO>> {
    private static final String TAG = ForecastDetailedWeatherTask.class.getSimpleName();

    private Context mDetailedWeatherContext;
    private Fragment mDetailedWeatherFragment;
    private int mSelectedDay;

    public ForecastDetailedWeatherTask(Context context, Fragment detailedWeatherFragment, int selectedDay) {
        Log.i(TAG, "is executing");

        mDetailedWeatherContext = context;
        mDetailedWeatherFragment = detailedWeatherFragment;
        mSelectedDay = selectedDay;
    }

    @Override
    protected List<DetailedWeatherDTO> doInBackground(Location... locations) {
        return new WeatherDAO(mDetailedWeatherContext).getDetailedWeatherList(locations[0]);
    }

    @Override
    protected void onPostExecute(List<DetailedWeatherDTO> result) {
        Log.i(TAG, "is executed");

        if (!result.isEmpty()) {
            DetailedWeatherDTO selectedDayWeatherDTO = result.get(mSelectedDay);

            IUpdateDetailedWeather iUpdateDetailedWeather = (DetailedWeatherFragment) mDetailedWeatherFragment;
            iUpdateDetailedWeather.updateDetailedWeather(selectedDayWeatherDTO);

            IUpdateForecastDayListWeather iUpdateForecastDayListWeather = (DetailedWeatherFragment) mDetailedWeatherFragment;
            iUpdateForecastDayListWeather.updateForecastDayListWeather(result);
        }
    }
}
