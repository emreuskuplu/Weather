package com.emre.android.weatherapp.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class WeatherListActivity extends SingleFragmentActivity {
    private static final String TAG = WeatherListActivity.class.getSimpleName();

    private static final int REQUEST_ERROR = 0;

    @Override
    protected Fragment createFragment() {
        return WeatherListFragment.newInstance();
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
