package com.example.muhdfauzan.myrestaurant.model;

public class ModelRestaurant {
    private String r_id,name, address, latitude, longitude, openhour, image;


    public ModelRestaurant(String r_id, String name, String address, String latitude, String longitude, String openhour, String image) {
        this.r_id = r_id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.openhour = openhour;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getR_id() {
        return r_id;
    }

    public void setR_id(String r_id) {
        this.r_id = r_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getOpenhour() {
        return openhour;
    }

    public void setOpenhour(String openhour) {
        this.openhour = openhour;
    }
}
