package com.example.ultron.androidlocationpicker;

import android.location.Address;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationPickerActivity extends AppCompatActivity implements GoogleMap.OnMapLongClickListener {
    private MapView mMapView;
    private GoogleMap mGoogleMap;

    private LocationModel mCurrentLocation;

    @Nullable
    private Marker mMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_picker_layout);

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

    private void clearMarker() {
        if (mMarker != null) {
            mMarker.remove();
            mMarker = null;
        }
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        clearMarker();
        mCurrentLocation = null;

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng);

        mMarker = mGoogleMap.addMarker(markerOptions);

        ReverseGeocodingTask task = new ReverseGeocodingTask(this) {
            @Override
            protected void onPostExecute(Address address) {
                if (address != null) {
                    Toast.makeText(LocationPickerActivity.this, address.getAddressLine(0), Toast.LENGTH_SHORT).show();
                    if (mMarker != null) {
                        mMarker.setTitle(address.getCountryName());
                        mMarker.showInfoWindow();
                        mCurrentLocation = new LocationModel(address, latLng);
                    }
                } else {
                    clearMarker();
                }
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
