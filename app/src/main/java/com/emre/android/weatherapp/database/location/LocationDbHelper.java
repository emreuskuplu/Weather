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

package com.emre.android.weatherapp.database.location;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Emre Üsküplü
 *
 * Creates location database
 */
public class LocationDbHelper extends SQLiteOpenHelper {
    private static final String TAG = LocationDbHelper.class.getSimpleName();

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "locationBase.db";

    public LocationDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + LocationDbSchema.LocationTable.NAME +
                "(" +
                "_id integer primary key autoincrement, " +
                LocationDbSchema.LocationTable.Cols.UUID + ", " +
                LocationDbSchema.LocationTable.Cols.LATITUDE + ", " +
                LocationDbSchema.LocationTable.Cols.LONGITUDE +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
