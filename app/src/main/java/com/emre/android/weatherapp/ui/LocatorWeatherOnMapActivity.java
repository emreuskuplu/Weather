package com.emre.android.weatherapp.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class LocatorWeatherOnMapActivity extends SingleFragmentActivity {
    private static final String TAG = LocatorWeatherOnMapActivity.class.getSimpleName();

    private static final int REQUEST_ERROR = 0;

    @Override
    protected Fragment createFragment() {
        return LocatorWeatherOnMapFragment.newInstance();
    }

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, LocatorWeatherOnMapActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = apiAvailability
                    .getErrorDialog(this, resultCode, REQUEST_ERROR);
            errorDialog.show();
        }
    }
}
