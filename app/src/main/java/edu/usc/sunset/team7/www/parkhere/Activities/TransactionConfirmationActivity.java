package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.objectmodule.Listing;

/**
 * Created by Jonathan on 10/26/16.
 */
public class TransactionConfirmationActivity extends AppCompatActivity {

    //Listing variables
    private Listing listing;

    private String paymentType;

    //Paypal variables
    private String email;

    //Credit Card
    private String name, creditCardNumber, securityCode, address, city, state, zipcode;


    public static void startActivity(Context context) {
        Intent i = new Intent(context, PaypalActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);
        ButterKnife.bind(this);
    }

}
