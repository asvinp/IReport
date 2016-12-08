package com.cmpe277group4.ireport;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
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

public class UpdateActivity extends AppCompatActivity {

    private static final String PROFILE_TAG = "PROFILE";
    TextView nameText, addressText, emailText, screenNameText;
    String email, name, address, screenName;
    Button update;
    private static final int SELECT_PICTURE = 0;
    private ImageView imageView;
    private static int RESULT_LOAD_IMAGE = 1;

    private ProgressDialog progressDialog;
    private AsyncHttpClient updateClient = new AsyncHttpClient();
    private JSONObject serverDataJSON = new JSONObject();
    private StringEntity serverDataEntity;
    private JSONObject residentDataJSON;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        addressText = (TextView) findViewById(R.id.Address);
        nameText = (TextView) findViewById(R.id.name);
        emailText = (TextView) findViewById(R.id.Email);
        screenNameText = (TextView) findViewById(R.id.ScreenName);
        Intent intent = getIntent();
        email = intent.getExtras().getString("email");

        Log.d(PROFILE_TAG, email);
        update = (Button) findViewById(R.id.Update);
        imageView = (ImageView) findViewById(android.R.id.icon);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting Profile data");
        progressDialog.show();

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

        try {
            serverDataJSON.put("id",email);
            serverDataEntity = new StringEntity(serverDataJSON.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        updateClient.get(UpdateActivity.this, getString(R.string.server_url) + "/getResidentData", serverDataEntity, "application/json", new AsyncHttpResponseHandler() {

            private final String TAG = "SERVER_UPDATE";

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.hide();
                Log.d(TAG,"Got User Data");
                String residentData = new String(responseBody);
                try {
                    residentDataJSON = new JSONObject(residentData);
                    JSONObject dataResidentJSON = residentDataJSON.getJSONObject("data");
                    emailText.setText(dataResidentJSON.getString("email"));
                    nameText.setText(dataResidentJSON.getString("name"));
                    addressText.setText(dataResidentJSON.getString("address"));
                    screenNameText.setText(dataResidentJSON.getString("screenName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG,residentData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.hide();
                Log.d(TAG,"Failed to get User data : Status Code : " + statusCode );
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailText.getText().toString();
                name = nameText.getText().toString();
                address = addressText.getText().toString();
                screenName = screenNameText.getText().toString();
                if(name == null){
                    name = "";
                }
                if(address == null){
                    address = "";
                }
                if(screenName == null){
                    screenName = email;
                }
                try {
                    serverDataJSON.put("id",email);
                    serverDataJSON.put("email",email);
                    serverDataJSON.put("name",name);
                    serverDataJSON.put("address",address);
                    serverDataJSON.put("screenName",screenName);
                    serverDataEntity = new StringEntity(serverDataJSON.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                updateClient.get(UpdateActivity.this, getString(R.string.server_url) + "updateResident", serverDataEntity, "application/json", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d(PROFILE_TAG,"User profile Updated");
                        Intent reportActivity = new Intent(UpdateActivity.this,ReportActivity.class);
                        startActivity(reportActivity);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d(PROFILE_TAG,  " Failure Status : " + Integer.toString(statusCode));
                    }
                });

            }
        });

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