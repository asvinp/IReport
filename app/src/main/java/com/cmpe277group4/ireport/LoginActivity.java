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
import com.google.firebase.auth.UserInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;
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
    private Button normalLoginButton;

    private FirebaseActivity firebase = new FirebaseActivity();
    private AsyncHttpClient loginClient = new AsyncHttpClient();
    private JSONObject serverDataJSON = new JSONObject();
    private StringEntity serverDataEntity;

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
                    Log.d("Auth"," " + user.getEmail());
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
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
                    }
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

        emailText = (EditText) findViewById(R.id.residentemail);
        passText = (EditText) findViewById(R.id.residentpassword);
        registerButton = (Button) findViewById(R.id.residentregister);
        normalLoginButton = (Button) findViewById(R.id.normalLoginButton);
        progressDialog = new ProgressDialog(this);

        normalLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Signing In");
                progressDialog.show();
                final String email = emailText.getText().toString().trim();
                String password = passText.getText().toString().trim();
                firebase.signInResidentUser(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("LOGIN", "Resident sign in");
                            try {
                                fetchResidentData(email);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }else{
                            Log.d("LOGIN", "Unable to sign in");
                            Toast.makeText(LoginActivity.this,"Unable to sign in",Toast.LENGTH_SHORT);
                        }
                    }
                });
            }
        });

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

    private void fetchResidentData(String email) throws JSONException, UnsupportedEncodingException {
        final String resident_id = email;
        serverDataJSON.put("id",email);
        serverDataEntity = new StringEntity(serverDataJSON.toString());
        loginClient.get(LoginActivity.this, getString(R.string.server_url) + "/getResidentData", serverDataEntity, "application/json", new AsyncHttpResponseHandler() {

            private final String TAG = "SERVER_LOGIN";

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.hide();
                Log.d(TAG,"Got User Data");
                String residentData = new String(responseBody);
                Log.d(TAG,residentData);
                Intent reportActivity = new Intent(LoginActivity.this,ReportActivity.class);
                reportActivity.putExtra("resident_id",resident_id);
                startActivity(reportActivity);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.hide();
                Log.d(TAG,"Failed to get User data : Status Code : " + statusCode );
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
            Log.d("LOGIN","Official Reg");
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
            Log.d("LOGIN","Resident FB Reg");
            callBackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
        progressDialog.setMessage("Registering official");
        progressDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                if(task.isSuccessful()){
                    AsyncHttpClient officialClient = new AsyncHttpClient();
                    JSONObject officialParams = new JSONObject();
                    StringEntity entity = null;
                    try {
                        officialParams.put("email",account.getEmail());
                        officialParams.put("name",account.getDisplayName());
                        entity = new StringEntity(officialParams.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.d("Server",account.getEmail());
                    officialClient.get(LoginActivity.this, getString(R.string.server_url) + "officialNewRegister",entity,"application/json", new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            progressDialog.hide();
                            Intent officialActivity = new Intent(LoginActivity.this, OfficialActivity.class);
                            officialActivity.putExtra("emailId",account.getEmail());
                            startActivity(officialActivity);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Log.d("Server",Integer.toString(statusCode));
                        }
                    });
                }
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithCredential", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        progressDialog.setMessage("Registering resident");
        progressDialog.show();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                            final String[] fbData = new String[2];
                            GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response){
                                    Log.d("Facebook Response",response.toString());
                                    try {
                                        Log.d("FBLogin",object.getString("email"));
                                        Log.d("FBLogin",object.getString("name"));
                                        fbData[0] = object.getString("email");
                                        fbData[1] = object.getString("name");
                                        progressDialog.hide();
                                        updateUI(fbData[0],fbData[1]);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id, name, email");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }else if(!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUIToOfficial(String email){
        Intent officialIntent = new Intent(LoginActivity.this, ProfileActivity.class);
        officialIntent.putExtra("id",email);
        startActivity(officialIntent);
    }

    private void updateUI(String email, String name){
//        Log.d("UIUPDATE",email);
//        Log.d("UIUPPDATE",name);
        Intent residentIntent = new Intent(LoginActivity.this, ProfileActivity.class);
        residentIntent.putExtra("email", email);
        residentIntent.putExtra("name",name);
        startActivity(residentIntent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("CONNECTION","Connection Failed");
        Toast.makeText(LoginActivity.this,"Unable to connect",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }
}
