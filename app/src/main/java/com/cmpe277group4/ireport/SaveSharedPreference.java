package com.cmpe277group4.ireport;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {

    private static final String USER_NAME = "username";
    private static final String USER_TYPE = "user_type";

    static SharedPreferences getSharedPreferences(Context ctx){
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String username){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(USER_NAME,username);
        editor.commit();
    }

    public static void setUserType(Context ctx, int userType){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(USER_TYPE,userType);
        editor.commit();
    }

    public static int getUserType(Context ctx){
        return getSharedPreferences(ctx).getInt(USER_TYPE,2);
    }

    public static String getUserName(Context ctx){
        return getSharedPreferences(ctx).getString(USER_NAME,"");
    }
}
