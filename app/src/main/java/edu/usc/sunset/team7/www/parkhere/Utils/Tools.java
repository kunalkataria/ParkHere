package edu.usc.sunset.team7.www.parkhere.Utils;

import java.util.Date;

/**
 * Created by kunal on 10/14/16.
 */

public class Tools {

    private static final String EMAIL_REGEX = "^.+\\@.+\\..+$";
    private static final String NAME_REGEX = "^[a-zA-Z ,.'-]+$";
    private static final String PHONE_REGEX = "^[0-9]{10}$";
    private static final String PASSWORD_REGEX = "^(?=.{10,})(?=.*[@#$%^&+=!?]).*$";
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

    public static String convertUnixTimeToDateString(long unix_time){
        Date date = new Date ();
        date.setTime((long)unix_time*1000);
        return date.toString();
    }

    public static boolean passwordValid(String password) {
        return password.matches(PASSWORD_REGEX);
    }

    public static String getDateString(int year, int month, int day) {
        return month + "/" + day + "/" + year;
    }

}
