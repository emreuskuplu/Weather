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

package com.emre.android.weatherapp.scenes.detailedweather;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.emre.android.weatherapp.R;
import com.emre.android.weatherapp.scenes.INetworkStatus;

import java.util.ArrayList;
import java.util.List;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

/**
 * @author Emre Üsküplü
 *
 * Starts view pager of detailed weather for bookmark list weather
 */
public class DetailedBookmarkListWeatherActivity extends AppCompatActivity implements INetworkStatus {
    private static final String TAG = DetailedBookmarkListWeatherActivity.class.getSimpleName();

    private static final String EXTRA_LIST_LOCATION_DATA_ACTIVITY =
            "com.emre.android.weatherapp.location";
    private static final String EXTRA_POSITION =
            "com.emre.android.weatherapp.position";

    private List<Location> mLocationList;
    private ViewPager mViewPager;
    private Toast mOfflineNetworkToast;
    private ImageView mChevronLeft;
    private ImageView mChevronRight;

    private FragmentStatePagerAdapter mFragmentStatePagerAdapter =
            new FragmentStatePagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        @NonNull
        @Override
        public Fragment getItem(int position) {
            Log.i(TAG, "Position: " + position);

            if (mViewPager.getCurrentItem() == 0) {
                if (mViewPager.getCurrentItem() == mLocationList.size() - 1) {
                    mChevronLeft.setVisibility(View.GONE);
                    mChevronRight.setVisibility(View.GONE);
                } else {
                    mChevronLeft.setVisibility(View.GONE);
                    mChevronRight.setVisibility(View.VISIBLE);
                }
            }

            Location location = mLocationList.get(position);
            return DetailedWeatherFragment.newInstance(location);

        }

        @Override
        public int getCount() {
            return mLocationList.size();
        }
    };

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {
            Log.i(TAG, "onPageScrolled: " + i);
        }

        @Override
        public void onPageSelected(int i) {
            Log.i(TAG, "onPageSelected " + i);

            if (i < 1 && i != mLocationList.size() - 1) {
                mChevronLeft.setVisibility(View.GONE);
                mChevronRight.setVisibility(View.VISIBLE);
            } else if (i > 0 && i != mLocationList.size() - 1){
                mChevronLeft.setVisibility(View.VISIBLE);
                mChevronRight.setVisibility(View.VISIBLE);
            } else if (i > 0 && i == mLocationList.size() - 1) {
                mChevronLeft.setVisibility(View.VISIBLE);
                mChevronRight.setVisibility(View.GONE);
            } else if (i < 0 && i == mLocationList.size() - 1){
                mChevronLeft.setVisibility(View.GONE);
                mChevronRight.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {
            Log.i(TAG, "onPageScrollStateChanged: " + i);
        }

    };

    public static Intent newIntent(Context packageContext, List<Location> locationList, int position) {
        Intent intent = new Intent(packageContext, DetailedBookmarkListWeatherActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_LIST_LOCATION_DATA_ACTIVITY, (ArrayList<? extends Parcelable>) locationList);
        intent.putExtra(EXTRA_POSITION, position);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_bookmark_weather);

        int position = getIntent().getIntExtra(EXTRA_POSITION, 0);

        mLocationList = getIntent().getParcelableArrayListExtra(EXTRA_LIST_LOCATION_DATA_ACTIVITY);

        mChevronLeft = findViewById(R.id.chevron_left);
        mChevronRight = findViewById(R.id.chevron_right);

        mViewPager = findViewById(R.id.detailed_weather_view_pager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mFragmentStatePagerAdapter);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        mViewPager.setCurrentItem(position);

        mOfflineNetworkToast = Toast.makeText(this,
                R.string.offline_network_alert_message,
                Toast.LENGTH_LONG);
    }

    @Override
    public boolean isOnlineNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;

        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        } else {
            // If there is not default network then ignore isAvailableNetworkConnection condition
            Log.i(TAG, "There is not default network");
            return true;
        }

        return networkInfo != null && networkInfo.isConnected();

    }

    @Override
    public void showOfflineNetworkAlertMessage() {
        mOfflineNetworkToast.show();
    }
}
