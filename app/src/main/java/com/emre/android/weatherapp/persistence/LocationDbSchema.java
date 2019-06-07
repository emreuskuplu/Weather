package com.emre.android.weatherapp.persistence;

public class LocationDbSchema {
    private static final String TAG = LocationDbSchema.class.getSimpleName();

    public static final class LocationTable {
        public static final String NAME = "locations";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
        }
    }
}
