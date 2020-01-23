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

package com.emre.android.weatherapp.scenes.userweather;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.emre.android.weatherapp.R;
import com.emre.android.weatherapp.dataaccessobjects.settingsdataaccess.SettingsDAO;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.UserWeatherDTO;
import com.emre.android.weatherapp.scenes.INetworkStatus;
import com.emre.android.weatherapp.scenes.IRefreshWeather;
import com.emre.android.weatherapp.scenes.detailedweather.DetailedUserWeatherActivity;
import com.emre.android.weatherapp.scenes.mainweather.MainWeatherActivity;
import com.emre.android.weatherapp.workerthread.UserWeatherTask;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * @author Emre Üsküplü
 *
 * Shows weather of user
 * If device location is active and location permissions is given from user then get user location
 * If device location is taken and device network connection is available then gets weather from api
 * When user clicks weather temp degree layout then starts detailed user weather activity
 */
public class UserWeatherFragment extends Fragment implements IUpdateUserWeather, IRefreshWeather {
    private static final String TAG = UserWeatherFragment.class.getSimpleName();

    private static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private static final int REQUEST_LOCATION_PERMISSIONS = 0;
    private static final int REQUEST_LOCATION = 0;

    private static UserWeatherDTO sUserWeatherDTO;

    private static Location sLocation;

    private Fragment mUserWeatherFragment = this;
    private String mUnitsFormat = "";
    private SettingsDAO mSettingsDAO;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private View mTempDegreeView;
    private ImageView mWeatherImageView;
    private TextView mLocationName;
    private TextView mTempDegree;
    private TextView mDescription;
    private ProgressBar mWeatherProgressBar;

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult result) {
            Log.i(TAG, "User location: " + result.getLastLocation());

            INetworkStatus iNetworkStatus = (MainWeatherActivity) requireActivity();

            sLocation = result.getLastLocation();

            if (iNetworkStatus.isOnlineNetworkConnection()) {
                mWeatherProgressBar.setVisibility(View.VISIBLE);


                new UserWeatherTask(requireContext(),
                        mUserWeatherFragment).execute(sLocation);

            } else {
                iNetworkStatus.showOfflineNetworkAlertMessage();
            }
        }
    };

    private View.OnClickListener weatherTempDegreeLayoutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (sLocation != null) {
                Intent intent = DetailedUserWeatherActivity.newIntent(getActivity(),
                        sLocation);
                startActivity(intent);
            }
        }
    };

    public static UserWeatherFragment newInstance() {
        return new UserWeatherFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSettingsDAO = new SettingsDAO();
        mFusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_weather, viewGroup, false);

        mTempDegreeView = v.findViewById(R.id.weather_temp_degree_view);
        mLocationName = v.findViewById(R.id.location_name);
        mWeatherImageView = v.findViewById(R.id.weather_image);
        mTempDegree = v.findViewById(R.id.temp_degree);
        mDescription = v.findViewById(R.id.description);
        mWeatherProgressBar = v.findViewById(R.id.weather_progress_bar);

        mTempDegreeView.setOnClickListener(weatherTempDegreeLayoutClickListener);

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

        executeUserWeatherTask();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSIONS) {
            if (hasLocationPermission()) {
                executeUserWeatherTask();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static Location getUserLocation() {
        return sLocation;
    }

    public static UserWeatherDTO getUserWeatherDTO() {
        return sUserWeatherDTO;
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void createLocationResolutionDialogIfDeviceLocationIsDeactivate(LocationRequest request) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();

        builder.addLocationRequest(request);

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(requireContext()).
                checkLocationSettings(builder.build());

        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);

                    Log.i(TAG, response + "");

                } catch (ApiException exception) {
                    if (exception.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            resolvable.startResolutionForResult(
                                    getActivity(), REQUEST_LOCATION);

                        } catch (IntentSender.SendIntentException e) {
                            if (e.getMessage() != null) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * If device location is active and location permissions is given from user then get user location
     * If device location is taken and device network connection is available then gets weather from api
     * Executes user weather task with user location in location callback
     */
    private void executeUserWeatherTask() {
        LocationRequest request = LocationRequest.create();
        // TODO PRIORITY_HIGH_ACCURACY is necessary for emulator testing.
        // TODO It must be PRIORITY_BALANCED_POWER_ACCURACY for real device that consumes less power
        request.setPriority(LocationRequest. PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);

        createLocationResolutionDialogIfDeviceLocationIsDeactivate(request);

        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationProviderClient.requestLocationUpdates(request, mLocationCallback, null);

        } else {
            requestPermissions(LOCATION_PERMISSIONS,
                    REQUEST_LOCATION_PERMISSIONS);
        }
    }

    private void updateUserWeatherImage(UserWeatherDTO userWeatherDTO,
                                        ImageView weatherImageView) {
        String mainDescription = userWeatherDTO.getMainDescription();

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
                if (userWeatherDTO.getMainDescription().equals(atmosphereDescription)) {
                    weatherImageView.setImageResource(R.drawable.mist);
                    weatherImageView.setContentDescription(getString(R.string.weather_image,
                            getString(R.string.user_location), getString(R.string.weather_image_is_mist)));
                }
            }
        }
    }

    @Override
    public void refreshWeather() {
        executeUserWeatherTask();
    }

    @Override
    public void updateUserWeather(UserWeatherDTO userWeatherDTO) {
        if (isAdded()) {
            sUserWeatherDTO = userWeatherDTO;

            String tempDegree =
                    getString(R.string.temp_degree, userWeatherDTO.getTempDegree(), mUnitsFormat);

            mLocationName.setText(userWeatherDTO.getLocationName());
            mTempDegree.setText(tempDegree);
            mDescription.setText(userWeatherDTO.getDescription());

            updateUserWeatherImage(userWeatherDTO, mWeatherImageView);

            mWeatherProgressBar.setVisibility(View.GONE);
        }
    }
}
