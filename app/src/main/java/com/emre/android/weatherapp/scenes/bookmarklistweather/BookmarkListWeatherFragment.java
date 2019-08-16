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
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.BookmarkWeatherDTO;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.BookmarkWeatherDTOListSafe;
import com.emre.android.weatherapp.scenes.bookmarklistweather.workerthread.BookmarkListWeatherTask;
import com.emre.android.weatherapp.scenes.detailedweather.DetailedBookmarkListWeatherActivity;
import com.emre.android.weatherapp.scenes.INetworkStatus;
import com.emre.android.weatherapp.scenes.IRefreshWeather;
import com.emre.android.weatherapp.scenes.bookmarkmap.CreateBookmarkOnMapActivity;
import com.emre.android.weatherapp.scenes.mainweather.MainWeatherActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emre Üsküplü
 *
 * Shows weather of bookmark list
 * When user clicks add bookmark button then starts create bookmark on map activity
 * If user network connection is available then get weather from api
 * Search bookmarks with location name
 * Delete bookmarks with swipe to right
 */
public class BookmarkListWeatherFragment extends Fragment implements IUpdateBookmarkListWeather, IRefreshWeather {
    private static final String TAG = BookmarkListWeatherFragment.class.getSimpleName();

    private static List<LocationDTO> sLocationDTOList = new ArrayList<>();
    private static List<BookmarkWeatherDTO> sBookmarkWeatherDTOList = new ArrayList<>();

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

            List<BookmarkWeatherDTO> bookmarkWeatherDTOList = new ArrayList<>();

            for (BookmarkWeatherDTO bookmarkWeatherDTO : sBookmarkWeatherDTOList) {
                if (query.equalsIgnoreCase(bookmarkWeatherDTO.getLocationName())) {
                    bookmarkWeatherDTOList.add(bookmarkWeatherDTO);
                }
            }

            if (bookmarkWeatherDTOList.isEmpty()) {
                mAddBookmarkInfoMessage.setVisibility(View.GONE);
                mBookmarkNotFoundMessage.setVisibility(View.VISIBLE);
            } else {
                mBookmarkNotFoundMessage.setVisibility(View.GONE);
            }

            updateBookmarkListWeather(bookmarkWeatherDTOList);
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
                    BookmarkWeatherDTO bookmarkWeatherDTO = sBookmarkWeatherDTOList.get(position);
                    String locationName = bookmarkWeatherDTO.getLocationName();

                    showDeletedBookmarkToast(locationName);

                    mLocationDAO.
                            locationDbDeleteLocationData(bookmarkWeatherDTO.getLocationDTOId());

                    sBookmarkWeatherDTOList.remove(position);
                    mWeatherAdapter.notifyDataSetChanged();

