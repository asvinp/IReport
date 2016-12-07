package com.cmpe277group4.ireport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;

public class OfficialActivity extends AppCompatActivity {

    private static final String OFFICIAL_TAG = "OFFICIAL";

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);
        Intent officialIntent = getIntent();
        String emailTest = officialIntent.getExtras().getString("emailId");
        mListView = (ListView) findViewById(R.id.report_list_view);
// 1
        final ArrayList<Report> reportList = Report.getReportsFromFile("reports.json", this);

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
                Intent detailIntent = new Intent(context, report_detail.class);

                // 3
                detailIntent.putExtra("id", selectedReport.id);
                detailIntent.putExtra("time", selectedReport.time);
                detailIntent.putExtra("url", selectedReport.instructionUrl);
                detailIntent.putExtra("image", selectedReport.imageUrl);
                detailIntent.putExtra("description", selectedReport.description);
                detailIntent.putExtra("status", selectedReport.status);
                detailIntent.putExtra("severity", selectedReport.severity);
                detailIntent.putExtra("size", selectedReport.size);
                detailIntent.putExtra("location", selectedReport.location);


                // 4
                startActivity(detailIntent);
            }});
    }
    }



