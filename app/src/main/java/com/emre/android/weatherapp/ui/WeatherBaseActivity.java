package com.emre.android.weatherapp.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.emre.android.weatherapp.R;
import com.emre.android.weatherapp.ui.dialog.RatingBarDialogFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.List;

public class WeatherBaseActivity extends MainFragmentActivity implements IRatingBarDialog, INetworkMessage {
    private static final String TAG = WeatherBaseActivity.class.getSimpleName();

    private static final String RATING_BAR = "RatingBar";
    private static final String DIALOG_RATING_BAR = "DialogRatingBar";
    private static final int REQUEST_ERROR = 0;

    private static boolean sIsShowedRatingBarDialog = false;

    private IRefreshWeather mIUserRefreshWeather;
    private IRefreshWeather mIBookmarkWeatherListRefreshWeather;
    private Toast mOfflineNetworkToast;

    @Override
    protected Fragment createSingleFragment() {
        return null;
    }

    @Override
    protected List<Fragment> createWeatherBaseFragment() {
        List<Fragment> fragmentList = new ArrayList<>();

        fragmentList.add(UserWeatherFragment.newInstance());
        fragmentList.add(BookmarkWeatherListFragment.newInstance());

        return fragmentList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mOfflineNetworkToast = Toast.makeText(this,
                R.string.offline_network_alert_message,
                Toast.LENGTH_LONG);

        ImageButton settingsButton = findViewById(R.id.settings_button);
        ImageButton refreshWeatherButton = findViewById(R.id.refresh_weather_button);
        ImageButton appInfoButton = findViewById(R.id.app_info_button);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = SettingsActivity.newIntent(WeatherBaseActivity.this);

                startActivity(intent);
            }
        });

        refreshWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIUserRefreshWeather =
                        (UserWeatherFragment) getSupportFragmentManager().findFragmentById(R.id.user_weather);

                if (mIUserRefreshWeather != null) {
                    mIUserRefreshWeather.refreshWeather();
                } else {
                    Log.e(TAG, "mIUserRefreshWeather is null");
                }

                mIBookmarkWeatherListRefreshWeather =
                        (BookmarkWeatherListFragment) getSupportFragmentManager().findFragmentById(R.id.bookmark_weather_list);

                if (mIBookmarkWeatherListRefreshWeather != null) {
                     mIBookmarkWeatherListRefreshWeather.refreshWeather();
                } else {
                    Log.e(TAG, "mIBookmarkWeatherListRefreshWeather is null");
                }
            }
        });

        appInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AppInfoPageActivity.newIntent(WeatherBaseActivity.this);

                startActivity(intent);
            }
        });

        if (savedInstanceState != null) {
            sIsShowedRatingBarDialog = savedInstanceState.getBoolean(DIALOG_RATING_BAR);
        }

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = apiAvailability
                    .getErrorDialog(this, resultCode, REQUEST_ERROR);
            errorDialog.show();
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(DIALOG_RATING_BAR, sIsShowedRatingBarDialog);
    }

    public static void deactivateRatingBarDialog() {
        sIsShowedRatingBarDialog = true;
    }

    @Override
    public void showRatingBarDialog() {
        if (!sIsShowedRatingBarDialog) {
            FragmentManager manager = getSupportFragmentManager();

            RatingBarDialogFragment dialog = RatingBarDialogFragment.newInstance();

            dialog.show(manager, RATING_BAR);

            sIsShowedRatingBarDialog = true;
        }
    }

    @Override
    public void showOfflineNetworkAlertMessage() {
        mOfflineNetworkToast.show();
    }
}
