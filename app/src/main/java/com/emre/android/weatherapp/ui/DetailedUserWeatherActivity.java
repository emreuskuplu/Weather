package com.emre.android.weatherapp.ui;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.emre.android.weatherapp.R;

import java.util.List;

public class DetailedUserWeatherActivity extends MainFragmentActivity implements INetworkMessage {
    private static final String TAG = DetailedUserWeatherActivity.class.getSimpleName();

    private static final String EXTRA_LOCATION_DATA_ACTIVITY =
            "com.emre.android.weatherapp.location";

    private Toast mOfflineNetworkToast;

    public static Intent newIntent(Context packageContext, Location location) {
        Intent intent = new Intent(packageContext, DetailedUserWeatherActivity.class);
        intent.putExtra(EXTRA_LOCATION_DATA_ACTIVITY, location);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mOfflineNetworkToast = Toast.makeText(this,
                R.string.offline_network_alert_message,
                Toast.LENGTH_LONG);
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

    @Override
    public void showOfflineNetworkAlertMessage() {
        mOfflineNetworkToast.show();
    }
}
