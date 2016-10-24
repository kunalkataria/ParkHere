package edu.usc.sunset.team7.www.parkhere.Objects;

import java.util.ArrayList;

/**
 * Created by kunal on 10/12/16.
 */

public class Listing {
    private String name;
    private double latitude;
    private double longitude;
    private String description;
    private boolean handicap;
    private boolean covered;
    private boolean compact;
    private long startTime;
    private long stopTime;
    private PublicUserProfile owner;
    private boolean active;
    private boolean refundable;
    private String listingID;

    //need to add picture functionality & list of pictures
    public Listing() {
        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHandicap() {
        return handicap;
    }

    public boolean isCovered() {
        return covered;
    }

    public boolean isCompact() {
        return compact;
    }

    public void setFilters(boolean handicap, boolean covered, boolean compact){
        this.handicap = handicap;
        this.covered = covered;
        this.compact = compact;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public void setTime(long startTime, long stopTime) {
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    public PublicUserProfile getOwner() {
        return owner;
    }

    public void setOwner(PublicUserProfile owner) {
        this.owner = owner;
    }

    public boolean isRefundable() {
        return refundable;
    }

    public void setRefundable(boolean refundable) {
        this.refundable = refundable;
    }

    public String getListingID() {
        return listingID;
    }

    public void setListingID(String listingID) {
        this.listingID = listingID;
    }
}
