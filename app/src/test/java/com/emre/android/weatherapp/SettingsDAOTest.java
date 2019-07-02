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

package com.emre.android.weatherapp;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.emre.android.weatherapp.dataaccessobjects.settingsdataaccess.ISettingsDAO;
import com.emre.android.weatherapp.dataaccessobjects.settingsdataaccess.SettingsDAO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Emre Üsküplü
 */
@RunWith(RobolectricTestRunner.class)
public class SettingsDAOTest {

    private Context mContext = ApplicationProvider.getApplicationContext();
    private ISettingsDAO mISettingsDAO;
    private String mExpectedUnitsFormat;

    @Test
    public void setsAndGetsUnitsFormatValuesInPrefUnitsFormatStorage() {
        givenSettingsDAOAndUnitsFormat();
        whenSetsAndGetsUnitsFormatValuesInPrefUnitsFormatStorage();
        thenVerifyTakenValuesInPrefUnitsFormatStorage();
    }

    private void givenSettingsDAOAndUnitsFormat() {
        mISettingsDAO = new SettingsDAO();
        mExpectedUnitsFormat = "metric";
    }

    private void whenSetsAndGetsUnitsFormatValuesInPrefUnitsFormatStorage() {
        mISettingsDAO.setPrefUnitsFormatStorage(mContext, "metric");
    }

    private void thenVerifyTakenValuesInPrefUnitsFormatStorage() {
        String actualUnitsFormat = mISettingsDAO.getPrefUnitsFormatStorage(mContext);

        assertThat(mExpectedUnitsFormat, is(equalTo(actualUnitsFormat)));
    }
}
