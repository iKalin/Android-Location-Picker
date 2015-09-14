package com.example.ultron.androidlocationpicker;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import com.google.android.gms.maps.model.LatLng;
import java.io.IOException;
import java.util.List;

public class ReverseGeocodingTask extends AsyncTask<LatLng, Void, Address> {
    private final Context mContext;

    public ReverseGeocodingTask(Context context) {
        mContext = context;
    }

    @Override
    protected Address doInBackground(LatLng... params) {
        LatLng coordinate = params[0];
        // Creating an instance of Geocoder class
        Geocoder geocoder = new Geocoder(mContext);
        try {
            List<Address> addresses = geocoder.getFromLocation(coordinate.latitude, coordinate.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
