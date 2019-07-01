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

package com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.weatherjsonschema;

import com.google.gson.annotations.SerializedName;

/**
 * @author Emre Üsküplü
 */
public class Rain {
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
