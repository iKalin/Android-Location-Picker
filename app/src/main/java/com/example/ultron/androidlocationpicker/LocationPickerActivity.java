package com.example.ultron.androidlocationpicker;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;

public class LocationPickerActivity extends AppCompatActivity
        implements GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraChangeListener,
        GoogleApiClient.ConnectionCallbacks {
    public static final String EXTRA_LOCATION = "selected-location";

    private SearchView mSearchView;

    private MapView mMapView;
    private GoogleMap mGoogleMap;

    private GoogleApiClient mGoogleApiClient;
    private SearchHistoryManager mHistoryManager;

    @Nullable
    private LocationModel mCurrentLocation;

    @Nullable
    private Marker mMarker = null;

    private boolean showCurrentLocationWhenConnected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_picker_layout);
        setSupportActionBar((Toolbar) findViewById(R.id.location_picker_toolbar));

        mMapView = (MapView) findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);

        // Gets GoogleMap from the MapView and does initialization stuff
        mGoogleMap = mMapView.getMap();
        if (mGoogleMap != null) {
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
            mGoogleMap.setMyLocationEnabled(true);

            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mGoogleMap.setOnMapLongClickListener(this);
            mGoogleMap.setOnCameraChangeListener(this);

            // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
            MapsInitializer.initialize(LocationPickerActivity.this);
        } else {
            Toast.makeText(this, "Google Play Services not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        // SEARCH

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();

        mHistoryManager = new SearchHistoryManager(this);

        final CardView searchResultsCardView = (CardView) findViewById(R.id.search_results_card_view);
        final ListView searchResultsView = (ListView) findViewById(R.id.search_results_list_view);

        final ArrayAdapter<AutocompleteItem> resultsAdapter =
                new ArrayAdapter<>(this, R.layout.row_autocomplete, R.id.suggestion_label,
                                   mHistoryManager.searchHistory());
        searchResultsView.setAdapter(resultsAdapter);

        mSearchView = (SearchView) findViewById(R.id.locations_search_view);

        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                searchResultsCardView.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
            }
        });
        // set initial card view state
        searchResultsCardView.setVisibility(mSearchView.hasFocus() ? View.VISIBLE : View.GONE);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    resultsAdapter.clear();
                    resultsAdapter.addAll(mHistoryManager.searchHistory());
                } else {
                    AutocompleteSuggestionsTask task =
                            new AutocompleteSuggestionsTask(mGoogleApiClient, searchBounds(), null) {
                                @Override
                                protected void onPostExecute(List<AutocompleteItem> autocompleteItems) {
                                    resultsAdapter.clear();
                                    resultsAdapter.addAll(autocompleteItems);
                                }
                            };
                    task.execute(newText);
                }
                return false;
            }
        });

        searchResultsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSearchView.clearFocus();

                AutocompleteItem selectedItem = resultsAdapter.getItem(position);
                FetchPlaceByIdTask task = new FetchPlaceByIdTask(mGoogleApiClient) {
                    @Override
                    protected void onPostExecute(LocationModel locationModel) {
                        setCurrentLocation(locationModel, true);
                    }
                };
                task.execute(selectedItem.getPlaceId());

                mHistoryManager.addToHistory(selectedItem);
            }
        });

        // INITIAL LOCATION

        LocationModel initialLocation = (LocationModel) getIntent().getSerializableExtra(EXTRA_LOCATION);
        if (initialLocation != null) {
            showCurrentLocationWhenConnected = false;
            setCurrentLocation(initialLocation, true);
        }
    }

    private LatLngBounds searchBounds() {
        LatLng northeast = new LatLng(45.497378, -132.107731);
        LatLng southwest = new LatLng(31.848028, -72.078436);
        return new LatLngBounds(southwest, northeast);
    }

    private void setCurrentLocation(@Nullable LocationModel location, boolean changeCamera) {
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

            if (changeCamera) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location.getLatLng(), 17);
                mGoogleMap.moveCamera(cameraUpdate);
            }
        }
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        setCurrentLocation(null, false);

        MarkerOptions markerOptions = new MarkerOptions()
                .title("Retrieving location...")
                .position(latLng);
        mMarker = mGoogleMap.addMarker(markerOptions);
        mMarker.showInfoWindow();

        ReverseGeocodingTask task = new ReverseGeocodingTask(this) {
            @Override
            protected void onPostExecute(LocationModel location) {
                setCurrentLocation(location, false);
            }
        };
        task.execute(latLng);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        showCurrentLocationWhenConnected = false;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (showCurrentLocationWhenConnected && lastLocation != null) {
            LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            mGoogleMap.moveCamera(cameraUpdate);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

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
