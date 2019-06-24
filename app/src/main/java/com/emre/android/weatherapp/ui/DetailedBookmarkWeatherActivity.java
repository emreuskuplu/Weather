package com.emre.android.weatherapp.ui;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
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

import java.util.ArrayList;
import java.util.List;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class DetailedBookmarkWeatherActivity extends AppCompatActivity implements INetworkMessage {
    private static final String TAG = DetailedBookmarkWeatherActivity.class.getSimpleName();

    private static final String EXTRA_LIST_LOCATION_DATA_ACTIVITY =
            "com.emre.android.weatherapp.location";
    private static final String EXTRA_POSITION =
            "com.emre.android.weatherapp.position";

    private List<Location> mLocationList;
    private ImageView mChevronLeft;
    private ImageView mChevronRight;
    private Toast mOfflineNetworkToast;

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

        mOfflineNetworkToast = Toast.makeText(this,
                R.string.offline_network_alert_message,
                Toast.LENGTH_LONG);

        mChevronLeft = findViewById(R.id.chevron_left);
        mChevronRight = findViewById(R.id.chevron_right);

        mLocationList = getIntent().getParcelableArrayListExtra(EXTRA_LIST_LOCATION_DATA_ACTIVITY);
        int position = getIntent().getIntExtra(EXTRA_POSITION, 0);

        final ViewPager viewPager = findViewById(R.id.detailed_weather_view_pager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int i) {
                Log.i(TAG, "Position: " + i);

                if (viewPager.getCurrentItem() == 0) {
                    if (viewPager.getCurrentItem() == mLocationList.size() - 1) {
                        mChevronLeft.setVisibility(View.GONE);
                        mChevronRight.setVisibility(View.GONE);
                    } else {
                        mChevronLeft.setVisibility(View.GONE);
                        mChevronRight.setVisibility(View.VISIBLE);

                    }
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

    @Override
    public void showOfflineNetworkAlertMessage() {
        mOfflineNetworkToast.show();
    }
}
