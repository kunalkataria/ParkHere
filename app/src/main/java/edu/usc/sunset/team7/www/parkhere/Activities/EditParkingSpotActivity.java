package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.objectmodule.ParkingSpot;

/**
 * Created by Wyatt-Kim on 11/20/16.
 */

public class EditParkingSpotActivity extends AppCompatActivity {

    public static void startActivity(Context context, ParkingSpot parkingSpot) {
        Intent i = new Intent(context, EditParkingSpotActivity.class);
        i.putExtra(Consts.PARKING_SPOT_EDIT_EXTRA, parkingSpot);
        context.startActivity(i);
    }
}
