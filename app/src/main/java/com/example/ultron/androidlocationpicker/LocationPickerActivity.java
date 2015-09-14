package com.example.ultron.androidlocationpicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;

public class LocationPickerActivity extends AppCompatActivity implements GoogleMap.OnMapLongClickListener {
    public final String EXTRA_LOCATION = "selected-location";

    private SearchView mSearchView;

    private MapView mMapView;
    private GoogleMap mGoogleMap;

    private GoogleApiClient mGoogleApiClient;

    @Nullable
    private LocationModel mCurrentLocation;

    @Nullable
    private Marker mMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_picker_layout);
        setSupportActionBar((Toolbar) findViewById(R.id.location_picker_toolbar));

        mMapView = (MapView) findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        mGoogleMap = mMapView.getMap();
        if (mGoogleMap != null) {
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
            mGoogleMap.setMyLocationEnabled(true);

            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mGoogleMap.setOnMapLongClickListener(this);

            // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
            MapsInitializer.initialize(LocationPickerActivity.this);
        } else {
            Toast.makeText(this, "Google Play Services not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        final CardView searchResultsCardView = (CardView) findViewById(R.id.search_results_card_view);
        // GONE initially
        searchResultsCardView.setVisibility(View.GONE);

        mSearchView = (SearchView) findViewById(R.id.locations_search_view);

        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                searchResultsCardView.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
            }
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                AutocompleteSuggestionsTask task = new AutocompleteSuggestionsTask(mGoogleApiClient, searchBounds(), null) {
                    @Override
                    protected void onPostExecute(List<AutocompleteItem> autocompleteItems) {
                        Log.d("SEARCH", autocompleteItems.toString());
                    }
                };
                task.execute(newText);
                return false;
            }
        });

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    private LatLngBounds searchBounds() {
        LatLng northeast = new LatLng(45.497378, -132.107731);
        LatLng southwest = new LatLng(31.848028, -72.078436);
        return new LatLngBounds(southwest, northeast);
    }

    private void setCurrentLocation(@Nullable LocationModel location) {
        if (mMarker != null) {
            mMarker.remove();
            mMarker = null;
            mSearchView.setQuery(null, false);
        }

        mCurrentLocation = location;
        if (location != null) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .title(location.getName())
                    .position(location.getLatLng());
            mMarker = mGoogleMap.addMarker(markerOptions);
            mMarker.showInfoWindow();
            mSearchView.setQuery(location.getName(), false);
        }
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        setCurrentLocation(null);

        MarkerOptions markerOptions = new MarkerOptions()
                .title("Retrieving location...")
                .position(latLng);
        mMarker = mGoogleMap.addMarker(markerOptions);
        mMarker.showInfoWindow();

        ReverseGeocodingTask task = new ReverseGeocodingTask(this) {
            @Override
            protected void onPostExecute(LocationModel location) {
                setCurrentLocation(location);
            }
        };
        task.execute(latLng);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_location_picker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_select) {
            if (mCurrentLocation != null) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_LOCATION, mCurrentLocation);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(this, "Please select location", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.connect();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
