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

package com.emre.android.weatherapp.scenes.appinfopage;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import com.emre.android.weatherapp.scenes.main.MainFragmentActivity;

import java.util.List;

/**
 * @author Emre Üsküplü
 *
 * Sends fragment of app info fragment to main fragment activity
 */
public class AppInfoPageActivity extends MainFragmentActivity {

    @Override
    protected Fragment createSingleFragment() {
        return AppInfoPageFragment.newInstance();
    }

    @Override
    protected List<Fragment> createUserAndBookmarkListWeatherFragment() {
        return null;
    }

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, AppInfoPageActivity.class);
    }
}
