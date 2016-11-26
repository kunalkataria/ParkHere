package edu.usc.sunset.team7.www.parkhere.Utils;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
        return email != null && email.matches(EMAIL_REGEX);
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
        return password != null && password.matches(PASSWORD_REGEX);
    }

    public static String getDateString(DateTime dateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("MM/dd/yyyy hh:mm aa");
        return dateTimeFormatter.print(dateTime);
    }

    public static String getDateString(long unixTimeStamp) {
        DateTime dateTime = new DateTime(unixTimeStamp * 1000);
        return getDateString(dateTime);
    }

    public static String getDateString(int year, int month, int day, int hour, int minute) {

        return month + "/" + day + "/" + year + " " + hour + ":" + minute;
    }

    public static String arrayToString(int[] mArray) {
        StringBuilder sBuilder = new StringBuilder();
        for (int i  = 0; i < mArray.length - 1; i++) {
            sBuilder.append(mArray[i]);
            sBuilder.append(",");
        }
        sBuilder.append(mArray[mArray.length - 1]);
        return sBuilder.toString();
    }

}
