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

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.emre.android.weatherapp.scenes.main.MainFragmentActivity;

import java.util.List;

/**
 * @author Emre Üsküplü
 *
 * Sends fragment of settings fragment to main fragment activity
 */
public class SettingsActivity extends MainFragmentActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected Fragment createSingleFragment() {
        return SettingsFragment.newInstance();
    }

    @Override
    protected List<Fragment> createUserAndBookmarkListWeatherFragment() {
        return null;
    }
}
