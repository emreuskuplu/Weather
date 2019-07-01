/*
 * Copyright (c) 2019. Emre Üsküplü
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.emre.android.weatherapp.scenes.detailedweather;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.emre.android.weatherapp.R;
import com.emre.android.weatherapp.scenes.INetworkStatus;
import com.emre.android.weatherapp.scenes.main.MainFragmentActivity;

import java.util.List;

/**
 * @author Emre Üsküplü
 *
 * Sends detailed weather fragment to main fragment activity
 */
public class DetailedUserWeatherActivity extends MainFragmentActivity implements INetworkStatus {
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
    protected Fragment createSingleFragment() {
        Location location = getIntent().getParcelableExtra(EXTRA_LOCATION_DATA_ACTIVITY);
        return DetailedWeatherFragment.newInstance(location);
    }

    @Override
    protected List<Fragment> createUserAndBookmarkListWeatherFragment() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mOfflineNetworkToast = Toast.makeText(this,
                R.string.offline_network_alert_message,
                Toast.LENGTH_LONG);
    }

    @Override
    public boolean isOnlineNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;

        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        } else {
            // If there is not default network then ignore isAvailableNetworkConnection condition
            Log.i(TAG, "There is not default network");
            return true;
        }

        return networkInfo != null && networkInfo.isConnected();

    }

    @Override
    public void showOfflineNetworkAlertMessage() {
        mOfflineNetworkToast.show();
    }
}
