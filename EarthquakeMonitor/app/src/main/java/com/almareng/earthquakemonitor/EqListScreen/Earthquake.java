package com.almareng.earthquakemonitor.EqListScreen;

import android.os.Parcel;
import android.os.Parcelable;

public class Earthquake implements Parcelable {
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

    protected Earthquake(Parcel in) {
        mMagnitude = in.readString();
        mPlace = in.readString();
        mTimeAndDate = in.readString();
        mLatitude = in.readString();
        mLongitude = in.readString();
        mDepth = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMagnitude);
        dest.writeString(mPlace);
        dest.writeString(mTimeAndDate);
        dest.writeString(mLatitude);
        dest.writeString(mLongitude);
        dest.writeString(mDepth);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Earthquake> CREATOR = new Parcelable.Creator<Earthquake>() {
        @Override
        public Earthquake createFromParcel(Parcel in) {
            return new Earthquake(in);
        }

        @Override
        public Earthquake[] newArray(int size) {
            return new Earthquake[size];
        }
    };
}
