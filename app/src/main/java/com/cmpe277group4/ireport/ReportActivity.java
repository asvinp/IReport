package com.cmpe277group4.ireport;

/**
 * Created by Vinay on 12/6/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class ReportActivity extends AppCompatActivity {

    String resident_id = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.resident_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add:
                return true;
            case R.id.signout:
                FirebaseAuth.getInstance().signOut();
                
                return true;
            case R.id.about:
                return true;
            case R.id.update:
                Intent updateActivity = new Intent(ReportActivity.this,UpdateActivity.class);
                updateActivity.putExtra("resident_id",resident_id);
                startActivity(updateActivity);
                return true;
            case R.id.exit:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_littering);

        Intent reportIntent = getIntent();
        resident_id = reportIntent.getExtras().getString("resident_id");
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