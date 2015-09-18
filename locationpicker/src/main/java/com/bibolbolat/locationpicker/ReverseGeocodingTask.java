package com.bibolbolat.locationpicker;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.text.TextUtils;
import com.google.android.gms.maps.model.LatLng;
import java.io.IOException;
import java.util.ArrayList;
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
                Address address = addresses.get(0);

                List<String> addressLines = new ArrayList<>();
                for (int i = 0; i<= address.getMaxAddressLineIndex(); i++) {
                    addressLines.add(i, address.getAddressLine(i));
                }
                String addressStr = TextUtils.join(", ", addressLines);

                return new LocationModel(addressStr, addressStr,
                                         address.getLongitude(), address.getLatitude());
            } else {
                return new LocationModel(coordinate);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new LocationModel(coordinate);
        }
    }
}
