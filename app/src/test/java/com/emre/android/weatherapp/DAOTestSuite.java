package com.emre.android.weatherapp;

import com.emre.android.weatherapp.dao.LocationDAO;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        LocationDAOTest.class,
        WeatherDAOTest.class
})
public class DAOTestSuite {
}
