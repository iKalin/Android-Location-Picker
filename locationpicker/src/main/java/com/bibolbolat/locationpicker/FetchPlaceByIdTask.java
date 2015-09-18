package com.bibolbolat.locationpicker;

import android.os.AsyncTask;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

public class FetchPlaceByIdTask extends AsyncTask<String, Void, LocationModel> {
    private final GoogleApiClient mGoogleApiClient;

    public FetchPlaceByIdTask(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }

    @Override
    protected LocationModel doInBackground(String... params) {
        String placeId = params[0];
        PendingResult<PlaceBuffer> pendingResult = Places.GeoDataApi
                .getPlaceById(mGoogleApiClient, placeId);
        PlaceBuffer placeBuffer = pendingResult.await();

        LocationModel location = null;
        if (placeBuffer.getStatus().isSuccess() && placeBuffer.getCount() > 0) {
            Place place = placeBuffer.get(0);
            location = new LocationModel((String) place.getName(), (String) place.getAddress(), place.getLatLng());
        }
        placeBuffer.release();

        return location;
    }
}
