package com.cmpe277group4.ireport;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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
                    Intent fireBaseIntent = new Intent(StartActivity.this, LoginActivity.class);
                    startActivity(fireBaseIntent);
                }else{
                    if(SaveSharedPreference.getUserType(StartActivity.this) == 0){
                        Log.d("START","Resident " + SaveSharedPreference.getUserName(StartActivity.this));
                    }else if(SaveSharedPreference.getUserType(StartActivity.this) == 1){
                        Log.d("START","Official " + SaveSharedPreference.getUserName(StartActivity.this));
                    }else{
                        Log.d("START","Invalid User type");
                    }
                }
            }
        },START_DELAY);
    }
}
