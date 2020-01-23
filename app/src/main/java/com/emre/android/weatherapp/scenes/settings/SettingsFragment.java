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

package com.emre.android.weatherapp.scenes.settings;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.emre.android.weatherapp.R;
import com.emre.android.weatherapp.dataaccessobjects.settingsdataaccess.SettingsDAO;
import com.emre.android.weatherapp.scenes.dialogs.DeleteAllBookmarksDialogFragment;

/**
 * @author Emre Üsküplü
 *
 * Shows settings
 * It has preferences that switch metric to fahrenheit, fahrenheit to metric and delete all bookmarks
 * When user clicks metric switch preference then switch all values to metric
 * When user clicks fahrenheit switch preference then switch all values to fahrenheit
 * When user clicks delete all bookmarks preference then delete all bookmarks in list
 */
public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = SettingsFragment.class.getSimpleName();

    private static final String DIALOG_DELETE_ALL_BOOKMARKS = "DeleteAllBookmarks";

    private SettingsDAO mSettingsDAO;
    private SwitchPreference mMetricSwitchPreference;
    private SwitchPreference mFahrenheitSwitchPreference;
    private PreferenceScreen mDeleteAllBookmarksPreferenceScreen;

    private Preference.OnPreferenceClickListener mMetricSwitchPreferenceClickListener =
            new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (mMetricSwitchPreference.isChecked()) {
                        mSettingsDAO.setPrefUnitsFormatStorage(requireContext(), "metric");

                        mFahrenheitSwitchPreference.setChecked(false);

                    } else if (!mMetricSwitchPreference.isChecked()) {
                        mFahrenheitSwitchPreference.setChecked(true);
                        mSettingsDAO.setPrefUnitsFormatStorage(requireContext(), "fahrenheit");
                    }

                    return true;
                }
            };

    private Preference.OnPreferenceClickListener mFahrenheitSwitchPreferenceClickListener =
            new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (mFahrenheitSwitchPreference.isChecked()) {
                mSettingsDAO.setPrefUnitsFormatStorage(requireContext(), "fahrenheit");

                mMetricSwitchPreference.setChecked(false);

            } else if (!mFahrenheitSwitchPreference.isChecked()) {
                mMetricSwitchPreference.setChecked(true);
                mSettingsDAO.setPrefUnitsFormatStorage(requireContext(), "metric");
            }

            return true;
        }
    };

    private Preference.OnPreferenceClickListener mDeleteAllBookmarksPreferenceScreenClickListener =
            new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    FragmentManager manager = getChildFragmentManager();
                    DeleteAllBookmarksDialogFragment dialogFragment = DeleteAllBookmarksDialogFragment.newInstance();
                    dialogFragment.show(manager, DIALOG_DELETE_ALL_BOOKMARKS);

                    return true;
                }
            };

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey);

        mSettingsDAO = new SettingsDAO();

        mMetricSwitchPreference = findPreference("metric");
        mFahrenheitSwitchPreference = findPreference("fahrenheit");
        mDeleteAllBookmarksPreferenceScreen = findPreference("bookmarks");

        mMetricSwitchPreference.setOnPreferenceClickListener(mMetricSwitchPreferenceClickListener);
        mFahrenheitSwitchPreference.setOnPreferenceClickListener(mFahrenheitSwitchPreferenceClickListener);
        mDeleteAllBookmarksPreferenceScreen.setOnPreferenceClickListener(mDeleteAllBookmarksPreferenceScreenClickListener);
    }
}
