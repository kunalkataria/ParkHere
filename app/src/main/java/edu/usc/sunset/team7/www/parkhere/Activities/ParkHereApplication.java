package edu.usc.sunset.team7.www.parkhere.Activities;

import android.app.Application;

/**
 * Created by kunal on 10/17/16.
 */

public class ParkHereApplication extends Application {

    @Override
    public void onCreate() {
        // set up connection to server here

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // don't write any additional code here, just used for emulator
    }


}
