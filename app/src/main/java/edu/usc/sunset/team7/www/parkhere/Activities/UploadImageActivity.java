package edu.usc.sunset.team7.www.parkhere.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.usc.sunset.team7.www.parkhere.R;

/**
 * Created by Jonathan on 10/23/16.
 */

public class UploadImageActivity extends AppCompatActivity {

    @BindView(R.id.image_to_upload)
    ImageView imageView;
    @BindView(R.id.confirm_button)
    AppCompatButton confirmButton;
    @BindView(R.id.select_picture)
    AppCompatButton selectPictureButton;

    private static final int RESULT_LOAD_IMAGE = 1;

    public static void startActivity(Context context) {
        System.out.print("AAAAAA");
        Intent i = new Intent(context, UploadImageActivity.class);
        System.out.print("BBBB");
        context.startActivity(i);
        System.out.print("CCCCC");
    }

    protected void onCreate(Bundle savedInstanceState) {
        System.out.print("FFFFF");
        super.onCreate(savedInstanceState);
        System.out.print("DDDDD");
        setContentView(R.layout.activity_upload_image);
        System.out.print("EEEE");
        ButterKnife.bind(this);
    }

    @OnClick(R.id.confirm_button)
    protected void confirmSelection() {
    }

    @OnClick(R.id.select_picture)
    protected void selectPicture() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }

    //Method called when user selects a picture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Make sure the gallery Intent called this method
        if(requestCode==RESULT_LOAD_IMAGE && resultCode==RESULT_OK && data!=null ){
            Uri selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
        }
    }
}