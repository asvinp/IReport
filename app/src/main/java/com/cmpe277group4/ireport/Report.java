package com.cmpe277group4.ireport;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Handler;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class Report {

    public String resident_id;
//    public String time;
    public String desc_litter;
    public String image_litter;
    public String instructionUrl;
    public String status_litter;
    public String severity_litter;
    public String size_litter;
    public String lat_loc, lon_loc;
    public String date;
    public Bitmap imageBm;

    private static AsyncHttpClient reportclient = new AsyncHttpClient();
    private static JSONObject serverdataJSON = new JSONObject();
    private static StringEntity serverdataentity;
    private static JSONObject reportdataobject;
    private static JSONArray reports;


    public static ArrayList<Report> getReportsFromFile(String filename, Context context, String resid) {
        final ArrayList<Report> reportList = new ArrayList<>();

        try {
            serverdataJSON.put("id", resid);
            serverdataentity = new StringEntity(serverdataJSON.toString());
            reportclient.get(context, context.getString(R.string.server_url) + "getReport", serverdataentity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                    Log.d("reports", "got Data");
//                    try {
//                        reportdataobject = new JSONObject(new String(responseBody));
//                        reports = reportdataobject.getJSONArray("data");
//                        // Get Report objects from data
//                        for(int i = 0; i < reports.length(); i++){
//                            Report report = new Report();
//
//                            report.id = reports.getJSONObject(i).getString("user_resident");
////                report.time = reports.getJSONObject(i).getString("time");
//                            report.description = reports.getJSONObject(i).getString("desc_litter");
////                report.imageUrl = reports.getJSONObject(i).getString("image");
////                report.instructionUrl = reports.getJSONObject(i).getString("url");
//                            report.status = reports.getJSONObject(i).getString("status_litter");
//                            report.severity = reports.getJSONObject(i).getString("severity_litter");
//                            report.size = reports.getJSONObject(i).getString("size_litter");
//                            report.lat = reports.getJSONObject(i).getString("lat_loc");
//                            report.lon = reports.getJSONObject(i).getString("lon_loc");
//
//                            reportList.add(report);
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.d("reports", "got Data FAILED status code " + statusCode);

                }
            });

//            // Load data
//            String jsonString = loadJsonFromAsset("reports.json", context);
//            JSONObject json = new JSONObject(jsonString);
//            JSONArray reports = json.getJSONArray("reports");


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        android.os.Handler handler = new android.os.Handler() ;

//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//            }
//        },10000);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
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
