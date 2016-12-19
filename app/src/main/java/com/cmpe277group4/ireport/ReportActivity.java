package com.cmpe277group4.ireport;

/**
 * Created by Vinay on 12/6/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

public class ReportActivity extends AppCompatActivity {

    String resident_id = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.myReport:
                Intent residentActivity = new Intent(ReportActivity.this,ResidentActivity.class);
                residentActivity.putExtra("resident_id",resident_id);
                startActivity(residentActivity);
                return true;
            case R.id.signout:
                LoginManager.getInstance().logOut();
                FirebaseAuth.getInstance().signOut();
                Intent goBackLogin = new Intent(ReportActivity.this,LoginActivity.class);
                startActivity(goBackLogin);
                return true;
            case R.id.update:
                Intent updateActivity = new Intent(ReportActivity.this,UpdateActivity.class);
                updateActivity.putExtra("resident_id",resident_id);
                startActivity(updateActivity);
                return true;
            case R.id.setting:
                Intent settingIntent = new Intent(ReportActivity.this, UserSettingsActivity.class);
                settingIntent.putExtra("resident_id",resident_id);
                startActivity(settingIntent);
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
        Log.d("REPORTACTIVITY","" + resident_id);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.litteringFragmentContainer);

        if (fragment == null) {
            fragment = new ReportFragment();
            fm.beginTransaction()
                    .add(R.id.litteringFragmentContainer, fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}