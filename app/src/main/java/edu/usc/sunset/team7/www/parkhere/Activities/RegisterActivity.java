package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;
import edu.usc.sunset.team7.www.parkhere.Utils.Consts;
import edu.usc.sunset.team7.www.parkhere.Utils.Tools;

/**
 * Created by kunal on 10/14/16.
 */

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    @BindView(R.id.email_textinputlayout) TextInputLayout emailTextInputLayout;
    @BindView(R.id.password_textinputlayout) TextInputLayout passwordTextInputLayout;
    @BindView(R.id.firstname_textinputlayout) TextInputLayout firstNameTextInputLayout;
    @BindView(R.id.lastname_textinputlayout) TextInputLayout lastNameTextInputLayout;
    @BindView(R.id.phonenumber_textinputlayout) TextInputLayout phoneNumberTextInputLayout;

    @BindView(R.id.email_edittext) AppCompatEditText emailEditText;
    @BindView(R.id.password_edittext) AppCompatEditText passwordEditText;
    @BindView(R.id.firstname_edittext) AppCompatEditText firstNameEditText;
    @BindView(R.id.lastname_edittext) AppCompatEditText lastNameEditText;
    @BindView(R.id.phonenumber_edittext) AppCompatEditText phoneNumberEditText;

    @BindView(R.id.register_button) AppCompatButton registerButton;
    @BindView(R.id.upload_button) AppCompatButton uploadButton;

    @BindView(R.id.uploadImage) ImageView imageView;

    @BindView(R.id.provider_switch)
    SwitchCompat provideSwitch;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private Uri sourceImageUri = null;
    private String email, password, firstName, lastName, phoneNumber, profilePictureURL;
    private boolean isProvider;

    private static final int RESULT_LOAD_IMAGE = 1;

    public static void startActivity(Context context) {
        Intent i = new Intent(context, RegisterActivity.class);
        context.startActivity(i);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        imageView.setImageResource(R.mipmap.default_profile);
    }

    @OnClick(R.id.register_button)
    protected void register() {
        removeErrors();
        collectValues();
        if(checkValues()){
            writeToDatabase();
        }
    }

    private void removeErrors() {
        emailTextInputLayout.setErrorEnabled(false);
        firstNameTextInputLayout.setErrorEnabled(false);
        lastNameTextInputLayout.setErrorEnabled(false);
        phoneNumberTextInputLayout.setErrorEnabled(false);
    }

    private void collectValues() {
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        firstName = firstNameEditText.getText().toString();
        lastName = lastNameEditText.getText().toString();
        phoneNumber = phoneNumberEditText.getText().toString();
        isProvider = provideSwitch.isChecked();
    }

    private boolean checkValues(){
        if (Tools.emailValid(email)) {
            if (Tools.nameValid(firstName)) {
                if (Tools.nameValid(lastName)) {
                    if (Tools.phoneValid(phoneNumber)) {
                        if(Tools.emailValid(email))
                            return true;
                        else {
                            emailTextInputLayout.setErrorEnabled(true);
                            emailTextInputLayout.setError("Password needs to atleast 10 characters long and contain a special character");
                        }
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
        return false;
    }

    private void writeToDatabase(){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registration failed. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        } else{
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            //Add user to users database
                            mDatabase.child(Consts.USERS_DATABASE).child(uid).child(Consts.USER_FIRSTNAME).setValue(firstName);
                            mDatabase.child(Consts.USERS_DATABASE).child(uid).child(Consts.USER_LASTNAME).setValue(lastName);
                            mDatabase.child(Consts.USERS_DATABASE).child(uid).child(Consts.USER_EMAIL).setValue(email);
                            mDatabase.child(Consts.USERS_DATABASE).child(uid).child(Consts.USER_PHONENUMBER).setValue(phoneNumber);
                            mDatabase.child(Consts.USERS_DATABASE).child(uid).child(Consts.USER_IS_PROVIDER).setValue(isProvider);
                            mDatabase.child(Consts.USERS_DATABASE).child(uid).child(Consts.USER_RATING).setValue(0);
                            mDatabase.child(Consts.USERS_DATABASE).child(uid).child(Consts.USER_BALANCE).setValue(0);

                            if(sourceImageUri!=null){
                                StorageReference storageRef = storage.getReferenceFromUrl(Consts.STORAGE_URL);
                                StorageReference profileRef = storageRef.child(Consts.STORAGE_PROFILE_PICTURES);
                                UploadTask uploadTask = profileRef.child(uid).putFile(sourceImageUri);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                        Log.d(TAG, exception.toString());
                                        Toast.makeText(RegisterActivity.this, "Unable to upload the image. Please check your internet connection and try again.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                         profilePictureURL = taskSnapshot.getDownloadUrl().toString();
                                    }
                                });
                            } else {
                                profilePictureURL = Consts.USER_DEFAULT_PROFILE_PIC_URL;
                            }

                            mDatabase.child(Consts.USERS_DATABASE).child(uid).child(Consts.USER_PROFILE_PIC).setValue(profilePictureURL);
                            finish();
                        }
                    }
                });
    }

    @OnClick(R.id.upload_button)
    protected void uploadImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }

    //Method called when user selects a picture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Make sure the gallery Intent called this method
        if(requestCode==RESULT_LOAD_IMAGE && resultCode==RESULT_OK && data!=null ){
            sourceImageUri = data.getData();
            imageView.setImageURI(sourceImageUri);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}