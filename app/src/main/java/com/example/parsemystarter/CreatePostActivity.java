package com.example.parsemystarter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

public class CreatePostActivity extends AppCompatActivity {

    Button postButton;
    EditText addCaptionTextField;

    ImageView addImageView;
    Bitmap bitmap;
    byte[] byteArray;

    ParseFile file;
    ParseObject object;

    public void getPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1); // still working -> ok
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImage = data.getData();

        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            try {

                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                Log.i("Image Selected", "Good work"); // it's appear
                Log.i("info", ParseUser.getCurrentUser().getUsername());

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream); // quality must be from 1 to 100 only!!!

                byteArray = stream.toByteArray();


            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void clickSelectPicture(View view){
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            getPhoto();
        }


    }

    // send button
    public void clickPost(View view){

        ParseObject tweet = new ParseObject("Tweet");

        file = new ParseFile("image3.jpg", byteArray);

        tweet.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Log.i("Notification", "image sharing success!!");
                    Toast.makeText(CreatePostActivity.this, "Image has been add!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.i("Notification", "image sharing NOT Working!!");
                    Toast.makeText(CreatePostActivity.this, "There is issue for uploading image!!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });


        tweet.put("tweet", addCaptionTextField.getText().toString());
        tweet.put("username", ParseUser.getCurrentUser().getUsername());
        tweet.put("image", file);

        tweet.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Log.i("Post", "success");
                    Toast.makeText(CreatePostActivity.this, "Posted success!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                    startActivity(intent);
                }
                else{
                    Log.i("Sending Post", "failure!!");
                    Toast.makeText(CreatePostActivity.this, "Posted Failed! :(", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
        setTitle("Create Post");

        postButton = findViewById(R.id.postButton);
        addCaptionTextField = findViewById(R.id.addCaptionTextField);
        addImageView = findViewById(R.id.addImageView);

    }
}