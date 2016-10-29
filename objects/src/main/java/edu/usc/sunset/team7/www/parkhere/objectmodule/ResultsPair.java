package edu.usc.sunset.team7.www.parkhere.objectmodule;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by johnsonhui on 10/23/16.
 */

public class ResultsPair implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("Listing")
    @Expose
    private Listing listing;

    @SerializedName("distance")
    @Expose
    private double distance;

    public ResultsPair(Listing listing, double distance) {
        this.listing = listing;
        this.distance = distance;
    }

    public Listing getListing() {
        return listing;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

}