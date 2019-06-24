package com.emre.android.weatherapp.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.emre.android.weatherapp.R;
import com.emre.android.weatherapp.dao.SettingsDAO;
import com.emre.android.weatherapp.dao.WeatherDAO;
import com.emre.android.weatherapp.dto.WeatherDTO;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
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

public class UserWeatherFragment extends Fragment implements IRefreshWeather{
    private static final String TAG = UserWeatherFragment.class.getSimpleName();

    private static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private static final int REQUEST_LOCATION_PERMISSIONS = 0;
    private static final int REQUEST_LOCATION = 0;

    private static WeatherDTO sUserWeatherDTO;

    private static Location sUserLocation;
    private static GoogleApiClient sClient;

    private String mUnitsFormat = "";
    private SettingsDAO mSettingsDAO;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ImageView mUserWeatherImageView;
    private TextView mLocationName;
    private TextView mTempDegree;
    private TextView mDescription;
    private ProgressBar mUserWeatherProgressBar;

    public static UserWeatherFragment newInstance() {
        return new UserWeatherFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSettingsDAO = new SettingsDAO();

        sClient = new GoogleApiClient.Builder(requireActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (hasLocationPermission() && isOnline()) {
                            executeUserWeatherTask();

                        } else if (!hasLocationPermission()){
                            requestPermissions(LOCATION_PERMISSIONS,
                                    REQUEST_LOCATION_PERMISSIONS);

                        } else if (!isOnline()) {
                            INetworkMessage iNetworkMessage = (WeatherBaseActivity) requireActivity();
                            iNetworkMessage.showOfflineNetworkAlertMessage();
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();

        mFusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_weather, viewGroup, false);

        LinearLayout userWeatherTempDegreeLayout = v.findViewById(R.id.weather_temp_degree_layout);
        mLocationName = v.findViewById(R.id.location_name);
        mUserWeatherImageView = v.findViewById(R.id.weather_image);
        mTempDegree = v.findViewById(R.id.temp_degree);
        mDescription = v.findViewById(R.id.description);
        mUserWeatherProgressBar = v.findViewById(R.id.user_weather_progress_bar);

        userWeatherTempDegreeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sUserLocation != null) {
                    Intent intent = DetailedUserWeatherActivity.newIntent(getActivity(),
                            sUserLocation);

                    startActivity(intent);
                }
            }
        });

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

        sClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        sClient.disconnect();
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

    public static boolean getGoogleApiClientStatus() {
        return sClient.isConnected();
    }

    public static Location getUserLocation() {
        return sUserLocation;
    }

    public static WeatherDTO getUserWeatherDTO() {
        return sUserWeatherDTO;
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
                            WeatherBaseActivity.deactivateRatingBarDialog();

                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            resolvable.startResolutionForResult(
                                    getActivity(), REQUEST_LOCATION);

                        } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }
            }
        });
    }

    private void executeUserWeatherTask() {
        Log.i(TAG, "User weather task is executing");

        LocationRequest request = LocationRequest.create();
        // PRIORITY_HIGH_ACCURACY is necessary for emulator testing.
        // It must be PRIORITY_BALANCED_POWER_ACCURACY for real device that consumes less power
        request.setPriority(LocationRequest. PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);

        createLocationResolutionDialogIfDeviceLocationIsDeactivate(request);

        // Following permission is already used in hasLocationPermission().
        // Same permission control is necessary for avoid the warning of FusedLocationProviderClient
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                Log.i(TAG, "User location: " + result.getLastLocation());

                mUserWeatherProgressBar.setVisibility(View.VISIBLE);

                sUserLocation = result.getLastLocation();

                new UserWeatherTask()
                        .execute(sUserLocation);
            }
        };

        mFusedLocationProviderClient.requestLocationUpdates(request, mLocationCallback, null);
    }

    private void updateUserWeather(WeatherDTO weatherDTO) {
        if (isAdded()) {
            sUserWeatherDTO = weatherDTO;

            String tempDegree =
                    getString(R.string.temp_degree, weatherDTO.getTempDegree(), mUnitsFormat);

            mLocationName.setText(weatherDTO.getLocationName());
            mTempDegree.setText(tempDegree);
            mDescription.setText(weatherDTO.getDescription());

            updateUserWeatherImage(weatherDTO, mUserWeatherImageView);

            mUserWeatherProgressBar.setVisibility(View.GONE);

            IRatingBarDialog IRatingBarDialog = (WeatherBaseActivity) requireActivity();
            IRatingBarDialog.showRatingBarDialog();
        }
    }

    private void updateUserWeatherImage(WeatherDTO weatherDTO,
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
    public void refreshWeather() {
        sClient.reconnect();
    }

    private class UserWeatherTask extends AsyncTask<Location, Void, WeatherDTO> {

        @Override
        protected WeatherDTO doInBackground(Location... locations) {
            if (isAdded()) {
                return new WeatherDAO(requireContext()).getUserWeather(locations[0]);
            } else {
                Log.e(TAG, "Activity is not added");
                return new WeatherDTO();

            }
        }

        @Override
        protected void onPostExecute(WeatherDTO result) {
            Log.i(TAG, "User weather task is executed");

            updateUserWeather(result);
        }
    }
}
