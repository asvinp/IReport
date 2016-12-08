package com.cmpe277group4.ireport;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

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

        // get data from previous activity (MainActivity/MapsActivity)
        String id = this.getIntent().getExtras().getString("id");
        String time = this.getIntent().getExtras().getString("time");
//        String url = this.getIntent().getExtras().getString("url");
        String imageUrl = this.getIntent().getExtras().getString("image");
        String description = this.getIntent().getExtras().getString("description");
        String status = this.getIntent().getExtras().getString("status");
        String severity = this.getIntent().getExtras().getString("severity");
        String size = this.getIntent().getExtras().getString("size");
        String location = this.getIntent().getExtras().getString("location");
        trashLoc = location;


        //Set title of appscreen to id of report
        setTitle(id);

        // set imageview
        ImageView detailImageView = (ImageView) findViewById(R.id.imgDetail);

        // set textviews
        TextView severityTextView = (TextView) findViewById(R.id.severityDetail);
        TextView timeTextView = (TextView) findViewById(R.id.timeDetail);
        TextView sizeTextView = (TextView) findViewById(R.id.sizeDetail);
        TextView locationTextView = (TextView) findViewById(R.id.locationDetail);
        TextView descriptionTextView = (TextView) findViewById(R.id.descriptionDetail);


        //Loading image from below url into imageView

        Picasso.with(this)
                .load(imageUrl)
                .into(detailImageView);

        //send strings to TextViews
        severityTextView.setText(severity);
        timeTextView.setText(time);
        sizeTextView.setText(size);
        locationTextView.setText(location);
        descriptionTextView.setText(description);


        //Spinner

        //Set spinner view
        statusSpinner = (Spinner) findViewById(R.id.statusSpinner);


        //items for spinner
        String[] items = new String[]{"Still There", "Removal Confirmed", "Removal Claimed"};

        //set adapter to spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, items);

        statusSpinner.setAdapter(adapter);



        //preset spinner according to JSON file
        if (status.equalsIgnoreCase("still there")) {
            //set spinner initial value
            statusSpinner.setSelection(0,false);

        } else if (status.equalsIgnoreCase("removal confirmed")) {
            //set spinner initial value
            statusSpinner.setSelection(1,false);

        } else if (status.equalsIgnoreCase("removal claimed")) {
            //set spinner initial value
            statusSpinner.setSelection(2,false);

        }

        //set spinner listener
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("statusspinner item", (String) parent.getItemAtPosition(position));

                String selected = statusSpinner.getSelectedItem().toString();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(alertcontext);
                alertDialogBuilder.setTitle("You Chose " + (String)parent.getItemAtPosition(position));
                // set dialog message
                alertDialogBuilder
                        .setMessage("Click OK to confirm!")
                        .setCancelable(false)
                        .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
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