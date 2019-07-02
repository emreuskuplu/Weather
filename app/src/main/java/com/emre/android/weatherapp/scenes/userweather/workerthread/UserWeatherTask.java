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

package com.emre.android.weatherapp.scenes.userweather.workerthread;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.emre.android.weatherapp.dataaccessobjects.weatherdataaccess.WeatherDAO;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.WeatherDTO;
import com.emre.android.weatherapp.scenes.IUpdateWeather;
import com.emre.android.weatherapp.scenes.userweather.UserWeatherFragment;

/**
 * @author Emre Üsküplü
 *
 * Executes task for fetch weather of user from api
 */
public class UserWeatherTask extends AsyncTask<Location, Void, WeatherDTO> {
    private static final String TAG = UserWeatherTask.class.getSimpleName();

    private Context mUserWeatherContext;
    private Fragment mUserWeatherFragment;


    public UserWeatherTask(Context userWeatherContext, Fragment userWeatherFragment) {
        Log.i(TAG, "is executing");

        mUserWeatherContext = userWeatherContext;
        mUserWeatherFragment = userWeatherFragment;
    }

    @Override
    protected WeatherDTO doInBackground(Location... locations) {
        return new WeatherDAO(mUserWeatherContext).getUserWeather(locations[0]);
    }

    @Override
    protected void onPostExecute(WeatherDTO result) {
        Log.i(TAG, "is executed");

        IUpdateWeather iUpdateWeather = (UserWeatherFragment) mUserWeatherFragment;
        iUpdateWeather.updateWeather(result);
    }
}
