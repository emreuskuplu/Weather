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

package com.emre.android.weatherapp;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.emre.android.weatherapp.datatransferobjects.locationdatatransfer.LocationDTO;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.BookmarkWeatherDTO;
import com.emre.android.weatherapp.scenes.bookmarklistweather.BookmarkListWeatherFragment;
import com.emre.android.weatherapp.scenes.bookmarkmap.CreateBookmarkOnMapFragment;
import com.emre.android.weatherapp.scenes.mainweather.MainWeatherActivity;
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

/**
 * @author Emre Üsküplü
 */
@RunWith(AndroidJUnit4.class)
public class CreateBookmarkOnMapFragmentTest {
    private static final String TAG = CreateBookmarkOnMapFragmentTest.class.getSimpleName();

    private Context mAppContext = ApplicationProvider.getApplicationContext();

    @Rule
    public ActivityTestRule<MainWeatherActivity> activityRule =
            new ActivityTestRule<>(MainWeatherActivity.class);

    @BeforeClass
    public static void deactivateRatingBarDialog() {
        MainWeatherActivity.deactivateRatingBarDialog();
    }


    @Test
    public void longClickOnMapForInsertingLocationOnDatabaseAndValidateLocationValuesIsAddedInLocationDTOListAndWeatherDTOList() {
        if (isAvailableGooglePlayServices()) {
            onView(withId(R.id.add_location_button)).perform(click());
            onView(withId(R.id.map_view)).perform(longClick());

            LatLng latLng = CreateBookmarkOnMapFragment.getLatLngFromLongClickedOnMap();

            onView(withId(R.id.back_button)).perform(click());

            List<LocationDTO> locationDTOList = BookmarkListWeatherFragment.getLocationDTOList();
            List<BookmarkWeatherDTO> bookmarkWeatherDTOList = BookmarkListWeatherFragment.getBookmarkWeatherDTOList();

            LocationDTO locationDTO = locationDTOList.get(locationDTOList.size() - 1);
            BookmarkWeatherDTO bookmarkWeatherDTO = bookmarkWeatherDTOList.get(bookmarkWeatherDTOList.size() - 1);

            assertThat(latLng.latitude, is(equalTo(locationDTO.getLatitude())));
            assertThat(latLng.longitude, is(equalTo(locationDTO.getLongitude())));
            assertThat(latLng.latitude, is(equalTo(bookmarkWeatherDTO.getLocationDTOLatitude())));
            assertThat(latLng.longitude, is(equalTo(bookmarkWeatherDTO.getLocationDTOLongitude())));
            assertThat(bookmarkWeatherDTO.getLocationDTOId(), is(equalTo(locationDTO.getId())));

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
