package com.example.styledmap;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.CreateEvent.CreateEvent;
import com.Settings.SettingsActivity;
import com.Utils.GoogleMapsUtils;
import com.Utils.HeatmapUtils;
import com.Utils.LatLngDataSet;
import com.ViewPagerContact.ViewPagerActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import com.volley.REST.VolleyController;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MapsActivityRaw
        extends AppCompatActivity
        implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnCameraMoveStartedListener,
        Response.Listener<JSONObject>,
        Response.ErrorListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        NavigationView.OnNavigationItemSelectedListener{



    public static final int MY_PERMISSION_REQUEST_ACCESS_LOCATION = 100;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 101;

    public static final double PRESSING_POSITION_PRECISION = 0.03;

    private static final String LIFECYCLE_ON_SAVE_POSITION_LAT = "lifecycle_lat";

    private static final String LIFECYCLE_ON_SAVE_POSITION_LNG = "lifecycle_lng";

    private static final String LIFECYCLE_ON_SAVE_POSITION_ZOOM = "lifecycle_zoom";

    private static final String LIFECYCLE_ON_SAVE_MARKER_LAT = "lifecycle_marker_lat";

    private static final String LIFECYCLE_ON_SAVE_MARKER_LNG = "lifecycle_marker_lng";

    private static final String LIFECYCLE_SERVICE_CLIENT_RESOLVING = "lifecycle_resolving";




    // The first Floating Action Button is used to show the others, and those are used
    // to move map position to the nearest city.
    private FloatingActionButton mFab, mFabCity1, mFabCity2, mFabCity3, mBackFab;

    private TextView mTextViewCity1, mTextViewCity2, mTextViewCity3;

    private Animation fab_open, fab_close, rotate_forward, rotate_backward;

    private boolean isFabOpen = false; // used to keep FAB state



    private static final String TAG = MapsActivityRaw.class.getSimpleName();

    private GoogleMap mMap;

    private Marker selectedMarkerLocation;

    private LatLng savedMarkerPosition;

    private JsonObjectRequest updatePositionRequest;

    private TileOverlay mOverlay;

    private HashMap<String, LatLngDataSet> mHeatmapList = new HashMap<>();

    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;

    private LatLng mLastMapLocation;

    private float mLastZoom;

    private CameraPosition previousCameraPosition;

    private String phoneOveringLocationRegion;

    private boolean isMapReady = false, isServiceClientReady = false, alreadyResolving = false;

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        WindowManager windowManager =  (WindowManager) getSystemService(WINDOW_SERVICE);
        if (windowManager != null) {
            int rotation = windowManager.getDefaultDisplay().getRotation();
            if (rotation == Surface.ROTATION_0)
                // Retrieve the content view that renders the map.
                setContentView(R.layout.activity_maps_raw_portrait);

            else if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)
                // Retrieve the content view that renders the map.
                setContentView(R.layout.activity_maps_raw_landscape);

            else
                // Standard position is portrait
                setContentView(R.layout.activity_maps_raw_portrait);

        }

        // Get the SupportMapFragment and register for the callback
        // when the map is ready for use.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar mToolbar =
                (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);

        mFab = (FloatingActionButton)findViewById(R.id.fab);
        mFabCity1 = (FloatingActionButton)findViewById(R.id.fab2);
        mFabCity2 = (FloatingActionButton)findViewById(R.id.fab3);
        mFabCity3 = (FloatingActionButton)findViewById(R.id.fab4);
        mBackFab =  (FloatingActionButton)findViewById(R.id.fab_back);

        mTextViewCity1 = (TextView)findViewById(R.id.text_view);
        mTextViewCity2 = (TextView)findViewById(R.id.text_view2);
        mTextViewCity3 = (TextView)findViewById(R.id.text_view3);

        fab_open  = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward  = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                mToolbar,
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {};

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setLogo(R.drawable.world_logo);
        }

        mDrawerToggle.syncState();

        setupSharedPreferences();

        addFABListener();

        // Restoring Instance State if necessary
        restoreInstanceState(savedInstanceState);

    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);

        mDrawerToggle.syncState();
    }




    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mDrawerToggle.onConfigurationChanged(newConfig);
    }




    private void setupSharedPreferences() {

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        phoneOveringLocationRegion = sharedPreferences.getString(
                getString(R.string.last_user_location),
                null);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }




    private void restoreInstanceState(Bundle savedInstanceState) {

        if (savedInstanceState != null) {

            // Restore map position and zoom
            if(savedInstanceState.containsKey(LIFECYCLE_ON_SAVE_POSITION_LAT) &&
                savedInstanceState.containsKey(LIFECYCLE_ON_SAVE_POSITION_LNG) &&
                savedInstanceState.containsKey(LIFECYCLE_ON_SAVE_POSITION_ZOOM)) {

                mLastMapLocation = new LatLng(
                        savedInstanceState.getDouble(LIFECYCLE_ON_SAVE_POSITION_LAT),
                        savedInstanceState.getDouble(LIFECYCLE_ON_SAVE_POSITION_LNG)
                );
                mLastZoom = savedInstanceState.getFloat(LIFECYCLE_ON_SAVE_POSITION_ZOOM);
            }

            // Saves the old marker in savedMarkerPosition, witch will be
            // restored after the onMapReady returned.
            if (savedInstanceState.containsKey(LIFECYCLE_ON_SAVE_MARKER_LAT) &&
                    savedInstanceState.containsKey(LIFECYCLE_ON_SAVE_MARKER_LNG)) {

                double markerLat = savedInstanceState.getDouble(LIFECYCLE_ON_SAVE_MARKER_LAT);
                double markerLng = savedInstanceState.getDouble(LIFECYCLE_ON_SAVE_MARKER_LNG);
                savedMarkerPosition = new LatLng(markerLat, markerLng);
            }

            // Boolean necessary to maintain the state of connection of Google Service Client
            if (savedInstanceState.containsKey(LIFECYCLE_SERVICE_CLIENT_RESOLVING)) {
                alreadyResolving = savedInstanceState.getBoolean(LIFECYCLE_SERVICE_CLIENT_RESOLVING);
            }
        }
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        CameraPosition cameraPosition;
        if (mMap != null) {

            cameraPosition = mMap.getCameraPosition();
            // Saving the last location
            if (cameraPosition != null) {
                outState.putDouble(LIFECYCLE_ON_SAVE_POSITION_LAT, cameraPosition.target.latitude);
                outState.putDouble(LIFECYCLE_ON_SAVE_POSITION_LNG, cameraPosition.target.longitude);
                outState.putFloat(LIFECYCLE_ON_SAVE_POSITION_ZOOM, cameraPosition.zoom);
            }

            if (selectedMarkerLocation != null) {
                outState.putDouble(LIFECYCLE_ON_SAVE_MARKER_LAT,
                        selectedMarkerLocation.getPosition().latitude);
                outState.putDouble(LIFECYCLE_ON_SAVE_MARKER_LNG,
                        selectedMarkerLocation.getPosition().longitude);
            }
        }

        outState.putBoolean(LIFECYCLE_SERVICE_CLIENT_RESOLVING, alreadyResolving);
    }



    private void restoreLocations() {

        if (!isMapReady || !isServiceClientReady) {
            return;
        }

        // If both GoogleMap and the ClientService are ready it asks for the FINE_LOCATION
        // permission, and do the initial restoring and adds the FAB buttons listener
        mayRequestPermissionLocation();
    }



    private void mayRequestPermissionLocation() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(this);

            // Restores all the Location variables if necessary, saves them in
            // preferences if not.
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {

                initWithNewPosition(sharedPreferences);

            } else if (sharedPreferences.contains(getString(R.string.last_location_lat)) &&
                    sharedPreferences.contains(getString(R.string.last_location_lng))) {

                initWithOldPosition(sharedPreferences);

            } else {

                // Can't get position!
                Toast.makeText(this, "Can't retrieve position.", Toast.LENGTH_LONG).show();
            }

            // Shows Google position button to move the map in phone position.
            GoogleMapsUtils.showGooglePositionButton(this, mMap);

        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {

            // Need to show permission rationale, display a snackbar and then request
            // the permission again when the snackbar is dismissed.
            Snackbar.make(findViewById(R.id.main_constraint_layout),
                    R.string.app_needs_permission, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Request the permission again.
                            ActivityCompat.requestPermissions(MapsActivityRaw.this,
                                    new String[]{ACCESS_FINE_LOCATION},
                                    MY_PERMISSION_REQUEST_ACCESS_LOCATION);
                        }
                    }).show();
        } else {

            // Need to show permission rationale, display a snackbar and then request
            // the permission again when the snackbar is dismissed.
            Snackbar.make(findViewById(R.id.main_constraint_layout),
                    R.string.app_needs_permission, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Request the permission again.
                            ActivityCompat.requestPermissions(MapsActivityRaw.this,
                                    new String[]{ACCESS_FINE_LOCATION},
                                    MY_PERMISSION_REQUEST_ACCESS_LOCATION);
                        }
                    }).show();

        }
    }



    private void initWithNewPosition(SharedPreferences sharedPreferences) {

        phoneOveringLocationRegion = GoogleMapsUtils.getCountryName(
                this,
                mLastLocation.getLatitude(),
                mLastLocation.getLongitude());


        // "Longitudes and latitudes are not generally known to
        // any greater precision than a 32-bit float.
        // So if you're concerned about storage space, you can use floats."
        // cit: Stack Overflow
        // Saves this location to preferences to use it again later

        sharedPreferences.edit()
                .putFloat(getString(R.string.last_location_lat), (float) mLastLocation.getLatitude())
                .putFloat(getString(R.string.last_location_lng), (float) mLastLocation.getLongitude())
                .putString(getString(R.string.last_user_location), phoneOveringLocationRegion)
                .apply();

        // Sets the initial Camera position. This is called both in onMapReady and after the location
        // permission is granted, since it's impossible to tell if the permission will be granted
        // before or later the map is ready. This causes the map to be set twice, sometimes in the
        // same place and sometimes in different place; but the second time the map will always be
        // set in the right position
        setInitialCameraPosition(mMap);

        // Creates the Json request for map position update requests.
        // Sends immediately the first request, to create the starting events in the map,
        // and for this uses a GeoCoder to find the current nation, and adds it to the request.
        requestUpdate(phoneOveringLocationRegion);

    }



    private void initWithOldPosition(SharedPreferences sharedPreferences) {

        float lastLocLat = sharedPreferences.getFloat(getString(R.string.last_location_lat), 0);
        float lastLocLng = sharedPreferences.getFloat(getString(R.string.last_location_lng), 0);
        // The preferences contains old position hold by the phone, and so it uses those
        // to save the current location.

        phoneOveringLocationRegion = GoogleMapsUtils.getCountryName(
                this, lastLocLat, lastLocLng);


        // Sets lastLocation by using the saved one in preferences
        mLastLocation = new Location("");
        mLastLocation.setLatitude(lastLocLat);
        mLastLocation.setLongitude(lastLocLng);

        // Sets the initial Camera position. This is called both in onMapReady and after the location
        // permission is granted, since it's impossible to tell if the permission will be granted
        // before or later the map is ready. This causes the map to be set twice, sometimes in the
        // same place and sometimes in different place; but the second time the map will always be
        // set in the right position
        setInitialCameraPosition(mMap);

        requestUpdate(phoneOveringLocationRegion);
    }



    @Override
    public void onConnected(Bundle connectionHint) {

        // Tries to restore all the necessary location variables from preferences and saved
        // instance state. If the map isn't ready yet, this call fails, but a new one will be made
        // after the map became ready to use. On the other hand, if the call from onMapReady is made
        // before this one, it is going to fail, since this one will have success (they need
        // the same permission, FINE_LOCATION).
        isServiceClientReady = true;
        restoreLocations();
    }



    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection has been suspend");
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (alreadyResolving) {
            // Already resolving this issue.
        } else if (connectionResult.hasResolution()) {
            try {
                alreadyResolving = true;
                connectionResult.startResolutionForResult(this, PLAY_SERVICES_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                // Try again
                mGoogleApiClient.connect();
            }
        } else {
            // can't continue
            alreadyResolving = true;
            showErrorDialog(connectionResult.getErrorCode());
        }
    }



    private void showErrorDialog(int errorCode) {
        //GooglePlayServicesUtil.getErrorDialog e' deprecato prof!

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        apiAvailability.getErrorDialog(this, errorCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
    }



    @Override
    protected void onStart() {
        // Connect the client.
        mGoogleApiClient.connect();
        super.onStart();
    }



    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        // unregister the OnSharedPreference Listener
        PreferenceManager
                .getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }



    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready for use.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.

            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(this);

            updateMapStyle(sharedPreferences, googleMap);

        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }


        // Sets the initial Camera position. This is called both in onMapReady and after the location
        // permission is granted, since it's impossible to tell if the permission will be granted
        // before or later the map is ready. This causes the map to be set twice, sometimes in the
        // same place and sometimes in different place; but the second time the map will always be
        // set in the right position
        setInitialCameraPosition(mMap);

        // Restore the Marker
        if (savedMarkerPosition != null) {

            selectedMarkerLocation = mMap.addMarker(
                    new MarkerOptions()
                            .position(savedMarkerPosition)
                            .title("You are here")
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }

        // Request the permission for location.
        // Most of the app needs this permission to work, so the app ask for it at start.
        // This call is in conjunction with the Google Service Client call.
        isMapReady = true;
        restoreLocations();

        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnCameraMoveStartedListener(this);
    }



    private void setInitialCameraPosition(GoogleMap mMap) {

        if (mMap == null) return;

        if (mLastMapLocation != null) {

            if (mLastZoom == 0)
                mMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(mLastMapLocation,
                                getResources().getInteger(R.integer.initial_map_zoom)));
            else
                // Since mLastMapLocation isn't null, the map has been destroyed and recreated.
                // Here it moves it back where it was before OnDestroy has been called.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastMapLocation, mLastZoom));

        } else if (mLastLocation != null) {
            // Position the map's camera in the phone location.
            mMap.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(
                            new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                            getResources().getInteger(R.integer.initial_map_zoom)));

        } else {
            // Position the map's camera near Sydney, Australia.
            Resources resources = getResources();
            mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            new LatLng(
                                    resources.getInteger(R.integer.sidney_coordinates_lat),
                                    resources.getInteger(R.integer.sidney_coordinates_lng)),
                            resources.getInteger(R.integer.initial_map_zoom)));
        }
    }



    private void updateMapStyle(SharedPreferences sharedPreferences, GoogleMap googleMap) {

        String mapStyle = sharedPreferences.getString(
                getString(R.string.pref_night_mode_key),
                null
        );

        boolean success = false;

        if (mapStyle == null || mapStyle.equals(getString(R.string.day))) {
            // It's using day-mode, so update to day-style
            success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style_day));

        } else if (mapStyle.equals(getString(R.string.night))) {
            // It's using night-mode, so update to night-style
            success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style_night));

        } else if (mapStyle.equals(getString(R.string.auto))) {

            // Uses Date.getTime to retrieve the time of the day, and uses dark theme map
            // if it's night, and light theme when it's day.
            Long secondsFromMidnight = (new Date().getTime() % (24 * 60 * 60 * 1000)) / 1000;
            if (secondsFromMidnight < 28800 || secondsFromMidnight > 64800) {
                // It's Night!
                success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.map_style_night));
            } else {
                // It's Day!
                success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.map_style_day));
            }
        }


        if (!success) {

            Toast.makeText(
                    this,
                    "Error: Style parsing failed. Can't update Map Style.",
                    Toast.LENGTH_LONG).show();
            Log.e(TAG, "Style parsing failed.");
        }
    }



    @Override
    public void onMapClick(LatLng point) {

        LatLng position = positionInList(point);

        if (position != null) {

            previousCameraPosition = mMap.getCameraPosition();

            //mBackFab.setVisibility(View.VISIBLE);

            GoogleMapsUtils.moveMapInLocation(
                    mMap,
                    position,
                    12.5f,
                    2000);

            if (selectedMarkerLocation != null) selectedMarkerLocation.remove();

            selectedMarkerLocation = mMap.addMarker(
                    new MarkerOptions()
                            .position(position)
                            .title("event!")
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        }
    }



    @Override
    public void onMapLongClick(LatLng point) {

        mMap.addMarker(new MarkerOptions()
                .position(point)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        GoogleMapsUtils.moveMapInLocation(mMap, point, 1000);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_ACCESS_LOCATION: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    // Now it can execute the loading code
                    mayRequestPermissionLocation();

                } else {

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request

            default:
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab:

                requestUpdate("italy");
                animateFAB();
                break;
            case R.id.fab2:

                break;
            case R.id.fab3:

                break;
            case R.id.fab4:

                break;
            case R.id.fab_back:

                break;
        }

    }



    @Override
    public void onCameraMoveStarted(int reason) {
        if (previousCameraPosition != null) {
            mBackFab.setVisibility(View.INVISIBLE);
            previousCameraPosition = null;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_activity_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        } else if (id == R.id.create_event) {

            Intent startCreateEventActivity = new Intent(this, CreateEvent.class);
            startActivity(startCreateEventActivity);
            return true;
        } else if (id == R.id.join_event) {

            Intent joinEventActivity = new Intent(this, ViewPagerActivity.class);
            startActivity(joinEventActivity);
        }

        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }



    private void addFABListener() {

        mFab.setOnClickListener(this);
        mFabCity1.setOnClickListener(this);
        mFabCity2.setOnClickListener(this);
        mFabCity3.setOnClickListener(this);
        mBackFab.setOnClickListener(this);
    }



    public void animateFAB() {

        if (isFabOpen) {

            mFab.startAnimation(rotate_backward);
            mFabCity1.startAnimation(fab_close);
            mFabCity2.startAnimation(fab_close);
            mFabCity3.startAnimation(fab_close);

            mTextViewCity1.startAnimation(fab_close);
            mTextViewCity2.startAnimation(fab_close);
            mTextViewCity3.startAnimation(fab_close);

            mFabCity1.setClickable(false);
            mFabCity2.setClickable(false);
            mFabCity3.setClickable(false);

            isFabOpen = false;

        } else {

            mFab.startAnimation(rotate_forward);
            mFabCity1.startAnimation(fab_open);
            mFabCity2.startAnimation(fab_open);
            mFabCity3.startAnimation(fab_open);

            mTextViewCity1.startAnimation(fab_open);
            mTextViewCity2.startAnimation(fab_open);
            mTextViewCity3.startAnimation(fab_open);

            mFabCity1.setClickable(true);
            mFabCity2.setClickable(true);
            mFabCity3.setClickable(true);
            isFabOpen = true;

        }
    }



    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }



    private LatLng positionInList(LatLng pressedPosition){

        if (mHeatmapList == null || pressedPosition == null) return null;

        ArrayList<LatLng> hmList;

        //TODO VERIFY hmList = mHeatmapList.get(regionName).getData();
        // for an optimized portion of the heatmapList: instead of keeping
        // all the values in the world map, it can just use a portion of it
        // made by just the events in the region of the user

        Collection<LatLngDataSet> collection = mHeatmapList.values();
        hmList = new ArrayList<>();
        for (LatLngDataSet dataSet: collection)
            hmList.addAll(dataSet.getData());

        int index = indexOfPositionInList(hmList, pressedPosition, PRESSING_POSITION_PRECISION);

        if (index != -1) return hmList.get(index);
        return null;
    }



    public int indexOfPositionInList(ArrayList<LatLng> array, LatLng value, double precision) {

        if (array == null || array.size() == 0) return -1;
        int i = 0, curPos = 0;
        double curDistance = pointDistance(array.get(0), value);

        for (LatLng elem: array) {

            double distance = pointDistance(elem, value);
            if (distance < curDistance) {
                curDistance = distance;
                curPos = i;
            }
            i++;
        }

        if (curDistance <= precision) return curPos;
        else return -1;

    }


    private double pointDistance(LatLng p1, LatLng p2) {
        return Math.sqrt(
                          (p1.latitude - p2.latitude) * (p1.latitude - p2.latitude)
                        + (p1.longitude - p2.longitude) * (p1.longitude - p2.longitude));
    }



    private void requestUpdate(String region) {

        createRequest(getString(R.string.server_address_get_url), region);
        // Instantiate the RequestQueue with the cache and network, start the request
        // and add it to the queue
        VolleyController.getInstance(getApplicationContext()).addToRequestQueue(updatePositionRequest);
    }



    private void createRequest(String url, final String region) {

        // POST parameters (the region of interest)
        Map<String, String> params = new HashMap<>();
        params.put("region", region);

        JSONObject jsonObjParams = new JSONObject(params);

        updatePositionRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObjParams, this, this);

    }



    @Override
    public void onResponse(JSONObject response) {

        // Get the heatmap information from the Raw file police_station and medicare
        HeatmapUtils.addHeatMapList(
                this,
                mHeatmapList,
                response);

        addEventsOverlay(response.keys().next());
    }



    @Override
    public void onErrorResponse(VolleyError error) {
        System.out.println("errore nella comunicazione " + error.toString());

    }



    private void addEventsOverlay(String region){

        // Generate a provider from the HeatmapList
        HeatmapTileProvider mProvider = HeatmapUtils.getProvider(mHeatmapList, region);

        if (mOverlay != null) mOverlay.remove();

        // Add a tile overlay to the map, using the heat map tile provider.
        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

    }



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_night_mode_key))) {
            updateMapStyle(sharedPreferences, mMap);
        }
    }
}