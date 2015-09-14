package com.example.ultron.androidlocationpicker;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;
import java.util.ArrayList;
import java.util.List;

public class AutocompleteSuggestionsTask extends AsyncTask<String, Void, List<AutocompleteItem>> {
    private final GoogleApiClient mGoogleApiClient;
    private final LatLngBounds mLatLngBounds;

    @Nullable private final AutocompleteFilter mAutocompleteFilter;

    public AutocompleteSuggestionsTask(GoogleApiClient googleApiClient, LatLngBounds latLngBounds, AutocompleteFilter autocompleteFilter) {
        mGoogleApiClient = googleApiClient;
        mLatLngBounds = latLngBounds;
        mAutocompleteFilter = autocompleteFilter;
    }

    @Override
    protected List<AutocompleteItem> doInBackground(String... params) {
        String query = params[0];
        PendingResult<AutocompletePredictionBuffer> results = Places.GeoDataApi
                .getAutocompletePredictions(mGoogleApiClient, query,
                                            mLatLngBounds, mAutocompleteFilter);
        AutocompletePredictionBuffer buffer = results.await();

        ArrayList<AutocompleteItem> items = new ArrayList<>();
        for (AutocompletePrediction prediction : buffer) {
            items.add(new AutocompleteItem(prediction.getDescription(), prediction.getPlaceId()));
        }

        return items;
    }
}
