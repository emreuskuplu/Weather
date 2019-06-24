package com.emre.android.weatherapp.ui;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import java.util.List;

public class SettingsActivity extends MainFragmentActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected Fragment createSingleFragment() {
        return SettingsFragment.newInstance();
    }

    @Override
    protected List<Fragment> createWeatherBaseFragment() {
        return null;
    }
}
