package edu.usc.sunset.team7.www.parkhere.Objects;

/**
 * Created by Acer on 10/18/2016.
 */

public class Booking {

    private Listing mListing;
    private PublicUserProfile seeker;
    private int providerRating;
    private int spaceRating;
    private String review;
    private long bookStartTime;
    private long bookEndTime;

    public Booking(Listing mListing, PublicUserProfile seeker) {
        this.mListing = mListing;
        this.seeker = seeker;
    }

    public Listing getMListing() {
        return this.mListing;
    }

    public void setMListing(Listing mListing) {
        this.mListing = mListing;
    }

    public PublicUserProfile getSeeker() {
        return this.seeker;
    }

    public void setSeeker(PublicUserProfile seeker) {
        this.seeker = seeker;
    }

    public int getProviderRating() {
        return this.providerRating;
    }

    public void setProviderRating(int providerRating) {
        this.providerRating = providerRating;
    }

    public int getSpaceRating() {
        return this.spaceRating;
    }

    public String getReview() {
        return this.review;
    }

    public void rate(int providerRating, String review) {
        this.providerRating = providerRating;
        this.review = review;
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

}