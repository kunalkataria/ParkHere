package edu.usc.sunset.team7.www.parkhere.objectmodule;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by johnsonhui on 10/14/16.
 */

public class SearchResult implements Serializable {

    @SerializedName("averageParkPrice")
    @Expose
    private double averageParkPrice;

    @SerializedName("allListings")
    @Expose
    private ArrayList<ResultsPair> allListings;

    @SerializedName("latitude")
    @Expose
    private double latitude;

    @SerializedName("longitude")
    @Expose
    private double longitude;


    public SearchResult(double averageParkPrice, long latitude, long longitude, double distanceFromSearchLocation){
        allListings = new ArrayList<ResultsPair>();
        this.averageParkPrice = averageParkPrice;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public ArrayList<ResultsPair> getAllListings() {
        return allListings;
    }

    public void addListings(ResultsPair addListing) {
        allListings.add(addListing);
    }

    public double getAverageParkPrice() {
        return averageParkPrice;
    }

    public void setAverageParkPrice(double averageParkPrice) {
        this.averageParkPrice = averageParkPrice;
    }

}
