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

    public long isLoggedIn() {
        return sharedPreferences.getLong(LOGIN_ID, -1);

    }

    public void saveLogIn(long id) {
        sharedPreferences.edit().putLong(LOGIN_ID, id).apply();

    }
}
