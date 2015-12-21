package com.almareng.earthquakemonitor.list;

import android.os.Parcel;
import android.os.Parcelable;

public class Earthquake implements Parcelable {
    private final Double mMagnitude;
    private final String mPlace;
    private final String mTimeAndDate;
    private final String mLatitude;
    private final String mLongitude;
    private final String mDepth;
    private final String mDistanceToEpicenter;

    public Earthquake(final Double mMagnitude, final String mPlace, final String mTimeAndDate, final String mLongitude,
                      final String mLatitude, final String mDepth, final String mDistanceToEpicenter) {
        this.mMagnitude = mMagnitude;
        this.mPlace = mPlace;
        this.mTimeAndDate = mTimeAndDate;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mDepth = mDepth;
        this.mDistanceToEpicenter = mDistanceToEpicenter;
    }

    public Double getMagnitude() {
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

    public String getDistanceToEpicenter() {
        return mDistanceToEpicenter;
    }

    protected Earthquake(Parcel in) {
        mMagnitude = in.readDouble();
        mPlace = in.readString();
        mTimeAndDate = in.readString();
        mLatitude = in.readString();
        mLongitude = in.readString();
        mDepth = in.readString();
        mDistanceToEpicenter = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mMagnitude);
        dest.writeString(mPlace);
        dest.writeString(mTimeAndDate);
        dest.writeString(mLatitude);
        dest.writeString(mLongitude);
        dest.writeString(mDepth);
        dest.writeString(mDistanceToEpicenter);
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
