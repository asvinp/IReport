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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class StartActivity extends AppCompatActivity {

    FirebaseAuth mAuth;


    private FirebaseActivity firebase = new FirebaseActivity();
    private AsyncHttpClient loginClient = new AsyncHttpClient();
    private JSONObject serverDataJSON = new JSONObject();
    private StringEntity serverDataEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //Delay to show the start page for 3 seconds using handler
        final int START_DELAY = 1000;

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null){
                    Log.d("START_AUTH","signed_out");
                    Intent fireBaseIntent = new Intent(StartActivity.this, LoginActivity.class);
                    startActivity(fireBaseIntent);
                }else{
                    Log.d("Auth"," " + user.getEmail());
                    Log.d("START_AUTH", "onAuthStateChanged:signed_in:" + user.getUid());
                    for(UserInfo userInfo: mAuth.getCurrentUser().getProviderData()){
                        Log.d("AUTH",userInfo.getProviderId());
                        if(userInfo.getProviderId() == "google.com"){
                            Log.d("AUTH","Official Signed In");
                            updateUIToOfficial(userInfo.getEmail());
                        }else {
                            Log.d("AUTH","Resident Signed In");
                            try {
                                fetchResidentData(userInfo.getEmail());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
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
//
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

    private void updateUIToOfficial(String email){
        Intent officialIntent = new Intent(StartActivity.this, ProfileActivity.class);
        officialIntent.putExtra("id",email);
        startActivity(officialIntent);
    }

    private void updateUI(String email, String name){
//        Log.d("UIUPDATE",email);
//        Log.d("UIUPPDATE",name);
        Intent residentIntent = new Intent(StartActivity.this, ProfileActivity.class);
        residentIntent.putExtra("email", email);
        residentIntent.putExtra("name",name);
        startActivity(residentIntent);
    }

    private void fetchResidentData(String email) throws JSONException, UnsupportedEncodingException {
        final String resident_id = email;
        serverDataJSON.put("id",email);
        serverDataEntity = new StringEntity(serverDataJSON.toString());
        loginClient.get(StartActivity.this, getString(R.string.server_url) + "/getResidentData", serverDataEntity, "application/json", new AsyncHttpResponseHandler() {

            private final String TAG = "SERVER_LOGIN";

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                progressDialog.hide();
                Log.d(TAG,"Got User Data");
                String residentData = new String(responseBody);
                Log.d(TAG,residentData);
                Intent reportActivity = new Intent(StartActivity.this,ReportActivity.class);
                reportActivity.putExtra("resident_id",resident_id);
                startActivity(reportActivity);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                progressDialog.hide();
                Log.d(TAG,"Failed to get User data : Status Code : " + statusCode );
            }
        });
    }
}
