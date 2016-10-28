package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;

/**
 * Created by Jonathan on 10/26/16.
 */

public class TransactionActivity extends AppCompatActivity {
    private static final String TAG = "TransactionActivity";

    @BindView(R.id.paypal_button)
    Button paypalButton;
    @BindView(R.id.credit_card_button)
    Button creditCardButton;

    public static void startActivity(Context context) {
        Intent i = new Intent(context, TransactionActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.paypal_button)
    protected void paypalClicked() {
        PaypalActivity.startActivity(TransactionActivity.this);
    }

    @OnClick(R.id.credit_card_button)
    protected void creditCardClicked() {
        CreditCardActivity.startActivity(TransactionActivity.this);
    }

}
