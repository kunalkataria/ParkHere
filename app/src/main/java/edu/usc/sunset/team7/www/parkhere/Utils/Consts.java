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
    public static final String BALANCE_FRAGMENT_TAG = "balance_fragment";
    public static final String MY_PROFILE_FRAGMENT_TAG = "my_profile_fragment";
    public static final String PARKING_SPOTS_FRAGMENT_TAG = "parking_spots_fragment";

    public static final String IMAGE_URI = "image_uri";

    public static final String LATITUDE_EXTRA = "latitude_extra";
    public static final String LONGITUDE_EXTRA = "longitude_extra";
    public static final String START_TIME_EXTRA = "start_time_extra";
    public static final String STOP_TIME_EXTRA = "stop_time_extra";
    public static final String SEARCH_RESULT_EXTRA = "search_result_extra";
    public static final String LISTING_RESULT_EXTRA = "listing_result_extra";
    public static final String COVERED_EXTRA = "covered_extra";
    public static final String HANDICAP_EXTRA = "handicap_extra";
    public static final String COMPACT_EXTRA = "compact_extra";
    public static final String LISTING_EDIT_EXTRA = "listing_edit_extra";
    public static final String PARKING_SPOT_EDIT_EXTRA = "parking_spot_edit_extra";
    public static final String MY_OWN_LISTING_EXTRA = "my_own_listing_extra";
    public static final String BOOKING_EXTRA = "booking_extra";
    public static final String LISTING_EXTRA = "listing_extra";
    public static final String INACTIVE_LISTINGS_EXTRA = "inactive_listings_extra";
    public static final String SIGN_OUT_EXTRA = "sign_out_extra";
    public static final String EMAIL_EXTRA = "email_extra";
    public static final String TEXT_BODY_EXTRA = "text_body_extra";
    public static final String PARKING_SPOT_EXTRA = "parking_spot_extra";
    public static final String PRIOR_BOOKING_EXTRA = "prior_booking_extra";


    public static final String LISTING_TO_BE_BOOKED = "listing_to_be_booked";
    public static final String LISTING_DISTANCE = "listing_distance";
    public static final String LISTING_DETAILS_STRING = "listing_details_string";

    public static final String SEARCH_INTENT_FILTER = "search_intent_filter";
    public static final String EMAIL_INTENT_FILTER = "email_intent_filter";

    public static final int FILTERS_CHANGED = 1001;
    public static final int FILTERS_UNCHANGED = 1002;
    public static final int PARKING_SPOT_REQUEST = 1003;
    public static final int PARKING_SPOT_SUCCESSFUL_RESULT = 10004;

    public static final String EMAIL_SERVICE = "Email Service";
    public static final String SEARCH_SERVICE = "Search Service";
    public static final String BASE_URL = "http://parkhere-ceccb.appspot.com";

    //Parking types
    public static final String HANDICAP = "handicap";
    public static final String COMPACT = "compact";
    public static final String COVERED = "covered";

    //Cancellation Types
    public static final String REFUNDABLE = "refundable";
    public static final String NONREFUNDABLE = "nonrefundable";

    //Firebase Storage
    public static final String STORAGE_URL = "gs://parkhere-ceccb.appspot.com";
    public static final String STORAGE_PROFILE_PICTURES = "profile_pictures/";
    public static final String STORAGE_PARKING_SPACES = "parking_spaces/";

    //Firebase Databases
    public static final String USERS_DATABASE = "Users";
    public static final String LISTINGS_DATABASE = "Listings";
    public static final String BOOKINGS_DATABASE = "Bookings";
    public static final String REVIEWS_DATABASE = "Reviews";
    public static final String PARKING_SPOTS_DATABASE = "Parking Spots";

    //User Database
    public static final String USER_FIRSTNAME = "First Name";
    public static final String USER_LASTNAME = "Last Name";
    public static final String USER_PHONENUMBER = "Phone Number";
    public static final String USER_EMAIL= "Email";
    public static final String USER_RATING = "Rating";
    public static final String USER_IS_PROVIDER = "Is Provider";
    public static final String USER_BALANCE = "Balance";
    public static final String USER_PROFILE_PIC = "Profile Picture URL";
    public static final String USER_DEFAULT_PROFILE_PIC_URL = "https://firebasestorage.googleapis.com/v0/b/parkhere-ceccb.appspot.com/o/profile_pictures%2Fdefault_profile-web.png?alt=media&token=e434babc-dffa-47f4-a3c6-deea0e2557b4";

    //Listing Database
    public static final String ACTIVE_LISTINGS = "Active Listings";
    public static final String INACTIVE_LISTINGS = "Inactive Listings";
    public static final String PROVIDER_ID = "Provider ID";
    public static final String LISTING_ID = "Listing ID";

    public static final String LISTING_NAME = "Listing Name";
    public static final String LISTING_DESCRIPTION = "Listing Description";
    public static final String LISTING_REFUNDABLE = "Is Refundable";
    public static final String LISTING_COMPACT = "Compact";
    public static final String LISTING_COVERED = "Covered";
    public static final String LISTING_HANDICAP = "Handicap";
    public static final String LISTING_PRICE = "Price";
    public static final String LISTING_LONGITUDE = "Longitude";
    public static final String LISTING_LATITUDE = "Latitude";
    public static final String LISTING_START_TIME = "Start Time";
    public static final String LISTING_END_TIME = "End Time";
    public static final String LISTING_IMAGE = "Image URL";
    public static final String LISTING_IS_PAID = "Paid";
    public static final String LISTING_ACTIVE_TIMES = "Active Times";
    public static final String LISTING_BOOK_TIME = "Book Time Increment";
    public static final String LISTING_CURRENT_ACTIVE = "Currently Active";


    public static final String DEFAULT_PARKING_IMAGE = "https://firebasestorage.googleapis.com/v0/b/parkhere-ceccb.appspot.com/o/parking_spaces%2Fempty_parking.png?alt=media&token=9f2a1602-9248-4485-8eef-1c73f7a5ea77";

    //Booking Database
    public static final String BOOKING_ID = "Booking ID";
    public static final String BOOKING_START_TIME = "Start Time";
    public static final String BOOKING_END_TIME = "End Time";
    public static final String BOOKING_PROVIDER_ID = "Provider ID";
    public static final String BOOKING_LISTING_ID = "Listing ID";


    public static final String BOOKING_SEEKER_ID = "Seeker ID";
    public static final String BOOKING_SPACE_RATING = "Space Rating";
    public static final String BOOKING_SPACE_REVIEW = "Space Review";


    //Transaction variables
    public static final String PAYMENT_TYPE = "payment_type";
    public static final String CREDIT_CARD = "credit_card";
    public static final String PAYPAL = "paypal";

    public static final String CREDIT_CARD_TYPE = "credit_card_type";
    public static final String VISA = "Visa";
    public static final String DISCOVER = "Discover";
    public static final String AMERICAN_EXPRESS = "American Express";
    public static final String MASTERCARD = "Mastercard";

    public static final String CREDIT_CARD_NUMBER = "credit_card_number";
    public static final String CREDIT_CARD_NAME = "name_on_card";
    public static final String SECURITY_CODE = "security_code";
    public static final String EXPIRATION_MONTH = "month";
    public static final String EXPIRATION_YEAR  = "year";
    public static final String ADDRESS = "address";
    public static final String CITY = "city";
    public static final String STATE = "state";
    public static final String ZIPCODE = "zipcode";

    public static final String PAYPAL_EMAIL = "paypal_email";

    public static final String USER_ID = "User ID";

    //Reviews Database
    public static final String REVIEW_DESCRIPTION = "Review";
    public static final String REVIEW_RATING = "Rating";

    public static final String PARKING_SPOT_DATABASE = "Parking Spots";
    public static final int RESULTS_LIST_VIEW_ID = 0;

    //Parking
    public static final String PARKING_SPOTS_ID = "ParkingID";
    public static final String PARKING_SPOTS_PROVIDER_ID = "ProviderID";
    public static final String PARKING_SPOTS_COMPACT = "Compact";
    public static final String PARKING_SPOTS_COVERED = "Covered";
    public static final String PARKING_SPOTS_HANDICAP = "Handicap";
    public static final String PARKING_SPOTS_LONGITUDE = "Longitude";
    public static final String PARKING_SPOTS_LATITUDE = "Latitude";
    public static final String PARKING_SPOTS_IMAGE = "ImageURL";
    public static final String PARKING_SPOTS_BOOKING_COUNT = "Booking Count";
    public static final String PARKING_SPOTS_NAME = "Name";
    public static final String PARKING_SPOTS_ACTIVE = "Active";

}
