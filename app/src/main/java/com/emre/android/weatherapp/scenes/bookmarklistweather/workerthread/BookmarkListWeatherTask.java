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

package com.emre.android.weatherapp.scenes.bookmarklistweather.workerthread;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.emre.android.weatherapp.dataaccessobjects.weatherdataaccess.WeatherDAO;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.BookmarkWeatherDTO;
import com.emre.android.weatherapp.datatransferobjects.weatherdatatransfer.BookmarkWeatherDTOListSafe;
import com.emre.android.weatherapp.scenes.bookmarklistweather.BookmarkListWeatherFragment;
import com.emre.android.weatherapp.scenes.bookmarklistweather.IUpdateBookmarkListWeather;

import java.util.List;

/**
 * @author Emre Üsküplü
 *
 * Executes task for fetch weather of bookmark list from api
 */
public class BookmarkListWeatherTask extends AsyncTask<BookmarkWeatherDTOListSafe, Void, List<BookmarkWeatherDTO>> {
    private static final String TAG = BookmarkListWeatherTask.class.getSimpleName();

    private Context mBookmarkListWeatherContext;
    private Fragment mBookmarkListWeatherFragment;

    public BookmarkListWeatherTask(Context bookmarkListWeatherContext, Fragment bookmarkListWeatherFragment) {
        Log.i(TAG, "is executing");

        mBookmarkListWeatherContext = bookmarkListWeatherContext;
        mBookmarkListWeatherFragment = bookmarkListWeatherFragment;
    }

    @Override
    protected List<BookmarkWeatherDTO> doInBackground(BookmarkWeatherDTOListSafe... bookmarkWeatherDTOListSafes) {
        List<BookmarkWeatherDTO> bookmarkWeatherDTOList = bookmarkWeatherDTOListSafes[0].getBookmarkWeatherDTOList();

        return new WeatherDAO(mBookmarkListWeatherContext).getBookmarkListWeather(bookmarkWeatherDTOList);
    }

    @Override
    protected void onPostExecute(List<BookmarkWeatherDTO> result) {
        Log.i(TAG, "is executed");

        IUpdateBookmarkListWeather iUpdateBookmarkListWeather = (BookmarkListWeatherFragment) mBookmarkListWeatherFragment;
        iUpdateBookmarkListWeather.updateBookmarkListWeather(result);
    }
}
