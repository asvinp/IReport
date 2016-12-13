package com.cmpe277group4.ireport;

/**
 * Created by Asvin on 12/11/2016.
 */

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


/**
 * Add a simple heat map to a map.
 * The heat map uses a single dataset, containing the locations of police stations
 * in Melbourne, Australia.
 */
public class HeatMapActivity extends FragmentActivity implements OnMapReadyCallback {
    /**
     * Note that this may be null if the Google Play services APK is not available.
     */
    private GoogleMap mGoogleMap;

    private static AsyncHttpClient reportclient = new AsyncHttpClient();
    private static JSONObject serverdataJSON = new JSONObject();
    private static StringEntity serverdataentity;
    private static JSONObject reportdataobject;
    private static JSONArray reports = new JSONArray();
    final ArrayList<Report> reportList = new ArrayList<Report>();

    ArrayList<LatLng> list = new ArrayList<LatLng>();



    String resident_id = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (googleServicesAvailable()) {
            Toast.makeText(this, "Gplay services are working", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_heatmap);
            initMap();

        } else {

        }


    }




    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */

    /**
     * Add a simple heat map to the map
     */
//    private void addHeatMap() {
//        List<LatLng> list = null;
//
//        // Get the data: latitude/longitude positions of police stations.
//        try {
//            list = readItems(R.raw.police_stations);
//        } catch (JSONException e) {
//            Toast.makeText(this, "Problem reading list of locations.", Toast.LENGTH_LONG).show();
//        }
//
//        // Create a heat map tile provider, passing it the latlngs of the police stations.
//        HeatmapTileProvider provider = new HeatmapTileProvider.Builder().data(list).build();
//        // Add a tile overlay to the map, using the heat map tile provider.
//        mGoogleMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
//    }
//    /**
//     * Read the data (locations of police stations) from raw resources.
//     */
//    private ArrayList<LatLng> readItems(int resource) throws JSONException {
//        ArrayList<LatLng> list = new ArrayList<LatLng>();
//        InputStream inputStream = getResources().openRawResource(resource);
//        @SuppressWarnings("resource")
//        String json = new Scanner(inputStream).useDelimiter("\\A").next();
//        JSONArray array = new JSONArray(json);
//        for (int i = 0; i < array.length(); i++) {
//            JSONObject object = array.getJSONObject(i);
//            double lat = object.getDouble("lat");
//            double lng = object.getDouble("lng");
//            list.add(new LatLng(lat, lng));
//            Log.d("MAPSSYDNE",list.toString());
//
//        }
//
//        return list;
//    }

    private void addMyHeatMap() {

  // Create a heat map tile provider, passing it the latlngs of the police stations.
        HeatmapTileProvider provider = new HeatmapTileProvider.Builder().data(list).build();
        // Add a tile overlay to the map, using the heat map tile provider.
        mGoogleMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        //San Jose initial location
        goToLocationZoom(37.3382, -121.8863, 13);

            getLocs();

//        addHeatMap();


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
    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapH);
        mapFragment.getMapAsync(this);
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

    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.moveCamera(update);
    }

private void getLocs(){
    Intent residentIntent = getIntent();
    resident_id = residentIntent.getExtras().getString("resident_id");

    try {
        serverdataJSON.put("resident_id", resident_id);
        serverdataentity = new StringEntity(serverdataJSON.toString());
        reportclient.get(HeatMapActivity.this, getString(R.string.server_url) + "getReport", serverdataentity, "application/json", new AsyncHttpResponseHandler() {
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

//                            report.resident_id = reports.getJSONObject(i).getString("resident_id");
//                            report.date = reports.getJSONObject(i).getString("date");
//                            report.desc_litter = reports.getJSONObject(i).getString("desc_report");
//                            report.image_litter = reports.getJSONObject(i).getString("image_litter");
//                            //                report.instructionUrl = reports.getJSONObject(i).getString("url");
//                            report.status_litter = reports.getJSONObject(i).getString("status_litter");
//                            report.severity_litter = reports.getJSONObject(i).getString("severity_litter");
//                            report.size_litter = reports.getJSONObject(i).getString("size_litter");
                        report.lat_loc = reports.getJSONObject(i).getString("lat_loc");
                        report.lon_loc = reports.getJSONObject(i).getString("lon_loc");
//                            report.imageBm = decodeBase64Image(report.image_litter);
                        reportList.add(report);
//                            drawMarker(new LatLng(Double.parseDouble(report.lat_loc),Double.parseDouble(report.lon_loc)),report.resident_id,report.date);

                        double heatlat = Double.parseDouble(report.lat_loc);

                        double heatlon = Double.parseDouble(report.lon_loc);

                        list.add(new LatLng(heatlat, heatlon));
                        Log.d("MAPS",list.toString());
                   }
                    addMyHeatMap();

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
}


}




