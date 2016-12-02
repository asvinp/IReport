package com.cmpe277group4.ireport;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseActivity{

    private FirebaseAuth getFireBaseInstance() {
        return FirebaseAuth.getInstance();
    }

    public Task<AuthResult> registerResidentUser(String email, String password) {
        return getFireBaseInstance().createUserWithEmailAndPassword(email, password);
    }
}
