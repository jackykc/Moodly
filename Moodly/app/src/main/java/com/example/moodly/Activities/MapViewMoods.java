package com.example.moodly.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.example.moodly.Controllers.MoodController;
import com.example.moodly.Controllers.UserController;
import com.example.moodly.Models.Mood;
import com.example.moodly.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by yxi on 2017-03-13.
 */

/**
 * MapViewMoods allows user to view mood events on a map
 * that depend on filters such as near mood events or history moods.
 */


public class MapViewMoods extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private static final String TAG = MapViewMoods.class.getSimpleName();

    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient mGoogleApiClient;
    private CameraPosition mCameraPosition;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);

    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private CheckBox nearbyCheckbox;

    private ArrayList<Integer> arrayListType;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_nearby_mood);

        boolean listType = getIntent().getBooleanExtra("list_type", true);
        arrayListType = new ArrayList<Integer>();
        String mapType;
        if(listType) {
            arrayListType.add(0);
            mapType = "My History";
        } else {
            arrayListType.add(1);
            mapType = "Following";
        }

        // set checkbox for nearby distance
        nearbyCheckbox = ((CheckBox) findViewById(R.id.nearby));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nearbymap);
        mapFragment.getMapAsync(this);


        // Build the Play services client for use by the Fused Location Provider and the Places API.
        // Use the addApi() method to request the Google Places API and the Fused Location Provider.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();

        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Which Map Do You Want?");

        builder.setNeutralButton(mapType, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("All Nearby Moods", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                arrayListType.clear();
                arrayListType.add(2);
                refreshMap();
                nearbyCheckbox.setVisibility(View.INVISIBLE);
                dialog.cancel();
            }
        });
        dialog = builder.create();
        dialog.show();


    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }
    /**
     * Builds the map when the Google Play services client is successfully connected.
     */
    public void onConnected(Bundle connectionHint) {
        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nearbymap);
        mapFragment.getMapAsync(this);
    }
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {

        Log.d(TAG, "Play services connection suspended");
    }

    /**
     * Prepares the map to show current locations of mood events
     * @param map Google Map
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;


        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        refreshMap();

        nearbyCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshMap();
            }

        });

    }

    /**
     * Shows the map with mood events with location.
     */
    private void refreshMap() {

        mMap.clear();

        if (mLastKnownLocation != null) {

            int width = 100;
            int height = 100;



            Bitmap anger = BitmapFactory.decodeResource(getResources(), R.drawable.angry);
            anger = Bitmap.createScaledBitmap(anger, width, height, false);

            Bitmap confusion = BitmapFactory.decodeResource(getResources(), R.drawable.confused);
            confusion = Bitmap.createScaledBitmap(confusion, width, height, false);

            Bitmap disgust = BitmapFactory.decodeResource(getResources(), R.drawable.disgust);
            disgust = Bitmap.createScaledBitmap(disgust, width, height, false);

            Bitmap fear = BitmapFactory.decodeResource(getResources(), R.drawable.afraid);
            fear = Bitmap.createScaledBitmap(fear, width, height, false);

            Bitmap happiness = BitmapFactory.decodeResource(getResources(), R.drawable.happy);
            happiness = Bitmap.createScaledBitmap(happiness, width, height, false);

            Bitmap sadness = BitmapFactory.decodeResource(getResources(), R.drawable.sad);
            sadness = Bitmap.createScaledBitmap(sadness, width, height, false);

            Bitmap shame = BitmapFactory.decodeResource(getResources(), R.drawable.shame);
            shame = Bitmap.createScaledBitmap(shame, width, height, false);

            Bitmap surprise = BitmapFactory.decodeResource(getResources(), R.drawable.surprise);
            surprise = Bitmap.createScaledBitmap(surprise, width, height, false);


            ArrayList<Bitmap> myEmojis = new ArrayList<Bitmap>();
            myEmojis.add(anger);
            myEmojis.add(confusion);
            myEmojis.add(disgust);
            myEmojis.add(fear);
            myEmojis.add(happiness);
            myEmojis.add(sadness);
            myEmojis.add(shame);
            myEmojis.add(surprise);

            MoodController moodController = MoodController.getInstance();
            // true for history list
            int intListType = arrayListType.get(0);

            if (intListType == 2) {
                ArrayList<String> userArray = new ArrayList<String>();
                userArray.add(UserController.getInstance().getCurrentUser().getName());
                userArray.add("");
                moodController.setLatitude(mLastKnownLocation.getLatitude());
                moodController.setLongitude(mLastKnownLocation.getLongitude());
                ArrayList<Mood> nearbyMood = moodController.getMoodList(userArray, true);

            }

            ArrayList<LatLng> myLocations = moodController.getLocations(intListType);
            ArrayList<Integer> myEmotions = moodController.getEmotions(intListType);

            LatLng invalidLatLng = new LatLng(0, 0);
            for (int i = 0; i < myLocations.size(); i++) {

                LatLng temp = myLocations.get(i);

                Location currentLocation = new Location("");
                currentLocation.setLatitude(mLastKnownLocation.getLatitude());
                currentLocation.setLongitude(mLastKnownLocation.getLongitude());

                // if locations are valid
                if (!temp.equals(invalidLatLng)) {
                    int tempEmotion = myEmotions.get(i);
                    Location tempLocation = new Location("");
                    tempLocation.setLatitude(temp.latitude);
                    tempLocation.setLongitude(temp.longitude);

                    float distance = 0;
                    // if the check box is checked, so the user wants only moods
                    // within 5km
                    if (nearbyCheckbox.isChecked()) {
                        distance = tempLocation.distanceTo(currentLocation);
                    }

                    if (distance <= 5000) {

                        Bitmap tempEmoji = myEmojis.get(tempEmotion - 1);

                        mMap.addMarker(new MarkerOptions().position(temp).icon(BitmapDescriptorFactory.fromBitmap(tempEmoji)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(temp));


                    }
                }


            }

        }
    }

    /**
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
    private void getDeviceLocation() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        if (mLocationPermissionGranted) {

            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);

        }

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    /**
     * Checks if we have permission for location. If not, request from user.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

       /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }

}
