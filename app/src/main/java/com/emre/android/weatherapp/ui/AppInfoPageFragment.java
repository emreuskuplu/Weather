package com.emre.android.weatherapp.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.emre.android.weatherapp.R;

public class AppInfoPageFragment extends Fragment {

    public static AppInfoPageFragment newInstance() {
        return new AppInfoPageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_app_info_page, viewGroup, false);

        WebView webView = v.findViewById(R.id.web_view);
        webView.loadUrl("file:///android_asset/weather_app_info.html");

        return v;
    }

}
