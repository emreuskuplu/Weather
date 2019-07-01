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

package com.emre.android.weatherapp.scenes.bookmarklistweather;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.emre.android.weatherapp.R;
import com.emre.android.weatherapp.dataaccessobjects.locationdataaccess.LocationDAO;
import com.emre.android.weatherapp.dataaccessobjects.settingsdataaccess.SettingsDAO;
import com.emre.android.weatherapp.datatransferobjects.locationdatatransfer.LocationDTO;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.WeatherDTO;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.WeatherDTOListBookmark;
import com.emre.android.weatherapp.scenes.IUpdateWeather;
import com.emre.android.weatherapp.scenes.bookmarklistweather.workerthread.BookmarkListWeatherTask;
import com.emre.android.weatherapp.scenes.detailedweather.DetailedBookmarkListWeatherActivity;
import com.emre.android.weatherapp.scenes.INetworkStatus;
import com.emre.android.weatherapp.scenes.IRefreshWeather;
import com.emre.android.weatherapp.scenes.createbookmark.CreateBookmarkOnMapActivity;
import com.emre.android.weatherapp.scenes.mainweather.MainWeatherActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emre Üsküplü
 *
 * Shows weather of bookmark list
 * When user clicks add bookmark button then starts locator weather on map activity
 * If user network connection is available then get weather from api
 * Search bookmarks with location name
 * Delete bookmarks with swipe to right
 */
public class BookmarkListWeatherFragment extends Fragment implements IUpdateWeather, IRefreshWeather {
    private static final String TAG = BookmarkListWeatherFragment.class.getSimpleName();

    private static List<LocationDTO> sLocationDTOList = new ArrayList<>();
    private static List<WeatherDTO> sWeatherDTOList = new ArrayList<>();

    private String mUnitsFormat = "";
    private SettingsDAO mSettingsDAO;
    private LocationDAO mLocationDAO;
    private INetworkStatus mINetworkStatus;

    private RecyclerView mWeatherRecyclerView;
    private WeatherAdapter mWeatherAdapter;
    private ImageButton mAddBookmarkButton;
    private SearchView mBookmarkListWeatherSearchView;
    private TextView mAddBookmarkInfoMessage;
    private TextView mBookmarkNotFoundMessage;
    private ProgressBar mBookmarkListWeatherProgressBar;

