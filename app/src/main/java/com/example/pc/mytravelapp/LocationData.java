package com.example.pc.mytravelapp;

public class LocationData {
    int id;
    String lat;
    String lng;
    String address;
    String note;
    String currentDate;
    String locationName;

    public LocationData() {
    }

    public LocationData(String lat, String lng, String address, String note, String currentDate, String locationName) {
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.note = note;
        this.currentDate = currentDate;
        this.locationName = locationName;
    }

    public LocationData(int id, String lat, String lng, String address, String note, String currentDate, String locationName) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.note = note;
        this.currentDate = currentDate;
        this.locationName = locationName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
}
