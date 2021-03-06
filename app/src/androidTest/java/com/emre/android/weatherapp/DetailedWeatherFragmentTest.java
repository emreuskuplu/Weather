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
import android.location.Location;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.emre.android.weatherapp.dataaccessobjects.settingsdataaccess.ISettingsDAO;
import com.emre.android.weatherapp.dataaccessobjects.settingsdataaccess.SettingsDAO;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.DetailedWeatherDTO;
import com.emre.android.weatherapp.scenes.detailedweather.DetailedWeatherFragment;
import com.emre.android.weatherapp.scenes.userweather.UserWeatherFragment;
import com.emre.android.weatherapp.scenes.mainweather.MainWeatherActivity;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * @author Emre Üsküplü
 */
@RunWith(AndroidJUnit4.class)
public class DetailedWeatherFragmentTest {

    private String mUnitsFormat = "";
    private ISettingsDAO mISettingsDAO;
    private Context mAppContext = ApplicationProvider.getApplicationContext();

    @Rule
    public ActivityTestRule<MainWeatherActivity> activityRule =
            new ActivityTestRule<>(MainWeatherActivity.class);

    @BeforeClass
    public static void deactivateRatingBarDialog() {
        MainWeatherActivity.deactivateRatingBarDialog();
    }

    @Test
    public void verifyLocationDTOInWeatherListFragmentMatchesLocationDTOInDetailedWeatherFragment() {
        Location weatherListFragmentLocation = UserWeatherFragment.getUserLocation();

        onView(withId(R.id.weather_temp_degree_view)).perform(click());

        Location detailedWeatherFragmentLocationDTO = DetailedWeatherFragment.getLocation();

        assertThat(weatherListFragmentLocation.getLatitude(), is(equalTo(detailedWeatherFragmentLocationDTO.getLatitude())));
        assertThat(weatherListFragmentLocation.getLongitude(), is(equalTo(detailedWeatherFragmentLocationDTO.getLongitude())));
    }

    @Test
    public void doubleVerifyIncomingWeatherListDTOWithRefreshWeatherButton() {
        int selectedDay;

        onView(withId(R.id.weather_temp_degree_view)).perform(click());

        for (int i = 0; i < 2; i++) {
            selectedDay = DetailedWeatherFragment.getSelectedDay();
            verifyDetailedWeatherDTOOfFiveDays(selectedDay);

            onView(withId(R.id.first_day_view)).perform(click());

            selectedDay = DetailedWeatherFragment.getSelectedDay();
            verifyDetailedWeatherDTOOfFiveDays(selectedDay);

            onView(withId(R.id.second_day_view)).perform(click());

            selectedDay = DetailedWeatherFragment.getSelectedDay();
            verifyDetailedWeatherDTOOfFiveDays(selectedDay);

            onView(withId(R.id.third_day_view)).perform(click());

            selectedDay = DetailedWeatherFragment.getSelectedDay();
            verifyDetailedWeatherDTOOfFiveDays(selectedDay);

            onView(withId(R.id.fourth_day_view)).perform(click());

            selectedDay = DetailedWeatherFragment.getSelectedDay();
            verifyDetailedWeatherDTOOfFiveDays(selectedDay);

            onView(withId(R.id.fifth_day_view)).perform(click());

            selectedDay = DetailedWeatherFragment.getSelectedDay();
            verifyDetailedWeatherDTOOfFiveDays(selectedDay);

            onView(withId(R.id.refresh_weather_button)).perform(click());
        }
    }

    @Test
    public void clickBackButtonAndVerifyWeatherListFragment() {
        onView(withId(R.id.weather_temp_degree_view)).perform(click());
        onView(withId(R.id.back_button)).perform(click());
        onView(withId(R.id.weather_list_recycler_view)).check(matches(isDisplayed()));
    }

    private void verifyDetailedWeatherDTOOfFiveDays(int selectedDayIndex) {
        List<DetailedWeatherDTO> detailedWeatherDTOList = DetailedWeatherFragment.getDetailedWeatherDTOList();
        DetailedWeatherDTO detailedWeatherDTO = detailedWeatherDTOList.get(selectedDayIndex);
        String detailedDayName = DetailedWeatherFragment.changeShortDateToLargeDate(detailedWeatherDTO.getDate());

        mISettingsDAO = new SettingsDAO();

        String units = mISettingsDAO.getPrefUnitsFormatStorage(mAppContext);

        if (units.equals("metric")) {
            mUnitsFormat = "°C";
        } else if (units.equals("fahrenheit")) {
            mUnitsFormat = "°F";
        }

        DetailedWeatherDTO firstDayDetailedWeatherDTO = detailedWeatherDTOList.get(0);
        DetailedWeatherDTO secondDayDetailedWeatherDTO = detailedWeatherDTOList.get(1);
        DetailedWeatherDTO thirdDayDetailedWeatherDTO = detailedWeatherDTOList.get(2);
        DetailedWeatherDTO fourthDayDetailedWeatherDTO = detailedWeatherDTOList.get(3);
        DetailedWeatherDTO fifthDayDetailedWeatherDTO = detailedWeatherDTOList.get(4);

        onView(withId(R.id.location_name)).check(matches(withText(detailedWeatherDTO.getLocationName())));
        onView(withId(R.id.detailed_day)).check(matches(withText(detailedDayName)));
        onView(withId(R.id.temp_degree)).check(matches(withText(detailedWeatherDTO.getTempDegree() + mUnitsFormat)));
        onView(withId(R.id.description)).check(matches(withText(detailedWeatherDTO.getDescription())));
        onView(withId(R.id.humidity_volume)).check(matches(withText(detailedWeatherDTO.getHumidity())));
        onView(withId(R.id.wind_volume)).check(matches(withText(detailedWeatherDTO.getWindVolume())));
        onView(withId(R.id.rain_volume)).check(matches(withText(detailedWeatherDTO.getRainVolume())));
        onView(withId(R.id.snow_volume)).check(matches(withText(detailedWeatherDTO.getSnowVolume())));

        onView(withId(R.id.first_day_date)).check(matches(withText(firstDayDetailedWeatherDTO.getDate())));
        onView(withId(R.id.first_day_temp_degree)).check(matches(withText(firstDayDetailedWeatherDTO.getTempDegree() + mUnitsFormat)));
        onView(withId(R.id.second_day_date)).check(matches(withText(secondDayDetailedWeatherDTO.getDate())));
        onView(withId(R.id.second_day_temp_degree)).check(matches(withText(secondDayDetailedWeatherDTO.getTempDegree() + mUnitsFormat)));
        onView(withId(R.id.third_day_date)).check(matches(withText(thirdDayDetailedWeatherDTO.getDate())));
        onView(withId(R.id.third_day_temp_degree)).check(matches(withText(thirdDayDetailedWeatherDTO.getTempDegree() + mUnitsFormat)));
        onView(withId(R.id.fourth_day_date)).check(matches(withText(fourthDayDetailedWeatherDTO.getDate())));
        onView(withId(R.id.fourth_day_temp_degree)).check(matches(withText(fourthDayDetailedWeatherDTO.getTempDegree() + mUnitsFormat)));
        onView(withId(R.id.fifth_day_date)).check(matches(withText(fifthDayDetailedWeatherDTO.getDate())));
        onView(withId(R.id.fifth_day_temp_degree)).check(matches(withText(fifthDayDetailedWeatherDTO.getTempDegree() + mUnitsFormat)));
    }
}
