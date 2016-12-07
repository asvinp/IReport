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

import static android.R.attr.name;

public class ProfileActivity extends AppCompatActivity {

    private static final String PROFILE_TAG = "PROFILE";
    TextView nameText, addressText, emailText;
    String email, name;
    Button Register;
    private static final int SELECT_PICTURE = 0;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        addressText = (TextView) findViewById(R.id.Address);
        nameText = (TextView) findViewById(R.id.name);
        emailText = (TextView) findViewById(R.id.Email);
        Intent intent = getIntent();
        email = intent.getExtras().getString("email");
        name = intent.getExtras().getString("name");
        if(name == null){
            name = "";
        }
        Log.d(PROFILE_TAG,email);
        Log.d(PROFILE_TAG,name);
        Register = (Button) findViewById(R.id.Register);
        imageView = (ImageView) findViewById(android.R.id.icon);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("mprofile ", nameText.getText().toString());
                Log.d("mprofile ", addressText.getText().toString());
            }
        });


        emailText.setText(email);
        //name.setText("Enter Name");
        //address.setText("Current Location ");


    }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = getPath(data.getData());
                imageView.setImageBitmap(bitmap);
            }
        }



    private Bitmap getPath(Uri uri) {

            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(uri, projection, null, null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(column_index);
            // cursor.close();
            // Convert file path into bitmap image using below line.
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);

            return bitmap;
        }
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }


    }




