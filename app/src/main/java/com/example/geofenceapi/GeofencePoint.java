package com.example.geofenceapi;

public class GeofencePoint {
    String sessionID;
    String circleID;
    String latitude;
    String Longitude;

    public GeofencePoint(String sessionID, String circleID, String latitude, String longitude) {
        this.sessionID = sessionID;
        this.circleID = circleID;
        this.latitude = latitude;
        Longitude = longitude;
    }

    public String getSessionID() {
        return sessionID;
    }

    public String getCircleID() {
        return circleID;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public void setCircleID(String circleID) {
        this.circleID = circleID;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }
}
