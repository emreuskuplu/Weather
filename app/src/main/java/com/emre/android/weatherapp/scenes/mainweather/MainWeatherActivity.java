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

package com.emre.android.weatherapp.scenes.mainweather;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.emre.android.weatherapp.R;
import com.emre.android.weatherapp.scenes.INetworkStatus;
import com.emre.android.weatherapp.scenes.IRefreshWeather;
import com.emre.android.weatherapp.scenes.settings.SettingsActivity;
import com.emre.android.weatherapp.scenes.userweather.UserWeatherFragment;
import com.emre.android.weatherapp.scenes.appinfopage.AppInfoPageActivity;
import com.emre.android.weatherapp.scenes.bookmarklistweather.BookmarkListWeatherFragment;
import com.emre.android.weatherapp.scenes.mainweather.dialog.RatingBarDialogFragment;
import com.emre.android.weatherapp.scenes.main.MainFragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emre Üsküplü
 *
 * First activity running when app starts
 * Sends fragments of user weather fragment and bookmark list weather fragment to main fragment activity
 * When user clicks settings button then starts settings activity
 * When user clicks refresh weather button then refrehes weather of user and bookmark list from fragments
 * When user clicks app info button starts app info activity
 * Checks availability of google play service
 */
public class MainWeatherActivity extends MainFragmentActivity implements INetworkStatus {
    private static final String TAG = MainWeatherActivity.class.getSimpleName();

    private static final String RATING_BAR = "RatingBar";
    private static final String DIALOG_RATING_BAR = "DialogRatingBar";
    private static final int REQUEST_ERROR = 0;
    private static boolean sIsShowedRatingBarDialog = false;

    private Toast mOfflineNetworkToast;

    private View.OnClickListener mISettingsButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = SettingsActivity.newIntent(MainWeatherActivity.this);
            startActivity(intent);
        }
    };

    private View.OnClickListener mIRefreshWeatherButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            IRefreshWeather IUserRefreshWeather =
                    (UserWeatherFragment) getSupportFragmentManager().findFragmentById(R.id.user_weather);

            if (IUserRefreshWeather != null) {
                IUserRefreshWeather.refreshWeather();
            } else {
                Log.e(TAG, "mIUserRefreshWeather is null");
            }

            IRefreshWeather IBookmarkListWeatherRefreshWeather =
                    (BookmarkListWeatherFragment) getSupportFragmentManager().findFragmentById(R.id.bookmark_weather_list);

            if (IBookmarkListWeatherRefreshWeather != null) {
                IBookmarkListWeatherRefreshWeather.refreshWeather();
            } else {
                Log.e(TAG, "mIBookmarkListWeatherRefreshWeather is null");
            }
        }
    };

    private View.OnClickListener mIAppInfoButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = AppInfoPageActivity.newIntent(MainWeatherActivity.this);
            startActivity(intent);
        }
    };

    @Override
    protected Fragment createSingleFragment() {
        return null;
    }

    @Override
    protected List<Fragment> createUserAndBookmarkListWeatherFragment() {
        List<Fragment> fragmentList = new ArrayList<>();

        fragmentList.add(UserWeatherFragment.newInstance());
        fragmentList.add(BookmarkListWeatherFragment.newInstance());

        return fragmentList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageButton settingsButton = findViewById(R.id.settings_button);
        ImageButton refreshWeatherButton = findViewById(R.id.refresh_weather_button);
        ImageButton appInfoButton = findViewById(R.id.app_info_button);

        settingsButton.setOnClickListener(mISettingsButtonClickListener);
        refreshWeatherButton.setOnClickListener(mIRefreshWeatherButtonClickListener);
        appInfoButton.setOnClickListener(mIAppInfoButtonClickListener);

        if (savedInstanceState != null) {
            sIsShowedRatingBarDialog = savedInstanceState.getBoolean(DIALOG_RATING_BAR);
        }

        mOfflineNetworkToast = Toast.makeText(this,
                R.string.offline_network_alert_message,
                Toast.LENGTH_LONG);

        checksAvailabilityGooglePlayService();
        showRatingBarDialog();
     }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(DIALOG_RATING_BAR, sIsShowedRatingBarDialog);
    }

    public static void deactivateRatingBarDialog() {
        sIsShowedRatingBarDialog = true;
    }

    private void checksAvailabilityGooglePlayService() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = apiAvailability
                    .getErrorDialog(this, resultCode, REQUEST_ERROR);
            errorDialog.show();
        }
    }

    private void showRatingBarDialog() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!sIsShowedRatingBarDialog) {
                    FragmentManager manager = getSupportFragmentManager();
                    RatingBarDialogFragment dialog = RatingBarDialogFragment.newInstance();

                    dialog.show(manager, RATING_BAR);

                    sIsShowedRatingBarDialog = true;
                }
            }
        }, 5000);
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
