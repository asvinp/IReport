package com.cmpe277group4.ireport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class OfficialActivity extends AppCompatActivity {

    private static final String OFFICIAL_TAG = "OFFICIAL";
    private TextView email;
    private TextView name;
    private TextView emailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);
        Intent officialIntent = getIntent();
        String emailTest = officialIntent.getExtras().getString("emailId");
        email = (TextView)findViewById(R.id.email);
        name = (TextView)findViewById(R.id.name);
        emailId = (TextView)findViewById(R.id.emailId);
        email.setText(emailTest);
    }
}
