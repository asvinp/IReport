package com.cmpe277group4.ireport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    TextView name, address, email;
    Button signout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        address = (TextView) findViewById(R.id.Address);
        name = (TextView) findViewById(R.id.Name);
        email = (TextView) findViewById(R.id.Email);


        email.setText(SaveSharedPreference.getUserId(ProfileActivity.this));

    }
}
