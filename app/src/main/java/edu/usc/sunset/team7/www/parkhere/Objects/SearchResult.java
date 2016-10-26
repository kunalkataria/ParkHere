package edu.usc.sunset.team7.www.parkhere.Objects;

import java.util.ArrayList;

import edu.usc.sunset.team7.www.parkhere.backend.Listing;

/**
 * Created by johnsonhui on 10/14/16.
 */

public class SearchResult {
    private double averageParkPrice;
    private ArrayList<Listing> allListings;
    private long latitude;
    private long longitude;
    private double distanceFromSearchLocation;

    public SearchResult(double averageParkPrice, long latitude, long longitude, double distanceFromSearchLocation){
        allListings = new ArrayList<Listing>();
        this.averageParkPrice = averageParkPrice;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceFromSearchLocation = distanceFromSearchLocation;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public ArrayList<Listing> getAllListings() {
        return allListings;
    }

    public void addListings(Listing addListing) {
        allListings.add(addListing);
    }

    public double getAverageParkPrice() {
        return averageParkPrice;
    }

    public void setAverageParkPrice(double averageParkPrice) {
        this.averageParkPrice = averageParkPrice;
    }

    public double getDistanceFromSearchLocation() {
        return this.distanceFromSearchLocation;
    }

    public void setDistanceFromSearchLocation(double distanceFromSearchLocation) {
        this.distanceFromSearchLocation = distanceFromSearchLocation;
    }

}
