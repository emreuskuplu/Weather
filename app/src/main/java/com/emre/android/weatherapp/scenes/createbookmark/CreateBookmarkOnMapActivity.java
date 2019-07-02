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

package com.emre.android.weatherapp.scenes.createbookmark;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.emre.android.weatherapp.scenes.main.MainFragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.List;

/**
 * @author Emre Üsküplü
 *
 * Sends fragment of locator location on map fragment
 */
public class CreateBookmarkOnMapActivity extends MainFragmentActivity {
    private static final String TAG = CreateBookmarkOnMapActivity.class.getSimpleName();

    private static final int REQUEST_ERROR = 0;

    @Override
    protected Fragment createSingleFragment() {
        return CreateBookmarkOnMapFragment.newInstance();
    }

    @Override
    protected List<Fragment> createUserAndBookmarkListWeatherFragment() {
        return null;
    }

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, CreateBookmarkOnMapActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checksAvailabilityGooglePlayService();
    }

    private void checksAvailabilityGooglePlayService() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = apiAvailability
                    .getErrorDialog(this, resultCode, REQUEST_ERROR);
            errorDialog.show();
        }
    }
}
