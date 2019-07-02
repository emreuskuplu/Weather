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

import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.emre.android.weatherapp.R;
import com.emre.android.weatherapp.dataaccessobjects.settingsdataaccess.SettingsDAO;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.WeatherDTO;
import com.emre.android.weatherapp.scenes.INetworkStatus;
import com.emre.android.weatherapp.scenes.IUpdateWeather;
import com.emre.android.weatherapp.scenes.detailedweather.workerthread.ForecastDetailedWeatherTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Emre Üsküplü
 *
 * Shows detailed weather and weather of forecast days
 * When user clicks a forecast day layout then refreshes all weather and sets detailed weather of selected forecast day
 * When user clicks back button then it's finish parent activity
 * When user clicks refresh button then refreshes all weather
 */
public class DetailedWeatherFragment extends Fragment implements IUpdateWeather {
    private static final String TAG = DetailedWeatherFragment.class.getSimpleName();

    private static final String ARG_LOCATION_DATA_INFO =
            "com.emre.android.weatherapp.locationdata";

    private static int sSelectedDay;
    private static List<WeatherDTO> sWeatherDTOList;
    private static Location sLocation;

    private String mUnitsFormat = "";
    private SettingsDAO mSettingsDAO;

    private int mSelectedDay = 0;
    private INetworkStatus mINetworkStatus;

    private Location mLocation;
    private ImageView mWeatherImageView;
    private TextView mLocationName;
    private TextView mDetailedDayName;
    private TextView mTempDegree;
    private TextView mDescription;
    private TextView mHumidityVolume;
    private TextView mWindVolume;
    private TextView mRainVolume;
    private TextView mSnowVolume;
    private ImageButton mBackButton;
    private ImageButton mRefreshWeatherButton;
    private ProgressBar mWeatherProgressBar;

    private ConstraintLayout mFirstDayLayout;
    private TextView mFirstDayName;
    private ImageView mFirstDayWeatherImage;
    private TextView mFirstDayTempDegree;
    private ConstraintLayout mSecondDayLayout;
    private TextView mSecondDayName;
    private ImageView mSecondDayWeatherImage;
    private TextView mSecondDayTempDegree;
    private ConstraintLayout mThirdDayLayout;
    private TextView mThirdDayName;
    private ImageView mThirdDayWeatherImage;
    private TextView mThirdDayTempDegree;
    private ConstraintLayout mFourthDayLayout;
    private TextView mFourthDayName;
    private ImageView mFourthDayWeatherImage;
    private TextView mFourthDayTempDegree;
    private ConstraintLayout mFifthDayLayout;
    private TextView mFifthDayName;
    private ImageView mFifthDayWeatherImage;
    private TextView mFifthDayTempDegree;

    private View.OnClickListener mDayLayoutOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Object tag = view.getTag();

            if (tag.equals(mFirstDayLayout.getTag())) {
                mSelectedDay = 0;
            } else if (tag.equals(mSecondDayLayout.getTag())) {
                mSelectedDay = 1;
            } else if (tag.equals(mThirdDayLayout.getTag())) {
                mSelectedDay = 2;
            } else if (tag.equals(mFourthDayLayout.getTag())) {
                mSelectedDay = 3;
            } else if (tag.equals(mFifthDayLayout.getTag())) {
                mSelectedDay = 4;
            }

            sSelectedDay = mSelectedDay;

