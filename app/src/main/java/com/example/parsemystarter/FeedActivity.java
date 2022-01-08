package com.example.parsemystarter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class FeedActivity extends AppCompatActivity {

    SimpleAdapter simpleAdapter;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.logout){
            ParseUser.logOut();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void getPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1); // still working -> ok
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImage = data.getData();

        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                Log.i("Image Selected", "Good work"); // it's appear
                Log.i("info", ParseUser.getCurrentUser().getUsername());

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream); // quality must be from 1 to 100 only!!!

                byte[] byteArray = stream.toByteArray();

                ParseFile file = new ParseFile("image3.jpg", byteArray);

                ParseObject object = new ParseObject("Image");

                object.put("image", file);

                object.put("username", ParseUser.getCurrentUser().getUsername());

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null){
                            Log.i("Notification", "image sharing success!!");
                            Toast.makeText(FeedActivity.this, "Image has been shared!!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Log.i("Notification", "image sharing NOT Working!!");
                            Toast.makeText(FeedActivity.this, "There is issue for uploading image!!", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });

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


    // create post *** go to CreatePostClass
    public void clickCreatePost(View view){
        Intent intent = new Intent(getApplicationContext(), CreatePostActivity.class);
        startActivity(intent);
    }
    public void clickChat(View view){
        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
        startActivity(intent);
    }
    public void clickUserProfile(View view){
        Intent intent = new Intent(getApplicationContext(), UserProfile.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        setTitle("Your Feed");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));

        final ListView listView = findViewById(R.id.listView);

        final List<Map<String,String>> tweetData = new ArrayList<>(); // ?
        final ArrayList<String> usernamesArrayList = new ArrayList<>(); // pass username
        final ArrayList<String> tweetArrayList = new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Tweet");

        if (ParseUser.getCurrentUser().getList("isFollowing") != null){
            query.whereContainedIn("username", ParseUser.getCurrentUser().getList("isFollowing"));
            query.orderByDescending("createdAt");
            query.setLimit(30);

            Log.i("Important", "This is Here");

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        for (ParseObject tweet : objects) {
                            Map<String,String> tweetInfo = new HashMap<>();
                            tweetInfo.put("content",tweet.getString("tweet"));
                            tweetInfo.put("username",tweet.getString("username"));

//                        usernamesArrayList.add(tweet.getString("username")); // fro keep only
                            tweetArrayList.add(tweet.getString("tweet"));
                            usernamesArrayList.add(tweet.getString("username"));

                            tweetData.add(tweetInfo);
                        }

                        simpleAdapter = new SimpleAdapter(FeedActivity.this, tweetData,
                                android.R.layout.simple_list_item_2, new String[] {"content","username"}
                                , new int[] {android.R.id.text1,android.R.id.text2});

                        listView.setAdapter(simpleAdapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Intent intent = new Intent(getApplicationContext(), UserFeedActivity.class);

                                intent.putExtra("tweet", tweetArrayList.get(i));
                                intent.putExtra("username", usernamesArrayList.get(i));
                                startActivity(intent);
                            }
                        });

                    }
                }
            });
        }
        else {
            Toast.makeText(this, "Welcome to Twogether!!", Toast.LENGTH_SHORT).show();
            Log.i("I", "You must follow someone!!");

            Intent intent = new Intent(getApplicationContext(), UserProfile.class);
            startActivity(intent);
        }





    }

}