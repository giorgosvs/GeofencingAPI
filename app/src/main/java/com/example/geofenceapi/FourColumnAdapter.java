package com.example.geofenceapi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FourColumnAdapter extends ArrayAdapter {

    private LayoutInflater mInflater;
    private ArrayList<GeofencePoint> geofencePoints;
    private int mViewResourceId;

    public FourColumnAdapter(Context context, int textViewResourceId, ArrayList<GeofencePoint> geofencePoints) {
        super(context,textViewResourceId,geofencePoints);
        this.geofencePoints = geofencePoints;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mViewResourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parents){
        convertView = mInflater.inflate(mViewResourceId,null);

        GeofencePoint geofencePoint = geofencePoints.get(position);
        if(geofencePoint != null ){
            TextView sessionId = (TextView) convertView.findViewById(R.id.item_sessionID);
            TextView circleId = (TextView) convertView.findViewById(R.id.item_circleID);
            TextView latitude = (TextView) convertView.findViewById(R.id.item_lat);
            TextView longitude = (TextView) convertView.findViewById(R.id.item_lon);


            if(sessionId != null) {
                sessionId.setText("Session ID :: " + geofencePoint.getSessionID());
            }
            if(sessionId != null) {
                circleId.setText("Circle ID :: " + geofencePoint.getCircleID());
            }
            if(sessionId != null) {
                latitude.setText("Latitude :: " + geofencePoint.getLatitude());
            }
            if(sessionId != null) {
                longitude.setText("Longitude :: " + geofencePoint.getLongitude());
            }
        }
        return convertView;
    }

}
