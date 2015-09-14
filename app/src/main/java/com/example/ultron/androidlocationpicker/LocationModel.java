package com.example.ultron.androidlocationpicker;

import android.location.Address;
import android.text.TextUtils;
import com.google.android.gms.maps.model.LatLng;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LocationModel implements Serializable {
    private String mName;
    private String mAddress;
    private double mLongitude, mLatitude;

    public LocationModel(Address address, LatLng  latLng) {
        List<String> addressLines = new ArrayList<>();
        for (int i = 0; i<= address.getMaxAddressLineIndex(); i++) {
            addressLines.add(i, address.getAddressLine(i));
        }
        String addressStr = TextUtils.join(", ", addressLines);
        mAddress = addressStr;
        mName = address.getFeatureName() != null ? address.getFeatureName() : addressStr;
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
