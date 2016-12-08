package com.cmpe277group4.ireport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

public class UserSettingsActivity extends AppCompatActivity {
    private Switch emailNotification , statusChange , anonymous;
    //switchEmailNotification switchStatusChange switchAnonymous
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        //emailNotification = (TextView) findViewById(R.id.switchStatus);
        emailNotification = (Switch) findViewById(R.id.switchEmailNotification);

        statusChange = (Switch) findViewById(R.id.switchStatusChange);
        anonymous = (Switch) findViewById(R.id.switchAnonymous);


        //set the switch to ON
       // mySwitch.setChecked(true);
        //attach a listener to check for changes in state
        emailNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    //switchStatus.setText("Switch is currently ON");
                    anonymous.setChecked(false);
                    Log.d("notification ","is on ");
                }else{
                    //switchStatus.setText("Switch is currently OFF");

                    Log.d("notification ","is off ");
                }

            }
        });

        statusChange.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    //switchStatus.setText("Switch is currently ON");
                    anonymous.setChecked(false);

                    Log.d("notification ","is on ");
                }else{
                    //switchStatus.setText("Switch is currently OFF");

                    Log.d("notification ","is off ");
                }

            }
        });

        anonymous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    //switchStatus.setText("Switch is currently ON");
                    statusChange.setChecked(false);
                    emailNotification.setChecked(false);

                    Log.d("notification ","is on ");

                }else{
                    //switchStatus.setText("Switch is currently OFF");

                    Log.d("notification ","is off ");
                }

            }
        });

        //check the current state before we display the screen
        if(emailNotification.isChecked()){
            //switchStatus.setText("Switch is currently ON");

            Log.d("notification ","is on 2 ");
        }
        else {
           // switchStatus.setText("Switch is currently OFF");

            Log.d("notification ","is off 2 ");
        }
    }
    }
