package com.example.ultron.androidlocationpicker;

import android.app.SearchManager;
import android.content.Context;
import android.location.Address;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationPickerActivity extends AppCompatActivity implements GoogleMap.OnMapLongClickListener {
    private SearchView mSearchView;
    private MapView mMapView;
    private GoogleMap mGoogleMap;

    private LocationModel mCurrentLocation;

    @Nullable
    private Marker mMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_picker_layout);
        setSupportActionBar((Toolbar) findViewById(R.id.location_picker_toolbar));

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

    }

    private void setCurrentLocation(LocationModel location) {
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
            finish();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
