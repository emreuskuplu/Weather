package com.emre.android.weatherapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageButton;

import com.emre.android.weatherapp.R;

import java.util.ArrayList;
import java.util.List;

public class WeatherBaseActivity extends MainFragmentActivity implements IRatingBarDialog {

    private static final String RATING_BAR = "RatingBar";
    private static final String DIALOG_RATING_BAR = "DialogRatingBar";

    private IRefreshWeather mIUserRefreshWeather;
    private IRefreshWeather mIBookmarkWeatherListRefreshWeather;

    private static boolean sIsShowedRatingBarDialog = false;

    @Override
    protected Fragment createSingleFragment() {
        return null;
    }

    @Override
    protected List<Fragment> createWeatherBaseFragment() {
        List<Fragment> fragmentList = new ArrayList<>();

        fragmentList.add( UserWeatherFragment.newInstance());
        fragmentList.add(BookmarkWeatherListFragment.newInstance());

        return fragmentList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageButton refreshWeatherButton = findViewById(R.id.refresh_weather_button);
        ImageButton appInfoButton = findViewById(R.id.app_info_button);

        refreshWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSupportFragmentManager().findFragmentById(R.id.user_weather) != null) {
                    mIUserRefreshWeather =
                            (UserWeatherFragment) getSupportFragmentManager().findFragmentById(R.id.user_weather);
                }

                if (getSupportFragmentManager().findFragmentById(R.id.bookmark_weather_list) != null) {
                    mIBookmarkWeatherListRefreshWeather =
                            (BookmarkWeatherListFragment) getSupportFragmentManager().findFragmentById(R.id.bookmark_weather_list);

                }

                mIUserRefreshWeather.refreshWeather();
                mIBookmarkWeatherListRefreshWeather.refreshWeather();
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
            RatingBarFragment dialog = RatingBarFragment.newInstance();

            dialog.show(manager, RATING_BAR);

            sIsShowedRatingBarDialog = true;
        }
    }
}
