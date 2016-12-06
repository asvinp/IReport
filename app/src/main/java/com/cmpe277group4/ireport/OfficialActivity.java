package com.cmpe277group4.ireport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class OfficialActivity extends AppCompatActivity {

    private TextView email;
    private TextView name;
    private TextView emailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);
        email = (TextView)findViewById(R.id.email);
        email.setText(SaveSharedPreference.getUserId(OfficialActivity.this));
        name = (TextView)findViewById(R.id.name);
        emailId = (TextView)findViewById(R.id.emailId);
    }
}
