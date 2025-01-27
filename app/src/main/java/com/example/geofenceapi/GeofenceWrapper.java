package com.example.geofenceapi;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class GeofenceWrapper extends ContextWrapper {

    private static final String TAG = "GeofenceHelper";
    PendingIntent pendingIntent;
    private static final java.util.UUID UUID = null;

    private String GEOFENCE_ID = "SOME_ID";

    public GeofenceWrapper(Context base) {
        super(base);
    }

    public GeofencingRequest getGeofencingRequest(List<Geofence> geofenceList){
        return new GeofencingRequest.Builder()
                .addGeofences(geofenceList)
                //or add a List of Geofences?
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();
    }

    public List<Geofence> getGeofences(String ID, List<LatLng> latLngList, float radius, int transitionTypes) {
        List<Geofence> geofenceList = new ArrayList<>();

        //Generate a unique ID
        for (LatLng latLng : latLngList) {
            String id = UUID.randomUUID().toString(); // generate a new unique ID for each geofence

            // Create a geofence for each LatLng in the list
            geofenceList.add(
                    new Geofence.Builder()
                            .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                            .setRequestId(id) // this ID is unique per geofence
                            .setTransitionTypes(transitionTypes)
                            .setLoiteringDelay(5000)
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .build());
        }

        return geofenceList;  // Now returns a list of geofences
    }



    public PendingIntent getPendingIntent() {

        // check if the pendingIntent is already created and return it
        if (pendingIntent != null) {
            return pendingIntent;
        }

        // Create a new PendingIntent if it's not already created
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        intent.setAction("com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION");

        // Ensure correct flag usage for different Android versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // For Android 12 (API 31) and above, use FLAG_IMMUTABLE
            pendingIntent = PendingIntent.getBroadcast(
                    this, 0, intent,
                    PendingIntent.FLAG_MUTABLE);
        } else {
            // For versions lower than Android 12, use FLAG_UPDATE_CURRENT
            pendingIntent = PendingIntent.getBroadcast(this, 2607, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // Log to confirm that the PendingIntent is created successfully
        Log.d(TAG, "getPendingIntent: Created PendingIntent: " + pendingIntent);

        return pendingIntent;
    }




    public String getError(Exception e) {
        if (e instanceof ApiException){
           ApiException apiException = (ApiException) e;
            switch (apiException.getStatusCode()) {
                case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                    return "Geofence Not Available.." + apiException.getStatusCode();
                case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                    return "Too Many Geofences..";
                case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    return "Too Many Pending Intents..";
            }
        }
        return e.getLocalizedMessage();
    }
}
