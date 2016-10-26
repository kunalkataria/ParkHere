package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Tools;

/**
 * Created by Jonathan on 10/26/16.
 */

public class PaypalActivity extends AppCompatActivity {


    @BindView(R.id.email_textinputlayout)
    TextInputLayout emailTextInputLayout;
    @BindView(R.id.password_textinputlayout)
    TextInputLayout passwordTextInputLayout;

    @BindView(R.id.email_edittext)
    AppCompatEditText emailEditText;
    @BindView(R.id.password_edittext)
    AppCompatEditText passwordEditText;

    @BindView(R.id.validate_button)
    Button validateButton;

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

    @OnClick(R.id.validate_button)
    protected void validate() {
        if (!emailEditText.getText().toString().equals("")){
            if (Tools.emailValid(emailEditText.getText().toString())) {
                if (!passwordEditText.getText().toString().equals("")) {
                    //START CONFIRMATION ACTIVITY
                    //Pass email address
                } else {
                    passwordTextInputLayout.setErrorEnabled(true);
                    passwordTextInputLayout.setError("Please enter a password.");
                }
            } else {
                emailTextInputLayout.setErrorEnabled(true);
                emailTextInputLayout.setError("Email not valid.");
            }
        } else {
            emailTextInputLayout.setErrorEnabled(true);
            emailTextInputLayout.setError("Please enter an email address.");
        }
    }
}
