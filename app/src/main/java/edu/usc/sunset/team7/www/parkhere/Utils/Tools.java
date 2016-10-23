package edu.usc.sunset.team7.www.parkhere.Utils;

/**
 * Created by kunal on 10/14/16.
 */

public class Tools {

    private static final String EMAIL_REGEX = ".+\\@.+\\..+";
    private static final String NAME_REGEX = "^[a-zA-Z ,.'-]+$";
    private static final String PHONE_REGEX = "^[0-9]{10}$";

    // very basic email regex checker, main validation is done through email confirmation link
    public static boolean emailValid(String email) {
        return email.matches(EMAIL_REGEX);
    }

    // returns true if name is valid (alpha characters and ' - . , and spaces only allowed)
    public static boolean nameValid(String name) {
        return name.matches(NAME_REGEX);
    }

    // returns true if phone number passed is 10 digits long ONLY
    public static boolean phoneValid(String phoneNumber) {
        return phoneNumber.matches(PHONE_REGEX);
    }

    public static boolean passwordValid(String password) {
        return true;
    }

}
