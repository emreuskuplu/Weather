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
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.DetailedWeatherDTO;
import com.emre.android.weatherapp.scenes.INetworkStatus;
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
 * Shows detailed weather of selected day and forecast days
 * When user clicks a forecast day view then refreshes all weather and sets detailed weather of selected forecast day
 * When user clicks back button then it's finish parent activity
 * When user clicks refresh button then refreshes all weather
 */
public class DetailedWeatherFragment extends Fragment implements IUpdateDetailedWeather, IUpdateForecastDayListWeather {
    private static final String TAG = DetailedWeatherFragment.class.getSimpleName();

    private static final String ARG_LOCATION_DATA_INFO =
            "com.emre.android.weatherapp.locationdata";

    private static int sSelectedDay;
    private static List<DetailedWeatherDTO> sDetailedWeatherDTOList;
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

    private View mFirstDayView;
    private TextView mFirstDayName;
    private ImageView mFirstDayWeatherImage;
    private TextView mFirstDayTempDegree;
    private View mSecondDayView;
    private TextView mSecondDayName;
    private ImageView mSecondDayWeatherImage;
    private TextView mSecondDayTempDegree;
    private View mThirdDayView;
    private TextView mThirdDayName;
    private ImageView mThirdDayWeatherImage;
    private TextView mThirdDayTempDegree;
    private View mFourthDayView;
    private TextView mFourthDayName;
    private ImageView mFourthDayWeatherImage;
    private TextView mFourthDayTempDegree;
    private View mFifthDayView;
    private TextView mFifthDayName;
    private ImageView mFifthDayWeatherImage;
    private TextView mFifthDayTempDegree;

    private View.OnClickListener mDayLayoutOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Object tag = view.getTag();

