package com.example.parsemystarter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    Boolean loginModeActive = true;
    TextView loginTextView;
    EditText usernameEditText;
    EditText passwordEditText;
    EditText confirmPasswordEditText;

    public void showUserList(){
        Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent){
        // เมื่อกด enter มันจะต้องไป go
        if(i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
            loginClicked(view);
        }
        return false;
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.signUpTextView){ // check ว่าที่กดจริงๆ คือเปน loginTextView นะ
            Button loginButton = findViewById(R.id.loginButton);

            if (loginModeActive){
                loginModeActive = false;
                loginButton.setText("Sign Up"); // **
                loginTextView.setText("or, Log in");
                confirmPasswordEditText.setVisibility(View.VISIBLE);
            }
            else{
                loginModeActive = true;
                loginButton.setText("Log in");
                loginTextView.setText("or, Sign Up");
                confirmPasswordEditText.setVisibility(View.INVISIBLE);
            }
        }
        else if(view.getId() == R.id.logoImageView || view.getId() == R.id.backgroundLayout){ // check ว่าคลิกที่ logo, bg keyboard จะต้องหาย
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if(inputMethodManager.isAcceptingText()) { // add from the Q&A section
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }

    }

    public void loginClicked(View view){

        if (usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")){
            Toast.makeText(this, "A username and a password are required.", Toast.LENGTH_SHORT).show();
        }
        else{
            if (!loginModeActive){
                // sign up
                ParseUser user = new ParseUser();
                user.setUsername(usernameEditText.getText().toString());

                if (passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())){
                    user.setPassword(passwordEditText.getText().toString()); // ใส่ password ไปแล้ว

                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){ // if no error occur, so sign up success
                                Log.i("Sign up", "Success");
                                showUserList();
                            }
                            else{
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(this, "Password don't match.", Toast.LENGTH_SHORT).show();
                }


            }
            else {
                // log in
                ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) { // if there is user inside, so the login is success
                            Log.i("Log in","Success");
                            showUserList();
                        } else {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // change this to "activity main"

        setTitle("Twogether");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));

        loginTextView = findViewById(R.id.signUpTextView);
        loginTextView.setOnClickListener(this);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);

        ImageView logoImageView = findViewById(R.id.logoImageView);
        ConstraintLayout backgroundLayout = findViewById(R.id.backgroundLayout);
        logoImageView.setOnClickListener(this);
        backgroundLayout.setOnClickListener(this);

        passwordEditText.setOnKeyListener(this);

        if (ParseUser.getCurrentUser() != null){ // if there is current user in the system, so log out
            ParseUser.logOut();

        }


        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }



}