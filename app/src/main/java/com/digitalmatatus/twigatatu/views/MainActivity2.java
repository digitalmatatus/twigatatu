package com.digitalmatatus.twigatatu.views;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.digitalmatatus.twigatatu.R;
import com.digitalmatatus.twigatatu.utils.Util;
import com.digitalmatatus.twigatatu.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.File;

import static com.digitalmatatus.twigatatu.utils.Utils.applyFontForToolbarTitle;

public class MainActivity2 extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    public static Boolean registered = true;
    public static Integer unitId = null;
    public static String userName = null;

    private static final int REQUEST_FINE_LOCATION = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    protected Typeface mTfLight;

    private SharedPreferences prefsManager = null;

    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

            if (key.equals("registered")) {

                updateRegistrationData();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appbar_main2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Twiga Tatu");
        applyFontForToolbarTitle(this);
        mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");


        SharedPreferences prefsManager = PreferenceManager.getDefaultSharedPreferences(MainActivity2.this);
        prefsManager.edit().putBoolean("registered", true).putString("unitId", "1234").putString("userName", "gtfs").commit();


        prefsManager = PreferenceManager.getDefaultSharedPreferences(this);
        prefsManager.registerOnSharedPreferenceChangeListener(prefListener);


        TextView userNameText = findViewById(R.id.UserNameText);
        userNameText.setTypeface(mTfLight);
        userNameText.setText("gtfs");

        TextView unitIdText = findViewById(R.id.UnitIdText);
        unitIdText.setText("Unit1");
        unitIdText.setTypeface(mTfLight);

        TextView desc = findViewById(R.id.descriptionText);
        desc.setTypeface(mTfLight);

        TextView total = findViewById(R.id.uploadText);
        total.setTypeface(mTfLight);

        TextView delete = findViewById(R.id.TextView02);
        delete.setTypeface(mTfLight);

        TextView saved = findViewById(R.id.ReviewText);
        saved.setTypeface(mTfLight);

        TextView visualize = findViewById(R.id.viewText);
        visualize.setTypeface(mTfLight);

        showPermissionDialog();


        showGpsDialogAndGetLocation();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {

                    case R.id.WandButton:

                        Intent settingsIntent = new Intent(MainActivity2.this, NewActivity.class);
                        startActivity(settingsIntent);
                        break;

                    case R.id.uploadButton:

                        Intent mailIntent = new Intent(MainActivity2.this, ReviewActivity.class);
                        startActivity(mailIntent);
                        break;

                    case R.id.mapButton:

                       /* Intent uploadIntent = new Intent(MainActivity2.this, ShowMap.class);
                        startActivity(uploadIntent);*/
                        Log.e("clicked", "delete");
                        for (File f : getFilesDir().listFiles()) {
                            f.delete();
                        }
                        deleteData(getBaseContext(), MainActivity2.class);
                        break;
                    case R.id.ReviewButton:

                        Intent reviewIntent = new Intent(MainActivity2.this, ViewData.class);
                        startActivity(reviewIntent);
                        break;

                    case R.id.viewButton:

                        Intent viewIntent = new Intent(MainActivity2.this, MapActivity.class);
                        startActivity(viewIntent);
                        break;

                    default:
                        break;
                }
            }
        };

        ImageButton wandButton = findViewById(R.id.WandButton);
        wandButton.setOnClickListener(listener);

        ImageButton reviewButton = findViewById(R.id.uploadButton);
        reviewButton.setOnClickListener(listener);

        ImageButton deleteButton = findViewById(R.id.mapButton);
        deleteButton.setOnClickListener(listener);

        ImageButton viewButton = findViewById(R.id.ReviewButton);
        viewButton.setOnClickListener(listener);

        ImageButton mapButton = findViewById(R.id.viewButton);
        mapButton.setOnClickListener(listener);

    }


    public void updateRegistrationData() {

        if (prefsManager != null) {

            registered = prefsManager.getBoolean("registered", false);

            TextView userNameText = (TextView) findViewById(R.id.UserNameText);
            userNameText.setText(prefsManager.getString("userName", ""));

            TextView unitIdText = (TextView) findViewById(R.id.UnitIdText);
            unitIdText.setText("Unit " + prefsManager.getString("unitId", "unregistered"));
        }
    }

    protected void showPermissionDialog() {
        if (!CaptureService.checkPermissions(this) || !CaptureService.checkPermission(this)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(intent);
        if (CaptureService.provider != null) {
            CaptureService.provider.onActivityResult(requestCode, resultCode, intent);
        }
        switch (requestCode) {
            case 1000:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                       /* if (googleApiClient.isConnected()) {
//                            startLocationUpdates();
                        }*/
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // granted

                    Looper looper = null;
                    if (CaptureService.checkPermission(getBaseContext())) {

//                        TODO Commented this location code

                        if (Util.hasInternetConnected(getBaseContext())) {
//                            CaptureService.startLocation();
//                            startCapture();
                        }
                    }
                } else {
                    // not granted
                    Util.showToast("You cannot get geo coordinates quickly without accepting this permission!", getBaseContext());
                    showPermissionDialog();
                }
                return;
            }

        }

    }

    private void showGpsDialogAndGetLocation() {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(MainActivity2.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        //initialize the builder and add location request paramenter like HIGH Aurracy
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(10 * 60 * 1000) // every 10 minutes
                .setExpirationDuration(10 * 1000) // After 10 seconds
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        // set builder to always true (Shows the dialog after never operation too)
        builder.setAlwaysShow(true);

        // Then check whether current location settings are satisfied:
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates locationSettingsStates = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MainActivity2.this,
                                    1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static void deleteData(Context context, Class<?> cls) {


        if (Utils.checkDefaults("data", context)) {
            Log.e("jsonArray data", Utils.getDefaults("data", context));
            Utils.setDefaults("data", "", context);


            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("data");
            editor.commit();

            if (!Utils.checkDefaults("data", context)) {
                Utils.showToast("Data deleted", context);
            }


        }

        if (Utils.checkDefaults("stops", context)) {
            Log.e("jsonArray stops", Utils.getDefaults("stops", context));
            Utils.setDefaults("stops", "", context);

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("stops");
            editor.commit();

            if (!Utils.checkDefaults("stops", context)) {
                Utils.showToast("Stops deleted", context);
            }


        }

        if (Utils.checkDefaults("route", context)) {
            Log.e("jsonArray routes", Utils.getDefaults("route", context));
            Utils.setDefaults("route", "", context);

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("route");
            editor.commit();

            if (!Utils.checkDefaults("route", context)) {
                Utils.showToast("route deleted", context);
            }

        }

        if (Utils.checkDefaults("routes", context)) {
            Log.e("jsonArray routes", Utils.getDefaults("routes", context));
            Utils.setDefaults("route", "", context);

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("routes");
            editor.commit();

            if (!Utils.checkDefaults("routes", context)) {
                Utils.showToast("routes deleted", context);
            }

        }

        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }
}
