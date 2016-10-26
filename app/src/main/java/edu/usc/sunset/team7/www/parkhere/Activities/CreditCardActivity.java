package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Hashtable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;

/**
 * Created by Jonathan on 10/26/16.
 */

public class CreditCardActivity extends AppCompatActivity {

    @BindView(R.id.name_textinputlayout)
    TextInputLayout nameTextInputLayout;
    @BindView(R.id.creditcard_textinputlayout)
    TextInputLayout creditCardTextInputLayout;
    @BindView(R.id.security_textinputlayout)
    TextInputLayout securityTextInputLayout;
    @BindView(R.id.address_textinputlayout)
    TextInputLayout addressTextInputLayout;
    @BindView(R.id.city_textinputlayout)
    TextInputLayout cityTextInputLayout;
    @BindView(R.id.state_textinputlayout)
    TextInputLayout stateTextInputLayout;
    @BindView(R.id.zipcode_textinputlayout)
    TextInputLayout zipCodeTextInputLayout;

    @BindView(R.id.name_edittext)
    AppCompatEditText nameEditText;
    @BindView(R.id.creditcard_edittext)
    AppCompatEditText creditCardEditText;
    @BindView(R.id.security_edittext)
    AppCompatEditText securityEditText;
    @BindView(R.id.address_edittext)
    AppCompatEditText addressEditText;
    @BindView(R.id.city_edittext)
    AppCompatEditText cityEditText;
    @BindView(R.id.state_edittext)
    AppCompatEditText stateEditText;
    @BindView(R.id.zipcode_edittext)
    AppCompatEditText zipCodeEditText;

    @BindView(R.id.confirm_button)
    Button confirmButton;

    @BindView(R.id.myRadioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.visa_rButton)
    RadioButton visa;
    @BindView(R.id.mastercard_rButton)
    RadioButton mastercard;
    @BindView(R.id.american_express_rButton)
    RadioButton americanExpress;
    @BindView(R.id.discover_rButton)
    RadioButton discover;

    private String name, creditCardNumber, securityCode, address, city, state, zipcode;

    private Hashtable<Integer, String> creditCardTypes;

    public static void startActivity(Context context) {
        Intent i = new Intent(context, CreditCardActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);
        ButterKnife.bind(this);

        name = "";
        creditCardNumber = "";
        securityCode = "";
        address = "";
        city ="";
        state="";
        zipcode="";

        creditCardTypes = new Hashtable<Integer, String>();
        creditCardTypes.put(R.id.visa_rButton, "visa");
        creditCardTypes.put(R.id.mastercard_rButton, "mastercard");
        creditCardTypes.put(R.id.discover_rButton, "discover");
        creditCardTypes.put(R.id.american_express_rButton, "american_express");
    }

    @OnClick(R.id.confirm_button)
    protected void confirm() {
        collectValues();
        if(checkValues()){
            writeToDatabase();
        }
    }

    private void collectValues(){
        name = nameEditText.getText().toString();
        creditCardNumber = creditCardEditText.getText().toString();
        securityCode = securityEditText.getText().toString();
        address = addressEditText.getText().toString();
        city = cityEditText.getText().toString();
        state = stateEditText.getText().toString();
        zipcode = zipCodeEditText.getText().toString();
    }

    private boolean checkValues(){
        if(radioGroup.getCheckedRadioButtonId() != -1){
            if(!name.equals("'")){
                if(checkCreditCard()){
                    if(checkSecurityCode()){
                        if(checkAddress()){
                            if(!city.equals("")){
                                if(!state.equals("")){
                                    if(checkZipCode()){
                                        writeToDatabase();
                                        return true;
                                    } else{
                                        zipCodeTextInputLayout.setErrorEnabled(true);
                                        zipCodeTextInputLayout.setError("Invalid zip code. Please try again.");
                                    }
                                } else{
                                    stateTextInputLayout.setErrorEnabled(true);
                                    stateTextInputLayout.setError("Please enter a state.");
                                }
                            } else{
                                cityTextInputLayout.setErrorEnabled(true);
                                cityTextInputLayout.setError("Please enter a city.");
                            }
                        } else{
                            addressTextInputLayout.setErrorEnabled(true);
                            addressTextInputLayout.setError("Invalid address. Please try again.");
                        }
                    } else{
                        securityTextInputLayout.setErrorEnabled(true);
                        securityTextInputLayout.setError("Invalid security code. Please try again.");
                    }
                } else{
                    creditCardTextInputLayout.setErrorEnabled(true);
                    creditCardTextInputLayout.setError("Invalid credit card number. Please try again.");
                }
            } else{
                nameTextInputLayout.setErrorEnabled(true);
                nameTextInputLayout.setError("Please enter your name.");
            }
        } else{
            Toast.makeText(CreditCardActivity.this, "Please select a credit card type.",
                    Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean checkCreditCard(){
        return true;
    }

    private boolean checkSecurityCode(){
        return true;
    }

    private boolean checkAddress(){
        return true;
    }

    private boolean checkZipCode(){
        return true;
    }

    private void writeToDatabase(){

    }
}
