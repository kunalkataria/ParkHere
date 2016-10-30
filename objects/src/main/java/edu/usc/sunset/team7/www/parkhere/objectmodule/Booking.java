package edu.usc.sunset.team7.www.parkhere.objectmodule;

import java.io.Serializable;

/**
 * Created by Acer on 10/18/2016.
 */

public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;

    private Listing mListing;
    //private PublicUserProfile seeker;
    private int spaceRating;
    private String seekerID;
    private String review;
    private long bookStartTime;
    private long bookEndTime;

    public Booking(Listing mListing) {
        this.mListing = mListing;
        //this.seeker = seeker;
    }

    public Listing getMListing() {
        return this.mListing;
    }

    public void setMListing(Listing mListing) {
        this.mListing = mListing;
    }

    public int getSpaceRating() {
        return this.spaceRating;
    }

    public String getReview() {
        return this.review;
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

    public String getSeekerID() {
        return seekerID;
    }

    public void setSeekerID(String seekerID) {
        this.seekerID = seekerID;
    }

    public void setSpaceRating(int spaceRating) {
        this.spaceRating = spaceRating;
    }

    public void setReview(String review) {
        this.review = review;
    }

}