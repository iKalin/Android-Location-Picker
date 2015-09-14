package com.example.ultron.androidlocationpicker;

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
        mName = String.format("%.2f, %.2f", latLng.latitude, latLng.longitude);
        mAddress = null;
        mLongitude = latLng.longitude;
        mLatitude = latLng.latitude;
    }

    public LocationModel(Address address, LatLng latLng) {
        List<String> addressLines = new ArrayList<>();
        for (int i = 0; i<= address.getMaxAddressLineIndex(); i++) {
            addressLines.add(i, address.getAddressLine(i));
        }
        String addressStr = TextUtils.join(", ", addressLines);
        mAddress = addressStr;
        mName = addressStr;
        mLongitude = latLng.longitude;
        mLatitude = latLng.latitude;
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
