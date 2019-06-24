package com.emre.android.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import androidx.core.app.ActivityCompat;
import android.util.Log;

import com.emre.android.weatherapp.dao.ISettingsDAO;
import com.emre.android.weatherapp.dao.SettingsDAO;
import com.emre.android.weatherapp.dto.WeatherDTO;
import com.emre.android.weatherapp.ui.BookmarkWeatherListFragment;
import com.emre.android.weatherapp.ui.UserWeatherFragment;
import com.emre.android.weatherapp.ui.WeatherBaseActivity;
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
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class WeatherBaseActivityTest {
    private static final String TAG = WeatherBaseActivityTest.class.getSimpleName();

    private boolean isResolutionRequired = true;
    private String mUnitsFormat;
    private ISettingsDAO mISettingsDAO;

    private Context mAppContext = ApplicationProvider.getApplicationContext();

    @Rule
    public ActivityTestRule<WeatherBaseActivity> activityRule =
            new ActivityTestRule<>(WeatherBaseActivity.class);

    @BeforeClass
    public static void deactivateRatingBarDialog() {
        WeatherBaseActivity.deactivateRatingBarDialog();
    }

    @Test
    public void verifyIncomingUserWeatherDataOnView() {
        if (isAvailableGooglePlayServices()) {
            if (isOnline()) {
                if (isGoogleApiClientConnected()) {
                    if (hasLocationPermission()) {
                        if (isDeviceLocationActive()) {
                            mISettingsDAO = new SettingsDAO();
                            String units = mISettingsDAO.getPrefUnitsFormatStorage(mAppContext);

                            if (units.equals("metric")) {
                                mUnitsFormat = "°C";
                            } else if (units.equals("fahrenheit")) {
                                mUnitsFormat = "°F";
                            }

                            WeatherDTO weatherDTO = UserWeatherFragment.getUserWeatherDTO();
                            onView(withId(R.id.location_name)).check(matches(withText(weatherDTO.getLocationName())));
                            onView(withId(R.id.temp_degree)).check(matches(withText(weatherDTO.getTempDegree() + mUnitsFormat)));
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
        if (isOnline()) {
            onView(withId(R.id.weather_temp_degree_layout)).perform(click());
            onView(withId(R.id.humidity_volume)).check(matches(isDisplayed()));
        } else {
            Assert.fail("User internet connection is offline for verify detailed weather fragment");
        }
    }

    @Test
    public void verifyWeatherDTOListItemsHasLocationDTOValues() {
        List<WeatherDTO> weatherDTOList = BookmarkWeatherListFragment.getWeatherDTOList();

        for (int i = 0; i < weatherDTOList.size(); i++) {
            WeatherDTO weatherDTO = weatherDTOList.get(i);

            if (weatherDTO.getLocationDTOId() == null ||
                    weatherDTO.getLocationDTOLatitude() == 0 || weatherDTO.getLocationDTOLongitude() == 0) {
                Assert.fail(i + ". item in WeatherDTOList has not locationDTO values");
            }
        }
    }

    @Test
    public void verifyRefreshWeatherButtonIsWorkingCorrectly() {
        List<WeatherDTO> weatherDTOList = BookmarkWeatherListFragment.getWeatherDTOList();
        int weatherDTOListHashCodeBeforeRefreshWeatherButtonIsClicked = weatherDTOList.hashCode();

        onView(withId(R.id.refresh_weather_button)).perform(click());

        weatherDTOList = BookmarkWeatherListFragment.getWeatherDTOList();
        int weatherDTOListHashCodeAfterRefreshWeatherButtonIsClicked = weatherDTOList.hashCode();

        assertThat(weatherDTOListHashCodeBeforeRefreshWeatherButtonIsClicked,
                not(equalTo(weatherDTOListHashCodeAfterRefreshWeatherButtonIsClicked)));
    }

    @Test
    public void verifyAddLocationBookmarkMessageIsWorkingCorrectly() {
        List<WeatherDTO> weatherDTOList = BookmarkWeatherListFragment.getWeatherDTOList();

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

    private boolean isGoogleApiClientConnected() {
        return UserWeatherFragment.getGoogleApiClientStatus();
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