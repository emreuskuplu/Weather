package com.emre.android.weatherapp.dto.weather_json_schema;

import java.util.List;

public class ForecastBody {
    private String cod;
    private double message;
    private int cnt;
    private List<ForecastDayBody> list;
    private City city;

    public String getCod() {
        return cod;
    }

    public double getMessage() {
        return message;
    }

    public int getCnt() {
        return cnt;
    }

    public List<ForecastDayBody> getList() {
        return list;
    }

    public City getCity() {
        return city;
    }
}
