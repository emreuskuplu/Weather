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

package com.emre.android.weatherapp.scenes.bookmarkmap;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.emre.android.weatherapp.R;
import com.emre.android.weatherapp.dataaccessobjects.locationdataaccess.LocationDAO;
import com.emre.android.weatherapp.datatransferobjects.locationdatatransfer.LocationDTO;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Emre Üsküplü
 *
 * Creates bookmark on map with the user long clicks of map
 * When user long cliks on map then get location on map and sets the location in database
 */
public class CreateBookmarkOnMapFragment extends Fragment {
    private static final String TAG = CreateBookmarkOnMapFragment.class.getSimpleName();

    private static LatLng sLatLng;

    private LocationDAO mLocationDAO;

    private GoogleMap mMap;
    private MapView mMapView;
    private LocatorOnLongClick mLocatorOnLongClick;
    private ImageButton mBackButton;

    private OnMapReadyCallback mOnMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.setOnMapLongClickListener(mLocatorOnLongClick);
            setBookmarksFromList();
        }
    };

    private View.OnClickListener mBackButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            requireActivity().finish();
        }
    };

    public static CreateBookmarkOnMapFragment newInstance() {
        return new CreateBookmarkOnMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocatorOnLongClick = new LocatorOnLongClick();
        mLocationDAO = new LocationDAO(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_locator_weather_on_map, viewGroup, false);

        mMapView = v.findViewById(R.id.map_view);
        mBackButton = v.findViewById(R.id.back_button);

        mBackButton.setOnClickListener(mBackButtonOnClickListener);

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(mOnMapReadyCallback);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    public static LatLng getLatLngFromLongClickedOnMap() {
        return sLatLng;
    }

    private void setBookmarksFromList() {
        List<LocationDTO> locationDTOList = mLocationDAO.locationDbExtract();
        List<LatLng> latLngList = new ArrayList<>();
        double latitude;
        double longitude;

        for (int i = 0; i < locationDTOList.size(); i++) {
            latitude = locationDTOList.get(i).getLatitude();
            longitude = locationDTOList.get(i).getLongitude();
            latLngList.add(new LatLng(latitude, longitude));
        }

        for (int i = 0; i < latLngList.size(); i++) {
            MarkerOptions bookmark = new MarkerOptions()
                    .position(latLngList.get(i))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

            mMap.addMarker(bookmark);
        }
    }

    private void showAddedBookmarkToast() {
        Toast.makeText(getContext(),
                R.string.location_added_message,
                Toast.LENGTH_SHORT)
                .show();
    }

    private class LocatorOnLongClick implements GoogleMap.OnMapLongClickListener {

        @Override
        public void onMapLongClick(LatLng latLng) {
            sLatLng = latLng;

            mLocationDAO.locationDbInserting(UUID.randomUUID(), latLng.latitude, latLng.longitude);
            Log.i(TAG, "New location is inserted to database (" +
                    latLng.latitude + " " + latLng.longitude + ")");

            MarkerOptions bookmark = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            mMap.addMarker(bookmark);

            showAddedBookmarkToast();
        }
    }
}
