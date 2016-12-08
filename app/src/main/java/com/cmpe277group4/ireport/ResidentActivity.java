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


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ResidentActivity extends AppCompatActivity {

    private static final String RESIDENT_TAG = "RESIDENT";

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
        Intent residentIntent = getIntent();
        String resid = residentIntent.getExtras().getString("id");
        mListView = (ListView) findViewById(R.id.report_list_view);
        final ArrayList<Report> reportList = new ArrayList<Report>();

        try {
            serverdataJSON.put("id", resid);
            serverdataentity = new StringEntity(serverdataJSON.toString());
            reportclient.get(ResidentActivity.this, getString(R.string.server_url) + "getReport", serverdataentity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.d("reports", "got Data");
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.d("reports", "got Data FAILED status code " + statusCode);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

        ReportAdapter adapter = new ReportAdapter(this, reportList);
        mListView.setAdapter(adapter);

        //Mapbutton
        final Button button = (Button) findViewById(R.id.mapbtn);
        final Context btncontext = this;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent mapIntent = new Intent(btncontext, MapsActivity.class);

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
                Intent detailIntent = new Intent(context, ResidentDetail.class);

                // 3
                detailIntent.putExtra("resident_id", selectedReport.resident_id);
                detailIntent.putExtra("date", selectedReport.date);
//                detailIntent.putExtra("url", selectedReport.instructionUrl);
//                detailIntent.putExtra("image", selectedReport.imageUrl);
                detailIntent.putExtra("desc_litter", selectedReport.desc_litter);
                detailIntent.putExtra("status_litter", selectedReport.status_litter);
                detailIntent.putExtra("severity_litter", selectedReport.severity_litter);
                detailIntent.putExtra("size_litter", selectedReport.size_litter);
                detailIntent.putExtra("lat_loc", selectedReport.lat_loc);
                detailIntent.putExtra("lon_loc", selectedReport.lon_loc);



                // 4
                startActivity(detailIntent);
            }});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.add:
            //add the function to perform here
            return(true);
        case R.id.signout:
            //add the function to perform here
            return(true);
        case R.id.about:
            Toast.makeText(this, "Made by Group 4", Toast.LENGTH_SHORT).show();
            return(true);
        case R.id.exit:
            finish();
            return(true);
    }
        return(super.onOptionsItemSelected(item));
    }
}



