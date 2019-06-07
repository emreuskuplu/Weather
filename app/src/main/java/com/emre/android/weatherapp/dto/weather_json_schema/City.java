package com.emre.android.weatherapp.dto.weather_json_schema;

public class City {
    private int id;
    private String name;
    private Coord coord;
    private String country;
    private int population;
    private int timezone;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coord getCoord() {
        return coord;
    }

    public String getCountry() {
        return country;
    }

    public int getPopulation() {
        return population;
    }

    public int getTimezone() {
        return timezone;
    }
}
