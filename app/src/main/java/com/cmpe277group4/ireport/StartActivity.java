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
                if(SaveSharedPreference.getUserName(StartActivity.this) == null){
                    //User not logged in
                    Intent fireBaseIntent = new Intent(StartActivity.this, LoginActivity.class);
                    startActivity(fireBaseIntent);
                }else{
                    Log.d("START",SaveSharedPreference.getUserName(StartActivity.this));
                    if(SaveSharedPreference.getUserType(StartActivity.this) == LoginActivity.RESIDENT){
                        Log.d("START","Resident " + SaveSharedPreference.getUserName(StartActivity.this));
                    }else if(SaveSharedPreference.getUserType(StartActivity.this) == LoginActivity.OFFICIAL){
                        Log.d("START","Official " + SaveSharedPreference.getUserId(StartActivity.this));
                        Intent officialIntent = new Intent(StartActivity.this,OfficialActivity.class);
                        startActivity(officialIntent);
                    }else{
                        Log.d("START","Invalid User type");
                    }
                }
            }
        },START_DELAY);
    }
}
