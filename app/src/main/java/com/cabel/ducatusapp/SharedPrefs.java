package com.cabel.ducatusapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefs {
    private SharedPreferences mySharedPref;
    private static final String LOGIN_STATUS = "status_login", USERTYPE = "usertype";
    private static final String CURRENT_USER = "current_user";
    private static final String CURRENT_USER_ID = "current_user_id";

    public SharedPrefs(Context context) {
        mySharedPref = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }

    public void setDarkModeTheme(Boolean state) {
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putBoolean("darkTheme", state);
        editor.apply();
    }

    public Boolean loadDarkModeTheme() {
        return mySharedPref.getBoolean("darkTheme", false);
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getCurrentUser(Context context) {
        return getSharedPreferences(context).getString(CURRENT_USER, "");
    }

    public static void setCurrentUser(Context context, String current_user) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(CURRENT_USER, current_user);
        editor.apply();
    }

    public static String getCurrentUserId(Context context) {
        return getSharedPreferences(context).getString(CURRENT_USER_ID, "");
    }

    public static void setCurrentUserId(Context context, String current_user_id) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(CURRENT_USER_ID, current_user_id);
        editor.apply();
    }

    public static String getUsertype(Context context) {
        return getSharedPreferences(context).getString(USERTYPE, "");
    }

    public static void setUsertype(Context context, String usertype) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(USERTYPE, usertype);
        editor.apply();
    }

    public static boolean getLoginStatus(Context context) {
        return getSharedPreferences(context).getBoolean(LOGIN_STATUS, false);
    }

    public static void setLoginStatus(Context context, boolean status) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(LOGIN_STATUS, status);
        editor.apply();
    }

    public static void clearData(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(CURRENT_USER);
        editor.remove(USERTYPE);
        editor.remove(LOGIN_STATUS);
        editor.apply();
    }
}