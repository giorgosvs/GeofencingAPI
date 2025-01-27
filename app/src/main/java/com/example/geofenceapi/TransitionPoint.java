package com.example.geofenceapi;

public class TransitionPoint {

    String sessionID;
    String latitude;
    String longitude;
    String transition;
    String circleID;

    public TransitionPoint(String sessionID,String circleID, String transition, String latitude, String longitude) {
        this.sessionID = sessionID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.transition = transition;
        this.circleID = circleID;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTransition() {
        return transition;
    }

    public void setTransition(String transition) {
        this.transition = transition;
    }

    public String getCircleID() {
        return circleID;
    }

    public void setCircleID(String circleID) {
        this.circleID = circleID;
    }
}
