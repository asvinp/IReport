package com.cmpe277group4.ireport;

import android.text.TextUtils;

/**
 * Created by 33843 on 12/1/2016.
 */
public class FactoryMethods {

    public final String NULL_EMAIL = "Please enter email";
    public final String NULL_PASSWORD = "Please enter password";

    public boolean isStringEmpty(String string){
        return TextUtils.isEmpty(string)?true:false;
    }

}
