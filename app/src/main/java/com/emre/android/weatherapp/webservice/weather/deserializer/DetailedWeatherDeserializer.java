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

import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.DetailedWeatherDTO;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Emre Üsküplü
 *
 * Gets weather values with deserializer
 */
public class DetailedWeatherDeserializer implements JsonDeserializer<List<DetailedWeatherDTO>> {
    @Override
    public List<DetailedWeatherDTO> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<DetailedWeatherDTO> weatherDTOList = new ArrayList<>();

        if (json != null && json.isJsonObject() && !json.isJsonNull()) {
            JsonObject jsonObject = json.getAsJsonObject();

            int indexPosition = 0;

            for (int i = 0; i < 5; i++) {
                DetailedWeatherDTO detailedWeatherDTO = new DetailedWeatherDTO();

                if (jsonObject.get("city") != null && jsonObject.get("city").isJsonObject() && !jsonObject.get("city").isJsonNull()) {
                    JsonObject city = jsonObject.get("city").getAsJsonObject();

                    if (city.get("name") != null && city.get("name").isJsonPrimitive() && !city.get("name").isJsonNull()) {
                        String name = city.get("name").getAsString();
                        detailedWeatherDTO.setLocationName(name);
                    }
                }

                if (jsonObject.get("list") != null && jsonObject.get("list").isJsonArray() && !jsonObject.get("list").isJsonNull()) {
                    JsonArray list = jsonObject.get("list").getAsJsonArray();

                    if (list.get(indexPosition) != null && list.get(indexPosition).isJsonObject() && !list.get(indexPosition).isJsonNull()) {
                        JsonObject listIndex = list.get(indexPosition).getAsJsonObject();

                        if (listIndex.get("main") != null && listIndex.get("main").isJsonObject() && !listIndex.get("main").isJsonNull()) {
                            JsonObject main = listIndex.get("main").getAsJsonObject();

                            if (main.get("temp") != null && main.get("temp").isJsonPrimitive() && !main.get("temp").isJsonNull() ) {
                                String temp = Double.toString(main.get("temp").getAsDouble());
                                detailedWeatherDTO.setTempDegree(temp);
                            }

                            if (main.get("humidity") != null && main.get("humidity").isJsonPrimitive() && !main.get("humidity").isJsonNull()) {
                                String humidity = Double.toString(main.get("humidity").getAsDouble());
                                detailedWeatherDTO.setHumidity(humidity);
                            }
                        }

                        if (listIndex.get("weather") != null && listIndex.get("weather").isJsonArray() && !listIndex.get("weather").isJsonNull()) {
                            JsonArray weather = listIndex.get("weather").getAsJsonArray();

                            if (weather.get(0) != null && weather.get(0).isJsonObject() && !weather.get(0).isJsonNull()) {
                                JsonObject weatherIndex = weather.get(0).getAsJsonObject();

                                if (weatherIndex.get("main") != null && weatherIndex.get("main").isJsonPrimitive() && !weatherIndex.get("main").isJsonNull()) {
                                    String main = weatherIndex.get("main").getAsString();
                                    detailedWeatherDTO.setMainDescription(main);
                                }

                                if (weatherIndex.get("description") != null && weatherIndex.get("description").isJsonPrimitive() && !weatherIndex.get("description").isJsonNull()) {
                                    String description = weatherIndex.get("description").getAsString();
                                    detailedWeatherDTO.setDescription(description);
                                }
                            }
                        }

                        if (listIndex.get("wind") != null && listIndex.get("wind").isJsonObject() && !listIndex.get("wind").isJsonNull()) {
                            JsonObject wind = listIndex.get("wind").getAsJsonObject();

                            if (wind.get("speed") != null && wind.get("speed").isJsonPrimitive() && !wind.get("speed").isJsonNull()) {
                                String speed = Double.toString(wind.get("speed").getAsDouble());
                                detailedWeatherDTO.setWindVolume(speed);
                            }
                        }

                        if (listIndex.get("rain") != null && listIndex.get("rain").isJsonObject() && !listIndex.get("rain").isJsonNull()) {
                            JsonObject rain = listIndex.get("rain").getAsJsonObject();

                            if (rain.get("3h") != null && rain.get("3h").isJsonPrimitive() && !rain.get("3h").isJsonNull()) {
                                String threeH = Double.toString(rain.get("3h").getAsDouble());
                                detailedWeatherDTO.setRainVolume(threeH);
                            }
                        }

                        if (listIndex.get("snow") != null && listIndex.get("snow").isJsonObject() && !listIndex.get("snow").isJsonNull()) {
                            JsonObject snow = listIndex.get("snow").getAsJsonObject();

                            if (snow.get("3h") != null && snow.get("3h").isJsonPrimitive() && !snow.get("3h").isJsonNull()) {
                                String threeH = Double.toHexString(snow.get("3h").getAsDouble());
                                detailedWeatherDTO.setSnowVolume(threeH);
                            }
                        }

                        if (listIndex.get("dt_txt") != null && listIndex.get("dt_txt").isJsonPrimitive() && !listIndex.get("dt_txt").isJsonNull()) {
                            String dt_txt = listIndex.get("dt_txt").getAsString();
                            detailedWeatherDTO.setDate(dt_txt);
                        }
                    }
                }

                weatherDTOList.add(detailedWeatherDTO);
                indexPosition += 8;
            }
        }

        return weatherDTOList;
    }
}
