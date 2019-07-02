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

package com.emre.android.weatherapp.scenes.main;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import com.emre.android.weatherapp.R;

import java.util.List;

/**
 * @author Emre Üsküplü
 *
 * Getting fragments from activity classes
 * Adding fragments in transaction
 */
public abstract class MainFragmentActivity extends AppCompatActivity {
    private static final String TAG = MainFragmentActivity.class.getSimpleName();

    private static final int USER_WEATHER_FRAGMENT = 0;
    private static final int BOOKMARK_WEATHER_LIST_FRAGMENT = 1;

    protected abstract Fragment createSingleFragment();
    protected abstract List<Fragment> createUserAndBookmarkListWeatherFragment();

    @LayoutRes
    private int getMainWeatherFragmentLayoutResId() {
        return R.layout.activity_main_weather;
    }

    @LayoutRes
    private int getSingleFragmentLayoutResId() {
        return R.layout.activity_single_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();

        if (createUserAndBookmarkListWeatherFragment() != null) {
            setContentView(getMainWeatherFragmentLayoutResId());

            Fragment userWeatherFragment = fm.findFragmentById(R.id.user_weather);
            Fragment bookmarkWeatherListFragment = fm.findFragmentById(R.id.bookmark_weather_list);

            if (userWeatherFragment == null) {
                userWeatherFragment = createUserAndBookmarkListWeatherFragment().get(USER_WEATHER_FRAGMENT);

                fm.beginTransaction()
                        .add(R.id.user_weather, userWeatherFragment)
                        .commit();
            }

            if (bookmarkWeatherListFragment == null) {
                bookmarkWeatherListFragment = createUserAndBookmarkListWeatherFragment().get(BOOKMARK_WEATHER_LIST_FRAGMENT);

                fm.beginTransaction()
                        .add(R.id.bookmark_weather_list, bookmarkWeatherListFragment)
                        .commit();
            }
        }

        if (createSingleFragment() != null) {
            setContentView(getSingleFragmentLayoutResId());

            Fragment detailedWeatherFragment = fm.findFragmentById(R.id.fragment_container);

            if (detailedWeatherFragment == null) {
                detailedWeatherFragment = createSingleFragment();

                fm.beginTransaction()
                        .add(R.id.fragment_container, detailedWeatherFragment)
                        .commit();
            }
        }
     }
}

