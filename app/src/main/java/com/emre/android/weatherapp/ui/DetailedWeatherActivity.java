package com.emre.android.weatherapp.ui;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.Fragment;

import com.emre.android.weatherapp.dto.LocationDTO;

public class DetailedWeatherActivity extends SingleFragmentActivity {
    private static final String TAG = DetailedWeatherActivity.class.getSimpleName();

    private static final String EXTRA_LOCATION_DATA_ACTIVITY =
            "com.emre.android.weatherapp.locationdata";

    public static Intent newIntent(Context packageContext, Location location) {
        Intent intent = new Intent(packageContext, DetailedWeatherActivity.class);
        intent.putExtra(EXTRA_LOCATION_DATA_ACTIVITY, location);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Location location = getIntent().getParcelableExtra(EXTRA_LOCATION_DATA_ACTIVITY);
        return DetailedWeatherFragment.newInstance(location);
    }
}
