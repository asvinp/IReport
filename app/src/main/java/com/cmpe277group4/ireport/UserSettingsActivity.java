package com.cmpe277group4.ireport;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class UserSettingsActivity extends AppCompatActivity {
    private Switch emailNotification , statusChange , anonymous;
    //switchEmailNotification switchStatusChange switchAnonymous

    private AsyncHttpClient updateClient = new AsyncHttpClient();
    private JSONObject serverDataJSON = new JSONObject();
    private StringEntity serverDataEntity;

    private static final String PROFILE_TAG = "PROFILE";
    private Button saveButtonText;
    private int eNot = 1;
    private int sChange = 1;
    private int  ana = 0;
    String email;
    public String resident_id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        //emailNotification = (TextView) findViewById(R.id.switchStatus);
        emailNotification = (Switch) findViewById(R.id.switchEmailNotification);

        statusChange = (Switch) findViewById(R.id.switchStatusChange);
        anonymous = (Switch) findViewById(R.id.switchAnonymous);
        Intent intent = getIntent();
        resident_id = intent.getExtras().getString("resident_id");


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
                    eNot = 1;
                    Log.d("notification ","is on ");
                }else{
                    //switchStatus.setText("Switch is currently OFF");

                    Log.d("notification ","is off ");
                    eNot = 0;

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
                    sChange = 1;
                }else{
                    //switchStatus.setText("Switch is currently OFF");

                    Log.d("notification ","is off ");
                    sChange = 0 ;
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
                    ana =1 ;


                }else{
                    //switchStatus.setText("Switch is currently OFF");

                    Log.d("notification ","is off ");
                    ana = 0 ;
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

//eNot sChange ana

        saveButtonText = (Button) findViewById(R.id.buttonSave);
        saveButtonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    //emailNotification , statusChange , anonymous;

                    serverDataJSON.put("resident_id",resident_id);
                    serverDataJSON.put("emailNotification",eNot);
                    serverDataJSON.put("statusChange",sChange);
                    serverDataJSON.put("anonymous",ana);
                    serverDataEntity = new StringEntity(serverDataJSON.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                updateClient.get(UserSettingsActivity.this, getString(R.string.server_url) + "updateSettings", serverDataEntity, "application/json", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d(PROFILE_TAG,"User profile Updated");
                        Intent reportActivity = new Intent(UserSettingsActivity.this,ReportActivity.class);
                        reportActivity.putExtra("resident_id",resident_id);
                        startActivity(reportActivity);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d(PROFILE_TAG,  " Failure Status : " + Integer.toString(statusCode));
                    }
                });

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.add:
            Intent reportActivity = new Intent(UserSettingsActivity.this,ReportActivity.class);
            reportActivity.putExtra("resident_id",resident_id);
            startActivity(reportActivity);
            return true;
        case R.id.myReport:
            Intent residentActivity = new Intent(UserSettingsActivity.this,ResidentActivity.class);
            residentActivity.putExtra("resident_id",resident_id);
            startActivity(residentActivity);
            return true;
        case R.id.update:
            Intent updateActivity = new Intent(UserSettingsActivity.this,UpdateActivity.class);
            updateActivity.putExtra("resident_id",resident_id);
            startActivity(updateActivity);
            return true;
        case R.id.signout:
            FirebaseAuth.getInstance().signOut();
            Intent goBackLogin = new Intent(UserSettingsActivity.this,LoginActivity.class);
            startActivity(goBackLogin);
            return(true);
        case R.id.exit:
            finish();
            return(true);
    }
        return(super.onOptionsItemSelected(item));
    }

}
