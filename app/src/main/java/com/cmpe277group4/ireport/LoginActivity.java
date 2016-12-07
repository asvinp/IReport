package com.cmpe277group4.ireport;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.entity.StringEntity;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    //User type
    public static final int RESIDENT = 0;
    public static final int OFFICIAL = 1;

    private static final String TAG = "Auth";
    private static final String SERVER_TAG = "Server";
    private LoginButton loginButton;
    private Button gmailLogin;

    private EditText emailText;
    private EditText passText;
    private Button registerButton;

    private ProgressDialog progressDialog;

    private final FactoryMethods factory = new FactoryMethods();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager callBackManager;

    private GoogleApiClient apiClient;
    private boolean officialReg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);



        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("Auth",user.getDisplayName());
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        emailText = (EditText) findViewById(R.id.residentemail);
        passText = (EditText) findViewById(R.id.residentpassword);
        registerButton = (Button) findViewById(R.id.residentregister);
        progressDialog = new ProgressDialog(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email = emailText.getText().toString().trim();
                String password = passText.getText().toString().trim();

                if(factory.isStringEmpty(email)){
                    Toast.makeText(LoginActivity.this,getString(R.string.email_null),Toast.LENGTH_SHORT).show();
                    return;
                }

                if(factory.isStringEmpty(password)){
                    Toast.makeText(LoginActivity.this,getString(R.string.password_null),Toast.LENGTH_SHORT).show();
                    return;
                }

                if(factory.isStringShort(password,6)){
                    Toast.makeText(LoginActivity.this,getString(R.string.password_short), Toast.LENGTH_SHORT).show();
                }

                progressDialog.setMessage("Registering resident");

                progressDialog.show();
                FirebaseActivity firebase = new FirebaseActivity();
                firebase.registerResidentUser(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isComplete()){
                                    if(task.isSuccessful()){
                                        Toast.makeText(LoginActivity.this,"User registered",Toast.LENGTH_SHORT).show();

                                        // intent for profile activity ( pass email )
                                        Intent intent= new Intent(LoginActivity.this, ProfileActivity.class);
                                        intent.putExtra("email",email);
                                        startActivity(intent);
                                        emailText.setText("");
                                        passText.setText("");
                                    }else{
                                        Toast.makeText(LoginActivity.this,"Unable to register",Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(LoginActivity.this,"Not Complete",Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.hide();
                            }
                        });
            }
        });

        callBackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email","public_profile"));
        loginButton.registerCallback(callBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        gmailLogin = (Button)findViewById(R.id.gmailOfficialLogin);
        gmailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"Gmail Register");
                officialReg = true;
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
                startActivityForResult(signInIntent,9001);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        if(officialReg){
            if(requestCode == 9001){
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                Log.d("Google Status",Integer.toString(result.getStatus().getStatusCode()));
                if(result.isSuccess()){
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                }else{
                    Log.d("Activity","Not logged in");
                    Toast.makeText(this,"Unable to sign in",Toast.LENGTH_SHORT).show();
                }
            }
            officialReg = false;
        }else{
            callBackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
//                SaveSharedPreference.setUserName(LoginActivity.this,account.getId());
//                SaveSharedPreference.setUserType(LoginActivity.this,OFFICIAL);
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                AsyncHttpClient officialClient = new AsyncHttpClient();
                JSONObject officialParams = new JSONObject();
                StringEntity entity = null;
                try {
                    officialParams.put("email",account.getEmail());
                    officialParams.put("name",account.getDisplayName());
//                    officialParams.put("address",Address);
                    entity = new StringEntity(officialParams.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.d("Server",account.getEmail());
                officialClient.get(LoginActivity.this,"http://ec2-54-187-196-140.us-west-2.compute.amazonaws.com/officialNewRegister",entity,"application/json", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                        SaveSharedPreference.setUserType(LoginActivity.this,OFFICIAL);
//                        SaveSharedPreference.setUserName(LoginActivity.this,account.getDisplayName());

//                        SaveSharedPreference.setUserId(LoginActivity.this, account.getEmail());
                        Intent officialActivity = new Intent(LoginActivity.this, OfficialActivity.class);
                        officialActivity.putExtra("emailId",account.getEmail());
                        startActivity(officialActivity);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d("Server",Integer.toString(statusCode));
                    }
                });
                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithCredential", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        final String[] fbData = new String[2];
                        GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response){
                                Log.d("Facebook Response",response.toString());
                                try {
                                    fbData[0] = object.getString("email");
                                    fbData[1] = object.getString("name");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, name, email");
                        request.setParameters(parameters);
                        request.executeAsync();
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Intent residentIntent = new Intent(LoginActivity.this, ProfileActivity.class);
                            residentIntent.putExtra("email", fbData[0]);
                            residentIntent.putExtra("name",fbData[1]);
                            startActivity(residentIntent);
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
