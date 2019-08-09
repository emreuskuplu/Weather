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

import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import com.emre.android.weatherapp.dataaccessobjects.locationdataaccess.ILocationDAO;
import com.emre.android.weatherapp.dataaccessobjects.locationdataaccess.LocationDAO;
import com.emre.android.weatherapp.datatransferobjects.locationdatatransfer.LocationDTO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class LocationDAOTest {

    private ILocationDAO mILocationDAO;
    private List<LocationDTO> mLocationDTOList;
    private double mExpectedLatitude;
    private double mExpectedLongitude;
    private List<Double> mExpectedLatitudeList = new ArrayList<>();
    private List<Double> mExpectedLongitudeList = new ArrayList<>();

    @Test
    public void insertingReturningAndDeletingLocationValuesInDatabase() {
        givenLocationDAOAndLocationValuesIsInitialized();
        whenInsertingAndGettingLocationValuesInDatabase();
        thenVerifyInsertedLocationValuesAndDeletingLocationValuesInDatabase();
    }

    @Test
    public void insertingReturningAndDeletingMultipleLocationValuesInDatabase() {
        givenLocationDAOAndLocationValuesListIsInitialized();
        whenInsertingAndGettingLocationValuesListInDatabase();
        thenVerifyAllInsertedLocationValuesAndDeletingAllLocationValuesInDatabase();
    }

    private void givenLocationDAOAndLocationValuesIsInitialized() {
        mILocationDAO = new LocationDAO(ApplicationProvider.getApplicationContext());
        mExpectedLatitude = ThreadLocalRandom.current().nextDouble(90);
        mExpectedLongitude = ThreadLocalRandom.current().nextDouble(180);
        System.out.println("Generated random values: " +
                "latitude = " + mExpectedLatitude + " longitude = " + mExpectedLongitude);
    }

    private void givenLocationDAOAndLocationValuesListIsInitialized() {
        mILocationDAO = new LocationDAO(ApplicationProvider.getApplicationContext());

        for (int i = 0; i < 10; i++) {
            mExpectedLatitude = ThreadLocalRandom.current().nextDouble(90);
            mExpectedLongitude = ThreadLocalRandom.current().nextDouble(180);
            mExpectedLatitudeList.add(mExpectedLatitude);
            mExpectedLongitudeList.add(mExpectedLongitude);
            System.out.println("Generated random values[" + i + "]: " +
                    "latitude = " + mExpectedLatitude + " longitude = " + mExpectedLongitude);
        }
    }

    private void whenInsertingAndGettingLocationValuesInDatabase() {
        mILocationDAO.locationDbInserting(UUID.randomUUID(), mExpectedLatitude, mExpectedLongitude);
        mLocationDTOList = mILocationDAO.locationDbExtract();
    }

    private void whenInsertingAndGettingLocationValuesListInDatabase() {
        for (int i = 0; i < 10; i++) {
            mILocationDAO.locationDbInserting(UUID.randomUUID(), mExpectedLatitudeList.get(i), mExpectedLongitudeList.get(i));
        }

        mLocationDTOList = mILocationDAO.locationDbExtract();
    }

    private void thenVerifyInsertedLocationValuesAndDeletingLocationValuesInDatabase() {
        LocationDTO locationDTO = mLocationDTOList.get(0);
        double actualLatitude = locationDTO.getLatitude();
        double actualLongitude = locationDTO.getLongitude();

        assertThat(mExpectedLatitude, is(equalTo(actualLatitude)));
        assertThat(mExpectedLongitude, is(equalTo(actualLongitude)));

        mILocationDAO.locationDbDeleteLocationData(locationDTO.getId());
        mLocationDTOList = mILocationDAO.locationDbExtract();

        assertThat(mLocationDTOList, is(empty()));
    }

    public void thenVerifyAllInsertedLocationValuesAndDeletingAllLocationValuesInDatabase() {
        for (int i = 0; i < 10; i++) {
            LocationDTO locationDTO = mLocationDTOList.get(i);
            double actualLatitude = locationDTO.getLatitude();
            double actualLongitude = locationDTO.getLongitude();

            assertThat(mExpectedLatitudeList.get(i), is(equalTo(actualLatitude)));
            assertThat(mExpectedLongitudeList.get(i), is(equalTo(actualLongitude)));
        }

        mILocationDAO.locationDbDeleteAllLocationData();
        mLocationDTOList = mILocationDAO.locationDbExtract();

        assertThat(mLocationDTOList, is(empty()));
    }
}
