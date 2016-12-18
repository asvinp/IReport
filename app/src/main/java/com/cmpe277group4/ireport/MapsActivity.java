package com.cmpe277group4.ireport;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    public String id, location, time, image, description, severity, size, status;
    public String[] stringlocations;
    public String[] stringlat_loc;
    public String[] stringlon_loc;
    public String[] stringid;
    public String[] stringtime;
    public String[] stringimage;
    public String[] stringdescription;
    public String[] stringseverity;
    public String[] stringsize;
    public String[] stringstatus;

    public LatLng markerlocation;

    private static AsyncHttpClient reportclient = new AsyncHttpClient();
    private static JSONObject serverdataJSON = new JSONObject();
    private static StringEntity serverdataentity;
    private static JSONObject reportdataobject;
    private static JSONArray reports = new JSONArray();

    String resident_id = null;
    //hashmap
    public HashMap<String, Integer> markerhash = new HashMap<>();

    final ArrayList<Report> reportList = new ArrayList<Report>();
    ArrayList<LatLng> list = new ArrayList<LatLng>();
    ArrayList<Marker> myMarkers = new ArrayList<Marker>();


    private TileOverlay mOverlay;
    private MarkerOptions litterMarkers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (googleServicesAvailable()) {
            Toast.makeText(this, "Gplay services are working", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_maps);
            initMap();
        } else {

        }
        Intent residentIntent = getIntent();
        resident_id = residentIntent.getExtras().getString("resident_id");

        try {
            serverdataJSON.put("resident_id", resident_id);
            serverdataentity = new StringEntity(serverdataJSON.toString());
            reportclient.get(MapsActivity.this, getString(R.string.server_url) + "getReport", serverdataentity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.d("MAP", "got Data");
                    try {
                        reportdataobject = new JSONObject(new String(responseBody));
                        reports = reportdataobject.getJSONArray("data");
                        Log.d("MAPS",reports.toString());
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
                            //report.imageBm = decodeBase64Image(report.image_litter);
                            reportList.add(report);

                            drawMarker(new LatLng(Double.parseDouble(report.lat_loc),Double.parseDouble(report.lon_loc)),report.resident_id,report.date);

                            //HEATMAP OVERLAY LAT LON DATA
                            double heatlat = Double.parseDouble(report.lat_loc);

                            double heatlon = Double.parseDouble(report.lon_loc);

                            list.add(new LatLng(heatlat, heatlon));
                            Log.d("MAPS",list.toString());
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

        //set buttons
        final Button LVBtn = (Button) findViewById(R.id.btnLVonMap);

        LVBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                finish();
            }

        });



        final Switch mySwitch = (Switch) findViewById(R.id.switch1);

        //set the switch to ON
        mySwitch.setChecked(false);
        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    hideMarkers();
                    addMyHeatMap();
                }else{
                    removeMyHeatMap();
                    showMarkers();
                }

            }
        });



    }


    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        //San Jose initial location
        goToLocationZoom(37.3382, -121.8863, 13);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
        }
        mGoogleMap.setMyLocationEnabled(true);

//        //Initial location user location (Constant update so will not be able to browse map while moving?
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//        mGoogleApiClient.connect();



        //Set the markers on map with relevant JSON data
//        for (int i = 0; i < stringlocations.length; i++) {
//
////            splitlatlng(stringlocations[i]);
//            drawMarker(markerlocation, stringid[i], stringtime[i]);
//            markerhash.put(stringid[i], i);
//
//        }
        mGoogleMap.setOnInfoWindowClickListener(this);


    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d("MAP","Marker clicked");
        int count = 0;
        for(Report report: reportList){
            Log.d("MARKER COUNT",Integer.toString(count++));
            if(marker.getSnippet().contentEquals(report.date)){
                Log.d("MARKER","found report");
                Intent detailIntent = new Intent(MapsActivity.this, ResidentDetail.class);

                detailIntent.putExtra("report_id",report.report_id);
//                detailIntent.putExtra("image", decodeBase64Image(report.image_litter));
//                // 3
                detailIntent.putExtra("resident_id", report.resident_id);
                detailIntent.putExtra("date", report.date);
//            detailIntent.putExtra("url", stringurl.instructionUrl);
                detailIntent.putExtra("image", report.image_litter);
                detailIntent.putExtra("desc_litter", report.desc_litter);
                detailIntent.putExtra("status_litter", report.status_litter);
                detailIntent.putExtra("severity_litter", report.severity_litter);
                detailIntent.putExtra("size_litter", report.size_litter);
                detailIntent.putExtra("lat_loc", report.lat_loc);
                detailIntent.putExtra("lon_loc", report.lon_loc);
                try {
                    Geocoder geocoder;
                    List<android.location.Address> addresses;
                    geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                    addresses = geocoder.getFromLocation(Double.parseDouble(report.lat_loc), Double.parseDouble(report.lon_loc), 1);
                    report.address = addresses.get(0).getAddressLine(0);
                }catch (Exception e){
                    e.printStackTrace();
                }

                detailIntent.putExtra("address", report.address);
                startActivity(detailIntent);
            }
        }
