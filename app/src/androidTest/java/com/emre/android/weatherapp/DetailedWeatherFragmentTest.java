package com.emre.android.weatherapp;

import android.content.Context;
import android.location.Location;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.emre.android.weatherapp.dao.ISettingsDAO;
import com.emre.android.weatherapp.dao.SettingsDAO;
import com.emre.android.weatherapp.dto.WeatherDTO;
import com.emre.android.weatherapp.ui.DetailedWeatherFragment;
import com.emre.android.weatherapp.ui.UserWeatherFragment;
import com.emre.android.weatherapp.ui.WeatherBaseActivity;

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

@RunWith(AndroidJUnit4.class)
public class DetailedWeatherFragmentTest {

    private String mUnitsFormat = "";
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
    public void verifyLocationDTOInWeatherListFragmentMatchesLocationDTOInDetailedWeatherFragment() {
        Location weatherListFragmentLocation = UserWeatherFragment.getUserLocation();

        onView(withId(R.id.weather_temp_degree_layout)).perform(click());

        Location detailedWeatherFragmentLocationDTO = DetailedWeatherFragment.getLocation();

        assertThat(weatherListFragmentLocation.getLatitude(), is(equalTo(detailedWeatherFragmentLocationDTO.getLatitude())));
        assertThat(weatherListFragmentLocation.getLongitude(), is(equalTo(detailedWeatherFragmentLocationDTO.getLongitude())));
    }

    @Test
    public void doubleVerifyIncomingWeatherListDTOWithRefreshWeatherButton() {
        int selectedDayIndex;

        onView(withId(R.id.weather_temp_degree_layout)).perform(click());

        for (int i = 0; i < 2; i++) {
            selectedDayIndex = DetailedWeatherFragment.getSelectedDayIndex();
            verifyDetailedWeatherDTOOfFiveDays(selectedDayIndex);

            onView(withId(R.id.first_day_layout)).perform(click());

            selectedDayIndex = DetailedWeatherFragment.getSelectedDayIndex();
            verifyDetailedWeatherDTOOfFiveDays(selectedDayIndex);

            onView(withId(R.id.second_day_layout)).perform(click());

            selectedDayIndex = DetailedWeatherFragment.getSelectedDayIndex();
            verifyDetailedWeatherDTOOfFiveDays(selectedDayIndex);

            onView(withId(R.id.third_day_layout)).perform(click());

            selectedDayIndex = DetailedWeatherFragment.getSelectedDayIndex();
            verifyDetailedWeatherDTOOfFiveDays(selectedDayIndex);

            onView(withId(R.id.fourth_day_layout)).perform(click());

            selectedDayIndex = DetailedWeatherFragment.getSelectedDayIndex();
            verifyDetailedWeatherDTOOfFiveDays(selectedDayIndex);

            onView(withId(R.id.fifth_day_layout)).perform(click());

            selectedDayIndex = DetailedWeatherFragment.getSelectedDayIndex();
            verifyDetailedWeatherDTOOfFiveDays(selectedDayIndex);

            onView(withId(R.id.refresh_weather_button)).perform(click());
        }
    }

    @Test
    public void clickBackButtonAndVerifyWeatherListFragment() {
        onView(withId(R.id.weather_temp_degree_layout)).perform(click());
        onView(withId(R.id.back_button)).perform(click());
        onView(withId(R.id.weather_list_recycler_view)).check(matches(isDisplayed()));
    }

    private void verifyDetailedWeatherDTOOfFiveDays(int selectedDayIndex) {
        List<WeatherDTO> weatherDTOList = DetailedWeatherFragment.getWeatherDTOList();
        WeatherDTO detailedWeatherDTO = weatherDTOList.get(selectedDayIndex);
        String detailedDayName = DetailedWeatherFragment.changeShortDateToLargeDate(detailedWeatherDTO.getDate());

        mISettingsDAO = new SettingsDAO();

        String units = mISettingsDAO.getPrefUnitsFormatStorage(mAppContext);

        if (units.equals("metric")) {
            mUnitsFormat = "°C";
        } else if (units.equals("fahrenheit")) {
            mUnitsFormat = "°F";
        }

        WeatherDTO firstDayLayoutWeatherDTO = weatherDTOList.get(0);
        WeatherDTO secondDayLayoutWeatherDTO = weatherDTOList.get(1);
        WeatherDTO thirdDayLayoutWeatherDTO = weatherDTOList.get(2);
        WeatherDTO fourthDayLayoutWeatherDTO = weatherDTOList.get(3);
        WeatherDTO fifthDayLayoutWeatherDTO = weatherDTOList.get(4);

        onView(withId(R.id.location_name)).check(matches(withText(detailedWeatherDTO.getLocationName())));
        onView(withId(R.id.detailed_day)).check(matches(withText(detailedDayName)));
        onView(withId(R.id.temp_degree)).check(matches(withText(detailedWeatherDTO.getTempDegree() + mUnitsFormat)));
        onView(withId(R.id.description)).check(matches(withText(detailedWeatherDTO.getDescription())));
        onView(withId(R.id.humidity_volume)).check(matches(withText(detailedWeatherDTO.getHumidity())));
        onView(withId(R.id.wind_volume)).check(matches(withText(detailedWeatherDTO.getWindVolume())));
        onView(withId(R.id.rain_volume)).check(matches(withText(detailedWeatherDTO.getRainVolume())));
        onView(withId(R.id.snow_volume)).check(matches(withText(detailedWeatherDTO.getSnowVolume())));

        onView(withId(R.id.first_day_date)).check(matches(withText(firstDayLayoutWeatherDTO.getDate())));
        onView(withId(R.id.first_day_temp_degree)).check(matches(withText(firstDayLayoutWeatherDTO.getTempDegree() + mUnitsFormat)));
        onView(withId(R.id.second_day_date)).check(matches(withText(secondDayLayoutWeatherDTO.getDate())));
        onView(withId(R.id.second_day_temp_degree)).check(matches(withText(secondDayLayoutWeatherDTO.getTempDegree() + mUnitsFormat)));
        onView(withId(R.id.third_day_date)).check(matches(withText(thirdDayLayoutWeatherDTO.getDate())));
        onView(withId(R.id.third_day_temp_degree)).check(matches(withText(thirdDayLayoutWeatherDTO.getTempDegree() + mUnitsFormat)));
        onView(withId(R.id.fourth_day_date)).check(matches(withText(fourthDayLayoutWeatherDTO.getDate())));
        onView(withId(R.id.fourth_day_temp_degree)).check(matches(withText(fourthDayLayoutWeatherDTO.getTempDegree() + mUnitsFormat)));
        onView(withId(R.id.fifth_day_date)).check(matches(withText(fifthDayLayoutWeatherDTO.getDate())));
        onView(withId(R.id.fifth_day_temp_degree)).check(matches(withText(fifthDayLayoutWeatherDTO.getTempDegree() + mUnitsFormat)));
    }
}
