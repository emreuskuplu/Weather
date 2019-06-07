package com.emre.android.weatherapp;

import android.location.Location;

import com.emre.android.weatherapp.dao.IWeatherDAO;
import com.emre.android.weatherapp.dao.WeatherDAO;
import com.emre.android.weatherapp.dao.WeatherDAOStub;
import com.emre.android.weatherapp.dto.LocationDTO;
import com.emre.android.weatherapp.dto.WeatherDTO;
import com.emre.android.weatherapp.dto.WeatherForecastDTO;

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
    private List<LocationDTO> mLocationDTOList;
    private WeatherDTO mWeatherDTO;
    private WeatherDTO mWeatherDTOStub;
    private List<WeatherDTO> mWeatherDTOList;
    private List<WeatherDTO> mWeatherDTOListStub;
    private WeatherForecastDTO mWeatherForecastDTO;
    private WeatherForecastDTO mWeatherForecastDTOStub;

    @Test
    public void searchUserWeatherShouldReturnAllValuesThatAllOfNotNullForUserWeather() {
        System.out.println("\nsearchUserWeatherShouldReturnAllValuesThatAllOfNotNullForUserWeather()");
        givenWeatherDAOAndLocationDTOIsInitialized();
        whenSearchUserWeather();
        thenVerifyAllValuesInWeatherDTOAllOfAreNotNull();
    }

    @Test
    public void searchWeatherListShouldReturnAllValuesThatAllOfNotNullForWeatherList() {
        System.out.println("\nsearchWeatherListShouldReturnAllValuesThatAllOfNotNullForWeatherList()");
        givenWeatherDAOAndLocationDTOListIsInitialized();
        whenSearchWeatherList();
        thenVerifyAllValuesInWeatherDTOListAllOfAreNotNull();
    }

    @Test
    public void searchDetailedWeatherShouldReturnAllValuesThatAllOfNotNullAndVerifyCorrectDateFormatForDetailedWeather() {
        System.out.println("\nsearchDetailedWeatherShouldReturnAllValuesThatAllOfNotNullAndVerifyCorrectDateFormatForDetailedWeather()");
        givenWeatherDAOAndLocationDTOIsInitialized();
        whenSearchDetailedWeather();
        thenVerifyAllValuesInWeatherForecastDTOAllOfAreNotNullAndCorrectDateFormat();
    }

    private void givenWeatherDAOAndLocationDTOIsInitialized() {
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

    private void whenSearchUserWeather() {
        mWeatherDTO = mIWeatherDAO.getUserWeather(mLocation);
        mWeatherDTOStub = mIWeatherDAOStub.getUserWeather(mLocation);
        System.out.println("Stub: " + mWeatherDTOStub);
        System.out.println("Real: " + mWeatherDTO);
    }

    private void thenVerifyAllValuesInWeatherDTOAllOfAreNotNull() {
        if (mWeatherDTO.toString().equals("null null null null")) {
            fail("All values for user weather are null");
        }
    }

    private void givenWeatherDAOAndLocationDTOListIsInitialized() {
        mIWeatherDAO = new WeatherDAO();
        mIWeatherDAOStub = new WeatherDAOStub();
        mLocationDTOList = new ArrayList<>();
        mWeatherDTOListStub = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            LocationDTO locationDTO = new LocationDTO();
            double latitude = ThreadLocalRandom.current().nextDouble(90);
            double longitude = ThreadLocalRandom.current().nextDouble(180);

            System.out.println("Generated random values: " +
                    "latitude = " + latitude + " longitude = " + longitude);
            locationDTO.setLatitude(latitude);
            locationDTO.setLongitude(longitude);
            mLocationDTOList.add(locationDTO);
        }
    }

    private void whenSearchWeatherList() {
        mWeatherDTOList = mIWeatherDAO.getWeatherList(mLocationDTOList);
        mWeatherDTOListStub = mIWeatherDAOStub.getWeatherList(mLocationDTOList);
        System.out.println("Stub: " + mWeatherDTOList);
        System.out.println("Real: " + mWeatherDTOListStub);
    }

    private void thenVerifyAllValuesInWeatherDTOListAllOfAreNotNull() {
        for (int i = 0; i < 10; i++) {
            WeatherDTO weatherDTO = mWeatherDTOList.get(i);
            if (weatherDTO.toString().equals("null null null null")) {
                fail("All values for list item weather are null");
            }
        }
    }

    private void whenSearchDetailedWeather() {
        mWeatherForecastDTO = mIWeatherDAO.getDetailedWeather(mLocation);
        mWeatherForecastDTOStub = mIWeatherDAOStub.getDetailedWeather(mLocation);
        System.out.println("Stub: " + mWeatherForecastDTOStub.getWeatherDTOList());
        System.out.println("Real: " + mWeatherForecastDTO.getWeatherDTOList());
    }

    private void thenVerifyAllValuesInWeatherForecastDTOAllOfAreNotNullAndCorrectDateFormat() {
        List<WeatherDTO> weatherDTOList = mWeatherForecastDTO.getWeatherDTOList();
        for (int i = 0; i < 5; i++) {
            WeatherDTO weatherDTO = weatherDTOList.get(i);
            if (weatherDTO.toString().equals("null null null null 0 0 0 0 null")) {
                fail("All values for detailed weather are null and zero");
            }

            System.out.println("weatherDTO.getDate() =  " + weatherDTO.getDate());
            assertThat(weatherDTO.getDate().length(), is(equalTo(3)));
        }
    }
}
