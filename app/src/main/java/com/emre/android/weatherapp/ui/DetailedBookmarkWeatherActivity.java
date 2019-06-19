package com.emre.android.weatherapp.ui;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.emre.android.weatherapp.R;

import java.util.ArrayList;
import java.util.List;

public class DetailedBookmarkWeatherActivity extends AppCompatActivity {
    private static final String TAG = DetailedBookmarkWeatherActivity.class.getSimpleName();

    private static final String EXTRA_LIST_LOCATION_DATA_ACTIVITY =
            "com.emre.android.weatherapp.location";
    private static final String EXTRA_POSITION =
            "com.emre.android.weatherapp.position";

    private List<Location> mLocationList;
    private ImageView mChevronLeft;
    private ImageView mChevronRight;

    public static Intent newIntent(Context packageContext, List<Location> locationList, int position) {
        Intent intent = new Intent(packageContext, DetailedBookmarkWeatherActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_LIST_LOCATION_DATA_ACTIVITY, (ArrayList<? extends Parcelable>) locationList);
        intent.putExtra(EXTRA_POSITION, position);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detailed_bookmark_weather);

        mChevronLeft = findViewById(R.id.chevron_left);
        mChevronRight = findViewById(R.id.chevron_right);

        mLocationList = getIntent().getParcelableArrayListExtra(EXTRA_LIST_LOCATION_DATA_ACTIVITY);
        int position = getIntent().getIntExtra(EXTRA_POSITION, 0);

        final ViewPager viewPager = findViewById(R.id.detailed_weather_view_pager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                Log.i(TAG, "Position: " + i);

                if (viewPager.getCurrentItem() == 0) {
                    mChevronLeft.setVisibility(View.GONE);
                    mChevronRight.setVisibility(View.VISIBLE);
                } else if (viewPager.getCurrentItem() == mLocationList.size() - 1) {
                    mChevronLeft.setVisibility(View.VISIBLE);
                    mChevronRight.setVisibility(View.GONE);
                }

                Location location = mLocationList.get(i);
                return DetailedWeatherFragment.newInstance(location);
            }

            @Override
            public int getCount() {
                return mLocationList.size();
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        });

        viewPager.setCurrentItem(position);
    }
}
