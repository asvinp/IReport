package com.cmpe277group4.ireport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class OfficialActivity extends AppCompatActivity {

    private static final String OFFICIAL_TAG = "OFFICIAL";

    private ListView mListView;

    private static AsyncHttpClient reportclient = new AsyncHttpClient();
    private static JSONObject serverdataJSON = new JSONObject();
    private static StringEntity serverdataentity;
    private static JSONObject reportdataobject;
    private static JSONArray reports = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);
        Intent officialIntent = getIntent();
//        String emailTest = officialIntent.getExtras().getString("emailId");
        mListView = (ListView) findViewById(R.id.report_list_view);
// 1
        final ArrayList<Report> reportList = new ArrayList<Report>();

        try {
            reportclient.get(OfficialActivity.this, getString(R.string.server_url) + "getAllReports", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.d(" Official Reports", "Got All Data");
                    try {
                        reportdataobject = new JSONObject(new String(responseBody));
                        reports = reportdataobject.getJSONArray("data");
                        Log.d("reports",reports.toString());
                        // Get Report objects from data
                        for (int i = 0; i < reports.length(); i++) {
                            Report report = new Report();

                            report.resident_id = reports.getJSONObject(i).getString("resident_id");
                            report.date = reports.getJSONObject(i).getString("date");
                            report.desc_litter = reports.getJSONObject(i).getString("desc_report");
                            report.image_litter = reports.getJSONObject(i).getString("image_litter");
                            //                report.instructionUrl = reports.getJSONObject(i).getString("url");
                            report.status_litter = reports.getJSONObject(i).getString("status_litter");
                            report.severity_litter = reports.getJSONObject(i).getString("severity_litter");
                            report.size_litter = reports.getJSONObject(i).getString("size_litter");
                            report.lat_loc = reports.getJSONObject(i).getString("lat_loc");
                            report.lon_loc = reports.getJSONObject(i).getString("lon_loc");
                            reportList.add(report);
                        }

                        ReportAdapter adapter = new ReportAdapter(OfficialActivity.this, reportList);
                        mListView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.d("reports", "FAILED status code " + statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

//        ReportAdapter adapter = new ReportAdapter(this, reportList);
//        mListView.setAdapter(adapter);

        //Mapbutton
        final Button button = (Button) findViewById(R.id.mapbtn);
        final Context btncontext = this;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent mapIntent = new Intent(btncontext, OfficialMapsActivity.class);

//                mapIntent.putExtra("location", selectedReport.location);

                startActivity(mapIntent);
            }
        });

        final Context context = this;
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 1
                Report selectedReport = reportList.get(position);

                // 2
                Intent detailIntent = new Intent(context, report_detail.class);
                String address;
                detailIntent.putExtra("report_id",selectedReport.report_id);
//                Log.d("ACTIVITY",selectedReport.report_id);
//
//                // 3
                detailIntent.putExtra("resident_id", selectedReport.resident_id);
                detailIntent.putExtra("date", selectedReport.date);
                detailIntent.putExtra("image_litter", selectedReport.image_litter);
                detailIntent.putExtra("desc_litter", selectedReport.desc_litter);
                detailIntent.putExtra("status_litter", selectedReport.status_litter);
                detailIntent.putExtra("severity_litter", selectedReport.severity_litter);
                detailIntent.putExtra("size_litter", selectedReport.size_litter);
                detailIntent.putExtra("lat_loc", selectedReport.lat_loc);
                detailIntent.putExtra("lon_loc", selectedReport.lon_loc);
                detailIntent.putExtra("address", selectedReport.address);


                // 3
//                detailIntent.putExtra("id", selectedReport.id);
////                detailIntent.putExtra("time", selectedReport.time);
////                detailIntent.putExtra("url", selectedReport.instructionUrl);
////                detailIntent.putExtra("image", selectedReport.imageUrl);
//                detailIntent.putExtra("description", selectedReport.description);
//                detailIntent.putExtra("status", selectedReport.status);
//                detailIntent.putExtra("severity", selectedReport.severity);
//                detailIntent.putExtra("size", selectedReport.size);
////                detailIntent.putExtra("location", selectedReport.location);


                // 4
                startActivity(detailIntent);
            }});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_official, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.signout:
            FirebaseAuth.getInstance().signOut();
            Intent goBackLogin = new Intent(OfficialActivity.this,LoginActivity.class);
            startActivity(goBackLogin);
            return(true);
//        case R.id.myReport:
//            Intent residentActivity = new Intent(OfficialActivity.this,ResidentActivity.class);
//            startActivity(residentActivity);
//            return true;
//        case R.id.exit:
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            return(true);
    }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}



