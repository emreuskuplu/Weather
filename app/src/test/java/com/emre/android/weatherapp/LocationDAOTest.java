package com.emre.android.weatherapp;

import androidx.test.core.app.ApplicationProvider;

import com.emre.android.weatherapp.dao.ILocationDAO;
import com.emre.android.weatherapp.dao.LocationDAO;
import com.emre.android.weatherapp.dto.LocationDTO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(RobolectricTestRunner.class)
public class LocationDAOTest {

    private ILocationDAO mILocationDAO;
    private List<LocationDTO> mLocationDTOList;
    private double mExpectedLatitude;
    private double mExpectedLongitude;

    @Test
    public void insertingReturningAndDeletingLocationValuesInDatabase() {
        givenLocationDAOAndLocationValuesIsInitialized();
        whenInsertingAndGettingLocationValuesInDatabase();
        thenVerifyInsertedLocationValuesAndDeletingLocationValuesInDatabase();
    }

    private void givenLocationDAOAndLocationValuesIsInitialized() {
        mILocationDAO = new LocationDAO(ApplicationProvider.getApplicationContext());
        mExpectedLatitude = ThreadLocalRandom.current().nextDouble(90);
        mExpectedLongitude = ThreadLocalRandom.current().nextDouble(180);
        System.out.println("Generated random values: " +
                "latitude = " + mExpectedLatitude + " longitude = " + mExpectedLongitude);
    }

    private void whenInsertingAndGettingLocationValuesInDatabase() {
        mILocationDAO.LocationDbInserting(UUID.randomUUID(), mExpectedLatitude, mExpectedLongitude);
        mLocationDTOList = mILocationDAO.LocationDbExtract();
    }

    private void thenVerifyInsertedLocationValuesAndDeletingLocationValuesInDatabase() {
        LocationDTO locationDTO = mLocationDTOList.get(0);
        double actualLatitude = locationDTO.getLatitude();
        double actualLongitude = locationDTO.getLongitude();

        assertThat(mExpectedLatitude, is(equalTo(actualLatitude)));
        assertThat(mExpectedLongitude, is(equalTo(actualLongitude)));

        mILocationDAO.LocationDbDeleteLocationData(locationDTO.getId());
        mLocationDTOList = mILocationDAO.LocationDbExtract();

        assertThat(mLocationDTOList, is(empty()));
    }
}
