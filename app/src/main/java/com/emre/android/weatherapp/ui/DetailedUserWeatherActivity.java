package com.emre.android.weatherapp.ui;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.Fragment;

public class DetailedUserWeatherActivity extends SingleFragmentActivity {
    private static final String TAG = DetailedUserWeatherActivity.class.getSimpleName();

    private static final String EXTRA_LOCATION_DATA_ACTIVITY =
            "com.emre.android.weatherapp.locationdata";

    public static Intent newIntent(Context packageContext, Location location) {
        Intent intent = new Intent(packageContext, DetailedUserWeatherActivity.class);
        intent.putExtra(EXTRA_LOCATION_DATA_ACTIVITY, location);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Location location = getIntent().getParcelableExtra(EXTRA_LOCATION_DATA_ACTIVITY);
        return DetailedWeatherFragment.newInstance(location);
    }

}
