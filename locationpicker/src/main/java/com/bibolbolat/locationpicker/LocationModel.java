package com.bibolbolat.locationpicker;

import android.location.Address;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.maps.model.LatLng;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LocationModel implements Serializable {
    private String mName;
    @Nullable private String mAddress;
    private double mLongitude, mLatitude;

    public LocationModel(LatLng latLng) {
        this(String.format("%.2f, %.2f", latLng.latitude, latLng.longitude), null, latLng);
    }

    public LocationModel(String name, String address, LatLng latLng) {
        this(name, address, latLng.longitude, latLng.latitude);
    }

    public LocationModel(String name, String address, double longitude, double latitude) {
        mName = name;
        mAddress = address;
        mLongitude = longitude;
        mLatitude = latitude;
    }

    public String getName() {
        return mName;
    }

    @Nullable
    public String getAddress() {
        return mAddress;
    }

    public LatLng getLatLng() {
        return new LatLng(mLatitude, mLongitude);
    }

    public double getLongitude() {
        return mLongitude;
    }

    public double getLatitude() {
        return mLatitude;
    }
}
