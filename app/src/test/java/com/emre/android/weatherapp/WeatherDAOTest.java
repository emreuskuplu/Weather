package com.emre.android.weatherapp;

import android.location.Location;

import com.emre.android.weatherapp.dao.IWeatherDAO;
import com.emre.android.weatherapp.dao.WeatherDAO;
import com.emre.android.weatherapp.dao.WeatherDAOStub;
import com.emre.android.weatherapp.dto.WeatherDTO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
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
        mIWeatherDAO = new WeatherDAO();
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
        mIWeatherDAO = new WeatherDAO();
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
        mWeatherDTOList = mIWeatherDAO.getBookmarkWeatherList(mWeatherDTOList);
        mWeatherDTOListStub = mIWeatherDAOStub.getBookmarkWeatherList(mWeatherDTOList);
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
