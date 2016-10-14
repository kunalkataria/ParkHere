package edu.usc.sunset.team7.www.parkhere.Activities;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;

/**
 * Created by kunal on 10/12/16.
 */

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.email_textinputlayout) TextInputLayout emailTextInputLayout;
    @BindView(R.id.password_textinputlayout) TextInputLayout passwordTextInputLayout;
    @BindView(R.id.email_edittext) AppCompatEditText emailEditText;
    @BindView(R.id.password_edittext) AppCompatEditText passwordEditText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick (R.id.login_button)
    private void attemptLogin() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
    }

    @OnClick (R.id.register_button)
    private void moveToRegister() {
        RegisterActivity.startActivity(this);
    }
}
