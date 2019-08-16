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
package com.emre.android.weatherapp.webservice.weather.deserializer;

import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.BookmarkWeatherDTO;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * @author Emre Üsküplü
 *
 * Gets weather values with deserializer
 */
public class BookmarkWeatherDeserializer implements JsonDeserializer<BookmarkWeatherDTO> {
    private BookmarkWeatherDTO mBookmarkWeatherDTO;

    public BookmarkWeatherDeserializer(BookmarkWeatherDTO bookmarkWeatherDTO) {
        mBookmarkWeatherDTO = bookmarkWeatherDTO;
    }

    @Override
    public BookmarkWeatherDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json != null && json.isJsonObject() && !json.isJsonNull()) {
            JsonObject jsonObject = json.getAsJsonObject();

            if (jsonObject.get("name") != null && jsonObject.get("name").isJsonPrimitive() && !jsonObject.get("name").isJsonNull()) {
                String name = jsonObject.get("name").getAsString();
                mBookmarkWeatherDTO.setLocationName(name);
            }

            if (jsonObject.get("weather") != null && jsonObject.get("weather").isJsonArray() && !jsonObject.get("weather").isJsonNull()) {
                JsonArray weather = jsonObject.get("weather").getAsJsonArray();

                if (weather.get(0) != null && weather.get(0).isJsonObject() && !weather.get(0).isJsonNull()) {
                    JsonObject weatherIndex = weather.get(0).getAsJsonObject();

                    if (weatherIndex.get("main") != null && weatherIndex.get("main").isJsonPrimitive() && !weatherIndex.get("main").isJsonNull()) {
                        String main = weatherIndex.get("main").getAsString();
                        mBookmarkWeatherDTO.setMainDescription(main);
                    }

                    if (weatherIndex.get("description") != null && weatherIndex.get("description").isJsonPrimitive() && !weatherIndex.get("description").isJsonNull()) {
                        String description = weatherIndex.get("description").getAsString();
                        mBookmarkWeatherDTO.setDescription(description);
                    }
                }
            }

            if (jsonObject.get("main") != null && jsonObject.get("main").isJsonObject() && !jsonObject.get("main").isJsonNull()) {
                JsonObject main = jsonObject.get("main").getAsJsonObject();

                if (main.get("temp") != null && main.get("temp").isJsonPrimitive() && !main.get("temp").isJsonNull()) {
                    String temp = Double.toString(main.get("temp").getAsDouble());
                    mBookmarkWeatherDTO.setTempDegree(temp);
                }
            }
        }

        return mBookmarkWeatherDTO;
    }
}
