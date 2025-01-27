package com.example.geofenceapi;

import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.geofenceapi.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Button button1,button2;
    private GeofencingClient geofencingClient;
    private GeofenceWrapper geofenceWrapper;
    MapsProvider mapsProvider;


    public List<Geofence> geofencesTemp = new ArrayList<>();
    public List<Geofence> triggeringGeofences = new ArrayList<>();
    private List<LatLng> lngs = new ArrayList<>();

    MutableLiveData<String> listen = new MutableLiveData<>();

    private float GEOFENCE_RADIUS = 100;
    private String GEOFENCE_ID = "SOME_ID";
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listenForTransitionsAndSave();

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check if Google Play Services is available
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            // If Google Play Services are not available, show an error dialog
            GoogleApiAvailability.getInstance().getErrorDialog(this, status, 2404).show();
        } else {
            // Google Play Services are available, proceed to load the map
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        }

        // Continue
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceWrapper = new GeofenceWrapper(this);

        listen.setValue(MainActivity.sessionId); //Initialize Session ID with a value

        listen.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String changedValue) {
                MainActivity.sessionId = changedValue;
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        button1 =  findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        mMap = googleMap;


        // Add a marker in pedion tou areos and move the camera
        LatLng pedion = new LatLng(37.992792, 23.735721);
        //mMap.addMarker(new MarkerOptions().position(pedion).title("Pedion tou Areos"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pedion,14));

        enableUserLocation();
        mMap.setOnMapLongClickListener(this::onMapLongClick);

//      listenForTransitionsAndSave();

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                openMainActivity();

            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!lngs.isEmpty()) {
                    //start background geofence monitoring service
                    startGeofenceService(lngs);

                    // Call addGeofence with a list of points
                    addGeofence(lngs, GEOFENCE_RADIUS);
                    Toast.makeText(getApplicationContext(), "Started Monitoring Device", Toast.LENGTH_SHORT).show();
                    lngs.clear(); // Clear the list after adding the geofences
                }
            }
        });



    }


    public void openMainActivity(){
        Log.d(TAG, "Navigating back to MainActivity");
        String value = MainActivity.sessionId;
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("sessionId",value); // Maintain sessionID
        startActivity(i);
    }


    private void startGeofenceService(List<LatLng> geofencePoints) {

        Intent serviceIntent = new Intent(this, GeofenceMonitoringService.class);
        serviceIntent.putParcelableArrayListExtra("geofence_points", new ArrayList<>(geofencePoints));
        ContextCompat.startForegroundService(this, serviceIntent);
        Log.d(TAG, "startGeofenceService: Service started with points: " + geofencePoints);
    }

    public void maintainSessionId() {
        Intent intent = new Intent();

    }

    public void printGeofences(List<Geofence> geofences) { // DEBUGGING METHOD
        for (Geofence a : geofences) {
            Log.d(TAG, "Geofence Object: " + a.toString());
        }
    }

    @SuppressLint("MissingPermission")
    private void enableUserLocation(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        } else {
            //Ask for permissions
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String [] {Manifest.permission.ACCESS_FINE_LOCATION} , FINE_LOCATION_ACCESS_REQUEST_CODE);

            } else {
                ActivityCompat.requestPermissions(this, new String [] {Manifest.permission.ACCESS_FINE_LOCATION} , FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //We have the permission for location
                mMap.setMyLocationEnabled(true);
            } else {
                //We do not have the permission

            }
        }

        if(requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission for location
                Toast.makeText(this , "Adding Geofences Now Allowed",Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission

            }
        }
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

        if(Build.VERSION.SDK_INT >= 29) {
            //Background permissions required
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                handleMapLongClick(latLng);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION }, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION }, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }

        }else {
            handleMapLongClick(latLng);


        }
    }

    private void handleMapLongClick(LatLng latLng) {
        /*if (lngs.isEmpty()) {
            addMarker(latLng , 0);
            addCircle(latLng,GEOFENCE_RADIUS,0);
            lngs.add(latLng);
        } else {
            for (LatLng a : lngs) {
                if(a == latLng) {
                    addMarker(latLng , 1);
                    addCircle(latLng,GEOFENCE_RADIUS ,1);
                    lngs.remove(a);
                } else {
                    addMarker(latLng , 0);
                    addCircle(latLng,GEOFENCE_RADIUS ,0);
                    lngs.add(latLng);
                }
                for (LatLng b : lngs) {
                    System.out.println(b.toString());
                }
            }
        }*/

        addMarker(latLng);
        addCircle(latLng,GEOFENCE_RADIUS);
        lngs.add(latLng);
    }



    @SuppressLint("MissingPermission")
    private void addGeofence(List<LatLng> latLng, float radius){


            geofencesTemp = geofenceWrapper.getGeofences(GEOFENCE_ID, latLng, radius, GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);

            // Debug the number of geofences
//            Log.d(TAG, "Number of geofences generated: " + geofencesTemp.size());

            // Print out the geofences for debugging
//            printGeofences(geofencesTemp);

            for (Geofence a : geofencesTemp) {
                doSaveContent(a); // Save each geofence
            }


        GeofencingRequest geofencingRequest = geofenceWrapper.getGeofencingRequest(geofencesTemp);
        PendingIntent pendingIntent = geofenceWrapper.getPendingIntent();

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Geofences Added!");
                        // Confirm geofences are added
                        Log.d(TAG, "Geofences added with PendingIntent: " + pendingIntent);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceWrapper.getError(e);
                        Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });

    }



    private void addMarker(LatLng latLng) {
        Marker m;
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
        /*if(markerArrayList.isEmpty()) {
            m = mMap.addMarker(markerOptions);
            markerArrayList.add(m);
        }else {
            for (Marker a : markerArrayList) {
                if (a.getPosition() == latLng) {
                    if(a!= null) {
                        a.remove();
                    }
                    markerArrayList.remove(a);
                } else {
                    mMap.addMarker(markerOptions);
                    markerArrayList.add(a);
                }

            }
         */
        }


    private void addCircle(LatLng latLng, float radius) {
        Circle mapCircle;
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255,255,0,0));
        circleOptions.fillColor(Color.argb(64,255,0,0));
        circleOptions.strokeWidth(4);
        mapCircle = mMap.addCircle(circleOptions);
        /*
        if(mapCircle != null) {
                mapCircle.remove();
        }

        if(lngs.isEmpty()) {
            lngs.add(latLng);
            mapCircle = mMap.addCircle(circleOptions);
        }else {
            for (LatLng a : lngs) {
                if (latLng.latitude == a.latitude && latLng.longitude == a.longitude) {
                    mapCircle = mMap.addCircle(circleOptions);
                    if(mapCircle != null) {
                        mapCircle.remove();
                    }
                    lngs.remove(a);
                }

            }
        }

         */

    }


    public void doSaveContent(Geofence geofence) {
        Log.d(TAG, "doSaveContent called");
        ContentValues values = new ContentValues();
        values.put("circleid",geofence.getRequestId());
        values.put("lat",geofence.getLatitude());
        values.put("lon",geofence.getLongitude());
        values.put("sessionid",MainActivity.sessionId);

        //save geofences
        Uri uri = getContentResolver().insert(MapsProvider.CONTENT_URI_1,values);

//        System.out.println("Session ID : " + MainActivity.sessionId + "\nLast Session ID : " + MainActivity.lastSessionId);
    }

    public void doSaveTriggeringGeofences(Geofence geofence, String transition, Location location) {
        Log.d(TAG, "doSaveTriggering called");
        ContentValues values = new ContentValues();
        values.put("circleid",geofence.getRequestId());
        values.put("sessionid",MainActivity.sessionId);
        values.put("lat",location.getLatitude());
        values.put("transition",transition);
        values.put("lon",geofence.getLongitude());


        Uri uri = getContentResolver().insert(MapsProvider.CONTENT_URI_2, values);
        if (uri != null) {
            Log.d(TAG, "Data inserted with URI: " + uri);
        } else {
            Log.e(TAG, "Failed to insert data into database");
        }

        Toast.makeText(this,uri.toString(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Transition data saved: " + uri, Toast.LENGTH_SHORT).show();
//        Log.d(TAG, "doSaveTriggeringGeofences: " + "Session ID : " + MainActivity.sessionId + "\nLast Session ID : " + MainActivity.lastSessionId);
    }

    public void listenForTransitionsAndSave() {
        GeofenceBroadcastReceiver.setOnReceiveListener((context, intent) -> {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

            if (geofencingEvent == null || geofencingEvent.hasError()) {
                Log.e(TAG, "Error resolving geofencing event");
                return;
            }

            triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            Location location = geofencingEvent.getTriggeringLocation();
            int transitionType = geofencingEvent.getGeofenceTransition();
//            Log.d(TAG, "listenForTransitionsAndSave: TRIGGERED GEO" + triggeringGeofences.toArray());
            for (Geofence geofence : triggeringGeofences) {
                switch (transitionType) {
                    case Geofence.GEOFENCE_TRANSITION_ENTER:
                        doSaveTriggeringGeofences(geofence, "ENTER", location);
                        Log.d(TAG, "listenForTransitionsAndSave: ENTERED");
                        break;
                    case Geofence.GEOFENCE_TRANSITION_EXIT:
                        doSaveTriggeringGeofences(geofence, "EXIT", location);
                        break;
                }
            }
        });
    }


    public void doLoadContent(){
        Cursor cr = getContentResolver().query(MapsProvider.CONTENT_URI_1,null,null,null,"_id");
        StringBuilder stringBuilder = new StringBuilder();

        while
        (cr.moveToNext()){
            int id = cr.getInt(0);
            String s1 = cr.getString(1);
            String s2 = cr.getString(2);
            String s3 = cr.getString(3);

            stringBuilder.append(id+ "    " + s1 + "    " + s2+"\n");
            Toast.makeText(this,stringBuilder.toString(),Toast.LENGTH_SHORT).show();

        }
    }










}

