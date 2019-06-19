package com.emre.android.weatherapp.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.List;

public class AppInfoPageActivity extends MainFragmentActivity {

    @Override
    protected Fragment createSingleFragment() {
        return AppInfoPageFragment.newInstance();
    }

    @Override
    protected List<Fragment> createWeatherBaseFragment() {
        return null;
    }

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, AppInfoPageActivity.class);
    }
}
