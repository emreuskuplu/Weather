package com.emre.android.weatherapp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        WeatherListFragmentTest.class,
        LocatorWeatherOnMapFragmentTest.class,
        DetailedWeatherFragmentTest.class
})
public class uiTestSuite {
}
