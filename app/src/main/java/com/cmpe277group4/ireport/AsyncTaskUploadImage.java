package com.cmpe277group4.ireport;

import android.os.AsyncTask;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vinay on 12/15/2016.
 */

public class AsyncTaskUploadImage extends AsyncTask<String, Void, Void> {


    public AsyncTaskUploadImage(){

    }

    @Override
    protected Void doInBackground(String... params) {
        try {

            System.out.println("ireportvinay Printing params FILE_NAME "+params[0]+" Image Path "+params[1]);
            Map config = new HashMap();
            File f = new File(params[1]);
            config.put("cloud_name", "vinaysh");
            config.put("api_key", "627262799682927");
            config.put("api_secret", "ew85rpJ1wQLvsSFoEPNttVh6Ac0");
            Cloudinary cloudinary = new Cloudinary(config);


            //System.out.println("ireportvinay Printing generated url "+cloudinary.url().generate("asynctask_2.jpg"));

            cloudinary.uploader().upload(f, ObjectUtils.asMap("public_id", params[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }



}