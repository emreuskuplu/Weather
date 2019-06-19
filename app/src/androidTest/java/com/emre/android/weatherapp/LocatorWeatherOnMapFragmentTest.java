package com.emre.android.weatherapp;

import android.content.Context;
import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.emre.android.weatherapp.dto.LocationDTO;
import com.emre.android.weatherapp.dto.WeatherDTO;
import com.emre.android.weatherapp.ui.BookmarkWeatherListFragment;
import com.emre.android.weatherapp.ui.LocatorWeatherOnMapFragment;
import com.emre.android.weatherapp.ui.WeatherBaseActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class LocatorWeatherOnMapFragmentTest {
    private static final String TAG = LocatorWeatherOnMapFragmentTest.class.getSimpleName();

    private Context mAppContext = InstrumentationRegistry.getTargetContext();

    @Rule
    public ActivityTestRule<WeatherBaseActivity> activityRule =
            new ActivityTestRule<>(WeatherBaseActivity.class);

    @BeforeClass
    public static void deactivateRatingBarDialog() {
        WeatherBaseActivity.deactivateRatingBarDialog();
    }


    @Test
    public void longClickOnMapForInsertingLocationOnDatabaseAndValidateLocationIsAddedInLocationDTOListAndItsIdInWeatherDTOList() {
        if (isAvailableGooglePlayServices()) {
            onView(withId(R.id.add_location_button)).perform(click());
            onView(withId(R.id.map_view)).perform(longClick());

            LatLng latLng = LocatorWeatherOnMapFragment.getLatLngFromLongClickedOnMap();

            onView(withId(R.id.back_button)).perform(click());

            List<LocationDTO> locationDTOList = BookmarkWeatherListFragment.getLocationDTOList();
            List<WeatherDTO> weatherDTOList = BookmarkWeatherListFragment.getWeatherDTOList();

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
