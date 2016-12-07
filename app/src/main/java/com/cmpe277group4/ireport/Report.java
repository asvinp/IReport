package com.cmpe277group4.ireport;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class Report {

    public String id;
    public String time;
    public String description;
    public String imageUrl;
    public String instructionUrl;
    public String status;
    public String severity;
    public String size;
    public String location;


    public static ArrayList<Report> getReportsFromFile(String filename, Context context){
        final ArrayList<Report> reportList = new ArrayList<>();

        try {
            // Load data
            String jsonString = loadJsonFromAsset("reports.json", context);
            JSONObject json = new JSONObject(jsonString);
            JSONArray reports = json.getJSONArray("reports");

            // Get Report objects from data
            for(int i = 0; i < reports.length(); i++){
               Report report = new Report();

                report.id = reports.getJSONObject(i).getString("id");
                report.time = reports.getJSONObject(i).getString("time");
                report.description = reports.getJSONObject(i).getString("description");
                report.imageUrl = reports.getJSONObject(i).getString("image");
                report.instructionUrl = reports.getJSONObject(i).getString("url");
                report.status = reports.getJSONObject(i).getString("status");
                report.severity = reports.getJSONObject(i).getString("severity");
                report.size = reports.getJSONObject(i).getString("size");
                report.location = reports.getJSONObject(i).getString("location");

                reportList.add(report);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reportList;
    }

    private static String loadJsonFromAsset(String filename, Context context) {
        String json = null;

        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        }
        catch (java.io.IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json;
    }

}
