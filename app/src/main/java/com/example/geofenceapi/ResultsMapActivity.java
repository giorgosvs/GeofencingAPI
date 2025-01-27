package com.example.geofenceapi;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.contentcapture.DataRemovalRequest;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.geofenceapi.databinding.ActivityResultsMapBinding;

import java.util.ArrayList;
import java.util.List;
public class ResultsMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityResultsMapBinding binding;
    private ArrayList<LatLng> lngs = new ArrayList<>();

    public static final String TAG = "ResultsMapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityResultsMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        lngs = getLatestTransitions();

        if (lngs.isEmpty()) {
            Log.e(TAG, "No points to display on the map.");
            return;
        }

        for (LatLng a : lngs) {
            mMap.addMarker(new MarkerOptions().position(a).title("ENTRY/EXIT POINT"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(a, 14)); // Zoom for better visibility
            Log.d(TAG, "LatLon object: " + a.toString());
        }
    }

    private ArrayList<LatLng> getLatestTransitions() {
        ArrayList<LatLng> temp = new ArrayList<>();
        Cursor cr = getContentResolver().query(MapsProvider.CONTENT_URI_2, null, null, null, "_id");

        if (cr == null || !cr.moveToFirst()) {
            Log.e(TAG, "Cursor is empty or null.");
            return temp;
        }

        Log.d(TAG, "Cursor has data: " + cr.getCount() + " rows.");

        do {
            String sessionId = cr.getString(1); // session ID
            String latStr = cr.getString(4); // latitude
            String lonStr = cr.getString(5); // longitude

            if (sessionId.equals(MainActivity.lastSessionId)) {
                try {
                    double lat = Double.parseDouble(latStr);
                    double lon = Double.parseDouble(lonStr);
                    temp.add(new LatLng(lat, lon));
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Failed to parse latitude/longitude: " + e.getMessage());
                }
            }
        } while (cr.moveToNext());

        cr.close();
        return temp;
    }

    private void openMainActivity() {
        String value = MainActivity.sessionId;
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("sessionId", value); // Maintain sessionID
        startActivity(i);
    }
}


