package com.emre.android.weatherapp.ui;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import androidx.fragment.app.Fragment;

import java.util.List;

public class DetailedUserWeatherActivity extends MainFragmentActivity {
    private static final String TAG = DetailedUserWeatherActivity.class.getSimpleName();

    private static final String EXTRA_LOCATION_DATA_ACTIVITY =
            "com.emre.android.weatherapp.locationdata";

    public static Intent newIntent(Context packageContext, Location location) {
        Intent intent = new Intent(packageContext, DetailedUserWeatherActivity.class);
        intent.putExtra(EXTRA_LOCATION_DATA_ACTIVITY, location);
        return intent;
    }

    @Override
    protected Fragment createSingleFragment() {
        Location location = getIntent().getParcelableExtra(EXTRA_LOCATION_DATA_ACTIVITY);
        return DetailedWeatherFragment.newInstance(location);
    }

    @Override
    protected List<Fragment> createWeatherBaseFragment() {
        return null;
    }
}
