package com.emre.android.weatherapp.ui;

import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import com.emre.android.weatherapp.R;

import java.util.List;

public abstract class MainFragmentActivity extends AppCompatActivity {
    private static final String TAG = MainFragmentActivity.class.getSimpleName();

    private static final int USER_WEATHER_FRAGMENT = 0;
    private static final int BOOKMARK_WEATHER_LIST_FRAGMENT = 1;

    protected abstract Fragment createSingleFragment();
    protected abstract List<Fragment> createWeatherBaseFragment();

    @LayoutRes
    private int getWeatherBaseFragmentLayoutResId() {
        return R.layout.activity_weather_base;
    }

    @LayoutRes
    private int getSingleFragmentLayoutResId() {
        return R.layout.activity_single_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();

        if (createWeatherBaseFragment() != null) {
            setContentView(getWeatherBaseFragmentLayoutResId());

            Fragment userWeatherFragment = fm.findFragmentById(R.id.user_weather);
            Fragment bookmarkWeatherListFragment = fm.findFragmentById(R.id.bookmark_weather_list);

            if (userWeatherFragment == null) {
                userWeatherFragment = createWeatherBaseFragment().get(USER_WEATHER_FRAGMENT);

                fm.beginTransaction()
                        .add(R.id.user_weather, userWeatherFragment)
                        .commit();
            }

            if (bookmarkWeatherListFragment == null) {
                bookmarkWeatherListFragment = createWeatherBaseFragment().get(BOOKMARK_WEATHER_LIST_FRAGMENT);

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

