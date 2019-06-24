package com.emre.android.weatherapp.ui;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.emre.android.weatherapp.R;
import com.emre.android.weatherapp.dao.SettingsDAO;
import com.emre.android.weatherapp.ui.dialog.DeleteAllBookmarksDialogFragment;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = SettingsFragment.class.getSimpleName();

    private static final String DIALOG_DELETE_ALL_BOOKMARKS = "DeleteAllBookmarks";

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey);

        final SettingsDAO settingsDAO = new SettingsDAO();
        final SwitchPreference metricSwitchPreference = findPreference("metric");
        final SwitchPreference fahrenheitSwitchPreference = findPreference("fahrenheit");
        PreferenceScreen deleteAllBookmarksPreferenceScreen = findPreference("bookmarks");

        if (metricSwitchPreference != null && fahrenheitSwitchPreference != null) {
            metricSwitchPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (metricSwitchPreference.isChecked()) {
                        settingsDAO.setPrefUnitsFormatStorage(requireContext(), "metric");

                        fahrenheitSwitchPreference.setChecked(false);

                    } else if (!metricSwitchPreference.isChecked()) {
                        fahrenheitSwitchPreference.setChecked(true);
                        settingsDAO.setPrefUnitsFormatStorage(requireContext(), "fahrenheit");
                    }

                    return true;
                }
            });
        }

        if (fahrenheitSwitchPreference != null && metricSwitchPreference != null) {
            fahrenheitSwitchPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (fahrenheitSwitchPreference.isChecked()) {
                        settingsDAO.setPrefUnitsFormatStorage(requireContext(), "fahrenheit");

                        metricSwitchPreference.setChecked(false);

                    } else if (!fahrenheitSwitchPreference.isChecked()) {
                        metricSwitchPreference.setChecked(true);
                        settingsDAO.setPrefUnitsFormatStorage(requireContext(), "metric");
                    }

                    return true;
                }
            });
        }

        if (deleteAllBookmarksPreferenceScreen != null) {
            deleteAllBookmarksPreferenceScreen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    FragmentManager manager = getChildFragmentManager();

                    DeleteAllBookmarksDialogFragment dialogFragment = DeleteAllBookmarksDialogFragment.newInstance();

                    dialogFragment.show(manager, DIALOG_DELETE_ALL_BOOKMARKS);
                    return true;
                }
            });
        }
    }
}
