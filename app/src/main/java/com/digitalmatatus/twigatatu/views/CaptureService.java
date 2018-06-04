package com.digitalmatatus.twigatatu.views;

import android.Manifest;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.digitalmatatus.twigatatu.R;
import com.digitalmatatus.twigatatu.controllers.NoCurrentCaptureException;
import com.digitalmatatus.twigatatu.model.RoutePoint;
import com.digitalmatatus.twigatatu.model.RouteStop;
import com.digitalmatatus.twigatatu.model.TransitWandProtos;
import com.digitalmatatus.twigatatu.utils.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.DetectedActivity;
import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import Interface.ICaptureActivity;
import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnGeofencingTransitionListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.geofencing.utils.TransitionGeofence;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;

import com.digitalmatatus.twigatatu.utils.Utils;


public class CaptureService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnActivityUpdatedListener, OnGeofencingTransitionListener, OnLocationUpdatedListener {

    public final static String SERVER = "transitwand.com"; // // "; //"192.168.43.137:9000";
    public final static String URL_BASE = "http://" + SERVER + "/";

    public static Boolean boundToService = false;

    private static int GPS_UPDATE_INTERVAL = 5;    // seconds
    private static int MIN_ACCURACY = 15;    // meters
    private static int MIN_DISTANCE = 5;    // meters

    private static Boolean gpsStarted = false;
    private static Boolean registered = false;

    public static String imei = null;
    public static Long phoneId = null;

    private static Application appContext;

    private static int NOTIFICATION_ID = 234231222;

    private final IBinder binder = new CaptureServiceBinder();

    private CaptureLocationListener locationListener;

    private LocationManager gpsLocationManager;
    private NotificationManager gpsNotificationManager;

    private static boolean gpsEnabled;
    boolean gps_enabled = false;


    public static RouteCapture currentCapture = null;
    public static RouteStop currentStop = null;
    public static JSONObject currentJStop = null;

    private static Location lastLocation = null;

    public static Boolean capturing = false;

    private static ICaptureActivity captureActivity;

    private SharedPreferences prefsManager = null;

    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    protected static LocationGooglePlayServicesProvider provider;

    private GoogleApiClient googleApiClient;

    //    Intent intent;
    public static final String BROADCAST_ACTION = "com.conveyal.transitwand.dismissprogressbar";


