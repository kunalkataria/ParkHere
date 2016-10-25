package edu.usc.sunset.team7.www.parkhere.backend;

import java.util.List;

/**
 * Created by Acer on 10/14/2016.
 */

public class PublicUserProfile {
    protected String firstName;
    protected double rating;
    protected List<Review> reviews;
    protected String userID;

    public PublicUserProfile(String firstName, double rating, List<Review> reviews) {
        this.firstName = firstName;
        this.rating = rating;
        this.reviews = reviews;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<Review> getReviews() {
        return this.reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

}
