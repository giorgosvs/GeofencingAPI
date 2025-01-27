package com.example.geofenceapi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FiveColumnAdapter extends ArrayAdapter {

    private LayoutInflater mInflater;
    private ArrayList<TransitionPoint> transitionPoints;
    private int mViewResourceId;

    public FiveColumnAdapter(Context context, int textViewResourceId, ArrayList<TransitionPoint> transitionPoints) {
        super(context, textViewResourceId, transitionPoints);
        this.transitionPoints = transitionPoints;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mViewResourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parents) {
        convertView = mInflater.inflate(mViewResourceId, null);

        TransitionPoint transitionPoint = transitionPoints.get(position);
        if (transitionPoint != null) {
            TextView sessionId = (TextView) convertView.findViewById(R.id.transition_sessionID);
            TextView transition = (TextView) convertView.findViewById(R.id.transition);
            TextView circleId = (TextView) convertView.findViewById(R.id.transition_circleID);
            TextView latitude = (TextView) convertView.findViewById(R.id.transition_lat);
            TextView longitude = (TextView) convertView.findViewById(R.id.transition_lon);


            if (sessionId != null) {
                sessionId.setText("Session ID :: " + transitionPoint.getSessionID());
            }
            if (sessionId != null) {
                circleId.setText("Circle ID :: " + transitionPoint.getCircleID());
            }
            if (sessionId != null) {
                transition.setText("Transition :: " + transitionPoint.getTransition());
            }
            if (sessionId != null) {
                latitude.setText("Latitude :: " + transitionPoint.getLatitude());
            }
            if (sessionId != null) {
                longitude.setText("Longitude :: " + transitionPoint.getLongitude());
            }
        }
        return convertView;
    }
}
