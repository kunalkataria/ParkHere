package edu.usc.sunset.team7.www.parkhere.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Semaphore;

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

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
    }

    @OnClick(R.id.register_button)
    protected void register() {
        removeErrors();
        collectValues();
        if(checkValues(firstName, lastName, phoneNumber, email, password)){
            new RegisterTask().execute(email, password);
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

    public boolean checkValues(String fName, String lName, String pNumber, String emailAddress, String pword){
        boolean allValid = true;
        if(!Tools.nameValid(fName)) {
            allValid = false;
            firstNameTextInputLayout.setErrorEnabled(true);
            firstNameTextInputLayout.setError("First name not valid");
        }
        if(!Tools.nameValid(lName)) {
            allValid = false;
            lastNameTextInputLayout.setErrorEnabled(true);
            lastNameTextInputLayout.setError("Last name not valid");
        }
        if(!Tools.phoneValid(pNumber)) {
            allValid = false;
            phoneNumberTextInputLayout.setErrorEnabled(true);
            phoneNumberTextInputLayout.setError("Phone number not valid");
        }
        if(!Tools.emailValid(emailAddress)) {
            allValid = false;
            emailTextInputLayout.setErrorEnabled(true);
            emailTextInputLayout.setError("Email not valid");
        }
        if(!Tools.passwordValid(pword)) {
            allValid = false;
            passwordTextInputLayout.setErrorEnabled(true);
            passwordTextInputLayout.setError("Password needs to at least 10 characters long and contain a special character");
        }
        return allValid;
    }

    private class RegisterTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            final Semaphore mSemaphore = new Semaphore(0);
            mAuth.createUserWithEmailAndPassword(params[0], params[1])
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Registration failed. Please try again.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
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
                                    System.out.println("Profile picture path!!!"+profileRef.toString());

                                    //compress image
                                    InputStream imageStream = null;
                                    try {
                                        imageStream = getContentResolver().openInputStream(sourceImageUri);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }

                                    Bitmap bmp = BitmapFactory.decodeStream(imageStream);

                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    String path = MediaStore.Images.Media.insertImage(getContentResolver(), bmp,
                                            "Title", null);
                                    sourceImageUri = Uri.parse(path);

                                    try {
                                        stream.close();
                                        stream = null;
                                    } catch (IOException e) {

                                        e.printStackTrace();
                                    }

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
                                System.out.println("PROFILE PICTURE URL:      " + profilePictureURL);
                                mDatabase.child(Consts.USERS_DATABASE).child(uid).child(Consts.USER_PROFILE_PIC).setValue(profilePictureURL);
                                sendEmailVerification();
                                mSemaphore.release();
                            }
                        }
                    });
            try {
                mSemaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(RegisterActivity.this, "User registered! Be sure to check your email for a verification link.",
                    Toast.LENGTH_LONG).show();
            LoginActivity.startActivity(RegisterActivity.this);
            RegisterActivity.this.finish();
        }
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

    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }
}