package com.cmpe277group4.ireport;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class StartActivity extends AppCompatActivity {
    private static final String START_ACTIVITY_TAG = "Start_Activity";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //Delay to show the start page for 3 seconds using handler
        final int START_DELAY = 1000;

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent fireBaseIntent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(fireBaseIntent);
            }
        },START_DELAY);
    }
}