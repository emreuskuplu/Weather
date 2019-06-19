package com.emre.android.weatherapp.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.List;

public class LocatorWeatherOnMapActivity extends MainFragmentActivity {
    private static final String TAG = LocatorWeatherOnMapActivity.class.getSimpleName();

    private static final int REQUEST_ERROR = 0;

    @Override
    protected Fragment createSingleFragment() {
        return LocatorWeatherOnMapFragment.newInstance();
    }

    @Override
    protected List<Fragment> createWeatherBaseFragment() {
        return null;
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
