package com.cmpe277group4.ireport;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ResidentDetail extends AppCompatActivity implements GeoTask.Geo, LocationListener {

    Spinner statusSpinner;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    String currentLoc;
    String trashLoc;


    protected Context context;
    final Context alertcontext = this;
    TextView txtLat;
    String lat;
    String provider;
    protected String latitude, longitude;
    protected boolean gps_enabled, network_enabled;

    String resident_id = null;

    String date;
    //        String url = this.getIntent().getExtras().getString("url");
//        String imageUrl = this.getIntent().getExtras().getString("image");
    String description;
    String status;
    String severity;
    String size;
    String lat_loc;
    String lon_loc,address,image;

    private static AsyncHttpClient reportclient = new AsyncHttpClient();
    private static JSONObject serverdataJSON = new JSONObject();
    private static StringEntity serverdataentity;
    private static JSONObject reportdataobject;
    private static JSONArray reports = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        try {
            serverdataJSON.put("report_id",this.getIntent().getExtras().getString("report_id"));
            Log.d("DETAILSREQ",serverdataJSON.toString());
            serverdataentity = new StringEntity(serverdataJSON.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        AsyncHttpClient reportClient = new AsyncHttpClient();
        Log.d("DETAILSREQ", serverdataentity.toString());
        reportClient.get(ResidentDetail.this, getString(R.string.server_url) + "getReportRid", serverdataentity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("SERVERDEATILS"," " +statusCode);
                try {
                    JSONObject dataServer = new JSONObject(new String(responseBody));
                    JSONObject data = dataServer.getJSONObject("data");
                    Log.d("DETAILSREQ", data.toString());
                    resident_id = data.getString("resident_id");
                    date = data.getString("date");
                    description = data.getString("desc_report");
                    status = data.getString("status_litter");
                    severity = data.getString("severity_litter");
                    size = data.getString("size_litter");
                    lat_loc = data.getString("lat_loc");
                    lon_loc = data.getString("lon_loc");
//                    String address = data.getString("address");
                    image = data.getString("image_litter");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("DETAILSREQ","FAILURE STATUS " + statusCode);
            }
        });

        //getLocation
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, this);

        trashLoc = lat_loc + "," + lon_loc;


        //Set title of appscreen to id of report
        setTitle(resident_id);

        // set imageview
        ImageView detailImageView = (ImageView) findViewById(R.id.imgDetail);

        // set textviews
        TextView severityTextView = (TextView) findViewById(R.id.severityDetail);
        TextView timeTextView = (TextView) findViewById(R.id.timeDetail);
        TextView sizeTextView = (TextView) findViewById(R.id.sizeDetail);
        TextView locationTextView = (TextView) findViewById(R.id.locationDetail);
        TextView descriptionTextView = (TextView) findViewById(R.id.descriptionDetail);


        //Loading image from below url into imageView

//        Picasso.with(this)
//                .load(imageUrl)
//                .into(detailImageView);

        //send strings to TextViews
//        if(image != null)
//            detailImageView.setImageBitmap(decodeBase64Image(image));
//        severityTextView.setText(severity);
//        timeTextView.setText(date);
//        sizeTextView.setText(size);
//
//        try {
//            Geocoder geocoder;
//            List<Address> addresses;
//            geocoder = new Geocoder(context, Locale.getDefault());
//            addresses = geocoder.getFromLocation(Double.parseDouble(lat_loc), Double.parseDouble(lon_loc), 1);
//            address = addresses.get(0).getAddressLine(0);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        if(address != null) {
//            locationTextView.setText(address);
//            Log.d("DETAILS", address);
//        }
//        descriptionTextView.setText(description);
//
//
//
//
//
//        //Spinner
//
//        //Set spinner view
//        statusSpinner = (Spinner) findViewById(R.id.statusSpinner);
//
//
//        //items for spinner
//        String[] items = new String[]{"Still There", "Removal Confirmed", "Removal Claimed"};
//
//        //set adapter to spinner
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
//                android.R.layout.simple_spinner_item, items);
//
//        statusSpinner.setAdapter(adapter);
//
//        //preset spinner according to JSON file
//        if (status.equalsIgnoreCase("still there")) {
//            //set spinner initial value
//            statusSpinner.setSelection(0,false);
//
//        } else if (status.equalsIgnoreCase("removal confirmed")) {
//            //set spinner initial value
//            statusSpinner.setSelection(1,false);
//
//        } else if (status.equalsIgnoreCase("removal claimed")) {
//            //set spinner initial value
//            statusSpinner.setSelection(2,false);
//
//        }
//
//        //set spinner listener
//        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Log.v("statusspinner item", (String) parent.getItemAtPosition(position));
//
//                String selected = statusSpinner.getSelectedItem().toString();
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(alertcontext);
//                alertDialogBuilder.setTitle("You Chose " + (String)parent.getItemAtPosition(position));
//                // set dialog message
//                alertDialogBuilder
//                        .setMessage("Click OK to confirm!")
//                        .setCancelable(false)
//                        .setPositiveButton("OK",new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,int id) {
//                                // if this button is clicked, close
//                                // current activity
//                                dialog.cancel();
//                            }
//                        })
//                        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,int id) {
//                                // if this button is clicked, just close
//                                // the dialog box and do nothing
//                                dialog.cancel();
//                            }
//                        });
//
//                // create alert dialog
//                AlertDialog alertDialog = alertDialogBuilder.create();
//
//                // show it
//                alertDialog.show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // TODO Auto-generated method stub
//            }
//        });
    }


    //DECODE IMAGE
    private Bitmap decodeBase64Image(String base64){
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
//        Drawable d = new BitmapDrawable(getResources(), decodedByte);
//        return d;
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLoc= "" + location.getLatitude() + "," + location.getLongitude();
        trashLoc = trashLoc.replace(" ", "");

        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=" + currentLoc + "&destinations=" + trashLoc + "&mode=walking&language=fr-FR&avoid=tolls&key=AIzaSyAP8hnEOoMqRMpvQ7glzj6phn7Z1M45g4M";
        new GeoTask(ResidentDetail.this).execute(url);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void setDouble(String result) {
        Double dist=Double.parseDouble(result)/0.3048;
        Log.d("dist in feet", String.valueOf(dist));
        if (dist <= 30){
            //enable spinner
            statusSpinner.setEnabled(true);
        }
        else {
            //disable spinner
            statusSpinner.setEnabled(false);
        }
    }
}