//        String markertitle = marker.getTitle();
//        if(markertitle.equals("Target Location")){
//            Toast.makeText(this, "You searched for this place", Toast.LENGTH_LONG).show();
//        }
//
//        else{
//            int m = markerhash.get(marker.getTitle());
//
//            if (m==m)
//            {
//                // 2
//
//
//            }
//        }

    }

    private void drawMarker(LatLng point, String id, String time) {
//        MarkerOptions multioptions = new MarkerOptions()
//                .title(id)
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
//                .position(point)
//                .snippet(time);
//        mGoogleMap.addMarker(multioptions);
        Log.d("MARKER",id);

        litterMarkers = new MarkerOptions()
                .position(point)
                .title(id)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                .snippet(time);
        myMarkers.add(mGoogleMap.addMarker(litterMarkers));

    }

    private void hideMarkers(){
        for (Marker m : myMarkers) {
            m.setVisible(false);
        }
    }

    private void showMarkers(){
        for (Marker m : myMarkers) {
            m.setVisible(true);
        }
    }

    private void splitlatlng(String x) {
        String[] latlong =  x.split(",");
        double latitude = Double.parseDouble(latlong[0]);
        double longitude = Double.parseDouble(latlong[1]);
        markerlocation = new LatLng(latitude, longitude);
    }


    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.moveCamera(update);
    }

    private void goToLocation(double lat, double lng) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
        mGoogleMap.moveCamera(update);
    }

    public void geoLocate (View view) throws IOException{
        EditText et = (EditText) findViewById(R.id.editText);
        String location = et.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<android.location.Address> list = gc.getFromLocationName(location, 1);
        android.location.Address address = list.get(0);
        String locality = address.getLocality();

        Toast.makeText(this, locality, Toast.LENGTH_LONG).show();

        double lat = address.getLatitude();
        double lng = address.getLongitude();
        goToLocationZoom(lat, lng, 15);


        MarkerOptions options = new MarkerOptions()
                .title("Target Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .position(new LatLng(lat, lng))
                .snippet(locality);
        mGoogleMap.addMarker(options);
    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Can't connect to Gplay services", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    LocationRequest mLocationRequest;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location == null){
            Toast.makeText(this, "Can't get current location", Toast.LENGTH_LONG).show();
        }else{
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
            mGoogleMap.animateCamera(update);
        }
    }

    //DECODE IMAGE
    private Bitmap decodeBase64Image(String base64){
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
//        Drawable d = new BitmapDrawable(getResources(), decodedByte);
//        return d;
    }

    private void addMyHeatMap() {

        // Create a heat map tile provider, passing it the latlngs of the trash.
        HeatmapTileProvider provider = new HeatmapTileProvider.Builder().data(list).build();
        provider.setRadius(50);
        // Add a tile overlay to the map, using the heat map tile provider.
        mOverlay = mGoogleMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
    }

    private void removeMyHeatMap(){
        mOverlay.remove();
    }





}



//        List<String> locationarr = new ArrayList();
//        List<String> idarr = new ArrayList();
//        List<String> timearr = new ArrayList();
//        List<String> imagearr = new ArrayList();
//        List<String> descriptionarr = new ArrayList();
//        List<String> severityarr = new ArrayList();
//        List<String> sizearr = new ArrayList();
//        List<String> statusarr = new ArrayList();
//
//
//        // Reading json file from assets folder
//        StringBuffer sb = new StringBuffer();
//        BufferedReader br = null;
//        try {
//            br = new BufferedReader(new InputStreamReader(getAssets().open(
//                    "reports.json")));
//            String temp;
//            while ((temp = br.readLine()) != null)
//                sb.append(temp);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                br.close(); // stop reading
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        String myjsonstring = sb.toString();
//        // Try to parse JSON
//        try {
//            // Creating JSONObject from String
//            JSONObject jsonObjMain = new JSONObject(myjsonstring);
//
//            // Creating JSONArray from JSONObject
//            JSONArray jsonArray = jsonObjMain.getJSONArray("reports");
//
////            // JSONArray has x JSONObject
////            for (int i = 0; i < jsonArray.length(); i++) {
////
////                // Creating JSONObject from JSONArray
////                JSONObject jsonObj = jsonArray.getJSONObject(i);
////
////                // Getting data from individual JSONObject
////                id = jsonObj.getString("id");
////                location = jsonObj.getString("location");
////                time = jsonObj.getString("time");
////                image = jsonObj.getString("image");
////                description = jsonObj.getString("description");
////                severity = jsonObj.getString("severity");
////                size = jsonObj.getString("size");
////                status = jsonObj.getString("status");
////
////
////                locationarr.add(jsonObj.getString("location"));
////                stringlocations = locationarr.toArray(new String[locationarr.size()]);
////
////                idarr.add(jsonObj.getString("id"));
////                stringid = idarr.toArray(new String[idarr.size()]);
////
////                timearr.add(jsonObj.getString("time"));
////                stringtime = timearr.toArray(new String[timearr.size()]);
////
////                imagearr.add(jsonObj.getString("image"));
////                stringimage = imagearr.toArray(new String[imagearr.size()]);
////
////                descriptionarr.add(jsonObj.getString("description"));
////                stringdescription = descriptionarr.toArray(new String[descriptionarr.size()]);
////
////                severityarr.add(jsonObj.getString("severity"));
////                stringseverity = severityarr.toArray(new String[severityarr.size()]);
////
////                sizearr.add(jsonObj.getString("size"));
////                stringsize = sizearr.toArray(new String[sizearr.size()]);
////
////                statusarr.add(jsonObj.getString("status"));
////                stringstatus = statusarr.toArray(new String[statusarr.size()]);
////
////
////            }