            executeForecastDetailedWeatherTask();
        }
    };

    private View.OnClickListener mBackButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            requireActivity().finish();
        }
    };

    private View.OnClickListener mRefreshWeatherOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            executeForecastDetailedWeatherTask();
        }
    };

    public static DetailedWeatherFragment newInstance(Location location) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOCATION_DATA_INFO, location);
        DetailedWeatherFragment detailedWeatherFragment = new DetailedWeatherFragment();
        detailedWeatherFragment.setArguments(args);
        return detailedWeatherFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSettingsDAO = new SettingsDAO();

        if (getArguments() != null) {
            mLocation = getArguments().getParcelable(ARG_LOCATION_DATA_INFO);
            sLocation = mLocation;
        } else {
            Log.e(TAG, "getArgument is null");

            issueLocationValuesToast();
            requireActivity().finish();
         }

        if (requireActivity().getClass().getSimpleName().equals("DetailedBookmarkListWeatherActivity")) {
            mINetworkStatus = (DetailedBookmarkListWeatherActivity) requireActivity();
        } else if (requireActivity().getClass().getSimpleName().equals("DetailedUserWeatherActivity")) {
            mINetworkStatus = (DetailedUserWeatherActivity) requireActivity();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detailed_weather, container, false);

        mWeatherImageView = v.findViewById(R.id.weather_image);
        mLocationName = v.findViewById(R.id.location_name);
        mDetailedDayName = v.findViewById(R.id.detailed_day);
        mTempDegree = v.findViewById(R.id.temp_degree);
        mDescription = v.findViewById(R.id.description);
        mHumidityVolume = v.findViewById(R.id.humidity_volume);
        mWindVolume = v.findViewById(R.id.wind_volume);
        mRainVolume = v.findViewById(R.id.rain_volume);
        mSnowVolume = v.findViewById(R.id.snow_volume);
        mBackButton = v.findViewById(R.id.back_button);
        mRefreshWeatherButton = v.findViewById(R.id.refresh_weather_button);
        mWeatherProgressBar = v.findViewById(R.id.weather_progress_bar);

        mFirstDayLayout = v.findViewById(R.id.first_day_layout);
        mFirstDayName = v.findViewById(R.id.first_day_date);
        mFirstDayWeatherImage = v.findViewById(R.id.first_day_weather_image);
        mFirstDayTempDegree = v.findViewById(R.id.first_day_temp_degree);
        mSecondDayLayout = v.findViewById(R.id.second_day_layout);
        mSecondDayName = v.findViewById(R.id.second_day_date);
        mSecondDayWeatherImage = v.findViewById(R.id.second_day_weather_image);
        mSecondDayTempDegree = v.findViewById(R.id.second_day_temp_degree);
        mThirdDayLayout = v.findViewById(R.id.third_day_layout);
        mThirdDayName = v.findViewById(R.id.third_day_date);
        mThirdDayWeatherImage = v.findViewById(R.id.third_day_weather_image);
        mThirdDayTempDegree = v.findViewById(R.id.third_day_temp_degree);
        mFourthDayLayout = v.findViewById(R.id.fourth_day_layout);
        mFourthDayName = v.findViewById(R.id.fourth_day_date);
        mFourthDayWeatherImage = v.findViewById(R.id.fourth_day_weather_image);
        mFourthDayTempDegree = v.findViewById(R.id.fourth_day_temp_degree);
        mFifthDayLayout = v.findViewById(R.id.fifth_day_layout);
        mFifthDayName = v.findViewById(R.id.fifth_day_date);
        mFifthDayWeatherImage = v.findViewById(R.id.fifth_day_weather_image);
        mFifthDayTempDegree = v.findViewById(R.id.fifth_day_temp_degree);

        mFirstDayLayout.setTag(mFirstDayLayout);
        mSecondDayLayout.setTag(mSecondDayLayout);
        mThirdDayLayout.setTag(mThirdDayLayout);
        mFourthDayLayout.setTag(mFourthDayLayout);
        mFifthDayLayout.setTag(mFifthDayLayout);

        mFirstDayLayout.setOnClickListener(mDayLayoutOnClickListener);
        mSecondDayLayout.setOnClickListener(mDayLayoutOnClickListener);
        mThirdDayLayout.setOnClickListener(mDayLayoutOnClickListener);
        mFourthDayLayout.setOnClickListener(mDayLayoutOnClickListener);
        mFifthDayLayout.setOnClickListener(mDayLayoutOnClickListener);

        mBackButton.setOnClickListener(mBackButtonOnClickListener);
        mRefreshWeatherButton.setOnClickListener(mRefreshWeatherOnClickListener);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        String units = mSettingsDAO.getPrefUnitsFormatStorage(requireContext());

        if (units.equals("metric")) {
            mUnitsFormat = "°C";

        } else if (units.equals("fahrenheit")) {
            mUnitsFormat = "°F";
        }

        mTempDegree.setText(mUnitsFormat);

        mFirstDayTempDegree.setText(mUnitsFormat);
        mSecondDayTempDegree.setText(mUnitsFormat);
        mThirdDayTempDegree.setText(mUnitsFormat);
        mFourthDayTempDegree.setText(mUnitsFormat);
        mFifthDayTempDegree.setText(mUnitsFormat);

        executeForecastDetailedWeatherTask();
    }

    public static Location getLocation() {
        return sLocation;
    }

    public static int getSelectedDay() {
        return sSelectedDay;
    }

    public static List<WeatherDTO> getWeatherDTOList() {
        return sWeatherDTOList;
    }

    public static String changeShortDateToLargeDate(String shortDate) {
        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String detailedDayName = "";
        try {
            Date longDate = outFormat.parse(shortDate);

            if (longDate != null) {
                detailedDayName = outFormat.format(longDate);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return detailedDayName;
    }

    private void issueLocationValuesToast() {
        Toast.makeText(requireContext(),
                R.string.issue_location_values,
                Toast.LENGTH_SHORT)
                .show();
    }

    private void executeForecastDetailedWeatherTask() {
        if (mINetworkStatus.isOnlineNetworkConnection()) {
            mWeatherProgressBar.setVisibility(View.VISIBLE);
            new ForecastDetailedWeatherTask(requireContext(),
                    this, mSelectedDay).execute(mLocation);
        } else {
            mINetworkStatus.showOfflineNetworkAlertMessage();
        }
    }

    private void updateForecastDaysName(List<WeatherDTO> weatherDTOList) {
        List<TextView> weatherDayNameList = new ArrayList<>();
        weatherDayNameList.add(mFirstDayName);
        weatherDayNameList.add(mSecondDayName);
        weatherDayNameList.add(mThirdDayName);
        weatherDayNameList.add(mFourthDayName);
        weatherDayNameList.add(mFifthDayName);

        int weatherForecastDaysLimit = 5;

        for (int i = 0; i < weatherForecastDaysLimit; i++) {
            WeatherDTO weatherDTO = weatherDTOList.get(i);
            String dayName = weatherDTO.getDate();
            TextView weatherDayNameTextView = weatherDayNameList.get(i);

            weatherDayNameTextView.setText(dayName);
            weatherDayNameTextView.setTextColor(mDescription.getTextColors());
        }

        TextView selectedDayName = weatherDayNameList.get(mSelectedDay);
        selectedDayName.setTextColor(mDetailedDayName.getTextColors());
    }

    private void updateForecastDaysImageView(List<WeatherDTO> weatherDTOList) {
        List<ImageView> weatherForecastDaysImageViewList = new ArrayList<>();
        weatherForecastDaysImageViewList.add(mFirstDayWeatherImage);
        weatherForecastDaysImageViewList.add(mSecondDayWeatherImage);
        weatherForecastDaysImageViewList.add(mThirdDayWeatherImage);
        weatherForecastDaysImageViewList.add(mFourthDayWeatherImage);
        weatherForecastDaysImageViewList.add(mFifthDayWeatherImage);

        int weatherForecastDaysLimit = 5;

        for (int i = 0; i < weatherForecastDaysLimit; i++) {
            WeatherDTO weatherDTO = weatherDTOList.get(i);
            updateWeatherImage(weatherDTO, weatherForecastDaysImageViewList.get(i));
        }
    }

    private void updateForecastDaysTempDegree(List<WeatherDTO> weatherDTOList) {
        List<TextView> weatherForecastDaysTempDegreeList = new ArrayList<>();
        weatherForecastDaysTempDegreeList.add(mFirstDayTempDegree);
        weatherForecastDaysTempDegreeList.add(mSecondDayTempDegree);
        weatherForecastDaysTempDegreeList.add(mThirdDayTempDegree);
        weatherForecastDaysTempDegreeList.add(mFourthDayTempDegree);
        weatherForecastDaysTempDegreeList.add(mFifthDayTempDegree);

        int weatherForecastDaysLimit = 5;

        for (int i = 0; i < weatherForecastDaysLimit; i++) {
            WeatherDTO weatherDTO = weatherDTOList.get(i);
            String tempDegree =
                    getString(R.string.temp_degree, weatherDTO.getTempDegree(), mUnitsFormat);
            TextView tempDegreeTextView = weatherForecastDaysTempDegreeList.get(i);
            tempDegreeTextView.setText(tempDegree);
        }
    }

    private void updateWeatherImage(WeatherDTO weatherDTO,
                                    ImageView weatherImageView) {
        String mainDescription = weatherDTO.getMainDescription();

        if (mainDescription.equals(getString(R.string.clear))) {
            weatherImageView.setImageResource(R.drawable.sun);
            weatherImageView.setContentDescription(getString(R.string.weather_image,
                    getString(R.string.user_location), getString(R.string.weather_image_is_sun)));

        } else if (mainDescription.equals(getString(R.string.clouds))) {
            if (mainDescription.equals(getString(R.string.few_clouds))) {
                weatherImageView.setImageResource(R.drawable.sunny_cloud);
                weatherImageView.setContentDescription(getString(R.string.weather_image,
                        getString(R.string.user_location), getString(R.string.weather_image_is_sunny_cloud)));
            } else {
                weatherImageView.setImageResource(R.drawable.cloud);
                weatherImageView.setContentDescription(getString(R.string.weather_image,
                        getString(R.string.user_location), getString(R.string.weather_image_is_cloud)));
            }

        } else if (mainDescription.equals(getString(R.string.drizzle))) {
            weatherImageView.setImageResource(R.drawable.rain);
            weatherImageView.setContentDescription(getString(R.string.weather_image,
                    getString(R.string.user_location), getString(R.string.weather_image_is_rain)));

        } else if (mainDescription.equals(getString(R.string.rain))) {
            weatherImageView.setImageResource(R.drawable.rain);
            weatherImageView.setContentDescription(getString(R.string.weather_image,
                    getString(R.string.user_location), getString(R.string.weather_image_is_rain)));

        } else if (mainDescription.equals(getString(R.string.thunderstorm))) {
            weatherImageView.setImageResource(R.drawable.thunderstorm);
            weatherImageView.setContentDescription(getString(R.string.weather_image,
                    getString(R.string.user_location), getString(R.string.weather_image_is_thunderstorm)));

        } else if (mainDescription.equals(getString(R.string.snow))) {
            weatherImageView.setImageResource(R.drawable.snow);
            weatherImageView.setContentDescription(getString(R.string.weather_image,
                    getString(R.string.user_location), getString(R.string.weather_image_is_snow)));

        } else {
            String[] atmosphereDescriptions = {getString(R.string.mist), getString(R.string.smoke), getString(R.string.haze),
                    getString(R.string.dust), getString(R.string.fog), getString(R.string.sand), getString(R.string.ash),
                    getString(R.string.squall), getString(R.string.tornado)};

            for (String atmosphereDescription : atmosphereDescriptions) {
                if (weatherDTO.getMainDescription().equals(atmosphereDescription)) {
                    weatherImageView.setImageResource(R.drawable.mist);
                    weatherImageView.setContentDescription(getString(R.string.weather_image,
                            getString(R.string.user_location), getString(R.string.weather_image_is_mist)));
                }
            }
        }
    }

    @Override
    public void updateWeather(WeatherDTO weatherDTO) {
        if (isAdded()) {
            String detailedDayName = changeShortDateToLargeDate(weatherDTO.getDate());
            String tempDegree =
                    getString(R.string.temp_degree, weatherDTO.getTempDegree(), mUnitsFormat);

            mLocationName.setText(weatherDTO.getLocationName());
            mDetailedDayName.setText(detailedDayName);
            mTempDegree.setText(tempDegree);
            mDescription.setText(weatherDTO.getDescription());
            mHumidityVolume.setText(weatherDTO.getHumidity());
            mWindVolume.setText(weatherDTO.getWindVolume());
            mRainVolume.setText(weatherDTO.getRainVolume());
            mSnowVolume.setText(weatherDTO.getSnowVolume());

            updateWeatherImage(weatherDTO, mWeatherImageView);

            mWeatherProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateListWeather(List<WeatherDTO> weatherDTOList) {
        if (isAdded()) {
            sWeatherDTOList = weatherDTOList;

            updateForecastDaysName(weatherDTOList);
            updateForecastDaysImageView(weatherDTOList);
            updateForecastDaysTempDegree(weatherDTOList);

            mWeatherProgressBar.setVisibility(View.GONE);
        }
    }
}
