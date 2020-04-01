package com.omar.task1.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class MySharedPref {

    private SharedPreferences prefs;

    private static MySharedPref instance;

    private static final String LOGIN_ID = "LOGIN_ID";

    private MySharedPref(Context context) {
        prefs = context.getSharedPreferences("myPref", Context.MODE_PRIVATE);
    }


    public static MySharedPref getInstance(Activity activity) {

        if (instance == null) {
            instance = new MySharedPref(activity);
            return instance;
        }
        return instance;
    }

    public String isLoggedIn() {
        return prefs.getString(LOGIN_ID, null);

    }

    public void saveLogIn(String username) {
        prefs.edit().putString(LOGIN_ID, username).apply();

    }


    public void saveAccessToken(String token) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("fb_access_token", token);
        editor.apply(); // This line is IMPORTANT !!!
    }


    public String getToken() {

        return prefs.getString("fb_access_token", null);
    }

    public void clearToken() {

        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply(); // This line is IMPORTANT !!!
    }

    public void saveFacebookUserInfo(String first_name,String last_name, String email, String profileURL){

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("fb_first_name", first_name);
        editor.putString("fb_last_name", last_name);
        editor.putString("fb_email", email);
        editor.putString("fb_profileURL", profileURL);
        editor.apply(); // This line is IMPORTANT !!!
        Log.d("MyApp", "Shared Name : "+first_name+"\nLast Name : "+last_name+"\nEmail : "+email+"\nProfile Pic : "+profileURL);
    }

    public void getFacebookUserInfo(){

        Log.d("MyApp", "Name : "+prefs.getString("fb_name",null)+"\nEmail : "+prefs.getString("fb_email",null));
    }

    public String getEmail(){
        return prefs.getString("fb_email",null);
    }
}
