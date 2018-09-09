package com.digitalmatatus.twigatatu.views;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitalmatatus.twigatatu.R;
import com.digitalmatatus.twigatatu.controllers.GetData;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import Interface.ServerCallback;

import static com.digitalmatatus.twigatatu.utils.Utils.applyFontForToolbarTitle;
import static com.digitalmatatus.twigatatu.utils.Utils.sentEmail;
import static com.digitalmatatus.twigatatu.utils.Utils.setClipboard;

public class MainActivity2 extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    public static Boolean registered = true;
    public static Integer unitId = null;
    public static String userName = null;

    private static final int REQUEST_FINE_LOCATION = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    protected Typeface mTfLight;

    private SharedPreferences prefsManager = null;

    ArrayList<String> stopList = new ArrayList<>();
    ArrayList<String> stopName = new ArrayList<>();
    ArrayList<String> descList = new ArrayList<>();
    ArrayList<String> stopIDs = new ArrayList<>();
    ArrayList<String> routeIDs = new ArrayList<>();

    private String stopTo = null, stopFrom = null, selectedItem = null;

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.send) {
            if (Utils.checkDefaults("data", getBaseContext())) {
                sentEmail(getBaseContext(), new String[]{"rstephenosoro@gmail.com"}
                        , "Bad Data", Utils.getDefaults("data", getBaseContext()));
            } else {
                Utils.showToast("No data to send!", getBaseContext());
            }
            return true;
        }

        if (id == R.id.copy) {
            if (Utils.checkDefaults("data", getBaseContext())) {
                setClipboard(getBaseContext(), Utils.getDefaults("data", getBaseContext()));
                Utils.showToast("Data successfully copied to clipboard!", getBaseContext());

            } else {
                Utils.showToast("No data to copy!", getBaseContext());
            }
            return true;
        }

        if (id == R.id.twigatatu) {

            loadRoutes();
            showDialog("Type and choose the route you want to submit data for",MainActivity2.this, MainActivity.class);



            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDialog(String msg, final Context ctx, final Class<?> cls) {
        android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(new ContextThemeWrapper(ctx, R.style.myDialog));

        builder1.setMessage(msg);
        builder1.setCancelable(true);

        LinearLayout layout = new LinearLayout(ctx);
        layout.setOrientation(LinearLayout.VERTICAL);

        final AutoCompleteTextView autoCompleteTextView = new AutoCompleteTextView(ctx);
        autoCompleteTextView.setTypeface(mTfLight);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_dropdown_item_1line, stopList);
        autoCompleteTextView.setAdapter(adapter);
        layout.addView(autoCompleteTextView);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (in != null) {
                    in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                }
                autoCompleteTextView.setText(adapter.getItem(position));
                selectedItem = parent.getItemAtPosition(position).toString();


                Log.e("stop_from is", adapter.getItem(position));
            }
        });

        builder1.setView(layout);


        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int i = 0;
                        if (stopList.size() > 0)
                            i = stopList.indexOf(selectedItem);

                        if (stopList.contains(selectedItem) && stopList.size() > 0) {
                            Utils.setDefaults("route_id", stopIDs.get(i), getBaseContext());

                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
                            prefs.edit().putBoolean("firstStart", false).apply();
                            prefs.edit().putBoolean("continuation_dc", true).apply();
                            prefs.edit().putString("route_id", stopIDs.get(i)).apply();

                            Intent intent = new Intent(ctx, cls);
                            ctx.startActivity(intent);

                        } else {
                            Utils.showToast(" Please choose one of the given routes to proceed! ", getBaseContext());
                            dialog.cancel();

                        }


                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        android.support.v7.app.AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    public void loadRoutes() {
        final ProgressDialog pd = new ProgressDialog(MainActivity2.this);
        pd.setMessage("loading routes");
        pd.show();
        pd.setCancelable(false);
        GetData getData = new GetData(MainActivity2.this);
        getData.get("routes/", new ServerCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONArray jsonArray = new JSONArray(result);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        stopList.add(jsonArray.getJSONObject(i).getString("short_name") + " - " + jsonArray.getJSONObject(i).getString("desc"));
                        stopName.add(jsonArray.getJSONObject(i).getString("short_name"));
                        stopIDs.add(jsonArray.getJSONObject(i).getString("id"));
                        routeIDs.add(jsonArray.getJSONObject(i).getString("route_id"));
                        descList.add(jsonArray.getJSONObject(i).getString("desc"));

                    }



                    pd.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onSuccess(JSONObject response) {

            }
        });
    }

}
