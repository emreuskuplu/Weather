package com.emre.android.weatherapp.dto.weather_json_schema;

import com.google.gson.annotations.SerializedName;

public class Snow {
    @SerializedName("1h")
    private double oneH;
    @SerializedName("3h")
    private double threeH;

    public double getOneH() {
        return oneH;
    }
    public double getThreeH() {
        return threeH;
    }
}
