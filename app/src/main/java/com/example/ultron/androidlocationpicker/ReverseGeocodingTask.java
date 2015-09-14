package com.example.ultron.androidlocationpicker;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import java.io.IOException;
import java.util.List;

public class ReverseGeocodingTask extends AsyncTask<LatLng, Void, LocationModel> {
    private final Context mContext;

    public ReverseGeocodingTask(Context context) {
        mContext = context;
    }

    @Override
    protected LocationModel doInBackground(LatLng... params) {
        LatLng coordinate = params[0];
        // Creating an instance of Geocoder class
        Geocoder geocoder = new Geocoder(mContext);
        try {
            List<Address> addresses = geocoder.getFromLocation(coordinate.latitude, coordinate.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Log.d("LOCATION", addresses.get(0).toString());
                return new LocationModel(addresses.get(0), coordinate);
            } else {
                return new LocationModel(coordinate);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new LocationModel(coordinate);
        }
    }
}
