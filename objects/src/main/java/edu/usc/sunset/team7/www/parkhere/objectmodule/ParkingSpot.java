package edu.usc.sunset.team7.www.parkhere.objectmodule;

import java.io.Serializable;

/**
 * Created by Acer on 11/18/2016.
 */

public class ParkingSpot implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private boolean handicap;
    private boolean covered;
    private boolean compact;
    private String imageURL;
    private String parkingSpotID;
    private int bookingCount;
    private String providerID;
    private boolean active;

    public boolean isCompact() {
        return compact;
    }

    public void setCompact(boolean compact) {
        this.compact = compact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setString(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isHandicap() {
        return handicap;
    }

    public void setHandicap(boolean handicap) {
        this.handicap = handicap;
    }

    public boolean isCovered() {
        return covered;
    }

    public void setCovered(boolean covered) {
        this.covered = covered;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getParkingSpotID() {
        return parkingSpotID;
    }

    public void setParkingSpotID(String parkingSpotID) {
        this.parkingSpotID = parkingSpotID;
    }

    public int getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(int bookingCount) {
        this.bookingCount = bookingCount;
    }

    public String getProviderID() {
        return providerID;
    }

    public void setProviderID(String providerID) {
        this.providerID = providerID;
    }

    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }

}
