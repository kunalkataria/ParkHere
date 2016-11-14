package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Hashtable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;

/**
 * Created by Jonathan on 10/26/16.
 */

public class CreditCardActivity extends AppCompatActivity {

    @BindView(R.id.name_textinputlayout) TextInputLayout nameTextInputLayout;
    @BindView(R.id.creditcard_textinputlayout) TextInputLayout creditCardTextInputLayout;
    @BindView(R.id.security_textinputlayout) TextInputLayout securityTextInputLayout;
    @BindView(R.id.address_textinputlayout) TextInputLayout addressTextInputLayout;
    @BindView(R.id.city_textinputlayout) TextInputLayout cityTextInputLayout;
    @BindView(R.id.state_textinputlayout) TextInputLayout stateTextInputLayout;
    @BindView(R.id.zipcode_textinputlayout) TextInputLayout zipCodeTextInputLayout;

    @BindView(R.id.name_edittext) AppCompatEditText nameEditText;
    @BindView(R.id.creditcard_edittext) AppCompatEditText creditCardEditText;
    @BindView(R.id.security_edittext) AppCompatEditText securityEditText;
    @BindView(R.id.address_edittext) AppCompatEditText addressEditText;
    @BindView(R.id.city_edittext) AppCompatEditText cityEditText;
    @BindView(R.id.state_edittext) AppCompatEditText stateEditText;
    @BindView(R.id.zipcode_edittext) AppCompatEditText zipCodeEditText;

    @BindView(R.id.confirm_button) Button confirmButton;

    @BindView(R.id.myRadioGroup) RadioGroup radioGroup;
    @BindView(R.id.visa_rButton) RadioButton visa;
    @BindView(R.id.mastercard_rButton) RadioButton mastercard;
    @BindView(R.id.american_express_rButton) RadioButton americanExpress;
    @BindView(R.id.discover_rButton) RadioButton discover;

    @BindView(R.id.month_np) NumberPicker monthNumberPicker;

    @BindView(R.id.year_np) NumberPicker yearNumberPicker;

    private String name, creditCardNumber, securityCode, month, year, address, city, state, zipcode, creditCardType;

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
        city = "";
        state = "";
        zipcode = "";

        creditCardTypes = new Hashtable<Integer, String>();
        creditCardTypes.put(R.id.visa_rButton, Consts.VISA);
        creditCardTypes.put(R.id.mastercard_rButton, Consts.MASTERCARD);
        creditCardTypes.put(R.id.discover_rButton, Consts.DISCOVER);
        creditCardTypes.put(R.id.american_express_rButton, Consts.AMERICAN_EXPRESS);

        monthNumberPicker.setMinValue(1); //from array first value
        monthNumberPicker.setMaxValue(12); //to array last value
        monthNumberPicker.setWrapSelectorWheel(true);

        yearNumberPicker.setMinValue(2016);
        yearNumberPicker.setMaxValue(2030);
    }

    @OnClick(R.id.confirm_button)
    protected void confirm() {
        collectValues();
        if(checkValues()) {

            Intent i = new Intent(CreditCardActivity.this, TransactionConfirmationActivity.class);
            Bundle b = getIntent().getExtras();

            //Start confirmation
            //pass data
            b.putString(Consts.CREDIT_CARD_NAME, name);
            b.putString(Consts.CREDIT_CARD_TYPE, creditCardType);
            b.putString(Consts.CREDIT_CARD_NUMBER, creditCardNumber);
            b.putString(Consts.SECURITY_CODE, securityCode);
            b.putString(Consts.EXPIRATION_MONTH, month);
            b.putString(Consts.EXPIRATION_YEAR, year);
            b.putString(Consts.ADDRESS, address);
            b.putString(Consts.CITY, city);
            b.putString(Consts.STATE, state);
            b.putString(Consts.ZIPCODE, zipcode);
            i.putExtras(b);
            startActivity(i);
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
        creditCardType = creditCardTypes.get(radioGroup.getCheckedRadioButtonId());

        month = "" + monthNumberPicker.getValue();
        year = "" + yearNumberPicker.getValue();
    }

    public boolean checkValues(){
        boolean isValid = true;
        if(radioGroup.getCheckedRadioButtonId() == -1){
            Toast.makeText(CreditCardActivity.this, "Please select a credit card type.",
                    Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if(name.equals("'")) {
            nameTextInputLayout.setErrorEnabled(true);
            nameTextInputLayout.setError("Please enter your name.");
            isValid = false;
        }
        if(!checkCreditCard()) {
            creditCardTextInputLayout.setErrorEnabled(true);
            creditCardTextInputLayout.setError("Invalid credit card number. Please try again.");
            isValid = false;
        }
        if(!checkSecurityCode()){
            securityTextInputLayout.setErrorEnabled(true);
            securityTextInputLayout.setError("Invalid security code. Please try again.");
            isValid = false;
        }
        if(!checkExpirationDate()) {
            Toast.makeText(CreditCardActivity.this, "Please enter an expiration date.",
                    Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (!checkAddress()) {
            addressTextInputLayout.setErrorEnabled(true);
            addressTextInputLayout.setError("Invalid address. Please try again.");
            isValid = false;
        }

        if (city.equals("")) {
            cityTextInputLayout.setErrorEnabled(true);
            cityTextInputLayout.setError("Please enter a city.");
            isValid = false;
        }
        if (state.equals("")) {
            stateTextInputLayout.setErrorEnabled(true);
            stateTextInputLayout.setError("Please enter a state.");
            isValid = false;
        }
        if (checkZipCode()) {
            zipCodeTextInputLayout.setErrorEnabled(true);
            zipCodeTextInputLayout.setError("Invalid zip code. Please try again.");
            isValid = false;
        }

        return isValid;
    }

    private boolean checkCreditCard() {
        return creditCardNumber.length()<19 && creditCardNumber.length()>12 && isLong(creditCardNumber);
    }

    private boolean checkSecurityCode() {
        return securityCode.length() < 5 && securityCode.length() > 2 && isLong(securityCode);
    }

    private boolean checkExpirationDate() {
        return !month.equals("") && !year.equals("");
    }

    private boolean checkAddress(){
        return !address.equals("");
    }

    private boolean checkZipCode(){
        return zipcode.length()==6 && isLong(zipcode);
    }

    private boolean isLong(String s){
        try{
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException nfe){
            return false;
        }
    }
}