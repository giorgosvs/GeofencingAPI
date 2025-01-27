package com.example.geofenceapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class DataActivity extends AppCompatActivity {
    MapsProvider mapsProvider;
    private Button b4, b5,b6;
    public ArrayList<GeofencePoint> geofencePoints = new ArrayList<>();
    public ArrayList<TransitionPoint> transitionPoints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);



        b4 = findViewById(R.id.button4);
        b5 = findViewById(R.id.button5);
        b6 = findViewById(R.id.map);


        Cursor cr = doLoadPoints();
        ArrayList<String> pointsList = new ArrayList<>();
        if (cr.getCount() == 0) {
            Toast.makeText(this, "No Geofences Added", Toast.LENGTH_SHORT).show();

        } else {
            while (cr.moveToNext()) {
                String s1 = cr.getString(1);
                String s2 = cr.getString(2);
                String s3 = cr.getString(3);
                String s4 = cr.getString(4);
                GeofencePoint geofencePoint = new GeofencePoint(s4, s1, s2, s3);
                geofencePoints.add(geofencePoint);
            }
            FourColumnAdapter adapter = new FourColumnAdapter(this, R.layout.single_item, geofencePoints);
            ListView points = findViewById(R.id.geoPoints);
            points.setAdapter(adapter);


        }

        Cursor c = doLoadTransitions();

        if(c.getCount() == 0) {

        } else {
            while(c.moveToNext()){
                String s1 = c.getString(1); //session
                String s2 = c.getString(2); //circleid
                String s3 = c.getString(3); // trasition
                String s4 = c.getString(4); // lon
                String s5 = c.getString(5); // lat
                TransitionPoint transitionPoint = new TransitionPoint(s1, s2, s3, s4,s5);
                transitionPoints.add(transitionPoint);
            }
            FiveColumnAdapter adapter = new FiveColumnAdapter(this, R.layout.double_item, transitionPoints);
            ListView points = findViewById(R.id.transitionPoints);
            points.setAdapter(adapter);
        }

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dropAllContent();
                Toast.makeText(getApplicationContext(), "Tables Dropped", Toast.LENGTH_SHORT).show();
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainActivity();
            }
        });
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMapsActivity();
            }
        });
    }

    private void openMapsActivity() {
            Intent intent = new Intent(this, ResultsMapActivity.class);
            startActivity(intent);
    }

    private void openMainActivity() {
        String value = MainActivity.sessionId;
        Intent i = new Intent(this, MainActivity.class);

        i.putExtra("sessionId", value); // Maintain sessionID
        startActivity(i);
    }

    private void dropAllContent() {

        getContentResolver().delete(MapsProvider.CONTENT_URI_1, null, null);
        getContentResolver().delete(MapsProvider.CONTENT_URI_2, null, null);

    }

    public Cursor doLoadPoints() {
        Cursor cr = getContentResolver().query(MapsProvider.CONTENT_URI_1, null, null, null, "_id");
        return cr;
    }

    public Cursor doLoadTransitions() {
        Cursor cr = getContentResolver().query(MapsProvider.CONTENT_URI_2, null, null, null, "_id");
        return cr;
    }




}