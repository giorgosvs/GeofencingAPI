package com.example.geofenceapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button button,b2,b3;
    MapsProvider mapsProvider;
    public static String sessionId;
    public static String lastSessionId;
    public boolean maintained;
    private GeofenceWrapper geofenceWrapper;
    private List<String> requestIds = new ArrayList<>();

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceWrapper = new GeofenceWrapper(this);


        Bundle extras = getIntent().getExtras();


        if(extras != null) { //Case we need to maintain the sessionID
            sessionId = extras.getString("sessionId");
            Log.d(TAG, "Session ID maintained : " + sessionId);
        } else { // Case we change it
            sessionId = setSessionId();
            Log.d(TAG, "Session ID changed to :" + sessionId);
        }
        lastSessionId=sessionId;


        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMapsActivity();
            }
        });

        b2 = findViewById(R.id.button2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeActiveGeofences(geofencingClient,geofenceWrapper);
                changeSession();
                stopGeofenceService();
                Toast.makeText(getApplicationContext(),"Removed Geofences, Session Changed",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Session Changed to : " + sessionId);
            }
        });

        b3 = findViewById(R.id.button3);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //doLoadContent();
                openDeviceActivity();
            }
        });
    }


    private void removeActiveGeofences(GeofencingClient client, GeofenceWrapper wrapper) {
        client.removeGeofences(wrapper.getPendingIntent()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG,"Remove Active Geofences , \"onSuccess: REMOVED\" " );
            }
        });
    }

    private void openDeviceActivity() {
        Intent i = new Intent(this,DataActivity.class);
        startActivity(i);
    }

    public void openMapsActivity(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void doLoadContent(){ //Load content as toast, debugging method
        Cursor cr = getContentResolver().query(MapsProvider.CONTENT_URI_1,null,null,null,"_id");
        StringBuilder stringBuilder = new StringBuilder();

        while
        (cr.moveToNext()){
            int id = cr.getInt(0);
            String s1 = cr.getString(1);
            String s2 = cr.getString(2);
            String s3 = cr.getString(3);
            String s4 = cr.getString(4);

            stringBuilder.append(id+ "    " + s1 + "    " + s2+ "    " + s3 + "    " + s4 +  "\n");
            Toast.makeText(this,stringBuilder.toString(),Toast.LENGTH_SHORT).show();

        }
    }
    private void changeSession() {
        lastSessionId = sessionId; //Save current session as the last session
        sessionId = setSessionId();
//        Log.d(TAG, "Session changed: lastSessionId=" + lastSessionId + ", sessionId=" + sessionId);
    }

    public static String setSessionId() { //Generates the session ID
        String id = UUID.randomUUID().toString();
        return id;
    }

    private void stopGeofenceService() {
        Intent serviceIntent = new Intent(this, GeofenceMonitoringService.class);
        stopService(serviceIntent);
    }


}