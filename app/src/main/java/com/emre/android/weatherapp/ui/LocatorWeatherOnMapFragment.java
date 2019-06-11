package com.emre.android.weatherapp.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.emre.android.weatherapp.R;
import com.emre.android.weatherapp.dao.LocationDAO;
import com.emre.android.weatherapp.dto.LocationDTO;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LocatorWeatherOnMapFragment extends Fragment {
    private static final String TAG = LocatorWeatherOnMapFragment.class.getSimpleName();

    private LocationDAO mLocationDAO;

    private GoogleMap mMap;
    protected MapView mMapView;
    private LocatorOnLongClick mLocatorOnLongClick;
    private static LatLng sLatLng;

    public static LocatorWeatherOnMapFragment newInstance() {
        return new LocatorWeatherOnMapFragment();
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
        ImageButton backButton = v.findViewById(R.id.back_button);

        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setOnMapLongClickListener(mLocatorOnLongClick);
                setBookmarksFromList();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().finish();
            }
        });

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

    public void setBookmarksFromList() {
        List<LocationDTO> locationDTOList = mLocationDAO.LocationDbExtract();
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

    public static LatLng getLatLngFromLongClickedOnMap() {
        return sLatLng;
    }

    public class LocatorOnLongClick implements GoogleMap.OnMapLongClickListener {

        @Override
        public void onMapLongClick(LatLng latLng) {
            sLatLng = latLng;

            mLocationDAO.LocationDbInserting(UUID.randomUUID(), latLng.latitude, latLng.longitude);
            Log.i(TAG, "New location is inserted to database (" +
                    latLng.latitude + " " + latLng.longitude + ")");

            MarkerOptions bookmark = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            mMap.addMarker(bookmark);

            Toast.makeText(getContext(),
                    R.string.location_added_message,
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
