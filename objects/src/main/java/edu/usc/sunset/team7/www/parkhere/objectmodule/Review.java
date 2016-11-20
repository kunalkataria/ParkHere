package edu.usc.sunset.team7.www.parkhere.objectmodule;

/**
 * Created by johnsonhui on 10/14/16.
 */

public class Review {
    private int reviewRating;
    private String review;

    public Review() {
        this.reviewRating = 0;
        this.review = "";
    }
    public Review(int reviewRating, String review){
        this.reviewRating = reviewRating;
        this.review = review;
    }

    public int getReviewRating(){
        return reviewRating;
    }

    public void setReviewRating(int reviewRating){
        this.reviewRating = reviewRating;
    }

    public String getReview(){
        return review;
    }

    public void setReview(String review){
        this.review = review;
    }
}
