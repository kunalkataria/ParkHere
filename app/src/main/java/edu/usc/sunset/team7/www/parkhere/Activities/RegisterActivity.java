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

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private Uri firebaseUri = null;
    private Uri sourceImageUri = null;

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
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();

        if (Tools.emailValid(email)) {
            if (Tools.nameValid(firstName)) {
                if (Tools.nameValid(lastName)) {
                    if (Tools.phoneValid(phoneNumber)) {
                        registerUser(email, password, firstName, lastName, phoneNumber);
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

    private void registerUser(final String email, final String password, final String firstName, final String lastName, final String phoneNumber) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.

                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registration failed. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            mDatabase.child("users").child(uid).child("firstname").setValue(firstName);
                            mDatabase.child("users").child(uid).child("lastname").setValue(lastName);
                            mDatabase.child("users").child(uid).child("email").setValue(email);
                            mDatabase.child("users").child(uid).child("phonenumber").setValue(phoneNumber);

                            if(sourceImageUri!=null){
                                StorageReference storageRef = storage.getReferenceFromUrl("gs://parkhere-ceccb.appspot.com");
                                StorageReference profileRef = storageRef.child("profile_pictures/");
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
                                        firebaseUri = taskSnapshot.getDownloadUrl();
                                    }
                                });
                            }
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