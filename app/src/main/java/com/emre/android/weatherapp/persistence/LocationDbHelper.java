package com.emre.android.weatherapp.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
