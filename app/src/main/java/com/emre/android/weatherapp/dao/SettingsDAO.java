package com.emre.android.weatherapp.dao;

import android.content.Context;

import androidx.preference.PreferenceManager;

public class SettingsDAO implements ISettingsDAO {
    private static final String TAG = SettingsDAO.class.getSimpleName();

    private static final String PREF_UNITS_FORMAT_STORAGE = "unitsFormatStorage";

    @Override
    public String getPrefUnitsFormatStorage(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_UNITS_FORMAT_STORAGE, "metric");

    }

    @Override
    public void setPrefUnitsFormatStorage(Context context, String unitsFormat) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_UNITS_FORMAT_STORAGE, unitsFormat)
                .apply();
    }
}
