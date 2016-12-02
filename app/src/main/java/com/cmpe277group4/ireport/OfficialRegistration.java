package com.cmpe277group4.ireport;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by 33843 on 12/1/2016.
 */
public class OfficialRegistration extends Fragment {

    EditText emailText;
    EditText passText;
    Button registerButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.official_registration,container,false);
        emailText = (EditText) rootView.findViewById(R.id.email);
        passText = (EditText) rootView.findViewById(R.id.password);
        registerButton = (Button) rootView.findViewById(R.id.register);



        return rootView;
    }


}
