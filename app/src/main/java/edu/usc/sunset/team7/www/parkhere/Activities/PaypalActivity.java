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
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
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

    private String email, password;

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
        email = "";
        password = "";
    }

    @OnClick(R.id.validate_button)
    protected void validate() {
        collectValues();
        if (checkValues()) {
            Intent intent = new Intent(PaypalActivity.this, TransactionConfirmationActivity.class);
            Bundle bundle = getIntent().getExtras();
            bundle.putString(Consts.PAYPAL_EMAIL, email);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void collectValues() {
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
    }

    public boolean checkValues() {
        if (Tools.emailValid(emailEditText.getText().toString())) {
            if (!passwordEditText.getText().toString().equals("")) {
                return true;
            } else {
                passwordTextInputLayout.setErrorEnabled(true);
                passwordTextInputLayout.setError("Please enter a password.");
            }
        } else {
            emailTextInputLayout.setErrorEnabled(true);
            emailTextInputLayout.setError("Invalid email. Please try again.");
        }
        return false;
    }
}


