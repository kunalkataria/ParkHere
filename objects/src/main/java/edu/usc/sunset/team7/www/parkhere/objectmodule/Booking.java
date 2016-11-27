package edu.usc.sunset.team7.www.parkhere.objectmodule;

import java.io.Serializable;

/**
 * Created by Acer on 10/18/2016.
 */

public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;

    private Listing mListing;
    private String providerID;
    private String bookingID;
    private long bookStartTime;
    private long bookEndTime;
    private int timeIncrement;

    public Booking(Listing mListing) {
        this.mListing = mListing;
    }

    public void setBookingID(String bookingID) { this.bookingID = bookingID; }

    public String getBookingID() { return this.bookingID; }

    public Listing getMListing() {
        return this.mListing;
    }

    public void setMListing(Listing mListing) {
        this.mListing = mListing;
    }

    public long getBookStartTime() {
        return this.bookStartTime;
    }

    public void setBookStartTime(long bookStartTime) {
        this.bookStartTime = bookStartTime;
    }

    public long getBookEndTime() {
        return this.bookEndTime;
    }

    public void setBookEndTime(long bookEndTime) {
        this.bookEndTime = bookEndTime;
    }

    public String getProviderID() { return providerID; }

    public void setProviderID(String providerID) {this.providerID = providerID; }

    public int getTimeIncrement() {return timeIncrement;    }

    public void setTimeIncrement(int timeIncrement) {this.timeIncrement = timeIncrement; }

}