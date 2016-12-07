package com.cmpe277group4.ireport;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static android.R.attr.name;

public class UpdateActivity extends AppCompatActivity {

    private static final String PROFILE_TAG = "PROFILE";
    TextView nameText, addressText, emailText, screenName;
    String email, name, address;
    Button Update;
    private static final int SELECT_PICTURE = 0;
    private ImageView imageView;
    private static int RESULT_LOAD_IMAGE = 1;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        addressText = (TextView) findViewById(R.id.Address);
        nameText = (TextView) findViewById(R.id.name);
        emailText = (TextView) findViewById(R.id.Email);
        screenName = (TextView) findViewById(R.id.ScreenName);
        Intent intent = getIntent();
        email = intent.getExtras().getString("email");
        name = intent.getExtras().getString("name");
        if (name == null) {
            name = "";
        }
        Log.d(PROFILE_TAG, email);
        Log.d(PROFILE_TAG, name);
        Update = (Button) findViewById(R.id.Register);
        imageView = (ImageView) findViewById(android.R.id.icon);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        address = addressText.getText().toString();
        if(address == null){
            address = " ";
        }

        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("mprofile ", nameText.getText().toString());
                Log.d("mprofile ", addressText.getText().toString());
                Log.d(PROFILE_TAG,address);
                StringEntity entity = null;
                JSONObject profileObj = new JSONObject();
                try{
                    profileObj.put("email",email);
                    profileObj.put("name",name);
                    profileObj.put("address",address);
                    profileObj.put("ScreenName",screenName);
                    entity = new StringEntity(profileObj.toString());
                }catch(JSONException e){
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                AsyncHttpClient profileClient = new AsyncHttpClient();
                profileClient.get(UpdateActivity.this, "http://ec2-54-187-196-140.us-west-2.compute.amazonaws.com/updateResident", entity, "application/json", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d(PROFILE_TAG,"User profile Updated");
                        Intent reportActivity = new Intent(UpdateActivity.this,ReportActivity.class);
                        startActivity(reportActivity);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d(PROFILE_TAG,"Failure Status : " + Integer.toString(statusCode));
                    }
                });

            }
        });


        emailText.setText(email);
        nameText.setText(name);
        //address.setText("Current Location ");


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }





    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Profile Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }


    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}




