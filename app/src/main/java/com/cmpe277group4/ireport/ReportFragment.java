package com.cmpe277group4.ireport;

/**
 * Created by Vinay on 12/6/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static android.app.Activity.RESULT_OK;

/**
 * A placeholder fragment containing a simple view.
 */
public class ReportFragment extends Fragment {

    private EditText descriptiontext;
    private ImageView mImageView;
    private ImageButton mImageButton;
    private Spinner mSizeSpinner;
    private Spinner mSeveritySpinner;
    private ImageButton fetchuserloc;
    private TextView mLatLng;
    private Button mSendButton;
    private static double ulatitude=0;
    private static double ulongitude=0;
    private String TAG = "ReportFraagment";
    private ImageView bitmapTest;

    private Bitmap mImageBitmap;
    private static final String CAMERA_DIR = "/dcim/";
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private String CLOUDINARY_PATH = "http://res.cloudinary.com/vinaysh/image/upload/q_40/";
    private String FILE_NAME;
    private String mCurrentPhotoPath;
    private String FILE_PATH;
    private AsyncTaskUploadImage asyncTask = new AsyncTaskUploadImage();

    private AsyncHttpClient reportClient = new AsyncHttpClient();
    private JSONObject serverDataJSON = new JSONObject();
    private StringEntity serverDataEntity;

    private static final String[] INITIAL_PERMS={
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int INITIAL_REQUEST=1337;

    public ReportFragment() {
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.d(TAG, "dispatchTakePictureIntent outside if statement");
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            Log.d(TAG, "dispatchTakePictureIntent within if statement");
            File f = null;
            try {
                f = setUpPhotoFile();
                mCurrentPhotoPath = f.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            } catch (IOException e) {
                e.printStackTrace();
                f = null;
                mCurrentPhotoPath = null;
            }

            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    public File getAlbumStorageDir(String albumName) {
        return new File(
                Environment.getExternalStorageDirectory()
                        + CAMERA_DIR
                        + albumName
        );
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        FILE_NAME = JPEG_FILE_PREFIX + timeStamp;
        Log.d(TAG,"ireportvinay FILE_NAME value is "+FILE_NAME);
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(FILE_NAME, JPEG_FILE_SUFFIX, albumF);
        FILE_PATH = imageF.getAbsolutePath();
        Log.d(TAG,"ireportvinay Printing absolute path of file stored"+FILE_PATH);
        //FILE_PATH = imageF.getAbsolutePath();
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        Log.d(TAG, "ireportvinay "+mCurrentPhotoPath);

        return f;
    }

    Bitmap bitmap;
    private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

        Log.d(TAG,"setPic() called");
		/* Get the size of the ImageView */
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
        mImageView.setImageBitmap(bitmap);
        mImageView.setVisibility(View.VISIBLE);
    }

    //Method to convert Image to Base64 encoded string
    private String encodeImagetoBase64(){
        String encodedImage=null;
        try {
           // Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Log.d(TAG, bitmap.toString());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        }catch(Exception e ){
            e.printStackTrace();
        }

        return encodedImage;
    }

    private Bitmap decodeBase64Image(String base64){
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
//        Drawable d = new BitmapDrawable(getResources(), decodedByte);
//        return d;
    }

    private void galleryAddPic() {
        Log.d(TAG,"galleryAddPic() called");
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    private void handleCameraPhoto(Intent intent) {

        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();
            mCurrentPhotoPath = null;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            handleCameraPhoto(data);
        }
    }

    private boolean canAccessLocation() {
        return(hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION));
    }
    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED== ContextCompat.checkSelfPermission(getActivity(),perm));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent fragmentIntent = getActivity().getIntent();
        final String resident_id = fragmentIntent.getExtras().getString("resident_id");

        View v = inflater.inflate(R.layout.fragment_littering, container, false);
        mImageView = (ImageView)v.findViewById(R.id.imageView2);
        mImageButton = (ImageButton)v.findViewById(R.id.imageButton2);
        mSizeSpinner = (Spinner)v.findViewById(R.id.spinnerSize);
        mSeveritySpinner = (Spinner)v.findViewById(R.id.spinnerSeverity);
        mLatLng = (TextView)v.findViewById(R.id.textViewLatLng);
        mSendButton = (Button)v.findViewById(R.id.mSendButton);
        descriptiontext = (EditText) v.findViewById(R.id.editText);
