package com.example.ultron.androidlocationpicker;

import java.io.Serializable;

public class LocationModel implements Serializable {

    private String mName;
    private String mAddress;
    private double mLongitude, mLatitude;

    public LocationModel(String name, String address, double longitude, double latitude) {
        mName = name;
        mAddress = address;
        mLongitude = longitude;
        mLatitude = latitude;
    }

    public String getName() {
        return mName;
    }

    public String getAddress() {
        return mAddress;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public double getLatitude() {
        return mLatitude;
    }
}
