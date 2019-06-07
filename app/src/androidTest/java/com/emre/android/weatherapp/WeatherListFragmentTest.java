package com.emre.android.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.emre.android.weatherapp.dto.WeatherDTO;
import com.emre.android.weatherapp.ui.WeatherListActivity;
import com.emre.android.weatherapp.ui.WeatherListFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class WeatherListFragmentTest {
    private static final String TAG = WeatherListFragmentTest.class.getSimpleName();
    private boolean isResolutionRequired = true;
    private String mDegreeCalculation = "°C";

    private Context mAppContext = InstrumentationRegistry.getTargetContext();

    @Rule
    public ActivityTestRule<WeatherListActivity> activityRule =
            new ActivityTestRule<>(WeatherListActivity.class);

    @Test
    public void verifyIncomingUserWeatherDataOnView() {
        if (isAvailableGooglePlayServices()) {
            if (isOnline()) {
                if (isGoogleApiClientConnected()) {
                    if (hasLocationPermission()) {
                        if (isDeviceLocationActive()) {
                            WeatherDTO weatherDTO = WeatherListFragment.getUserWeatherDTO();
                            onView(withId(R.id.location_name)).check(matches(withText(weatherDTO.getLocationName())));
                            onView(withId(R.id.temp_degree)).check(matches(withText(weatherDTO.getTempDegree() + mDegreeCalculation)));
                            onView(withId(R.id.description)).check(matches(withText(weatherDTO.getDescription())));
                        } else {
                            Assert.fail("Device location is not active for get user location");
                        }
                    } else {
                        Assert.fail("Location permissions is not available for get user location");
                    }
                } else {
                    Assert.fail("Google api client is not connected for get user location");
                }
            } else {
                Assert.fail("User internet connection is offline for get userWeatherDTO data");
            }
        } else {
            Assert.fail("Google play services is not available for get user location");
        }
    }

    @Test
    public void clickUserWeatherTempDegreeLayoutAndVerifyDetailedWeatherFragment() {
        onView(withId(R.id.weather_temp_degree_layout)).perform(click());
        onView(withId(R.id.humidity_volume)).check(matches(isDisplayed()));
    }

    @Test
    public void verifyWeatherDTOListItemsHasLocationId() {
        List<WeatherDTO> weatherDTOList = WeatherListFragment.getWeatherDTOList();

        for (WeatherDTO weatherDTO : weatherDTOList) {
            if (weatherDTO.getLocationDTOId() == null) {
                Assert.fail("WeatherDTOList items has not id, it must be has id matches LocationDTO for use it's latitude and longitude values");
            }
        }
    }

    @Test
    public void verifyRefreshWeatherButtonIsWorkingCorrectly() {
        List<WeatherDTO> weatherDTOList = WeatherListFragment.getWeatherDTOList();
        int weatherDTOListHashCodeBeforeRefreshWeatherButtonIsClicked = weatherDTOList.hashCode();

        onView(withId(R.id.refresh_weather_button)).perform(click());

        weatherDTOList = WeatherListFragment.getWeatherDTOList();
        int weatherDTOListHashCodeAfterRefreshWeatherButtonIsClicked = weatherDTOList.hashCode();

        assertThat(weatherDTOListHashCodeBeforeRefreshWeatherButtonIsClicked,
                not(equalTo(weatherDTOListHashCodeAfterRefreshWeatherButtonIsClicked)));
    }

    @Test
    public void verifyAddLocationBookmarkMessageIsWorkingCorrectly() {
        List<WeatherDTO> weatherDTOList = WeatherListFragment.getWeatherDTOList();

        if (weatherDTOList.isEmpty()) {
            onView(withId(R.id.add_bookmark_info)).check(matches(isDisplayed()));
        } else {
            onView(withId(R.id.add_bookmark_info)).check(matches(not(isDisplayed())));
        }

        onView(withId(R.id.refresh_weather_button)).perform(click());

        if (weatherDTOList.isEmpty()) {
            onView(withId(R.id.add_bookmark_info)).check(matches(isDisplayed()));
        } else {
            onView(withId(R.id.add_bookmark_info)).check(matches(not(isDisplayed())));
        }
    }

    @Test
    public void clickLocationAddButtonAndVerifyLocatorWeatherOnMapActivity() {
        onView(withId(R.id.add_location_button)).perform(click());
        onView(withId(R.id.map_view)).check(matches(isDisplayed()));
    }

    @Test
    public void clickAppInfoButtonAndVerifyAppInfoActivity() {
        onView(withId(R.id.app_info_button)).perform(click());
        onView(withId(R.id.web_view)).check(matches(isDisplayed()));
    }

    private boolean isAvailableGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(mAppContext);
        return resultCode == ConnectionResult.SUCCESS;
    }

    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                activityRule.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private boolean isGoogleApiClientConnected() {
        return WeatherListFragment.getGoogleApiClientStatus();
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(mAppContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mAppContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isDeviceLocationActive() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest. PRIORITY_BALANCED_POWER_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(request);

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(mAppContext).
                checkLocationSettings(builder.build());

        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Log.i(TAG, response + "");
                } catch (ApiException exception) {
                    isResolutionRequired = exception.getStatusCode() != LocationSettingsStatusCodes.RESOLUTION_REQUIRED;
                }
            }
        });

        return isResolutionRequired;
    }

}
