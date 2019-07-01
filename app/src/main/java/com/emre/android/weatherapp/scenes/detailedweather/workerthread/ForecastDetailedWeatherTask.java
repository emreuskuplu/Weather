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

package com.emre.android.weatherapp.scenes.detailedweather.workerthread;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.emre.android.weatherapp.dataaccessobjects.weatherdataaccess.WeatherDAO;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.WeatherDTO;
import com.emre.android.weatherapp.scenes.IUpdateWeather;
import com.emre.android.weatherapp.scenes.detailedweather.DetailedWeatherFragment;

import java.util.List;

public class ForecastDetailedWeatherTask extends AsyncTask<Location, Void, List<WeatherDTO>> {
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
    protected List<WeatherDTO> doInBackground(Location... locations) {
        return new WeatherDAO(mDetailedWeatherContext).getForecastDetailedWeatherList(locations[0]);
    }

    @Override
    protected void onPostExecute(List<WeatherDTO> result) {
        Log.i(TAG, "is executed");

        if (!result.isEmpty()) {
            WeatherDTO selectedDayWeatherDTO = result.get(mSelectedDay);

            IUpdateWeather iUpdateWeather = (DetailedWeatherFragment) mDetailedWeatherFragment;
            iUpdateWeather.updateWeather(selectedDayWeatherDTO);
            iUpdateWeather.updateListWeather(result);
        }
    }
}
