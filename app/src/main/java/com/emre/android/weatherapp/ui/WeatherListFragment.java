package com.emre.android.weatherapp.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.emre.android.weatherapp.dao.LocationDAO;
import com.emre.android.weatherapp.dao.WeatherDAO;
import com.emre.android.weatherapp.dto.LocationDTO;
import com.emre.android.weatherapp.dto.WeatherDTO;
import com.emre.android.weatherapp.dto.WeatherDTOListBookmark;
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

import java.util.ArrayList;
import java.util.List;

public class WeatherListFragment extends Fragment {
    private static final String TAG = WeatherListFragment.class.getSimpleName();

    private static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private static final String RATING_BAR = "RatingBar";
    private static final String DIALOG_RATING_BAR = "DialogRatingBar";
    private static final int REQUEST_LOCATION_PERMISSIONS = 0;
    private static final int REQUEST_LOCATION = 0;

    private String mDegreeCalculation = "°C";
    private static boolean sIsShowedRatingBarDialog = false;
    private static Location sUserLocation;
    private LocationDAO mLocationDAO;
    private static List<LocationDTO> sLocationDTOList;
    private static WeatherDTO sUserWeatherDTO;
    private List<WeatherDTO> mWeatherDTOList;
    private static List<WeatherDTO> sWeatherDTOList;

    private static GoogleApiClient sClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ImageView mUserWeatherImageView;
    private TextView mLocationName;
    private TextView mTempDegree;
    private TextView mDescription;
    private RecyclerView mWeatherRecyclerView;
    private SearchView mBookmarkWeatherListSearchView;
    private TextView mAddBookmarkInfoMessage;
    private TextView mBookmarkNotFoundMessage;
    private ProgressBar mUserWeatherProgressBar;
    private ProgressBar mBookmarkWeatherListProgressBar;

    public static WeatherListFragment newInstance() {
        return new WeatherListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sClient = new GoogleApiClient.Builder(requireActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (hasLocationPermission()) {
                            executeUserWeatherTask();
                        } else {
                            requestPermissions(LOCATION_PERMISSIONS,
                                    REQUEST_LOCATION_PERMISSIONS);
                        }
                    }
                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();

        mFusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity());

        mLocationDAO = new LocationDAO(getContext());

        if (savedInstanceState != null) {
            sIsShowedRatingBarDialog = savedInstanceState.getBoolean(DIALOG_RATING_BAR);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_weather_list, viewGroup, false);

        LinearLayout userWeatherTempDegreeLayout = v.findViewById(R.id.weather_temp_degree_layout);
        mLocationName = v.findViewById(R.id.location_name);
        mUserWeatherImageView = v.findViewById(R.id.weather_image);
        mTempDegree = v.findViewById(R.id.temp_degree);
        mDescription = v.findViewById(R.id.description);
        ImageButton weatherAppInfoButton = v.findViewById(R.id.app_info_button);
        mBookmarkWeatherListSearchView = v.findViewById(R.id.search_list_item);
        ImageButton refreshWeatherButton = v.findViewById(R.id.refresh_weather_button);
        ImageButton addLocationButton = v.findViewById(R.id.add_location_button);
        mWeatherRecyclerView = v.findViewById(R.id.weather_list_recycler_view);
        mAddBookmarkInfoMessage = v.findViewById(R.id.add_bookmark_info);
        mBookmarkNotFoundMessage = v.findViewById(R.id.query_not_found_message);
        mUserWeatherProgressBar = v.findViewById(R.id.user_weather_progress_bar);
        mBookmarkWeatherListProgressBar = v.findViewById(R.id.weather_list_progress_bar);

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

        weatherAppInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = AppInfoPageActivity.newIntent(getContext());
                startActivity(intent);
            }
        });

        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = LocatorWeatherOnMapActivity.newIntent(getContext());
                startActivity(intent);
            }
        });

        mBookmarkWeatherListSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "QueryTextSubmit: " + s);

                List<WeatherDTO> weatherDTOList = new ArrayList<>();

                for (WeatherDTO weatherDTO : mWeatherDTOList) {
                    if (s.equalsIgnoreCase(weatherDTO.getLocationName())) {
                        weatherDTOList.add(weatherDTO);
                    }
                }

                if (weatherDTOList.isEmpty()) {
                    mAddBookmarkInfoMessage.setVisibility(View.GONE);
                    mBookmarkNotFoundMessage.setVisibility(View.VISIBLE);
                } else {
                    mBookmarkNotFoundMessage.setVisibility(View.GONE);
                }

                updateBookmarkWeatherList(weatherDTOList);
                mBookmarkWeatherListSearchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        refreshWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeUserWeatherTask();
                executeWeatherListTask();
            }
        });

        mWeatherRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper listItemWeatherSwipe = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder viewHolder1) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                        int position = viewHolder.getAdapterPosition();
                        WeatherDTO weatherDTO = mWeatherDTOList.get(position);
                        String locationName = mWeatherDTOList.get(position).getLocationName();
                        mLocationDAO.
                                LocationDbDeleteLocationData(weatherDTO.getLocationDTOId());
                        Toast.makeText(getContext(),
                                getString(R.string.location_deleted_message, locationName),
                                Toast.LENGTH_SHORT)
                                .show();

                        executeWeatherListTask();
                    }
                }
        );

        listItemWeatherSwipe.attachToRecyclerView(mWeatherRecyclerView);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (isOnline()) {
            sClient.connect();
            executeWeatherListTask();
        } else {
            mBookmarkWeatherListProgressBar.setVisibility(View.GONE);
            showOfflineNetworkAlertDialog();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        sClient.disconnect();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DIALOG_RATING_BAR, sIsShowedRatingBarDialog);
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

    public static Location getUserLocationDTO() {
        return sUserLocation;
    }

    public static WeatherDTO getUserWeatherDTO() {
        return sUserWeatherDTO;
    }

    public static List<LocationDTO> getLocationDTOList() {
        return sLocationDTOList;
    }

    public static List<WeatherDTO> getWeatherDTOList() {
        return sWeatherDTOList;
    }

    public static void deactivateRatingBarDialog() {
        sIsShowedRatingBarDialog = true;
    }

    private void showRatingBar() {
        if (isAdded() && !sIsShowedRatingBarDialog) {
            FragmentManager manager = getChildFragmentManager();
            RatingBarFragment dialog = RatingBarFragment.newInstance();
            dialog.show(manager, RATING_BAR);

            sIsShowedRatingBarDialog = true;
        }
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
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

    private void executeWeatherListTask() {
        Log.i(TAG, "Weather list task is executing");

        List<LocationDTO> locationDTOList = mLocationDAO.LocationDbExtract();
        sLocationDTOList = locationDTOList;

        WeatherDTOListBookmark weatherDTOListBookmark = getWeatherDTOListBookmark(locationDTOList);

        if (weatherDTOListBookmark.getWeatherDTOList().isEmpty()) {
            mBookmarkNotFoundMessage.setVisibility(View.GONE);
            mAddBookmarkInfoMessage.setVisibility(View.VISIBLE);
        } else {
            mBookmarkWeatherListProgressBar.setVisibility(View.VISIBLE);
            mAddBookmarkInfoMessage.setVisibility(View.GONE);
            mBookmarkNotFoundMessage.setVisibility(View.GONE);
        }

        new BookmarkWeatherListTask().execute(weatherDTOListBookmark);
    }

    private void updateUserWeather(WeatherDTO weatherDTO) {
        if (isAdded()) {
            sUserWeatherDTO = weatherDTO;

            String tempDegree =
                    getString(R.string.temp_degree, weatherDTO.getTempDegree(), mDegreeCalculation);

            mLocationName.setText(weatherDTO.getLocationName());
            mTempDegree.setText(tempDegree);
            mDescription.setText(weatherDTO.getDescription());

            updateUserWeatherImage(weatherDTO, mUserWeatherImageView);
            mUserWeatherProgressBar.setVisibility(View.GONE);
        }
    }

    private void updateBookmarkWeatherList(List<WeatherDTO> weatherDTOList) {
        if (isAdded()) {
            mWeatherDTOList = weatherDTOList;
            sWeatherDTOList = weatherDTOList;

            WeatherAdapter weatherAdapter = new WeatherAdapter(weatherDTOList);
            mWeatherRecyclerView.setAdapter(weatherAdapter);

            mBookmarkWeatherListProgressBar.setVisibility(View.GONE);
        }
    }

    private WeatherDTOListBookmark getWeatherDTOListBookmark(List<LocationDTO> locationDTOList) {
        WeatherDTOListBookmark weatherDTOListBookmark = new WeatherDTOListBookmark();
        List<WeatherDTO> weatherDTOList = new ArrayList<>();

        for (LocationDTO locationDTO : locationDTOList) {
            WeatherDTO weatherDTO = new WeatherDTO();
            weatherDTO.setLocationDTOId(locationDTO.getId());
            weatherDTO.setLocationDTOLatitude(locationDTO.getLatitude());
            weatherDTO.setLocationDTOLongitude(locationDTO.getLongitude());
            weatherDTOList.add(weatherDTO);
        }

        weatherDTOListBookmark.setWeatherDTOList(weatherDTOList);

        return weatherDTOListBookmark;
    }

    private void updateUserWeatherImage(WeatherDTO weatherDTO,
                                        ImageView weatherImageView) {
        switch (weatherDTO.getMainDescription()) {
            case "Clear":
                weatherImageView.setImageResource(R.drawable.sun);
                weatherImageView.setContentDescription(getString(R.string.weather_image,
                        getString(R.string.user_location), getString(R.string.weather_image_is_sun)));
                break;
            case "Clouds":
                if (weatherDTO.getDescription().equals("few clouds")) {
                        weatherImageView.setImageResource(R.drawable.sunny_cloud);
                        weatherImageView.setContentDescription(getString(R.string.weather_image,
                                getString(R.string.user_location), getString(R.string.weather_image_is_sunny_cloud)));
                } else {
                    weatherImageView.setImageResource(R.drawable.cloud);
                    weatherImageView.setContentDescription(getString(R.string.weather_image,
                            getString(R.string.user_location), getString(R.string.weather_image_is_cloud)));
                }
                break;
            case "Drizzle":
                weatherImageView.setImageResource(R.drawable.rain);
                weatherImageView.setContentDescription(getString(R.string.weather_image,
                        getString(R.string.user_location), getString(R.string.weather_image_is_rain)));
                break;
            case "Rain":
                weatherImageView.setImageResource(R.drawable.rain);
                weatherImageView.setContentDescription(getString(R.string.weather_image,
                        getString(R.string.user_location), getString(R.string.weather_image_is_rain)));
                break;
            case "Thunderstorm":
                weatherImageView.setImageResource(R.drawable.thunderstorm);
                weatherImageView.setContentDescription(getString(R.string.weather_image,
                        getString(R.string.user_location), getString(R.string.weather_image_is_thunderstorm)));
                break;
            case "Snow":
                weatherImageView.setImageResource(R.drawable.snow);
                weatherImageView.setContentDescription(getString(R.string.weather_image,
                        getString(R.string.user_location), getString(R.string.weather_image_is_snow)));
                break;
            default:
                String[] atmosphereDescriptions = {"Mist", "Smoke", "Haze",
                        "Dust", "Fog", "Sand", "Dust", "Ash", "Squall", "Tornado"};

                for (String atmosphereDescription : atmosphereDescriptions) {
                    if (weatherDTO.getMainDescription().equals(atmosphereDescription)) {
                        weatherImageView.setImageResource(R.drawable.mist);
                        weatherImageView.setContentDescription(getString(R.string.weather_image,
                                getString(R.string.user_location), getString(R.string.weather_image_is_mist)));
                    }
                }
        }
    }

    private void updateListItemWeatherImage(WeatherDTO weatherDTO,
                                            ImageView weatherImageView, CardView weatherCardView) {
        ConstraintLayout weatherCardViewLayout = weatherCardView.findViewById(R.id.weather_card_view_layout);

        switch (weatherDTO.getMainDescription()) {
            case "Clear":
                    weatherImageView.setImageResource(R.drawable.sun_solid);
                    weatherImageView.setContentDescription(getString(R.string.weather_image_is_sun));
                    weatherCardViewLayout.setBackgroundResource(R.drawable.sun_background);
                break;
            case "Clouds":
                if (weatherDTO.getDescription().equals("few clouds")) {
                    weatherImageView.setImageResource(R.drawable.sunny_cloud_solid);
                    weatherImageView.setContentDescription(getString(R.string.weather_image_is_sunny_cloud));
                    weatherCardViewLayout.setBackgroundResource(R.drawable.sunny_cloud_background);
                } else {
                    weatherImageView.setImageResource(R.drawable.cloud_solid);
                    weatherImageView.setContentDescription(getString(R.string.weather_image_is_cloud));
                    weatherCardViewLayout.setBackgroundResource(R.drawable.cloud_background);
                }
                break;
            case "Drizzle":
                weatherImageView.setImageResource(R.drawable.rain_solid);
                weatherImageView.setContentDescription(getString(R.string.weather_image_is_rain));
                weatherCardViewLayout.setBackgroundResource(R.drawable.rain_background);
                break;
            case "Rain":
                weatherImageView.setImageResource(R.drawable.rain_solid);
                weatherImageView.setContentDescription(getString(R.string.weather_image_is_rain));
                weatherCardViewLayout.setBackgroundResource(R.drawable.rain_background);
                break;
            case "Thunderstorm":
                weatherImageView.setImageResource(R.drawable.thunderstorm_solid);
                weatherImageView.setContentDescription(getString(R.string.weather_image_is_thunderstorm));
                weatherCardViewLayout.setBackgroundResource(R.drawable.thunderstorm_background);
                break;
            case "Snow":
                weatherImageView.setImageResource(R.drawable.snow_solid);
                weatherImageView.setContentDescription(getString(R.string.weather_image_is_snow));
                weatherCardViewLayout.setBackgroundResource(R.drawable.snow_background);
                break;
            default:
                String[] atmosphereDescriptions = {"Mist", "Smoke", "Haze",
                        "Dust", "Fog", "Sand", "Dust", "Ash", "Squall", "Tornado"};

                for (String atmosphereDescription : atmosphereDescriptions) {
                    if (weatherDTO.getMainDescription().equals(atmosphereDescription)) {
                        weatherImageView.setImageResource(R.drawable.mist_solid);
                        weatherImageView.setContentDescription(getString(R.string.weather_image_is_mist));
                        weatherCardViewLayout.setBackgroundResource(R.drawable.mist_background);
                    }
                }
        }
    }

    private class WeatherHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {
        private CardView weatherCardView;
        private TextView locationNameTextView;
        private ImageView weatherImageView;
        private TextView tempDegreeTextView;
        private WeatherDTO weatherDTO;

        private WeatherHolder(@NonNull LayoutInflater inflater, ViewGroup viewGroup) {
            super(inflater.inflate(R.layout.list_item_weather, viewGroup, false));
            itemView.setOnClickListener(this);

            weatherCardView = itemView.findViewById(R.id.weather_card_view);
            locationNameTextView = weatherCardView.findViewById(R.id.list_item_location_name);
            weatherImageView = weatherCardView.findViewById(R.id.list_item_weather_image);
            tempDegreeTextView = weatherCardView.findViewById(R.id.list_item_temp_degree);
        }

        private void bind(WeatherDTO weatherDTO) {
            String tempDegree =
                    getString(R.string.temp_degree, weatherDTO.getTempDegree(), mDegreeCalculation);
            locationNameTextView.setText(weatherDTO.getLocationName());
            tempDegreeTextView.setText(tempDegree);
            this.weatherDTO = weatherDTO;

            if (weatherDTO.getMainDescription() != null && weatherDTO.getDescription() != null) {
                updateListItemWeatherImage(weatherDTO, weatherImageView, weatherCardView);
            }
         }

        @Override
        public void onClick(View view) {
            List<Location> locationList = new ArrayList<>();

            for (WeatherDTO weatherDTO : mWeatherDTOList) {
                Location location = new Location("");
                location.setLatitude(weatherDTO.getLocationDTOLatitude());
                location.setLongitude(weatherDTO.getLocationDTOLongitude());
                locationList.add(location);
            }

            Intent intent = DetailedBookmarkWeatherActivity.newIntent(getActivity(),
                    locationList, getAdapterPosition());
            startActivity(intent);
        }
    }

    private class WeatherAdapter extends RecyclerView.Adapter<WeatherHolder> {
        private List<WeatherDTO> weatherDTOList;

        private WeatherAdapter(List<WeatherDTO> weatherDTOList) {
            this.weatherDTOList = weatherDTOList;
        }

        @NonNull
        @Override
        public WeatherHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new WeatherHolder(layoutInflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull WeatherHolder weatherHolder, int position) {
            WeatherDTO weatherDTO = weatherDTOList.get(position);
            weatherHolder.bind(weatherDTO);
        }

        @Override
        public int getItemCount() {
            return weatherDTOList.size();
        }
    }

    private class UserWeatherTask extends AsyncTask<Location, Void, WeatherDTO> {

        @Override
        protected WeatherDTO doInBackground(Location... locations) {
            return new WeatherDAO().getUserWeather(locations[0]);
        }

        @Override
        protected void onPostExecute(WeatherDTO result) {
            Log.i(TAG, "User weather task is executed");

            updateUserWeather(result);
            showRatingBar();
        }
    }

    private class BookmarkWeatherListTask extends AsyncTask<WeatherDTOListBookmark, Void, List<WeatherDTO>> {

        @Override
        protected List<WeatherDTO> doInBackground(WeatherDTOListBookmark... weatherDTOListBookmarks) {
            List<WeatherDTO> weatherDTOList = weatherDTOListBookmarks[0].getWeatherDTOList();
            return new WeatherDAO().getBookmarkWeatherList(weatherDTOList);
        }

        @Override
        protected void onPostExecute(List<WeatherDTO> result) {
            Log.i(TAG, "Weather list task is executed");

            updateBookmarkWeatherList(result);
            showRatingBar();
        }
    }
}

