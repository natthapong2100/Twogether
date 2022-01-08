package com.example.parsemystarter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.time.format.TextStyle;
import java.util.List;


public class UserFeedActivity extends AppCompatActivity {

    LinearLayout linLayout;

    // create a post picture
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));

        Intent intent = getIntent();

        String tweet = intent.getStringExtra("tweet");
        String user = intent.getStringExtra("username");

        setTitle("Twogether");

        TextView captionText = new TextView(getApplicationContext());
        TextView userText = new TextView(getApplicationContext());

        linLayout = findViewById(R.id.linLayout);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Tweet");

        query.whereEqualTo("tweet", tweet);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null && objects.size() > 0){
                    for (ParseObject object : objects){
                        ParseFile file = (ParseFile) object.get("image");

                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {

                                if (e == null && data != null){
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    ImageView imageView = new ImageView(getApplicationContext());

                                    imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                    ));

                                    imageView.setImageBitmap(bitmap); // bitmap -> imageView -> linLayout

                                    linLayout.addView(imageView); // for Image

                                    captionText.setText(tweet); captionText.setTextSize(18); captionText.setTextColor(Color.DKGRAY);
                                    userText.setText(user); userText.setTextSize(28); userText.setTextColor(Color.BLACK);
                                    linLayout.addView(userText);
                                    linLayout.addView(captionText);
                                }

                            }
                        });

                    }
                }

            }
        });


    }
}