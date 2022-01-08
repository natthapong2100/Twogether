package com.example.parsemystarter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class UserProfile extends AppCompatActivity {

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout){
            ParseUser.logOut();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void clickCreatePost(View view){
        Intent intent = new Intent(getApplicationContext(), CreatePostActivity.class);
        startActivity(intent);
    }

    public void feedClick(View view){
        Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
        startActivity(intent);
    }

    public void clickChat(View view){
        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
        startActivity(intent);
    }

    ArrayList<String> users = new ArrayList<>();
    ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_user_profile);
        super.onCreate(savedInstanceState);
        setTitle("User Profile");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));

        ImageView chat = findViewById(R.id.chatImageViewUP);
        ImageView createPost = findViewById(R.id.createPostImageViewUP);
        ImageView userprofile = findViewById(R.id.userprofileImageViewUP);
        ImageView viewfeed = findViewById(R.id.feedImageViewUP);

        userprofile.setImageResource(R.drawable.userprofile1);
        viewfeed.setImageResource(R.drawable.viewfeed1);
        createPost.setImageResource(R.drawable.createpost1);
        chat.setImageResource(R.drawable.chat1);

        // set user profile
        TextView profileUsernameTextView = findViewById(R.id.signUpUsernameEditText);
        String usernameProfile = ParseUser.getCurrentUser().getUsername(); // get the current username
        profileUsernameTextView.setText("Username: " + usernameProfile);
        ImageView imageView = findViewById(R.id.userProfileImage);
        imageView.setImageResource(R.drawable.profile_gen);


        ListView listView = findViewById(R.id.listView);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked,users);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView checkedTextView = (CheckedTextView) view;
                if(checkedTextView.isChecked()){ // status
                    Log.i("Info","Checked!");
                    ParseUser.getCurrentUser().add("isFollowing",users.get(position));
                }
                else{
                    Log.i("Info","NOT Checked!"); // remove(users.get(position));
                    ParseUser.getCurrentUser().getList("isFollowing").remove(users.get(position));
                    List tempUsers = ParseUser.getCurrentUser().getList("isFollowing");
                    ParseUser.getCurrentUser().remove("isFollowing");
                    ParseUser.getCurrentUser().put("isFollowing",tempUsers);

                }
                ParseUser.getCurrentUser().saveInBackground();
            }
        });
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username" ,ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null && objects.size() > 0){
                    Log.i("Error", "There is size");
                    for(ParseUser user : objects){
                        users.add(user.getUsername()); // null -> empty
                        Log.i("I", "add already!");
                    }
                    adapter.notifyDataSetChanged();

                    for (String username: users){
                        Log.i("I", "in user");
                        try{
                            if(ParseUser.getCurrentUser().getList("isFollowing").contains(username)){
                                listView.setItemChecked(users.indexOf(username),true);
                            }
                        } catch(NullPointerException ne){
                            ne.printStackTrace();
                        }

                    }

                }
            }
        });

    }


}