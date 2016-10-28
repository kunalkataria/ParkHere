package edu.usc.sunset.team7.www.parkhere.Utils;

/**
 * Created by kunal on 10/11/16.
 */
public class Consts {
    public static final String firebaseURL = "https://parkhere-ceccb.firebaseio.com/";

    // FRAGMENT TAGS
    public static final String SEARCH_FRAGMENT_TAG = "search_fragment";
    public static final String BOOKING_FRAGMENT_TAG = "booking_fragment";
    public static final String LISTING_FRAGMENT_TAG = "listing_fragment";

    public static final String IMAGE_URI = "image_uri";

    public static final String LATITUDE_EXTRA = "latitude_extra";
    public static final String LONGITUDE_EXTRA = "longitude_extra";
    public static final String START_TIME_EXTRA = "start_time_extra";
    public static final String STOP_TIME_EXTRA = "stop_time_extra";
    public static final String SEARCH_RESULT_EXTRA = "search_result_extra";
    public static final String LISTING_RESULT_EXTRA = "listing_result_extra";
    public static final String LISTING_EDIT_EXTRA = "listing_edit_extra";

    public static final String SEARCH_INTENT_FILTER = "search_intent_filter";

    public static final String SEARCH_SERVICE = "Search Service";

    public static final String BASE_URL = "http://parkhere-ceccb.appspot.com";

    //Parking types
    public static final String HANDICAP = "handicap";
    public static final String COMPACT = "compact";
    public static final String COVERED = "covered";
    public static final String SUV = "suv";
    public static final String TRUCK = "truck";

    //Cancellation Types
    public static final String REFUNDABLE = "refundable";
    public static final String NONREFUNDABLE = "nonrefundable";

    public static final String CANCELLATION_DETAILS = "Flexible: Full refund 1 day prior to arrival, except fees\n" +
            "Moderate: Full refund 5 days prior to arrival, except fees\n" +
            "Strict: 50% refund up until 1 week prior to arrival, except fees\n" +
            "Super Strict 30: 50% refund up until 30 days prior to arrival, except fees\n" +
            "Super Strict 60: 50% refund up until 60 days prior to arrival, except fees\n" +
            "Long Term: First month not refundable, 30 day notice for cancellation";


    //Firebase Storage
    public static final String STORAGE_URL = "gs://parkhere-ceccb.appspot.com";
    public static final String STORAGE_PROFILE_PICTURES = "profile_pictures/";
    public static final String STORAGE_PARKING_SPACES = "parking_spaces/";

    //Firebase Databases
    public static final String USERS_DATABASE = "users";
    public static final String LISTINGS_DATABASE = "listings";
    public static final String PUBLIC_PROFILE_DATABASE = "publicusers";
    public static final String BOOKINGS_DATABASE = "bookings";
    public static final String TRANSACTIONS_DATABASE = "transactions";

    public static final String DATABASE_FIRSTNAME = "firstname";
    public static final String DATABASE_LASTNAME = "lastname";
    public static final String DATABASE_PHONENUMBER = "phoneumber";
    public static final String DATABASE_EMAIL= "email";
    public static final String DATABASE_RATING = "rating";

    //Transaction variables
    public static final String PAYMENT_TYPE = "payment_type";

}
