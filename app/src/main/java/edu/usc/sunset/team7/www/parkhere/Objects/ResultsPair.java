package edu.usc.sunset.team7.www.parkhere.Objects;


/**
 * Created by johnsonhui on 10/23/16.
 */

public class ResultsPair {
    private Listing listing;
    private double distance;
    public ResultsPair(Listing listing, double distance){
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
