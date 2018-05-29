package com.digitalmatatus.twigatatu.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalmatatus.twigatatu.CaptureService;
import com.digitalmatatus.twigatatu.R;

import java.util.HashMap;


/**
 * Created by stephineosoro on 07/06/16.
 */
public class Utils {
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;

    //    Setting up base URL
    public static String baseURL() {
        return "http://41.89.64.3/api/";
//        return "http://41.89.64.90:8000/api/";
//        return "http://41.204.186.47:8000/twiga/";

    }

    public static void set(String username, String password, Context context) {
        setDefaults("username", username, context);
        setDefaults("password", password, context);
    }

    //    Basic Aunthentication headers
    public static HashMap<String, String> AunthenticationHeaders(Context context) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        String creds = String.format("%s:%s", "web@oneshoppoint.com", "spr0iPpQAiwS8u");
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        headers.put("Authorization", auth);
        return headers;
    }

    public static HashMap<String, String> AunthenticationHeadersAdmin(Context context) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        String creds = String.format("%s:%s", getDefaults("username", context), getDefaults("password", context));
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        headers.put("Authorization", auth);
        return headers;
    }

    //  Storing temporary values to shared preferences
    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    //    Getting item from shared preference
    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }


    //    Showing toast
    public static void showToast(String text, Context context) {

        /*Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();*/

        Toast ToastMessage = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        View toastView = ToastMessage.getView();
        toastView.setBackgroundResource(R.drawable.toast_background_color);
        ToastMessage.show();
    }

    //    checking if there's internet connection before proceeding
    public static boolean hasInternetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                return true;
            }
        }
        return false;
    }

    //    Checking if item is in shared preference
    public static Boolean checkDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.contains(key);
    }

    //    Deleting all items in shared preference
    public static void Delete(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    public static void applyFontForToolbarTitle(Activity context) {
        Toolbar toolbar = (Toolbar) context.findViewById(R.id.toolbar);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                Typeface titleFont = Typeface.
                        createFromAsset(context.getAssets(), "OpenSans-Light.ttf");
                if (tv.getText().equals(toolbar.getTitle())) {
                    tv.setTypeface(titleFont);
                    break;
                }
            }
        }
    }

    public static Typeface font(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf");
    }

    public static void showProceed(String msg, final Context ctx, final Class<?> cls, final Class<?> cls2) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ctx);
        builder1.setMessage(msg);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent intent = new Intent(ctx, cls);
                        ctx.startActivity(intent);
//                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(ctx, cls2);
                        ctx.startActivity(intent);
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    public static void showDialog(String msg, final Context ctx, final Class<?> cls, final Class<?> cls2) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ctx);
        builder1.setMessage(msg);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent intent = new Intent(ctx, cls);
                        ctx.startActivity(intent);
//                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(ctx, cls2);
                        ctx.startActivity(intent);
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }



    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void showPermissionDialog(Activity context) {
        if (!CaptureService.checkPermissions(context) || !CaptureService.checkPermission(context)) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

}
