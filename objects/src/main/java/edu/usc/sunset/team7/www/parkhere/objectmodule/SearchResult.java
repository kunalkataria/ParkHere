package edu.usc.sunset.team7.www.parkhere.objectmodule;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
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

    public SearchResult(double averageParkPrice){
        allListings = new ArrayList<ResultsPair>();
        this.averageParkPrice = averageParkPrice;
    }

    public ArrayList<ResultsPair> getAllListings() {
        return allListings;
    }

    public void addListing(ResultsPair addListing) {
        System.out.println(addListing.getListing().getName());
        allListings.add(addListing);
    }

    public double getAverageParkPrice() {
        return averageParkPrice;
    }

    public void setAverageParkPrice(double averageParkPrice) {
        this.averageParkPrice = averageParkPrice;
    }

}
