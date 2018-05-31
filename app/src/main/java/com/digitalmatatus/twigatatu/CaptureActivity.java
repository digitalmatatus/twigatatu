package com.digitalmatatus.twigatatu;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalmatatus.twigatatu.controllers.GetData;
import com.digitalmatatus.twigatatu.utils.Util;
import com.digitalmatatus.twigatatu.views.MainActivity;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.digitalmatatus.twigatatu.utils.Utils;

import Interface.ServerCallback;

public class CaptureActivity extends AppCompatActivity implements ICaptureActivity, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static Intent serviceIntent;
    private CaptureService captureService;

    private Vibrator vibratorService;
    private static final int REQUEST_FINE_LOCATION = 0;
    private boolean registered = false;
    private boolean mIsBound;


    ArrayList<String> stopIDs = new ArrayList<>();
    ArrayList<String> stopList = new ArrayList<>();
    ArrayList<String> routeIDs = new ArrayList<>();


    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    private final ServiceConnection caputreServiceConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            captureService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            captureService = ((CaptureService.CaptureServiceBinder) service).getService();
            captureService.setCaptureActivity(CaptureActivity.this);

            initButtons();

            updateRouteName();
            updateDistance();
            updateDuration();
            updateStopCount();
            updatePassengerCountDisplay();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        setTitle("Twiga Tatu");
        showStops();
