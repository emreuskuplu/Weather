package com.emre.android.weatherapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.emre.android.weatherapp.dto.LocationDTO;
import com.emre.android.weatherapp.dto.WeatherDTO;
import com.emre.android.weatherapp.ui.LocatorWeatherOnMapFragment;
import com.emre.android.weatherapp.ui.WeatherListActivity;
import com.emre.android.weatherapp.ui.WeatherListFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class LocatorWeatherOnMapFragmentTest {
    private static final String TAG = LocatorWeatherOnMapFragmentTest.class.getSimpleName();

    private Context mAppContext = InstrumentationRegistry.getTargetContext();

    @Rule
    public ActivityTestRule<WeatherListActivity> activityRule =
            new ActivityTestRule<>(WeatherListActivity.class);

    @BeforeClass
    public static void deactivateRatingBarDialog() {
        WeatherListFragment.deactivateRatingBarDialog();
    }


    @Test
    public void longClickOnMapForInsertingLocationOnDatabaseAndValidateLocationIsAddedInLocationDTOListAndItsIdInWeatherDTOList() {
        if (isAvailableGooglePlayServices()) {
            onView(withId(R.id.add_location_button)).perform(click());
            onView(withId(R.id.map_view)).perform(longClick());

            LatLng latLng = LocatorWeatherOnMapFragment.getLatLngFromLongClickedOnMap();

            onView(withId(R.id.back_button)).perform(click());

            List<LocationDTO> locationDTOList = WeatherListFragment.getLocationDTOList();
            List<WeatherDTO> weatherDTOList = WeatherListFragment.getWeatherDTOList();

            LocationDTO locationDTO = locationDTOList.get(locationDTOList.size() - 1);
            WeatherDTO weatherDTO = weatherDTOList.get(weatherDTOList.size() - 1);

            assertThat(latLng.latitude, is(equalTo(locationDTO.getLatitude())));
            assertThat(latLng.longitude, is(equalTo(locationDTO.getLongitude())));
            assertThat(weatherDTO.getLocationDTOId(), is(equalTo(locationDTO.getId())));

        } else {
            Assert.fail("Google play services is not available for use google map");
        }
    }


    private boolean isAvailableGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(mAppContext);
        return resultCode == ConnectionResult.SUCCESS;
    }

}
