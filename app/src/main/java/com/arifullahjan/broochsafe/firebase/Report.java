package com.arifullahjan.broochsafe.firebase;

import com.google.firebase.firestore.GeoPoint;

/**
 * Created by Arifullah Jan on 3/30/2019.
 */
public class Report {
    public String id;
    public String details;
    public int intensity;
    public String category;
    public GeoPoint location;

    public Report() {
    }

    public Report(String id, String details, int intensity, String category, GeoPoint location) {
        this.id = id;
        this.details = details;
        this.intensity = intensity;
        this.category = category;
        this.location = location;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id='" + id + '\'' +
                ", details='" + details + '\'' +
                ", intensity=" + intensity +
                ", category='" + category + '\'' +
                ", location=" + location +
                '}';
    }
}
