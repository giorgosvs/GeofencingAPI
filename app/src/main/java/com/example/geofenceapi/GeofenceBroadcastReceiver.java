package com.example.geofenceapi;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {


    private static final String TAG = "GeofenceBroadcastReceiver";
    public ContentValues values;
    MapsActivity mapsActivity;
    public static OnReceiveListener static_listener;

    public static abstract interface OnReceiveListener
    {
        public void onReceive(Context context, Intent intent);
    }

    public static void setOnReceiveListener(OnReceiveListener listener)
    {
        static_listener = listener;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Intent received");

        if (intent == null) {
            Log.e(TAG, "onReceive: Received null intent!");
            return;
        }

        Log.d(TAG, "onReceive: Checking if static_listener is set");
        if (static_listener == null) {
            Log.e(TAG, "onReceive: static_listener is null. Listener was not set.");
            return;
        } else {
            Log.d(TAG, "onReceive: static_listener is properly set.");
        }

        static_listener.onReceive(context, intent); // Ensure this is safe to call

        // Check if the geofencing event is extracted properly
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent == null) {
            Log.e(TAG, "onReceive: GeofencingEvent is null");
            return;
        }

        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error resolving geofencing event");
            return;
        }

        // Log the GeofencingEvent details
        Log.d(TAG, "onReceive: GeofencingEvent = " + geofencingEvent);

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence : geofenceList) {
            Log.d(TAG, "Geofence Event: ID : " + geofence.getRequestId() +
                    "\nLat : " + geofence.getLatitude() + "\nLon : " + geofence.getLongitude());
        }

        Location location = geofencingEvent.getTriggeringLocation();
        Log.d(TAG, "Location: Lat - " + location.getLatitude() + " Lon - " + location.getLongitude());

        int transitionType = geofencingEvent.getGeofenceTransition();
        Log.d(TAG, "Transition Type: " + transitionType);

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "Entered Geofence", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Entered Geofence");
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "Exited Geofence", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Exited Geofence");
                break;
            default:
                Log.e(TAG, "Unknown Geofence Transition");
        }
    }
    @SuppressLint("LongLogTag")
    private void saveGeofenceTransition(Context context, Geofence geofence, int transitionType, Location location) {
        String transition = (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) ? "ENTER" : "EXIT";

        ContentValues values = new ContentValues();
        values.put("circleid", geofence.getRequestId());
        values.put("lat", location.getLatitude());
        values.put("lon", location.getLongitude());
        values.put("transition", transition);
        values.put("sessionid", MainActivity.sessionId);

        Uri uri = context.getContentResolver().insert(MapsProvider.CONTENT_URI_2, values);

        if (uri != null) {
            Log.d(TAG, "Geofence transition saved to database: " + uri);
        } else {
            Log.e(TAG, "Failed to save geofence transition to database.");
        }
    }





}