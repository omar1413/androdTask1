package com.omar.task1.utils;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class Utils {


    public static void errorAlert(Context context, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Error");
        builder.setMessage(message);
        builder.show();
    }
}
