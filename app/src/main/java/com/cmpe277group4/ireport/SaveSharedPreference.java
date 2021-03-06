package com.cmpe277group4.ireport;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class SaveSharedPreference {

    private static final String USER_NAME = "user_name";
    private static final String USER_TYPE = "user_type";
    private static final String USER_ID = "user_id";

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

    public static void setUserId(Context ctx, String id){
        Log.d("a",id);
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(USER_ID, id);
    }

    public static int getUserType(Context ctx){
        return getSharedPreferences(ctx).getInt(USER_TYPE,2);
    }

    public static String getUserName(Context ctx){
        return getSharedPreferences(ctx).getString(USER_NAME,null);
    }

    public static String getUserId(Context ctx){
        return getSharedPreferences(ctx).getString(USER_ID,null);
    }
}
