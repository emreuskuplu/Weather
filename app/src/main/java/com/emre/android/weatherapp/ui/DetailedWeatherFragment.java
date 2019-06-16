package com.emre.android.weatherapp.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.emre.android.weatherapp.R;
import com.emre.android.weatherapp.dao.WeatherDAO;
import com.emre.android.weatherapp.dto.WeatherDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailedWeatherFragment extends Fragment {
    private static final String TAG = DetailedWeatherFragment.class.getSimpleName();

    private static final String ARG_LOCATION_DATA_INFO =
            "com.emre.android.weatherapp.locationdata";

    private String mDegreeCalculation = "°C";
    private int mSelectedDayIndex = 0;
    private static int sSelectedDayIndex;
    private static List<WeatherDTO> sWeatherDTOList;

    private Location mLocation;
    private static Location sLocation;
    private ImageView mWeatherImageView;
    private TextView mLocationName;
    private TextView mDetailedDayName;
    private TextView mTempDegree;
    private TextView mDescription;
    private TextView mHumidityVolume;
    private TextView mWindVolume;
    private TextView mRainVolume;
    private TextView mSnowVolume;
    private ProgressBar mWeatherProgressBar;

    private TextView mFirstDayName;
    private ImageView mFirstDayWeatherImage;
    private TextView mFirstDayTempDegree;
    private TextView mSecondDayName;
    private ImageView mSecondDayWeatherImage;
    private TextView mSecondDayTempDegree;
    private TextView mThirdDayName;
    private ImageView mThirdDayWeatherImage;
    private TextView mThirdDayTempDegree;
    private TextView mFourthDayName;
    private ImageView mFourthDayWeatherImage;
    private TextView mFourthDayTempDegree;
    private TextView mFifthDayName;
    private ImageView mFifthDayWeatherImage;
    private TextView mFifthDayTempDegree;

    public static DetailedWeatherFragment newInstance(Location location) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOCATION_DATA_INFO, location);
        DetailedWeatherFragment detailedWeatherFragment = new DetailedWeatherFragment();
        detailedWeatherFragment.setArguments(args);
        return detailedWeatherFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mLocation = getArguments().getParcelable(ARG_LOCATION_DATA_INFO);
            sLocation = mLocation;
        } else {
            Log.e(TAG, "getArgument is null");
            requireActivity().finish();
            Toast.makeText(requireContext(),
                    R.string.issue_location_values,
                    Toast.LENGTH_SHORT)
                    .show();
        }
        super.onCreate(savedInstanceState);
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
        ImageButton backButton = v.findViewById(R.id.back_button);
        ImageButton refreshWeatherButton = v.findViewById(R.id.refresh_weather_button);
        mWeatherProgressBar = v.findViewById(R.id.weather_progress_bar);

        LinearLayout firstDayLayout = v.findViewById(R.id.first_day_layout);
        mFirstDayName = v.findViewById(R.id.first_day_date);
        mFirstDayWeatherImage = v.findViewById(R.id.first_day_weather_image);
        mFirstDayTempDegree = v.findViewById(R.id.first_day_temp_degree);
        LinearLayout secondDayLayout = v.findViewById(R.id.second_day_layout);
        mSecondDayName = v.findViewById(R.id.second_day_date);
        mSecondDayWeatherImage = v.findViewById(R.id.second_day_weather_image);
        mSecondDayTempDegree = v.findViewById(R.id.second_day_temp_degree);
        LinearLayout thirdDayLayout = v.findViewById(R.id.third_day_layout);
        mThirdDayName = v.findViewById(R.id.third_day_date);
        mThirdDayWeatherImage = v.findViewById(R.id.third_day_weather_image);
        mThirdDayTempDegree = v.findViewById(R.id.third_day_temp_degree);
        LinearLayout fourthDayLayout = v.findViewById(R.id.fourth_day_layout);
        mFourthDayName = v.findViewById(R.id.fourth_day_date);
        mFourthDayWeatherImage = v.findViewById(R.id.fourth_day_weather_image);
        mFourthDayTempDegree = v.findViewById(R.id.fourth_day_temp_degree);
        LinearLayout fifthDayLayout = v.findViewById(R.id.fifth_day_layout);
        mFifthDayName = v.findViewById(R.id.fifth_day_date);
        mFifthDayWeatherImage = v.findViewById(R.id.fifth_day_weather_image);
        mFifthDayTempDegree = v.findViewById(R.id.fifth_day_temp_degree);

        firstDayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWeatherProgressBar.setVisibility(View.VISIBLE);
                mSelectedDayIndex = 0;
                sSelectedDayIndex = mSelectedDayIndex;
                new ForecastDetailedWeatherTask().execute(mLocation);
            }
        });

        secondDayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWeatherProgressBar.setVisibility(View.VISIBLE);
                mSelectedDayIndex = 1;
                sSelectedDayIndex = mSelectedDayIndex;
                new ForecastDetailedWeatherTask().execute(mLocation);
            }
        });

        thirdDayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWeatherProgressBar.setVisibility(View.VISIBLE);
                mSelectedDayIndex = 2;
                sSelectedDayIndex = mSelectedDayIndex;
                new ForecastDetailedWeatherTask().execute(mLocation);
            }
        });

        fourthDayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWeatherProgressBar.setVisibility(View.VISIBLE);
                mSelectedDayIndex = 3;
                sSelectedDayIndex = mSelectedDayIndex;
                new ForecastDetailedWeatherTask().execute(mLocation);
            }
        });

        fifthDayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWeatherProgressBar.setVisibility(View.VISIBLE);
                mSelectedDayIndex = 4;
                sSelectedDayIndex = mSelectedDayIndex;
                new ForecastDetailedWeatherTask().execute(mLocation);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().finish();
            }
        });

        refreshWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWeatherProgressBar.setVisibility(View.VISIBLE);
                new ForecastDetailedWeatherTask().execute(mLocation);
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isOnline()) {
            mWeatherProgressBar.setVisibility(View.VISIBLE);
            new ForecastDetailedWeatherTask().execute(mLocation);
        } else {
            showOfflineNetworkAlertDialog();
        }
    }

    public static Location getLocation() {
        return sLocation;
    }

    public static int getSelectedDayIndex() {
        return sSelectedDayIndex;
    }

    public static List<WeatherDTO> getWeatherDTOList() {
        return sWeatherDTOList;
    }

    public static String changeShortDateToLargeDate(String shortDate) {
        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String detailedDayName = "";
        try {
            Date longDate = outFormat.parse(shortDate);
            detailedDayName = outFormat.format(longDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return detailedDayName;
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;

        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        } else {
            // If there is not default network then ignore isOnline condition
            Log.i(TAG, "There is not default network");
            return true;
        }

        return networkInfo != null && networkInfo.isConnected();
    }

    private void showOfflineNetworkAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage(getString(R.string.offline_network_alert_dialog_message));
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Continue the process
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateDetailedWeatherDescriptions(WeatherDTO weatherDTO) {
        if (isAdded()) {
            String detailedDayName = changeShortDateToLargeDate(weatherDTO.getDate());
            String tempDegree =
                    getString(R.string.temp_degree, weatherDTO.getTempDegree(), mDegreeCalculation);
            mLocationName.setText(weatherDTO.getLocationName());
            mDetailedDayName.setText(detailedDayName);
            mTempDegree.setText(tempDegree);
            mDescription.setText(weatherDTO.getDescription());
            mHumidityVolume.setText(weatherDTO.getHumidity());
            mWindVolume.setText(weatherDTO.getWindVolume());
            mRainVolume.setText(weatherDTO.getRainVolume());
            mSnowVolume.setText(weatherDTO.getSnowVolume());

            updateWeatherImage(weatherDTO, mWeatherImageView);
        }
    }

    private void updateWeatherForecastDays(List<WeatherDTO> weatherDTOList) {
        if (isAdded()) {
            sWeatherDTOList = weatherDTOList;

            List<TextView> weatherDayList = new ArrayList<>();
            weatherDayList.add(mFirstDayName);
            weatherDayList.add(mSecondDayName);
            weatherDayList.add(mThirdDayName);
            weatherDayList.add(mFourthDayName);
            weatherDayList.add(mFifthDayName);

            List<ImageView> weatherForecastDaysImageViewList = new ArrayList<>();
            weatherForecastDaysImageViewList.add(mFirstDayWeatherImage);
            weatherForecastDaysImageViewList.add(mSecondDayWeatherImage);
            weatherForecastDaysImageViewList.add(mThirdDayWeatherImage);
            weatherForecastDaysImageViewList.add(mFourthDayWeatherImage);
            weatherForecastDaysImageViewList.add(mFifthDayWeatherImage);

            List<TextView> weatherForecastDaysTempDegreeList = new ArrayList<>();
            weatherForecastDaysTempDegreeList.add(mFirstDayTempDegree);
            weatherForecastDaysTempDegreeList.add(mSecondDayTempDegree);
            weatherForecastDaysTempDegreeList.add(mThirdDayTempDegree);
            weatherForecastDaysTempDegreeList.add(mFourthDayTempDegree);
            weatherForecastDaysTempDegreeList.add(mFifthDayTempDegree);

            int weatherForecastDaysLimit = 5;

            for (int i = 0; i < weatherForecastDaysLimit; i++) {
                WeatherDTO weatherDTO = weatherDTOList.get(i);
                String dayName = weatherDTO.getDate();
                TextView weatherDayNameTextView = weatherDayList.get(i);
                weatherDayNameTextView.setText(dayName);
                weatherDayNameTextView.setTextColor(mDescription.getTextColors());
            }

            weatherDayList.get(mSelectedDayIndex).setTextColor(mDetailedDayName.getTextColors());

            for (int i = 0; i < weatherForecastDaysLimit; i++) {
                WeatherDTO weatherDTO = weatherDTOList.get(i);
                updateWeatherImage(weatherDTO, weatherForecastDaysImageViewList.get(i));
            }

            for (int i = 0; i < weatherForecastDaysLimit; i++) {
                WeatherDTO weatherDTO = weatherDTOList.get(i);
                String tempDegree =
                        getString(R.string.temp_degree, weatherDTO.getTempDegree(), mDegreeCalculation);
                TextView tempDegreeTextView = weatherForecastDaysTempDegreeList.get(i);
                tempDegreeTextView.setText(tempDegree);
            }
        }
    }

    private void updateWeatherImage(WeatherDTO weatherDTO,
                                    ImageView weatherImageView) {

        switch (weatherDTO.getMainDescription()) {
            case "Clear":
                weatherImageView.setImageResource(R.drawable.sun);
                weatherImageView.setContentDescription(getString(R.string.weather_image_is_sun));
                break;
            case "Clouds":
                if (weatherDTO.getDescription().equals("few clouds")) {
                    weatherImageView.setImageResource(R.drawable.sunny_cloud);
                    weatherImageView.setContentDescription(getString(R.string.weather_image_is_sunny_cloud));
                } else {
                    weatherImageView.setImageResource(R.drawable.cloud);
                    weatherImageView.setContentDescription(getString(R.string.weather_image_is_cloud));
                }
                break;
            case "Drizzle":
                weatherImageView.setImageResource(R.drawable.rain);
                weatherImageView.setContentDescription(getString(R.string.weather_image_is_rain));
                break;
            case "Rain":
                weatherImageView.setImageResource(R.drawable.rain);
                weatherImageView.setContentDescription(getString(R.string.weather_image_is_rain));
                break;
            case "Thunderstorm":
                weatherImageView.setImageResource(R.drawable.thunderstorm);
                weatherImageView.setContentDescription(getString(R.string.weather_image_is_thunderstorm));
                break;
            case "Snow":
                weatherImageView.setImageResource(R.drawable.snow);
                weatherImageView.setContentDescription(getString(R.string.weather_image_is_snow));
            default:
                String[] atmosphereDescriptions = {"Mist", "Smoke", "Haze",
                        "Dust", "Fog", "Sand", "Dust", "Ash", "Squall", "Tornado"};

                for (String atmosphereDescription : atmosphereDescriptions) {
                    if (weatherDTO.getMainDescription().equals(atmosphereDescription)) {
                        weatherImageView.setImageResource(R.drawable.mist);
                        weatherImageView.setContentDescription(getString(R.string.weather_image_is_mist));
                    }
                }
        }
    }

    private class ForecastDetailedWeatherTask extends AsyncTask<Location, Void, List<WeatherDTO>> {

        @Override
        protected List<WeatherDTO> doInBackground(Location... location) {
            return new WeatherDAO().getForecastDetailedWeatherList(location[0]);
        }

        @Override
        protected void onPostExecute(List<WeatherDTO> result) {
            if (!result.isEmpty()) {
                WeatherDTO weatherDTO = result.get(mSelectedDayIndex);
                updateDetailedWeatherDescriptions(weatherDTO);
                updateWeatherForecastDays(result);
            } else {
                Toast.makeText(requireContext(),
                        R.string.issue_weather_load,
                        Toast.LENGTH_SHORT)
                        .show();
            }

            if (isAdded()) {
                mWeatherProgressBar.setVisibility(View.GONE);
            }
        }
    }
}

