package com.cmpe277group4.ireport;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StartActivity extends AppCompatActivity {

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
                if(SaveSharedPreference.getUserName(StartActivity.this).length() == 0){
                    //User not logged in
                    Intent fireBaseIntent = new Intent(StartActivity.this, RegistrationActivity.class);
                    startActivity(fireBaseIntent);
                }else{
                    //User looged in

                }
            }
        },START_DELAY);
    }
}
