package com.cmpe277group4.ireport;

/**
 * Created by Vinay on 12/6/2016.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_littering);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.litteringFragmentContainer);

        if (fragment == null) {
            fragment = new ReportFragment();
            fm.beginTransaction()
                    .add(R.id.litteringFragmentContainer, fragment)
                    .commit();

        }

    }
}