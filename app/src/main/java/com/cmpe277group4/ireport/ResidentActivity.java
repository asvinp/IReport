package com.cmpe277group4.ireport;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    ReportAdapter adapter;


    public String resident_id = null;

    private EditText searchBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);
        Intent residentIntent = getIntent();
        resident_id = residentIntent.getExtras().getString("resident_id");
        Log.d("RESIDENTACTIVITY",resident_id);
        mListView = (ListView) findViewById(R.id.report_list_view);
        final ArrayList<Report> reportList = new ArrayList<Report>();
        searchBox = (EditText) findViewById(R.id.searchField);


        try {
            serverdataJSON.put("resident_id", resident_id);
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
                            report.report_id = reports.getJSONObject(i).getString("_id");
                            report.resident_id = reports.getJSONObject(i).getString("resident_id");
                            report.date = reports.getJSONObject(i).getString("date");
                            report.desc_litter = reports.getJSONObject(i).getString("desc_report");
                            report.image_litter = reports.getJSONObject(i).getString("image_litter");
                            report.status_litter = reports.getJSONObject(i).getString("status_litter");
                            report.severity_litter = reports.getJSONObject(i).getString("severity_litter");
                            report.size_litter = reports.getJSONObject(i).getString("size_litter");
                            report.lat_loc = reports.getJSONObject(i).getString("lat_loc");
                            report.lon_loc = reports.getJSONObject(i).getString("lon_loc");
                            Log.d("RESIDENTACT",report.date);
                            reportList.add(report);
                        }
                        adapter = new ReportAdapter(ResidentActivity.this, reportList);
                        mListView.setAdapter(adapter);

                        searchBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                String text = searchBox.getText().toString().toLowerCase(Locale.getDefault());

                                adapter.filter(text);
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

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

        //Mapbutton
        final Button button = (Button) findViewById(R.id.mapbtn);
        final Context btncontext = this;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent mapIntent = new Intent(btncontext, ResidentMapsActivity.class);
                mapIntent.putExtra("resident_id",resident_id);
                startActivity(mapIntent);
            }
        });


        final Context context = this;

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Report selectedReport = reportList.get(position);
                Intent detailIntent = new Intent(context, ResidentDetail.class);
                detailIntent.putExtra("report_id",selectedReport.report_id);
                Log.d("ACTIVITY",selectedReport.report_id);
                detailIntent.putExtra("resident_id", selectedReport.resident_id);
                detailIntent.putExtra("date", selectedReport.date);
                detailIntent.putExtra("image_litter", selectedReport.image_litter);
                detailIntent.putExtra("desc_litter", selectedReport.desc_litter);
                detailIntent.putExtra("status_litter", selectedReport.status_litter);
                detailIntent.putExtra("severity_litter", selectedReport.severity_litter);
                detailIntent.putExtra("size_litter", selectedReport.size_litter);
                detailIntent.putExtra("lat_loc", selectedReport.lat_loc);
                detailIntent.putExtra("lon_loc", selectedReport.lon_loc);

                try {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(context, Locale.getDefault());
                    addresses = geocoder.getFromLocation(Double.parseDouble(selectedReport.lat_loc), Double.parseDouble(selectedReport.lon_loc), 1);
                    selectedReport.address = addresses.get(0).getAddressLine(0);
                }catch (Exception e){
                    e.printStackTrace();
                }

                detailIntent.putExtra("address", selectedReport.address);


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
            Intent reportActivity = new Intent(ResidentActivity.this,ReportActivity.class);
            reportActivity.putExtra("resident_id",resident_id);
            startActivity(reportActivity);
            return true;
        case R.id.setting:
            Intent settingIntent = new Intent(ResidentActivity.this, UserSettingsActivity.class);
            settingIntent.putExtra("resident_id",resident_id);
            startActivity(settingIntent);
            return true;
        case R.id.update:
            Intent updateActivity = new Intent(ResidentActivity.this,UpdateActivity.class);
            updateActivity.putExtra("resident_id",resident_id);
            startActivity(updateActivity);
            return true;
        case R.id.signout:
            LoginManager.getInstance().logOut();
            FirebaseAuth.getInstance().signOut();
            Intent goBackLogin = new Intent(ResidentActivity.this,LoginActivity.class);
            startActivity(goBackLogin);
            return(true);
    }
        return(super.onOptionsItemSelected(item));
    }
}