package com.cmpe277group4.ireport;

import android.text.TextUtils;

/**
 * Created by 33843 on 12/1/2016.
 */
public class FactoryMethods {

    public boolean isStringEmpty(String string){
        return TextUtils.isEmpty(string)?true:false;
    }

    public boolean isStringShort(String password, int i) { return password.length() < 6?true:false; }
}