//        bitmapTest = (ImageView)v.findViewById(R.id.imageView3);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String encB= encodeImagetoBase64();
                Log.d(TAG, "ireportvinay Passing params to asynctask FILE_NAME "+FILE_NAME+" FILE_PATH"+FILE_PATH);
                asyncTask.execute(FILE_NAME, FILE_PATH);
                Log.d(TAG, "ireportvinay final cloudinary path "+CLOUDINARY_PATH+FILE_NAME+".jpg");
                String lat = Double.toString(ulatitude);
                String longi = Double.toString(ulongitude);
                String severity = mSeveritySpinner.getSelectedItem().toString();
                String size = mSizeSpinner.getSelectedItem().toString();
                String description = descriptiontext.getText().toString();

                try {
                    serverDataJSON.put("resident_id",resident_id);
                    serverDataJSON.put("image_litter",CLOUDINARY_PATH+FILE_NAME+".jpg");
                    serverDataJSON.put("lat_loc",lat);
                    serverDataJSON.put("lon_loc",longi);
                    serverDataJSON.put("severity_litter",severity);
                    serverDataJSON.put("size_litter",size);
                    serverDataJSON.put("desc_litter",description);
                    serverDataJSON.put("status_litter","Still_there");
                    serverDataJSON.put("date",new Date(System.currentTimeMillis()));
                    serverDataJSON.put("anonymous_setting","0");
//                    Log.d("REPORT",new Date(System.currentTimeMillis()));
                    serverDataEntity = new StringEntity(serverDataJSON.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                reportClient.get(getContext(), getString(R.string.server_url) + "fileReport", serverDataEntity, "application/json", new AsyncHttpResponseHandler() {


                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d(TAG,"report posted");
                        Intent reportIntent = new Intent (getContext(), ResidentActivity.class);
                        Log.d("REPOORT_FRAG",resident_id);
                        reportIntent.putExtra("resident_id", resident_id);
                        startActivity(reportIntent);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d(TAG,"Failure Status : " + Integer.toString(statusCode));
                        Toast.makeText(getContext(), "Unable to post report ",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



        if (!canAccessLocation()) {
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        }

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                System.out.println("**********VINAY OnLocationChanged  "+location.getLatitude()+" , "+location.getLongitude());
                ulatitude = location.getLatitude();
                ulongitude = location.getLongitude();
                //mLatLng.setText(ulatitude+"\n"+ulongitude);
                try {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(getContext(), Locale.getDefault());
                    addresses = geocoder.getFromLocation(ulatitude, ulongitude, 1);
                    String address = addresses.get(0).getAddressLine(0);
                    System.out.println("Lat and Lon " + address);
                    mLatLng.setText(/*ulatitude+"\n"+ulongitude+"\n"+*/address);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        try {
            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } catch(SecurityException e){
            e.printStackTrace();
        }

        fetchuserloc = (ImageButton)v.findViewById(R.id.imagebuttonFetchuserLoc);
        fetchuserloc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final LocationManager manager = (LocationManager) getActivity().getSystemService( Context.LOCATION_SERVICE );

                if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    Toast.makeText(getActivity().getApplicationContext(), "Switch on the GPS to fetch user's current location", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "Fetching user's current location", Toast.LENGTH_LONG).show();
                }
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.Size, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSizeSpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.Severity, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSeveritySpinner.setAdapter(adapter1);

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        return v;
    }
}

