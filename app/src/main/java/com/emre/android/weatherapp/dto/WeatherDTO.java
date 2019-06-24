package com.emre.android.weatherapp.dto;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class WeatherDTO {
    private static final String TAG = WeatherDTO.class.getSimpleName();

    private UUID mLocationDTOId;
    private double mLocationDTOLatitude;
    private double mLocationDTOLongitude;
    private String mLocationName = "";
    private String mMainDescription = "";
    private String mTempDegree = "";
    private String mDescription = "";
    private String mHumidity = "0";
    private String mWindVolume = "0";
    private String mRainVolume = "0";
    private String mSnowVolume = "0";
    private String mDate = "";

    public UUID getLocationDTOId() {
        return mLocationDTOId;
    }

    public void setLocationDTOId(UUID mLocationDataID) {
        this.mLocationDTOId = mLocationDataID;
    }

    public double getLocationDTOLatitude() {
        return mLocationDTOLatitude;
    }

    public void setLocationDTOLatitude(double locationDTOLatitude) {
        mLocationDTOLatitude = locationDTOLatitude;
    }

    public double getLocationDTOLongitude() {
        return mLocationDTOLongitude;
    }

    public void setLocationDTOLongitude(double locationDTOLongitude) {
        mLocationDTOLongitude = locationDTOLongitude;
    }

    public String getLocationName() {
        return mLocationName;
    }

    public void setLocationName(String locationName) {
        mLocationName = locationName;
    }

    public String getMainDescription() {
        return mMainDescription;
    }

    public void setMainDescription(String mainDescription) {
        mMainDescription = mainDescription;
    }

    public String getTempDegree() {
        return mTempDegree;
    }

    public void setTempDegree(String tempDegree) {
        float fTempDegree = Float.parseFloat(tempDegree);
        int iTempDegree = Math.round(fTempDegree);
        mTempDegree = Integer.toString(iTempDegree);
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getHumidity() {
        return mHumidity;
    }

    public void setHumidity(String humidity) {
        float fHumidity = Float.parseFloat(humidity);
        int iHumidity = Math.round(fHumidity);
        mHumidity = Integer.toString(iHumidity);
    }

    public String getWindVolume() {
        return mWindVolume;
    }

    public void setWindVolume(String windVolume) {
        float fWindVolume = Float.parseFloat(windVolume);
        int iWindVolume = Math.round(fWindVolume);
        mWindVolume = Integer.toString(iWindVolume);
    }

    public String getRainVolume() {
        return mRainVolume;
    }

    public void setRainVolume(String rainVolume) {
        float fRainVolume = Float.parseFloat(rainVolume);
        int iRainVolume = Math.round(fRainVolume);
        mRainVolume = Integer.toString(iRainVolume);
    }

    public String getSnowVolume() {
        return mSnowVolume;
    }

    public void setSnowVolume(String snowVolume) {
        float fSnowVolume = Float.parseFloat(snowVolume);
        int iSnowVolume = Math.round(fSnowVolume);
        mSnowVolume = Integer.toString(iSnowVolume);
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String dateString) {
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = inFormat.parse(dateString);
            SimpleDateFormat outFormat = new SimpleDateFormat("EE", Locale.getDefault());
            mDate = outFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public String toString() {
        return mLocationName + " " + mMainDescription + " " + mTempDegree + " "
                + mDescription + " " + mHumidity + " " + mWindVolume + " " + mRainVolume + " "
                + mSnowVolume + " " + mDate;
    }
}
