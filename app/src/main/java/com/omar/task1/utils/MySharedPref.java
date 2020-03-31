package com.omar.task1.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPref {

    private SharedPreferences sharedPreferences;

    private static MySharedPref instance;

    private static final String LOGIN_ID = "LOGIN_ID";

    private MySharedPref(Context context) {
        sharedPreferences = context.getSharedPreferences("myPref", Context.MODE_PRIVATE);
    }


    public static MySharedPref getInstance(Activity activity) {

        if (instance == null) {
            instance = new MySharedPref(activity);
            return instance;
        }
        return instance;
    }

    public String isLoggedIn() {
        return sharedPreferences.getString(LOGIN_ID, null);

    }

    public void saveLogIn(String username) {
        sharedPreferences.edit().putString(LOGIN_ID, username).apply();

    }
}
