package com.cmpe277group4.ireport;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class FirebaseActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GoogleActivity";

    private FirebaseAuth auth = null;


    private FirebaseAuth getFireBaseInstance() {
        if(auth == null){
            Log.d("Auth","auth was null");
            return FirebaseAuth.getInstance();
        }else{
            return auth;
        }
    }

    public Task<AuthResult> registerResidentUser(String email, String password) {
        return getFireBaseInstance().createUserWithEmailAndPassword(email, password);
    }

    public void registerOfficialUser(String client_id){

    }

    public Task<AuthResult> signInResidentUser(String email, String password){
        return getFireBaseInstance().signInWithEmailAndPassword(email,password);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
