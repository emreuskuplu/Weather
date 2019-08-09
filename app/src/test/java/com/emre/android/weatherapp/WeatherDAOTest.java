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
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.WeatherDTO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;
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
    private WeatherDTO mWeatherDTO;
    private List<WeatherDTO> mWeatherDTOList;
    private List<WeatherDTO> mWeatherDTOListStub;

    @Test
    public void UserWeatherTaskShouldReturnAllWeatherValuesThatAllOfNotNullForUserWeather() {
        System.out.println("\nUserWeatherTaskShouldReturnAllWeatherValuesThatAllOfNotNullForUserWeather()");
        givenWeatherDAOAndLocationIsInitialized();
        whenUserWeatherTaskExecuted();
        thenVerifyAllWeatherValuesInWeatherDTOAllOfAreNotNull();
    }

    @Test
    public void BookmarkWeatherListTaskShouldReturnAllWeatherValuesThatAllOfNotNullForWeatherList() {
        System.out.println("\nBookmarkWeatherListTaskShouldReturnAllWeatherValuesThatAllOfNotNullForWeatherList()");
        givenWeatherDAOAndWeatherDTOListIsInitialized();
        whenBookmarkWeatherListTaskExecuted();
        thenVerifyAllWeatherValuesInWeatherDTOListAllOfAreNotNull();
    }

    @Test
    public void ForecastDetailedWeatherTaskShouldReturnAllWeatherValuesThatAllOfNotNullAndVerifyCorrectDateFormatForDetailedWeather() {
        System.out.println("\nForecastDetailedWeatherTaskShouldReturnAllWeatherValuesThatAllOfNotNullAndVerifyCorrectDateFormatForDetailedWeather()");
        givenWeatherDAOAndLocationIsInitialized();
        whenForecastDetailedWeatherTaskExecuted();
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
        mWeatherDTO = mIWeatherDAO.getUserWeather(mLocation);
        WeatherDTO weatherDTOStub = mIWeatherDAOStub.getUserWeather(mLocation);
        System.out.println("Stub: " + weatherDTOStub);
        System.out.println("Real: " + mWeatherDTO);
    }

    private void thenVerifyAllWeatherValuesInWeatherDTOAllOfAreNotNull() {
        if (mWeatherDTO.toString().equals("null null null null")) {
            fail("All weather values for user weather are null");
        }
    }

    private void givenWeatherDAOAndWeatherDTOListIsInitialized() {
        mIWeatherDAO = new WeatherDAO(ApplicationProvider.getApplicationContext());
        mIWeatherDAOStub = new WeatherDAOStub();
        mWeatherDTOList = new ArrayList<>();
        mWeatherDTOListStub = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            WeatherDTO weatherDTO = new WeatherDTO();
            double latitude = ThreadLocalRandom.current().nextDouble(90);
            double longitude = ThreadLocalRandom.current().nextDouble(180);

            System.out.println("Generated random values: " +
                    "latitude = " + latitude + " longitude = " + longitude);
            weatherDTO.setLocationDTOLatitude(latitude);
            weatherDTO.setLocationDTOLongitude(longitude);
            mWeatherDTOList.add(weatherDTO);
        }
    }

    private void whenBookmarkWeatherListTaskExecuted() {
        mWeatherDTOList = mIWeatherDAO.getBookmarkListWeather(mWeatherDTOList);
        mWeatherDTOListStub = mIWeatherDAOStub.getBookmarkListWeather(mWeatherDTOList);
        System.out.println("Stub: " + mWeatherDTOList);
        System.out.println("Real: " + mWeatherDTOListStub);
    }

    private void thenVerifyAllWeatherValuesInWeatherDTOListAllOfAreNotNull() {
        for (int i = 0; i < 10; i++) {
            WeatherDTO weatherDTO = mWeatherDTOList.get(i);
            if (weatherDTO.toString().equals("null null null null")) {
                fail("All weather values for list item weather are null");
            }
        }
    }

    private void whenForecastDetailedWeatherTaskExecuted() {
        mWeatherDTOList = mIWeatherDAO.getForecastDetailedWeatherList(mLocation);
        mWeatherDTOListStub = mIWeatherDAOStub.getForecastDetailedWeatherList(mLocation);
        System.out.println("Stub: " + mWeatherDTOListStub);
        System.out.println("Real: " + mWeatherDTOList);
    }

    private void thenVerifyAllWeatherValuesInWeatherDTOListAllOfAreNotNullAndCorrectDateFormat() {
        for (int i = 0; i < 5; i++) {
            WeatherDTO weatherDTO = mWeatherDTOListStub.get(i);
            if (weatherDTO.toString().equals("null null null null 0 0 0 0 null")) {
                fail("All weather values for detailed weather are null and zero");
            }

            System.out.println("weatherDTO.getDate() =  " + weatherDTO.getDate());
            assertThat(weatherDTO.getDate().length(), is(equalTo(3)));
        }
    }
}
