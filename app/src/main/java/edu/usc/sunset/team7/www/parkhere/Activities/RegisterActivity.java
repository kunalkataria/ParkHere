package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Tools;

/**
 * Created by kunal on 10/14/16.
 */

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.register_button) AppCompatButton registerButton;

    @BindView(R.id.email_textinputlayout) TextInputLayout emailTextInputLayout;
    @BindView(R.id.firstname_textinputlayout) TextInputLayout firstNameTextInputLayout;
    @BindView(R.id.lastname_textinputlayout) TextInputLayout lastNameTextInputLayout;
    @BindView(R.id.phonenumber_textinputlayout) TextInputLayout phoneNumberTextInputLayout;

    @BindView(R.id.email_edittext) AppCompatEditText emailEditText;
    @BindView(R.id.firstname_edittext) AppCompatEditText firstNameEditText;
    @BindView(R.id.lastname_edittext) AppCompatEditText lastNameEditText;
    @BindView(R.id.phonenumber_edittext) AppCompatEditText phoneNumberEditText;

    public static void startActivity(Context context) {
        Intent i = new Intent(context, RegisterActivity.class);
        context.startActivity(i);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick (R.id.register_button)
    private void register() {
        removeErrors();
        String email = emailEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();

        if (!Tools.emailValid(email)) {
            if (!Tools.nameValid(firstName)) {
                if (!Tools.nameValid(lastName)) {
                    if (!Tools.phoneValid(phoneNumber)) {

                    } else {
                        // phone number not valid
                        phoneNumberTextInputLayout.setErrorEnabled(true);
                        phoneNumberTextInputLayout.setError("Phone number not valid");
                    }
                } else {
                    // last name not valid
                    lastNameTextInputLayout.setErrorEnabled(true);
                    lastNameTextInputLayout.setError("Last name not valid");
                }
            } else {
                // firstName not valid
                firstNameTextInputLayout.setErrorEnabled(true);
                firstNameTextInputLayout.setError("First name not valid");
            }
        } else {
            // email not valid
            emailTextInputLayout.setErrorEnabled(true);
            emailTextInputLayout.setError("Email not valid");
        }

    }

    private void removeErrors() {
        emailTextInputLayout.setErrorEnabled(false);
        firstNameTextInputLayout.setErrorEnabled(false);
        lastNameTextInputLayout.setErrorEnabled(false);
        phoneNumberTextInputLayout.setErrorEnabled(false);
    }

}
