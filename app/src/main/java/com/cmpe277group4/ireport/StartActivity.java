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

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                int i = 0;
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null){
                    Intent loginIntent = new Intent(StartActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    return;
                }
                    for(UserInfo info : firebaseAuth.getCurrentUser().getProviderData()){
                        Log.d(START_ACTIVITY_TAG, "" + i++);
                        if(info == null){
                            Intent loginIntent = new Intent(StartActivity.this, LoginActivity.class);
                            startActivity(loginIntent);
                        }


                        if(info.getProviderId().contentEquals("google.com")){
                            Log.d(START_ACTIVITY_TAG,info.getProviderId());
                            Log.d(START_ACTIVITY_TAG,"Official Login" + info.getEmail());
                            Intent officialIntent = new Intent(StartActivity.this, OfficialActivity.class);
                            officialIntent.putExtra("emailId",info.getEmail());
                            startActivity(officialIntent);
                        }else if(info.getProviderId().contentEquals("facebook.com")){
                            Log.d(START_ACTIVITY_TAG,info.getProviderId());
                            Log.d(START_ACTIVITY_TAG,"Resident Login through fb");
                            Intent reportIntent = new Intent(StartActivity.this, ReportActivity.class);
                            reportIntent.putExtra("resident_id", info.getEmail());
                            startActivity(reportIntent);
                        }else{
                            Log.d(START_ACTIVITY_TAG,info.getProviderId());
                            Log.d(START_ACTIVITY_TAG,"Resident Login normal" + info.getEmail());
                            Intent reportIntent = new Intent(StartActivity.this, ReportActivity.class);
                            reportIntent.putExtra("resident_id",info.getEmail());
                            startActivity(reportIntent);
                            return;
                        }
                }
            }
        });


//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(SaveSharedPreference.getUserName(StartActivity.this) == null){
//                    //User not logged in
//                    Intent fireBaseIntent = new Intent(StartActivity.this, LoginActivity.class);
//                    startActivity(fireBaseIntent);
//                }else{
//                    Log.d("START",SaveSharedPreference.getUserName(StartActivity.this));
//                    if(SaveSharedPreference.getUserType(StartActivity.this) == LoginActivity.RESIDENT){
//                        Log.d("START","Resident " + SaveSharedPreference.getUserName(StartActivity.this));
//                    }else if(SaveSharedPreference.getUserType(StartActivity.this) == LoginActivity.OFFICIAL){
//                        Log.d("START","Official " + SaveSharedPreference.getUserId(StartActivity.this));
//                        Intent officialIntent = new Intent(StartActivity.this,OfficialActivity.class);
//                        startActivity(officialIntent);
//                    }else{
//                        Log.d("START","Invalid User type");
//                    }
//                }
//            }
//        },START_DELAY);
    }
}
