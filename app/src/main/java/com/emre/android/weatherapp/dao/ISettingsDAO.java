package com.emre.android.weatherapp.dao;

import android.content.Context;

public interface ISettingsDAO {
    String getPrefUnitsFormatStorage(Context context);
    void setPrefUnitsFormatStorage(Context context, String unitsFormat);
}