    private View.OnClickListener mAddLocationButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = CreateBookmarkOnMapActivity.newIntent(getContext());
            startActivity(intent);
        }
    };

    private SearchView.OnQueryTextListener mOnQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            Log.i(TAG, "QueryTextSubmit: " + query);

            List<WeatherDTO> weatherDTOList = new ArrayList<>();

            for (WeatherDTO weatherDTO : sWeatherDTOList) {
                if (query.equalsIgnoreCase(weatherDTO.getLocationName())) {
                    weatherDTOList.add(weatherDTO);
                }
            }

            if (weatherDTOList.isEmpty()) {
                mAddBookmarkInfoMessage.setVisibility(View.GONE);
                mBookmarkNotFoundMessage.setVisibility(View.VISIBLE);
            } else {
                mBookmarkNotFoundMessage.setVisibility(View.GONE);
            }

            updateBookmarkListWeather(weatherDTOList);
            mBookmarkListWeatherSearchView.clearFocus();

            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    private ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(
            new ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView,
                                      @NonNull RecyclerView.ViewHolder viewHolder,
                                      @NonNull RecyclerView.ViewHolder viewHolder1) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                    int position = viewHolder.getAdapterPosition();
                    WeatherDTO weatherDTO = sWeatherDTOList.get(position);
                    String locationName = weatherDTO.getLocationName();

                    showDeletedBookmarkToast(locationName);

                    mLocationDAO.
                            LocationDbDeleteLocationData(weatherDTO.getLocationDTOId());

                    sWeatherDTOList.remove(position);
                    mWeatherAdapter.notifyDataSetChanged();

                    if (sWeatherDTOList.size() == 0 && mBookmarkListWeatherSearchView.getQuery().length() < 1) {
                        mAddBookmarkInfoMessage.setVisibility(View.VISIBLE);
                    }
                }
            }
    );

    public static BookmarkListWeatherFragment newInstance() {
        return new BookmarkListWeatherFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSettingsDAO = new SettingsDAO();
        mLocationDAO = new LocationDAO(getContext());
        mINetworkStatus = (MainWeatherActivity) requireActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bookmark_weather_list, viewGroup, false);

        mBookmarkListWeatherSearchView = v.findViewById(R.id.search_list_item);
        mAddBookmarkButton = v.findViewById(R.id.add_location_button);
        mWeatherRecyclerView = v.findViewById(R.id.weather_list_recycler_view);
        mAddBookmarkInfoMessage = v.findViewById(R.id.add_bookmark_info);
        mBookmarkNotFoundMessage = v.findViewById(R.id.query_not_found_message);
        mBookmarkListWeatherProgressBar = v.findViewById(R.id.weather_list_progress_bar);

        mAddBookmarkButton.setOnClickListener(mAddLocationButtonClickListener);
        mBookmarkListWeatherSearchView.setOnQueryTextListener(mOnQueryTextListener);
        mWeatherRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mItemTouchHelper.attachToRecyclerView(mWeatherRecyclerView);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        String units = mSettingsDAO.getPrefUnitsFormatStorage(requireContext());

        if (units.equals("metric")) {
            mUnitsFormat = "°C";
        } else if (units.equals("fahrenheit")) {
            mUnitsFormat = "°F";
        }

        if (mINetworkStatus.isOnlineNetworkConnection()) {
            executeWeatherListTask();
        } else {
            mINetworkStatus.showOfflineNetworkAlertMessage();
        }
    }

    public static List<LocationDTO> getLocationDTOList() {
        return sLocationDTOList;
    }

    public static List<WeatherDTO> getWeatherDTOList() {
        return sWeatherDTOList;
    }

    private void showDeletedBookmarkToast(String locationName) {
        Toast.makeText(getContext(),
                getString(R.string.location_deleted_message, locationName),
                Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * Gets location values from database
     * Executes bookmark list weather task with weather list
     */
    private void executeWeatherListTask() {
        List<LocationDTO> locationDTOList = mLocationDAO.LocationDbExtract();
        sLocationDTOList = locationDTOList;

        WeatherDTOListBookmark weatherDTOListBookmark = getWeatherDTOListBookmark(locationDTOList);

        if (locationDTOList.isEmpty()) {
            mBookmarkNotFoundMessage.setVisibility(View.GONE);
            mAddBookmarkInfoMessage.setVisibility(View.VISIBLE);
        } else {
            mBookmarkListWeatherProgressBar.setVisibility(View.VISIBLE);
            mAddBookmarkInfoMessage.setVisibility(View.GONE);
            mBookmarkNotFoundMessage.setVisibility(View.GONE);
        }

        new BookmarkListWeatherTask(requireContext(),
                this).execute(weatherDTOListBookmark);
    }

    private void updateBookmarkListWeather(List<WeatherDTO> weatherDTOList) {
        if (isAdded()) {
            sWeatherDTOList = weatherDTOList;

            mWeatherAdapter = new WeatherAdapter(weatherDTOList);
            mWeatherRecyclerView.setAdapter(mWeatherAdapter);

            mBookmarkListWeatherProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Sets location values to weather list
     * Sets weather list to class for prevent possible heap pollution in async task
     * @param locationDTOList the location values from database
     * @return weather list that contain location values
     */
    private WeatherDTOListBookmark getWeatherDTOListBookmark(List<LocationDTO> locationDTOList) {
        WeatherDTOListBookmark weatherDTOListBookmark = new WeatherDTOListBookmark();
        List<WeatherDTO> weatherDTOList = new ArrayList<>();

        for (LocationDTO locationDTO : locationDTOList) {
            WeatherDTO weatherDTO = new WeatherDTO();
            weatherDTO.setLocationDTOId(locationDTO.getId());
            weatherDTO.setLocationDTOLatitude(locationDTO.getLatitude());
            weatherDTO.setLocationDTOLongitude(locationDTO.getLongitude());
            weatherDTOList.add(weatherDTO);
        }

        weatherDTOListBookmark.setWeatherDTOList(weatherDTOList);

        return weatherDTOListBookmark;
    }

    private void updateListItemWeatherImage(WeatherDTO weatherDTO,
                                            ImageView weatherImageView, CardView weatherCardView) {
        ConstraintLayout weatherCardViewLayout = weatherCardView.findViewById(R.id.weather_card_view_layout);
        String mainDescription = weatherDTO.getMainDescription();

        if (mainDescription.equals(getString(R.string.clear))) {
            weatherImageView.setImageResource(R.drawable.sun_solid);
            weatherImageView.setContentDescription(getString(R.string.weather_image_is_sun));
            weatherCardViewLayout.setBackgroundResource(R.drawable.sun_background);

        } else if (mainDescription.equals(getString(R.string.clouds))) {
            if (mainDescription.equals(getString(R.string.few_clouds))) {
                weatherImageView.setImageResource(R.drawable.sunny_cloud_solid);
                weatherImageView.setContentDescription(getString(R.string.weather_image_is_sunny_cloud));
                weatherCardViewLayout.setBackgroundResource(R.drawable.sunny_cloud_background);
            } else {
                weatherImageView.setImageResource(R.drawable.cloud_solid);
                weatherImageView.setContentDescription(getString(R.string.weather_image_is_cloud));
                weatherCardViewLayout.setBackgroundResource(R.drawable.cloud_background);
            }

        } else if (mainDescription.equals(getString(R.string.drizzle))) {
            weatherImageView.setImageResource(R.drawable.rain_solid);
            weatherImageView.setContentDescription(getString(R.string.weather_image_is_rain));
            weatherCardViewLayout.setBackgroundResource(R.drawable.rain_background);

        } else if (mainDescription.equals(getString(R.string.rain))) {
            weatherImageView.setImageResource(R.drawable.rain_solid);
            weatherImageView.setContentDescription(getString(R.string.weather_image_is_rain));
            weatherCardViewLayout.setBackgroundResource(R.drawable.rain_background);

        } else if (mainDescription.equals(getString(R.string.thunderstorm))) {
            weatherImageView.setImageResource(R.drawable.thunderstorm_solid);
            weatherImageView.setContentDescription(getString(R.string.weather_image_is_thunderstorm));
            weatherCardViewLayout.setBackgroundResource(R.drawable.thunderstorm_background);

        } else if (mainDescription.equals(getString(R.string.snow))) {
            weatherImageView.setImageResource(R.drawable.snow_solid);
            weatherImageView.setContentDescription(getString(R.string.weather_image_is_snow));
            weatherCardViewLayout.setBackgroundResource(R.drawable.snow_background);

        } else {
            String[] atmosphereDescriptions = {getString(R.string.mist), getString(R.string.smoke), getString(R.string.haze),
                    getString(R.string.dust), getString(R.string.fog), getString(R.string.sand), getString(R.string.ash),
                    getString(R.string.squall), getString(R.string.tornado)};

            for (String atmosphereDescription : atmosphereDescriptions) {
                if (weatherDTO.getMainDescription().equals(atmosphereDescription)) {
                    weatherImageView.setImageResource(R.drawable.mist_solid);
                    weatherImageView.setContentDescription(getString(R.string.weather_image_is_mist));
                    weatherCardViewLayout.setBackgroundResource(R.drawable.mist_background);
                }
            }
        }
    }

    @Override
    public void refreshWeather() {
        if (mINetworkStatus.isOnlineNetworkConnection()) {
            executeWeatherListTask();
        } else {
            mINetworkStatus.showOfflineNetworkAlertMessage();
        }
    }

    @Override
    public void updateWeather(WeatherDTO weatherDTO) {

    }

    @Override
    public void updateListWeather(List<WeatherDTO> weatherDTOList) {
        if (isAdded()) {
            sWeatherDTOList = weatherDTOList;

            mWeatherAdapter = new WeatherAdapter(weatherDTOList);
            mWeatherRecyclerView.setAdapter(mWeatherAdapter);

            mBookmarkListWeatherProgressBar.setVisibility(View.GONE);
        }
    }

    private class WeatherHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private CardView weatherCardView;
        private TextView locationNameTextView;
        private ImageView weatherImageView;
        private TextView tempDegreeTextView;

        private WeatherHolder(@NonNull LayoutInflater inflater, ViewGroup viewGroup) {
            super(inflater.inflate(R.layout.list_item_weather, viewGroup, false));
            itemView.setOnClickListener(this);

            weatherCardView = itemView.findViewById(R.id.weather_card_view);
            locationNameTextView = weatherCardView.findViewById(R.id.list_item_location_name);
            weatherImageView = weatherCardView.findViewById(R.id.list_item_weather_image);
            tempDegreeTextView = weatherCardView.findViewById(R.id.list_item_temp_degree);
        }

        private void bind(WeatherDTO weatherDTO) {
            String tempDegree =
                    getString(R.string.temp_degree, weatherDTO.getTempDegree(), mUnitsFormat);

            locationNameTextView.setText(weatherDTO.getLocationName());
            tempDegreeTextView.setText(tempDegree);

            if (weatherDTO.getMainDescription() != null && weatherDTO.getDescription() != null) {
                updateListItemWeatherImage(weatherDTO, weatherImageView, weatherCardView);
            }
        }

        @Override
        public void onClick(View view) {
            List<Location> locationList = new ArrayList<>();

            for (WeatherDTO weatherDTO : sWeatherDTOList) {
                Location location = new Location("");
                location.setLatitude(weatherDTO.getLocationDTOLatitude());
                location.setLongitude(weatherDTO.getLocationDTOLongitude());
                locationList.add(location);
            }

            Intent intent = DetailedBookmarkListWeatherActivity.newIntent(getActivity(),
                    locationList, getAdapterPosition());

            startActivity(intent);
        }
    }

    private class WeatherAdapter extends RecyclerView.Adapter<WeatherHolder> {
        private List<WeatherDTO> weatherDTOList;

        private WeatherAdapter(List<WeatherDTO> weatherDTOList) {
            this.weatherDTOList = weatherDTOList;
        }

        @NonNull
        @Override
        public WeatherHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new WeatherHolder(layoutInflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull WeatherHolder weatherHolder, int position) {
            WeatherDTO weatherDTO = weatherDTOList.get(position);
            weatherHolder.bind(weatherDTO);
        }

        @Override
        public int getItemCount() {
            return weatherDTOList.size();
        }
    }
}
