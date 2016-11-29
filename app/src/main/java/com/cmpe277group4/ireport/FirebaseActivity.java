package com.cmpe277group4.ireport;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseActivity extends AppCompatActivity {


    private Button registerButton;
    private EditText emailText;
    private EditText passwordText;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);

        registerButton = (Button)findViewById(R.id.register);
        emailText = (EditText)findViewById(R.id.email);
        passwordText = (EditText)findViewById(R.id.password);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("registe clicked");
                registerUser();
            }
        });
    }

    private void registerUser() {
        System.out.println("in registere");
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        //check if fields are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter email",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password",Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering User..");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isComplete()){
                    if(task.isSuccessful()){
                        Toast.makeText(FirebaseActivity.this,"User registered",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(FirebaseActivity.this,"Unable to register",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(FirebaseActivity.this,"not complete",Toast.LENGTH_SHORT).show();
                }
                progressDialog.hide();
            }
        });
    }
}
