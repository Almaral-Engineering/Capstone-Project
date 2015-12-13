package com.almareng.earthquakemonitor.EqListScreen;

public class Earthquake {
    private final String mMagnitude;
    private final String mPlace;
    private final String mTimeAndDate;
    private final String mLatitude;
    private final String mLongitude;
    private final String mDepth;

    public Earthquake(String mMagnitude, String mPlace, String mTimeAndDate, String mLatitude, String mLongitude,
                      String mDepth) {
        this.mMagnitude = mMagnitude;
        this.mPlace = mPlace;
        this.mTimeAndDate = mTimeAndDate;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mDepth = mDepth;
    }

    public String getMagnitude() {
        return mMagnitude;
    }

    public String getPlace() {
        return mPlace;
    }

    public String getTimeAndDate() {
        return mTimeAndDate;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public String getDepth() {
        return mDepth;
    }
}