                    if (sBookmarkWeatherDTOList.size() == 0 && mBookmarkListWeatherSearchView.getQuery().length() < 1) {
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

    public static List<BookmarkWeatherDTO> getBookmarkWeatherDTOList() {
        return sBookmarkWeatherDTOList;
    }

    private void showDeletedBookmarkToast(String locationName) {
        Toast.makeText(getContext(),
                getString(R.string.location_deleted_message, locationName),
                Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * Gets location values from database
     * Executes bookmark list weather task
     */
    private void executeWeatherListTask() {
        List<LocationDTO> locationDTOList = mLocationDAO.locationDbExtract();
        sLocationDTOList = locationDTOList;

        BookmarkWeatherDTOListSafe bookmarkWeatherDTOListSafe = getBookmarkWeatherDTOList(locationDTOList);

        if (locationDTOList.isEmpty()) {
            mBookmarkNotFoundMessage.setVisibility(View.GONE);
            mAddBookmarkInfoMessage.setVisibility(View.VISIBLE);
        } else {
            mBookmarkListWeatherProgressBar.setVisibility(View.VISIBLE);
            mAddBookmarkInfoMessage.setVisibility(View.GONE);
            mBookmarkNotFoundMessage.setVisibility(View.GONE);
        }

        new BookmarkListWeatherTask(requireContext(),
                this).execute(bookmarkWeatherDTOListSafe);
    }

    /**
     * Sets location values to bookmark list
     * Sets bookmark list to class for prevent possible heap pollution in async task
     * @param locationDTOList the location values from database
     * @return bookmark list that contain location values
     */
    private BookmarkWeatherDTOListSafe getBookmarkWeatherDTOList(List<LocationDTO> locationDTOList) {
        BookmarkWeatherDTOListSafe bookmarkWeatherDTOListSafe = new BookmarkWeatherDTOListSafe();
        List<BookmarkWeatherDTO> bookmarkWeatherDTOList= new ArrayList<>();

        for (LocationDTO locationDTO : locationDTOList) {
            BookmarkWeatherDTO bookmarkWeatherDTO = new BookmarkWeatherDTO(
                    locationDTO.getId(),
                    locationDTO.getLatitude(),
                    locationDTO.getLongitude());

            bookmarkWeatherDTOList.add(bookmarkWeatherDTO);
        }

        bookmarkWeatherDTOListSafe.setBookmarkWeatherDTOList(bookmarkWeatherDTOList);

        return bookmarkWeatherDTOListSafe;
    }

    private void updateListItemWeatherImage(BookmarkWeatherDTO bookmarkWeatherDTO,
                                            ImageView weatherImageView, CardView weatherCardView) {
        ConstraintLayout weatherCardViewLayout = weatherCardView.findViewById(R.id.weather_card_view_layout);
        String mainDescription = bookmarkWeatherDTO.getMainDescription();

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
                if (bookmarkWeatherDTO.getMainDescription().equals(atmosphereDescription)) {
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
    public void updateBookmarkListWeather(List<BookmarkWeatherDTO> bookmarkWeatherDTOList) {
        if (isAdded()) {
            sBookmarkWeatherDTOList = bookmarkWeatherDTOList;

            mWeatherAdapter = new WeatherAdapter(bookmarkWeatherDTOList);
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

        private void bind(BookmarkWeatherDTO bookmarkWeatherDTO) {
            String tempDegree =
                    getString(R.string.temp_degree, bookmarkWeatherDTO.getTempDegree(), mUnitsFormat);

            locationNameTextView.setText(bookmarkWeatherDTO.getLocationName());
            tempDegreeTextView.setText(tempDegree);

            if (bookmarkWeatherDTO.getMainDescription() != null && bookmarkWeatherDTO.getDescription() != null) {
                updateListItemWeatherImage(bookmarkWeatherDTO, weatherImageView, weatherCardView);
            }
        }

        @Override
        public void onClick(View view) {
            List<Location> locationList = new ArrayList<>();

            for (BookmarkWeatherDTO bookmarkWeatherDTO : sBookmarkWeatherDTOList) {
                Location location = new Location("");
                location.setLatitude(bookmarkWeatherDTO.getLocationDTOLatitude());
                location.setLongitude(bookmarkWeatherDTO.getLocationDTOLongitude());
                locationList.add(location);
            }

            Intent intent = DetailedBookmarkListWeatherActivity.newIntent(getActivity(),
                    locationList, getAdapterPosition());

            startActivity(intent);
        }
    }

    private class WeatherAdapter extends RecyclerView.Adapter<WeatherHolder> {
        private List<BookmarkWeatherDTO> mBookmarkWeatherDTOList;

        private WeatherAdapter(List<BookmarkWeatherDTO> mBookmarkWeatherDTOList) {
            this.mBookmarkWeatherDTOList = mBookmarkWeatherDTOList;
        }

        @NonNull
        @Override
        public WeatherHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new WeatherHolder(layoutInflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull WeatherHolder weatherHolder, int position) {
            BookmarkWeatherDTO bookmarkWeatherDTO = mBookmarkWeatherDTOList.get(position);
            weatherHolder.bind(bookmarkWeatherDTO);
        }

        @Override
        public int getItemCount() {
            return mBookmarkWeatherDTOList.size();
        }
    }
}
