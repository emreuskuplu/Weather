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

import android.location.Location;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import com.emre.android.weatherapp.dataaccessobjects.weatherdataaccess.IWeatherDAO;
import com.emre.android.weatherapp.dataaccessobjects.weatherdataaccess.WeatherDAO;
import com.emre.android.weatherapp.dataaccessobjects.weatherdataaccess.WeatherDAOStub;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.BookmarkWeatherDTO;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.DetailedWeatherDTO;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.UserWeatherDTO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

/**
 * @author Emre Üsküplü
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class WeatherDAOTest {

    private IWeatherDAO mIWeatherDAO;
    private IWeatherDAO mIWeatherDAOStub;
    private Location mLocation;
    private UserWeatherDTO mUserWeatherDTO;
    private List<BookmarkWeatherDTO> mBookmarkWeatherDTOList;
    private List<BookmarkWeatherDTO> mBookmarkWeatherDTOListStub;
    private List<DetailedWeatherDTO> mDetailedWeatherDTOList;
    private List<DetailedWeatherDTO> mDetailedWeatherDTOListStub;

    @Test
    public void userWeatherTaskShouldReturnAllWeatherValuesThatAllOfNotNullForUserWeather() {
        System.out.println("\nUserWeatherTaskShouldReturnAllWeatherValuesThatAllOfNotNullForUserWeather()");
        givenWeatherDAOAndLocationIsInitialized();
        whenUserWeatherTaskExecuted();
        thenVerifyAllWeatherValuesInWeatherDTOAllOfAreNotNull();
    }

    @Test
    public void bookmarkWeatherListTaskShouldReturnAllWeatherValuesThatAllOfNotNullForWeatherList() {
        System.out.println("\nBookmarkWeatherListTaskShouldReturnAllWeatherValuesThatAllOfNotNullForWeatherList()");
        givenWeatherDAOAndWeatherDTOListIsInitialized();
        whenBookmarkWeatherListTaskExecuted();
        thenVerifyAllValuesInWeatherDTOListAllOfAreNotNull();
    }

    @Test
    public void forecastDetailedWeatherTaskShouldReturnAllWeatherValuesThatAllOfNotNullAndVerifyCorrectDateFormatForDetailedWeather() {
        System.out.println("\nForecastDetailedWeatherTaskShouldReturnAllWeatherValuesThatAllOfNotNullAndVerifyCorrectDateFormatForDetailedWeather()");
        givenWeatherDAOAndLocationIsInitialized();
        whenDetailedWeatherTaskExecuted();
        thenVerifyAllWeatherValuesInWeatherDTOListAllOfAreNotNullAndCorrectDateFormat();
    }

    private void givenWeatherDAOAndLocationIsInitialized() {
        mIWeatherDAO = new WeatherDAO(ApplicationProvider.getApplicationContext());
        mIWeatherDAOStub = new WeatherDAOStub();
        mLocation =  new Location("");
        double latitude = ThreadLocalRandom.current().nextDouble(90);
        double longitude = ThreadLocalRandom.current().nextDouble(180);

        System.out.println("Generated random values: " +
                "latitude = " + latitude + " longitude = " + longitude);
        mLocation.setLatitude(latitude);
        mLocation.setLongitude(longitude);
    }

    private void whenUserWeatherTaskExecuted() {
        mUserWeatherDTO = mIWeatherDAO.getUserWeather(mLocation);
        UserWeatherDTO userWeatherDTOStub = mIWeatherDAOStub.getUserWeather(mLocation);
        System.out.println("Stub: " + userWeatherDTOStub);
        System.out.println("Real: " + mUserWeatherDTO);
    }

    private void thenVerifyAllWeatherValuesInWeatherDTOAllOfAreNotNull() {
        if (mUserWeatherDTO.toString().equals("null null null null")) {
            fail("All weather values for user weather are null");
        }
    }

    private void givenWeatherDAOAndWeatherDTOListIsInitialized() {
        mIWeatherDAO = new WeatherDAO(ApplicationProvider.getApplicationContext());
        mIWeatherDAOStub = new WeatherDAOStub();
        mBookmarkWeatherDTOList = new ArrayList<>();
        mBookmarkWeatherDTOListStub = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            double latitude = ThreadLocalRandom.current().nextDouble(90);
            double longitude = ThreadLocalRandom.current().nextDouble(180);

            System.out.println("Generated random values: " +
                    "latitude = " + latitude + " longitude = " + longitude);

            BookmarkWeatherDTO bookmarkWeatherDTO = new BookmarkWeatherDTO(
                    UUID.randomUUID(),
                    latitude,
                    longitude
            );

            mBookmarkWeatherDTOList.add(bookmarkWeatherDTO);
            mBookmarkWeatherDTOListStub.add(bookmarkWeatherDTO);
        }
    }

    private void whenBookmarkWeatherListTaskExecuted() {
        mBookmarkWeatherDTOList = mIWeatherDAO.getBookmarkListWeather(mBookmarkWeatherDTOList);
        mBookmarkWeatherDTOListStub = mIWeatherDAOStub.getBookmarkListWeather(mBookmarkWeatherDTOListStub);
        System.out.println("Stub: " + mBookmarkWeatherDTOList);
        System.out.println("Real: " + mBookmarkWeatherDTOListStub);
    }

    private void thenVerifyAllValuesInWeatherDTOListAllOfAreNotNull() {
        for (int i = 0; i < 10; i++) {
            BookmarkWeatherDTO bookmarkWeatherDTO = mBookmarkWeatherDTOList.get(i);
            if (bookmarkWeatherDTO.toString().equals("null null null null null null null")) {
                fail("All values for list item weather are null");
            }
        }
    }

    private void whenDetailedWeatherTaskExecuted() {
        mDetailedWeatherDTOList = mIWeatherDAO.getDetailedWeatherList(mLocation);
        mDetailedWeatherDTOListStub = mIWeatherDAOStub.getDetailedWeatherList(mLocation);
        System.out.println("Stub: " + mDetailedWeatherDTOListStub);
        System.out.println("Real: " + mDetailedWeatherDTOList);
    }

    private void thenVerifyAllWeatherValuesInWeatherDTOListAllOfAreNotNullAndCorrectDateFormat() {
        for (int i = 0; i < 5; i++) {
            DetailedWeatherDTO detailedWeatherDTO = mDetailedWeatherDTOList.get(i);
            if (detailedWeatherDTO.toString().equals("null null null null 0 0 0 0 null")) {
                fail("All weather values for detailed weather are null and zero");
            }

            System.out.println("weatherDTO.getDate() =  " + detailedWeatherDTO.getDate());
            assertThat(detailedWeatherDTO.getDate().length(), is(equalTo(3)));
        }
    }
}
