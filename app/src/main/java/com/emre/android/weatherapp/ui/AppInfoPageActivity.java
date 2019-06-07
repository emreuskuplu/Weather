package com.emre.android.weatherapp.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class AppInfoPageActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return AppInfoPageFragment.newInstance();
    }

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, AppInfoPageActivity.class);
    }
}