            if (tag.equals(mFirstDayView.getTag())) {
                mSelectedDay = 0;
            } else if (tag.equals(mSecondDayView.getTag())) {
                mSelectedDay = 1;
            } else if (tag.equals(mThirdDayView.getTag())) {
                mSelectedDay = 2;
            } else if (tag.equals(mFourthDayView.getTag())) {
                mSelectedDay = 3;
            } else if (tag.equals(mFifthDayView.getTag())) {
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

        mFirstDayView = v.findViewById(R.id.first_day_view);
        mFirstDayName = v.findViewById(R.id.first_day_date);
        mFirstDayWeatherImage = v.findViewById(R.id.first_day_weather_image);
        mFirstDayTempDegree = v.findViewById(R.id.first_day_temp_degree);
        mSecondDayView = v.findViewById(R.id.second_day_view);
        mSecondDayName = v.findViewById(R.id.second_day_date);
        mSecondDayWeatherImage = v.findViewById(R.id.second_day_weather_image);
        mSecondDayTempDegree = v.findViewById(R.id.second_day_temp_degree);
        mThirdDayView = v.findViewById(R.id.third_day_view);
        mThirdDayName = v.findViewById(R.id.third_day_date);
        mThirdDayWeatherImage = v.findViewById(R.id.third_day_weather_image);
        mThirdDayTempDegree = v.findViewById(R.id.third_day_temp_degree);
        mFourthDayView = v.findViewById(R.id.fourth_day_view);
        mFourthDayName = v.findViewById(R.id.fourth_day_date);
        mFourthDayWeatherImage = v.findViewById(R.id.fourth_day_weather_image);
        mFourthDayTempDegree = v.findViewById(R.id.fourth_day_temp_degree);
        mFifthDayView = v.findViewById(R.id.fifth_day_view);
        mFifthDayName = v.findViewById(R.id.fifth_day_date);
        mFifthDayWeatherImage = v.findViewById(R.id.fifth_day_weather_image);
        mFifthDayTempDegree = v.findViewById(R.id.fifth_day_temp_degree);

        mFirstDayView.setTag(mFirstDayView);
        mSecondDayView.setTag(mSecondDayView);
        mThirdDayView.setTag(mThirdDayView);
        mFourthDayView.setTag(mFourthDayView);
        mFifthDayView.setTag(mFifthDayView);

        mFirstDayView.setOnClickListener(mDayLayoutOnClickListener);
        mSecondDayView.setOnClickListener(mDayLayoutOnClickListener);
        mThirdDayView.setOnClickListener(mDayLayoutOnClickListener);
        mFourthDayView.setOnClickListener(mDayLayoutOnClickListener);
        mFifthDayView.setOnClickListener(mDayLayoutOnClickListener);

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

    public static List<DetailedWeatherDTO> getDetailedWeatherDTOList() {
        return sDetailedWeatherDTOList;
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

    private void updateForecastDaysName(List<DetailedWeatherDTO> detailedWeatherDTOList) {
        List<TextView> weatherDayNameList = new ArrayList<>();
        weatherDayNameList.add(mFirstDayName);
        weatherDayNameList.add(mSecondDayName);
        weatherDayNameList.add(mThirdDayName);
        weatherDayNameList.add(mFourthDayName);
        weatherDayNameList.add(mFifthDayName);

        int weatherForecastDaysLimit = 5;

        for (int i = 0; i < weatherForecastDaysLimit; i++) {
            DetailedWeatherDTO detailedWeatherDTO = detailedWeatherDTOList.get(i);
            String dayName = detailedWeatherDTO.getDate();
            TextView weatherDayNameTextView = weatherDayNameList.get(i);

            weatherDayNameTextView.setText(dayName);
            weatherDayNameTextView.setTextColor(mDescription.getTextColors());
        }

        TextView selectedDayName = weatherDayNameList.get(mSelectedDay);
        selectedDayName.setTextColor(mDetailedDayName.getTextColors());
    }

    private void updateForecastDaysImageView(List<DetailedWeatherDTO> detailedWeatherDTOList) {
        List<ImageView> weatherForecastDaysImageViewList = new ArrayList<>();
        weatherForecastDaysImageViewList.add(mFirstDayWeatherImage);
        weatherForecastDaysImageViewList.add(mSecondDayWeatherImage);
        weatherForecastDaysImageViewList.add(mThirdDayWeatherImage);
        weatherForecastDaysImageViewList.add(mFourthDayWeatherImage);
        weatherForecastDaysImageViewList.add(mFifthDayWeatherImage);

        int weatherForecastDaysLimit = 5;

        for (int i = 0; i < weatherForecastDaysLimit; i++) {
            DetailedWeatherDTO detailedWeatherDTO = detailedWeatherDTOList.get(i);
            updateWeatherImage(detailedWeatherDTO, weatherForecastDaysImageViewList.get(i));
        }
    }

    private void updateForecastDaysTempDegree(List<DetailedWeatherDTO> detailedWeatherDTOList) {
        List<TextView> weatherForecastDaysTempDegreeList = new ArrayList<>();
        weatherForecastDaysTempDegreeList.add(mFirstDayTempDegree);
        weatherForecastDaysTempDegreeList.add(mSecondDayTempDegree);
        weatherForecastDaysTempDegreeList.add(mThirdDayTempDegree);
        weatherForecastDaysTempDegreeList.add(mFourthDayTempDegree);
        weatherForecastDaysTempDegreeList.add(mFifthDayTempDegree);

        int weatherForecastDaysLimit = 5;

        for (int i = 0; i < weatherForecastDaysLimit; i++) {
            DetailedWeatherDTO detailedWeatherDTO = detailedWeatherDTOList.get(i);
            String tempDegree =
                    getString(R.string.temp_degree, detailedWeatherDTO.getTempDegree(), mUnitsFormat);
            TextView tempDegreeTextView = weatherForecastDaysTempDegreeList.get(i);
            tempDegreeTextView.setText(tempDegree);
        }
    }

    private void updateWeatherImage(DetailedWeatherDTO detailedWeatherDTO,
                                    ImageView weatherImageView) {
        String mainDescription = detailedWeatherDTO.getMainDescription();

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
                if (detailedWeatherDTO.getMainDescription().equals(atmosphereDescription)) {
                    weatherImageView.setImageResource(R.drawable.mist);
                    weatherImageView.setContentDescription(getString(R.string.weather_image,
                            getString(R.string.user_location), getString(R.string.weather_image_is_mist)));
                }
            }
        }
    }

    @Override
    public void updateDetailedWeather(DetailedWeatherDTO detailedWeatherDTO) {
        if (isAdded()) {
            String detailedDayName = changeShortDateToLargeDate(detailedWeatherDTO.getDate());
            String tempDegree =
                    getString(R.string.temp_degree, detailedWeatherDTO.getTempDegree(), mUnitsFormat);

            mLocationName.setText(detailedWeatherDTO.getLocationName());
            mDetailedDayName.setText(detailedDayName);
            mTempDegree.setText(tempDegree);
            mDescription.setText(detailedWeatherDTO.getDescription());
            mHumidityVolume.setText(detailedWeatherDTO.getHumidity());
            mWindVolume.setText(detailedWeatherDTO.getWindVolume());
            mRainVolume.setText(detailedWeatherDTO.getRainVolume());
            mSnowVolume.setText(detailedWeatherDTO.getSnowVolume());

            updateWeatherImage(detailedWeatherDTO, mWeatherImageView);

            mWeatherProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateForecastDayListWeather(List<DetailedWeatherDTO> detailedWeatherDTOList) {
        if (isAdded()) {
            sDetailedWeatherDTOList = detailedWeatherDTOList;

            updateForecastDaysName(detailedWeatherDTOList);
            updateForecastDaysImageView(detailedWeatherDTOList);
            updateForecastDaysTempDegree(detailedWeatherDTOList);

            mWeatherProgressBar.setVisibility(View.GONE);
        }
    }
}
