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

package com.emre.android.weatherapp.scenes;

/**
 * @author Emre Üsküplü
 *
 * Checks network connection of device
 * If there is no network connection then shows network alert message
 */
public interface INetworkStatus {
    /**
     * Checks network connection of device
     * @return whether or not device connection is online
     */
    boolean isOnlineNetworkConnection();

    /**
     * If there is no network connection then shows network alert message
     */
    void showOfflineNetworkAlertMessage();
}
