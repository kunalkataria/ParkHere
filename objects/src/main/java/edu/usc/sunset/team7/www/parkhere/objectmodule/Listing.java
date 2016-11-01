package edu.usc.sunset.team7.www.parkhere.objectmodule;

import java.io.Serializable;

/**
 * Created by kunal on 10/12/16.
 */

public class Listing implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private double latitude;
    private double longitude;
    private String description;
    private boolean handicap;
    private boolean covered;
    private boolean compact;
    private long startTime;
    private long stopTime;
    //private PublicUserProfile owner;
    private boolean active;
    private boolean refundable;
    private String listingID;
    private double price;
    private int spaceRating;
    private String review;
    private String providerID;
    private String seekerID;
    private String imageURL;
    //need to add functionality for can cancel policy
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

    public void setLatitude (double latitude) { this.latitude = latitude; }

    public void setLongitude (double longitude) { this.longitude = longitude; }

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

    public void setHandicap(boolean handicap) { this.handicap = handicap; }

    public void setCovered(boolean covered) { this.covered = covered; }

    public void setCompact(boolean compact) { this.compact = compact; }

    public long getStartTime() {
        return startTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public void setStartTime(long startTime) { this.startTime = startTime; }

    public void setStopTime(long endTime) { this.stopTime = endTime; }

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

//    public String getReview() {
//        return review;
//    }
//
//    public void setReview(String review) {
//        this.review = review;
//    }
//
//    public int getSpaceRating() {
//        return spaceRating;
//    }
//
//    public void setSpaceRating(int spaceRating) {
//        this.spaceRating = spaceRating;
//    }

    public String getProviderID() {
        return providerID;
    }

    public void setProviderID(String providerID) {
        this.providerID = providerID;
    }

//    public String getSeekerID() {
//        return seekerID;
//    }
//
//    public void setSeekerID(String seekerID) {
//        this.seekerID = seekerID;
//    }
}