//        applyFontForToolbarTitle(this);

        // Start the service in case it isn't already running

        serviceIntent = new Intent(this, CaptureService.class);
        startService(serviceIntent);

        showGpsDialogAndGetLocation();

        bindService(serviceIntent, caputreServiceConnection, Context.BIND_AUTO_CREATE);
        CaptureService.boundToService = true;
        mIsBound = true;


        vibratorService = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        showPermissionDialog();

        // setup button listeners

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {

                    case R.id.StartCaptureButton:

                        if (!captureService.capturing) {
                            startCapture();
                        }

                        break;

                    case R.id.StopCaptureButton:

                        if (captureService.capturing) {

                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            Log.e("stop", "cap");
                                            stopCapture();
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked
                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(CaptureActivity.this);
                            builder.setMessage("Do you want to finish capturing?").setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();

                        }

                        break;

                    case R.id.transitStopButton:

                        transitStop();

                        break;

                    case R.id.PassengerAlightButton:

                        passengerAlight();

                        break;

                    case R.id.PassengerBoardButton:

                        passengerBoard();

                        break;

                    default:
                        break;
                }
            }
        };

        ImageButton startCaptureButton = (ImageButton) findViewById(R.id.StartCaptureButton);
        startCaptureButton.setOnClickListener(listener);

        ImageButton stopCaptureButton = (ImageButton) findViewById(R.id.StopCaptureButton);
        stopCaptureButton.setOnClickListener(listener);

        ImageButton transitStopButton = (ImageButton) findViewById(R.id.transitStopButton);
        transitStopButton.setOnClickListener(listener);

        ImageButton passengerAlightButton = (ImageButton) findViewById(R.id.PassengerAlightButton);
        passengerAlightButton.setOnClickListener(listener);

        ImageButton passengerBoardButton = (ImageButton) findViewById(R.id.PassengerBoardButton);
        passengerBoardButton.setOnClickListener(listener);

        ImageView passengerImageView = (ImageView) findViewById(R.id.passengerImageView);
        passengerImageView.setAlpha(128);

        initButtons();

        updateRouteName();
        updateDistance();
        updateStopCount();
        updatePassengerCountDisplay();
    }


    private void initButtons() {

        if (captureService == null) {
            ImageButton startCaptureButton = (ImageButton) findViewById(R.id.StartCaptureButton);
            startCaptureButton.setImageResource(R.drawable.start_button_gray);

            ImageButton stopCaptureButton = (ImageButton) findViewById(R.id.StopCaptureButton);
            stopCaptureButton.setImageResource(R.drawable.stop_button_gray);
        } else if (captureService.capturing) {
            ImageButton startCaptureButton = (ImageButton) findViewById(R.id.StartCaptureButton);
            startCaptureButton.setImageResource(R.drawable.start_button_gray);

            ImageButton stopCaptureButton = (ImageButton) findViewById(R.id.StopCaptureButton);
            stopCaptureButton.setImageResource(R.drawable.stop_button);
        } else if (!captureService.capturing && captureService.currentCapture == null) {

            ImageButton startCaptureButton = (ImageButton) findViewById(R.id.StartCaptureButton);
            startCaptureButton.setImageResource(R.drawable.start_button_gray);

            ImageButton stopCaptureButton = (ImageButton) findViewById(R.id.StopCaptureButton);
            stopCaptureButton.setImageResource(R.drawable.stop_button_gray);
        }

        if (!captureService.capturing && captureService.currentCapture == null && captureService.atStop()) {

            ImageButton transitCaptureButton = (ImageButton) findViewById(R.id.transitStopButton);
            transitCaptureButton.setImageResource(R.drawable.transit_stop_button_red);
        }
    }

    private void startCapture() {

        if (captureService != null) {
            try {
                captureService.startCapture();
            } catch (NoCurrentCaptureException e) {
                Intent settingsIntent = new Intent(CaptureActivity.this, NewActivity.class);
                startActivity(settingsIntent);
                return;
            }

            vibratorService.vibrate(100);

            Toast.makeText(CaptureActivity.this, "Starting capture...", Toast.LENGTH_SHORT).show();

            ((Chronometer) findViewById(R.id.captureChronometer)).setBase(SystemClock.elapsedRealtime());
            ((Chronometer) findViewById(R.id.captureChronometer)).start();

            initButtons();
        }
    }

    private void stopCapture() {

        if (captureService != null) {

            vibratorService.vibrate(100);

            groupData();

            if (captureService.currentCapture.points.size() > 0) {

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
//                intent.putExtra("routes",captureService.currentCapture);
                intent.putExtra("continuation", "continuation");

                startActivity(intent);
                Toast.makeText(CaptureActivity.this, "Capture complete!", Toast.LENGTH_SHORT).show();

            } else
                Toast.makeText(CaptureActivity.this, "No data collected, canceling.", Toast.LENGTH_SHORT).show();


            captureService.stopCapture();

        } else {
            Log.e("data null", "null");
        }
        // else handle


        ((Chronometer) findViewById(R.id.captureChronometer)).stop();

        initButtons();

        Intent finishCaptureIntent = new Intent(CaptureActivity.this, MainActivity.class);
//        finishCaptureIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        finishCaptureIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finishCaptureIntent.putExtra("continuation", "continuation");

        startActivity(finishCaptureIntent);
    }

    private void passengerAlight() {

        if (captureService != null && captureService.capturing) {
            if (captureService.currentCapture.totalPassengerCount == 0)
                return;

            vibratorService.vibrate(5);

            captureService.currentCapture.totalPassengerCount--;

            captureService.currentCapture.alightCount++;
            updatePassengerCountDisplay();
        }
    }

    private void passengerBoard() {

        if (captureService != null && captureService.capturing) {
            captureService.currentCapture.totalPassengerCount++;

            vibratorService.vibrate(5);

            captureService.currentCapture.boardCount++;
            updatePassengerCountDisplay();
        }
    }

    public void triggerTransitStopDepature() {

        if (captureService != null) {
            if (captureService.atStop()) {
                transitStop();
            }
        }
    }

    private void transitStop() {

        if (captureService != null && captureService.capturing) {

            if (captureService.atStop()) {
                Toast.makeText(CaptureActivity.this, "Stop departure", Toast.LENGTH_SHORT).show();

                captureService.departStopStop(captureService.currentCapture.boardCount, captureService.currentCapture.alightCount);
                captureService.currentCapture.alightCount = 0;
                captureService.currentCapture.boardCount = 0;

                ImageButton transitCaptureButton = (ImageButton) findViewById(R.id.transitStopButton);
                transitCaptureButton.setImageResource(R.drawable.transit_stop_button);

                vibratorService.vibrate(25);

                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                vibratorService.vibrate(25);

            } else {
//                Capturing ariveAtStop after getting the route name
                setRouteName();
                captureService.ariveAtStop();

                ImageButton transitCaptureButton = (ImageButton) findViewById(R.id.transitStopButton);
                transitCaptureButton.setImageResource(R.drawable.transit_stop_button_red);

                vibratorService.vibrate(25);

                Toast.makeText(CaptureActivity.this, "Stop arrival", Toast.LENGTH_SHORT).show();
            }

            updatePassengerCountDisplay();

            updateStopCount();

        }

    }

    private void updateRouteName() {
        TextView routeNameText = (TextView) findViewById(R.id.routeNameText);

        routeNameText.setText("Capture: " + captureService.currentCapture.name);
    }

    private void updateStopCount() {
        TextView stopsText = (TextView) findViewById(R.id.stopsText);

        stopsText.setText(captureService.currentCapture.stops.size() + "");
    }

    public void updateDistance() {

        TextView distanceText = (TextView) findViewById(R.id.distanceText);

        DecimalFormat distanceFormat = new DecimalFormat("#,##0.00");

        distanceText.setText(distanceFormat.format((double) (captureService.currentCapture.distance) / 1000) + "km");

    }

    public void updateDuration() {
        if (captureService.capturing) {
            ((Chronometer) findViewById(R.id.captureChronometer)).setBase(captureService.currentCapture.startMs);
            ((Chronometer) findViewById(R.id.captureChronometer)).start();
        } else {
            ((Chronometer) findViewById(R.id.captureChronometer)).setBase(SystemClock.elapsedRealtime());
        }
    }

    public void updateGpsStatus() {
        TextView distanceText = (TextView) findViewById(R.id.gpsStatus);

        distanceText.setText(captureService.getGpsStatus());
    }

    private void updatePassengerCountDisplay() {

        if (captureService != null && captureService.capturing) {

            TextView totalPassengerCount = (TextView) findViewById(R.id.totalPasssengerCount);
            TextView alightingPassengerCount = (TextView) findViewById(R.id.alightingPassengerCount);
            TextView boardingPassengerCount = (TextView) findViewById(R.id.boardingPassengerCount);

            totalPassengerCount.setText(captureService.currentCapture.totalPassengerCount.toString());

            if (captureService.currentCapture.alightCount > 0)
                alightingPassengerCount.setText("-" + captureService.currentCapture.alightCount.toString());
            else
                alightingPassengerCount.setText("0");

            if (captureService.currentCapture.boardCount > 0)
                boardingPassengerCount.setText("+" + captureService.currentCapture.boardCount.toString());
            else
                boardingPassengerCount.setText("0");
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


    //   Requesting GPS to be turned on automatically before proceeding to collect data
    private void showGpsDialogAndGetLocation() {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(CaptureActivity.this)
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
                                    CaptureActivity.this,
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mIsBound) {
            unbindService(caputreServiceConnection);
            mIsBound = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mIsBound) {
            unbindService(caputreServiceConnection);
            mIsBound = false;
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        serviceIntent = new Intent(this, CaptureService.class);
        startService(serviceIntent);

        bindService(serviceIntent, caputreServiceConnection, Context.BIND_AUTO_CREATE);
        CaptureService.boundToService = true;
        mIsBound = true;

    }

    private void groupData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("route_name", Utils.getDefaults("route_name", getBaseContext()));
            jsonObject.put("description", Utils.getDefaults("route_description", getBaseContext()));
            jsonObject.put("notes", Utils.getDefaults("field_notes", getBaseContext()));
            jsonObject.put("vehicle_capacity", Utils.getDefaults("vehicle_capacity", getBaseContext()));
            jsonObject.put("vehicle_type", Utils.getDefaults("vehicle_type", getBaseContext()));
            jsonObject.put("startTime", Utils.getDefaults("start_time", getBaseContext()));
            jsonObject.put("route_id", Utils.getDefaults("route_id", getBaseContext()));
            JSONArray jsonArray = null;
            if (Utils.checkDefaults("routes", getBaseContext())) {
                try {
                    jsonArray = new JSONArray(Utils.getDefaults("routes", getBaseContext()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Utils.setDefaults("routes", jsonArray.toString(), getBaseContext());
            } else {
                Toast.makeText(getBaseContext(), "No route  data", Toast.LENGTH_LONG);
                Log.e("No route  data", "No route  data");
            }

            jsonObject.put("route", jsonArray);
            JSONArray jsonArray2 = null;

            if (Utils.checkDefaults("stops", getBaseContext())) {
                try {
                    jsonArray2 = new JSONArray(Utils.getDefaults("stops", getBaseContext()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getBaseContext(), "No stops data", Toast.LENGTH_LONG);
                Log.e("No stops data", "No stops  data");
            }
            jsonObject.put("stops", jsonArray2);

            if (Utils.checkDefaults("data", getBaseContext())) {
                JSONArray jsonArray1;
                try {
                    jsonArray1 = new JSONArray(Utils.getDefaults("data", getBaseContext()));
                    jsonArray1.put(jsonObject);
                    Utils.setDefaults("data", jsonArray1.toString(), getBaseContext());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                JSONArray jsonArray1 = new JSONArray();
                jsonArray1.put(jsonObject);
                Utils.setDefaults("data", jsonArray1.toString(), getBaseContext());

            }

            Log.e("DATA to upload is", Utils.getDefaults("data", getBaseContext()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void setRouteName() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CaptureActivity.this);

        alertDialogBuilder.setTitle("Stop Name");
        alertDialogBuilder.setMessage("Enter stop name below");

        LinearLayout layout = new LinearLayout(CaptureActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final AutoCompleteTextView autoCompleteTextView = new AutoCompleteTextView(CaptureActivity.this);

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

                Log.e("stop_from is", adapter.getItem(position));
            }
        });

        TextView hint = new TextView (CaptureActivity.this);
        hint.setText("Choose below - Designated/ Undesignated");
        layout.addView(hint);



        Spinner dropdown = new Spinner (CaptureActivity.this);
        String[] items = new String[]{"Designated", "Undesignated"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter2);
        layout.addView(dropdown);


        alertDialogBuilder.setView(layout);

        alertDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        alertDialogBuilder.setNegativeButton("Close", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();


            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void showStops() {
        final ProgressDialog pd = new ProgressDialog(CaptureActivity.this);
        pd.setMessage("loading stops");
        pd.show();
        pd.setCancelable(true);
        GetData stops = new GetData(getBaseContext());
        stops.online_stops("stops", new ServerCallback() {
            @Override
            public void onSuccess(String result) {
                Log.e("result", result.toString());
                try {
                    JSONArray stops = new JSONArray(result);


                    for (int i = 0; i < stops.length(); i++) {
                        stopList.add(stops.getJSONObject(i).getString("name"));
                        stopIDs.add(stops.getJSONObject(i).getString("id"));
                        routeIDs.add(stops.getJSONObject(i).getString("stop_id"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pd.dismiss();

            }

            @Override
            public void onSuccess(JSONObject response) {

            }
        });
    }


}