    @Override
    public void onCreate() {
        Log.i("CaptureService", "onCreate");

        appContext = this.getApplication();
        gpsNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        prefsManager = PreferenceManager.getDefaultSharedPreferences(this);
//        intent = new Intent(BROADCAST_ACTION);


        if (imei == null) {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                CaptureService.imei = telephonyManager.getDeviceId();
            }


            // fall back to Secure.ANDROID_ID if IMEI isn't set -- continuing to use IMEI as primary ID mechanism for backwards compatibility
            if (CaptureService.imei == null || CaptureService.imei.length() == 0) {
                CaptureService.imei = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
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
    public void onActivityUpdated(DetectedActivity detectedActivity) {

    }

    @Override
    public void onGeofenceTransition(TransitionGeofence transitionGeofence) {

    }

    @Override
    public void onLocationUpdated(Location location) {
// Location has changed update location
        locationChanged(location);

    }


    public class CaptureServiceBinder extends Binder {
        public CaptureService getService() {
            return CaptureService.this;
        }
    }

    public static void setCaptureActivity(ICaptureActivity ca) {
        captureActivity = ca;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("CaptureService", "onStartCommand");
        handleIntent(intent);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("CaptureService", "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        Log.i("CaptureService", "onLowMemory");
        super.onLowMemory();
    }

    private void handleIntent(Intent intent) {
        // handle received intents

    }

    public void newCapture(String name, String description, String notes, String vehicleType, String vehicleCapacity, String route_id) {
        startGps();

        showNotificationTray();

        synchronized (this) {
            if (currentCapture != null && capturing)
                stopCapture();

            lastLocation = null;

            Integer id = prefsManager.getInt("routeId", 10000);

            id++;

            prefsManager.edit().putInt("routeId", id).commit();

            currentCapture = new RouteCapture();
            currentCapture.id = id;
            currentCapture.setRouteName(name);
            currentCapture.description = description;
            currentCapture.notes = notes;
            currentCapture.vehicleCapacity = vehicleCapacity;
            currentCapture.vehicleType = vehicleType;

//            If the user's phone has interent connection, we use below code to get location updates from the network
//            provider which is more accurate than getting location from GPS
            if (Util.hasInternetConnected(getApplicationContext())) {
                startLocation();
            }

        }
    }

    public void startCapture() throws NoCurrentCaptureException {
        startGps();


        showNotificationTray();

        if (currentCapture != null) {
            currentCapture.startTime = new Date().getTime();
            currentCapture.startMs = SystemClock.elapsedRealtime();
            capturing = true;
        } else {
            throw new NoCurrentCaptureException();
        }
    }

    public void stopCapture() {
        stopGps();

        hideNotificationTray();

        currentCapture.stopTime = new Date().getTime();

        if (currentCapture.points.size() > 0) {

            TransitWandProtos.Upload.Route routePb = currentCapture.seralize();

            File file = new File(getFilesDir(), "route_" + currentCapture.id + ".pb");

            FileOutputStream os;

            try {

                os = new FileOutputStream(file);
                routePb.writeDelimitedTo(os);
                os.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        capturing = false;
        currentCapture = null;
    }

    public void ariveAtStop(String stop_name, String designation) {

        if (currentStop != null) {
            departStopStop(0, 0);
        }

        if (lastLocation != null) {
            currentStop = new RouteStop();

            currentStop.arrivalTime = SystemClock.elapsedRealtime();
            currentStop.location = lastLocation;

            currentJStop = new JSONObject();
            try {
                currentJStop.put("arrival_time", SystemClock.elapsedRealtime());
                currentJStop.put("latitude", lastLocation.getLatitude());
                currentJStop.put("longitude", lastLocation.getLongitude());
                currentJStop.put("stop_name", stop_name);
                currentJStop.put("stop_designation",designation);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }


    public void departStopStop(int board, int alight) {

        if (lastLocation != null) {

            if (currentStop == null) {
                currentStop = new RouteStop();
                currentStop.arrivalTime = SystemClock.elapsedRealtime();
                currentStop.location = lastLocation;

                try {
                    currentJStop.put("arrival_time", SystemClock.elapsedRealtime());
                    currentJStop.put("latitude", lastLocation.getLatitude());
                    currentJStop.put("longitude", lastLocation.getLongitude());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            currentStop.alight = alight;
            currentStop.board = board;
            currentStop.departureTime = SystemClock.elapsedRealtime();
            try {
                currentJStop.put("alight", alight);
                currentJStop.put("board", board);
                currentJStop.put("departure_time", SystemClock.elapsedRealtime());
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (Utils.checkDefaults("stops", appContext)) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(Utils.getDefaults("stops", appContext));
                    jsonArray.put(currentJStop);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Utils.setDefaults("stops", jsonArray.toString(), appContext);
            } else {
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(currentJStop);
                Utils.setDefaults("stops", jsonArray.toString(), appContext);
            }

            currentCapture.stops.add(currentStop);

            currentStop = null;
            currentJStop = null;
        }

    }

    public boolean atStop() {

        if (currentStop == null)
            return false;
        else
            return true;

    }

    public long distanceFromLocation(Location l1, Location l2) {

        LatLng ll1 = new LatLng(l1.getLatitude(), l1.getLongitude());
        LatLng ll2 = new LatLng(l2.getLatitude(), l2.getLongitude());

        return Math.round(LatLngTool.distance(ll1, ll2, LengthUnit.METER));
    }

    public void onLocationChanged(Location location) {

        Log.i("", "onLocationChanged: " + location);

        if (atStop() && lastLocation != null && distanceFromLocation(currentStop.location, lastLocation) > MIN_DISTANCE * 2) {
            captureActivity.triggerTransitStopDepature();
        }

        if (currentCapture != null && location.getAccuracy() < MIN_ACCURACY * 2) {

            RoutePoint rp = new RoutePoint();
            rp.location = location;
            rp.time = SystemClock.elapsedRealtime();

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("latitude", location.getLatitude());
                jsonObject.put("longitude", location.getLongitude());
                jsonObject.put("time", SystemClock.elapsedRealtime());

            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (Utils.checkDefaults("routes", appContext)) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(Utils.getDefaults("routes", appContext));
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Utils.setDefaults("routes", jsonArray.toString(), appContext);
            } else {
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObject);
                Utils.setDefaults("routes", jsonArray.toString(), appContext);
            }

            Log.e("inside current cap", "inSide");

            currentCapture.points.add(rp);

            if (lastLocation != null) {

                currentCapture.distance += distanceFromLocation(lastLocation, location);

                if (captureActivity != null)
                    captureActivity.updateDistance();
            }

            lastLocation = location;

            if (captureActivity != null)
                captureActivity.updateGpsStatus();
        }
    }

    private void startGps() {
        Log.i("LocationService", "startGps");

        if (gpsStarted)
            return;

        gpsStarted = true;

        if (locationListener == null) {
            locationListener = new CaptureLocationListener();
        }

        // connect location manager
        gpsLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        gpsEnabled = gpsLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (gpsEnabled) {
            Log.i("LocationService", "startGps attaching listeners");
            // request gps location and status updates

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            gpsLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPDATE_INTERVAL * 1000, MIN_DISTANCE, locationListener);
            gpsLocationManager.addGpsStatusListener(locationListener);

            // update gps status in main activity
        } else {
            Log.e("LocationService", "startGps failed, GPS not enabled");
            // update gps status in main activity
        }
    }

    private void stopGps() {
        Log.i("LocationService", "stopGps");

        if (gpsLocationManager == null)
            gpsLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationListener != null) {
            gpsLocationManager.removeUpdates(locationListener);
            gpsLocationManager.removeGpsStatusListener(locationListener);
        }

        gpsStarted = false;
    }

    public void restartGps() {
        Log.i("LocationService", "restartGps");

        stopGps();
        startGps();
    }

    public void stopGpsAndRetry() {

        Log.d("LocationService", "stopGpsAndRetry");

        restartGps();
    }

    public String getGpsStatus() {

        String status = "";

        if (lastLocation != null)
            status = "GPS +/-" + Math.round(lastLocation.getAccuracy()) + "m";
        else
            status = "GPS Pending";

        return status;
    }

    private void showNotificationTray() {

        Intent contentIntent = new Intent(this, CaptureActivity.class);

//  TODO Deprecated
      /* 	PendingIntent pending = PendingIntent.getActivity(getApplicationContext(), 0, contentIntent,
             android.content.Intent.FLAG_ACTIVITY_NEW_TASK);

        Notification notification = new Notification(R.drawable.tray_icon, null, System.currentTimeMillis());
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
    

        notification.setLatestEventInfo(getApplicationContext(), "TransitWand", "", pending);
        
        gpsNotificationManager.notify(NOTIFICATION_ID, notification);
        startForeground(NOTIFICATION_ID, notification);


*/
// TODO Notification Replacement
        PendingIntent pending = PendingIntent.getActivity(getApplicationContext(), 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder b = new NotificationCompat.Builder((getApplicationContext()));

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.tray_icon)
                .setTicker("")
                .setContentTitle("Twiga Tatu")
                .setContentText("Twiga Tatu")
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentIntent(pending)
                .setContentInfo("Info");


        gpsNotificationManager.notify(NOTIFICATION_ID, b.build());
        startForeground(NOTIFICATION_ID, b.build());

    }

    private void hideNotificationTray() {

        gpsNotificationManager.cancel(NOTIFICATION_ID);
        stopForeground(true);
    }


    private class CaptureLocationListener implements LocationListener, GpsStatus.Listener {

        @Override
        public void onLocationChanged(Location location) {
            try {
                if (location != null) {
                    Log.i("ProbeLocationListener", "onLocationChanged");
                    CaptureService.this.onLocationChanged(location);
                }

            } catch (Exception ex) {
                Log.e("ProbeLocationListener", "onLocationChanged failed", ex);
            }

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i("ProbeLocationListener", "onProviderDisabled");
            //this.restartGps();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i("ProbeLocationListener", "onProviderEnabled");
            //locationService.restartGps();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GpsStatus.GPS_EVENT_FIRST_FIX:

                    break;

                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

                    break;

                case GpsStatus.GPS_EVENT_STARTED:

                    break;

                case GpsStatus.GPS_EVENT_STOPPED:

                    break;

            }
        }
    }


    public static boolean checkPermissions(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    protected void startLocation() {

        provider = new LocationGooglePlayServicesProvider();
        provider.setCheckLocationSettings(true);

        SmartLocation smartLocation = new SmartLocation.Builder(this).logging(true).build();

        smartLocation.location(provider).start(this);
        smartLocation.activity().start(this);


    }


    //    Function for stopping location updates from network provider
    private void stopLocation() {
        SmartLocation.with(this).location().stop();

        SmartLocation.with(this).activity().stop();
//        SmartLocation.with(this).geofencing().stop();

    }

    private void locationChanged(Location location) {
        if (location != null) {


            final String text = String.format("Latitude %.6f, Longitude %.6f",
                    location.getLatitude(),
                    location.getLongitude());
            Log.e("text loc", text);

            if (atStop() && lastLocation != null && distanceFromLocation(currentStop.location, lastLocation) > MIN_DISTANCE * 2) {
                captureActivity.triggerTransitStopDepature();
            }

           /* if (currentCapture != null) {

                Log.e("accuracy", location.getAccuracy() + "");
                Log.e("min accuracy", MIN_ACCURACY * 2 + "");
            }*/

//TODO check on the min_accuracy

            if (currentCapture != null && location.getAccuracy() < MIN_ACCURACY * 2) {

                RoutePoint rp = new RoutePoint();
                rp.location = location;
                rp.time = SystemClock.elapsedRealtime();
                Log.e("Network Location", "Network Location");

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("latitude", location.getLatitude());
                    jsonObject.put("longitude", location.getLongitude());
                    jsonObject.put("time", SystemClock.elapsedRealtime());

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if (Utils.checkDefaults("routes", appContext)) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(Utils.getDefaults("routes", appContext));
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Utils.setDefaults("routes", jsonArray.toString(), appContext);
                } else {
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(jsonObject);
                    Utils.setDefaults("routes", jsonArray.toString(), appContext);
                }

                currentCapture.points.add(rp);

                if (lastLocation != null) {

                    currentCapture.distance += distanceFromLocation(lastLocation, location);

                    if (captureActivity != null)
                        captureActivity.updateDistance();
                }

                lastLocation = location;


                if (captureActivity != null)
                    captureActivity.updateGpsStatus();
            }
        }
    }
}
