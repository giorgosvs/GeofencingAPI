package com.example.geofenceapi;

import static com.example.geofenceapi.R.drawable.ic_geofence;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class GeofenceMonitoringService extends Service {
    private static final String TAG = "GeofenceService";
    private static final String CHANNEL_ID = "GeofenceServiceChannel";
    private GeofencingClient geofencingClient;
    private GeofenceWrapper geofenceWrapper;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: GeofenceMonitoringService has started");
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceWrapper = new GeofenceWrapper(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: Service started");
        createNotificationChannel();


        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Geofence Monitoring Active")
                .setContentText("Monitoring geofence transitions in the background")
                .setSmallIcon(R.drawable.ic_geofence)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        try {
            startForeground(1, notification.build());
        } catch (Exception e) {
            Log.e(TAG, "Error starting foreground service: " + e.getMessage());
            stopSelf(); // Stop the service gracefully if foreground setup fails
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permissions are not granted. Stopping service.");
            stopSelf(); // Stop the service gracefully
            return START_NOT_STICKY; // Do not restart the service if it's stopped
        }
        List<LatLng> geofencePoints = intent.getParcelableArrayListExtra("geofence_points");

        if (geofencePoints != null && !geofencePoints.isEmpty()) {
            List<Geofence> geofences = geofenceWrapper.getGeofences(
                    "GEOFENCE_ID",
                    geofencePoints,
                    100,
                    Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT
            );

            PendingIntent pendingIntent = getGeofencePendingIntent();
            GeofencingRequest geofencingRequest = geofenceWrapper.getGeofencingRequest(geofences);

            geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Geofences added successfully"))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to add geofences: " + e.getMessage()));
        } else {
            Log.e(TAG, "No geofence points provided.");
        }

        return START_STICKY;

    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Geofence Monitoring Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        return PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
        geofencingClient.removeGeofences(geofenceWrapper.getPendingIntent())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Geofences removed"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to remove geofences: " + e.getMessage()));
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not binding the service
    }
}
