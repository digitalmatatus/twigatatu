package com.digitalmatatus.twigatatu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import Interface.ServerCallback;
import com.digitalmatatus.twigatatu.R;

import com.digitalmatatus.twigatatu.controllers.GetData;
import com.digitalmatatus.twigatatu.utils.Utils;

public class NewActivity extends Activity {

    private static Intent serviceIntent;
    private CaptureService captureService;
    private AutoCompleteTextView route,description,vehicle_type;
    EditText notes,capacity;
    ArrayList<String> stopList = new ArrayList<>();
    ArrayList<String> descList = new ArrayList<>();
    ArrayList<String> stopIDs = new ArrayList<>();
    ArrayList<String> routeIDs = new ArrayList<>();
    private String stopTo = null, stopFrom = null;
    private boolean mIsBound;
    private static final int REQUEST_FINE_LOCATION = 0;


    private final ServiceConnection caputreServiceConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            captureService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            captureService = ((CaptureService.CaptureServiceBinder) service).getService();
            //com.digitalmatatus.twigatatu.CaptureService.setServiceClient(CaptureActivity.this);

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        route = findViewById(R.id.routeID);
        description = findViewById(R.id.routeDescription);
        vehicle_type = findViewById(R.id.vehicleType);

        notes = findViewById(R.id.fieldNotes);
        capacity = findViewById(R.id.vehicleCapacity);

        ArrayList<String> v_types = new ArrayList<>();
        v_types.add("Bus - 64 seater");
        v_types.add("Minibus - 45 Seater");
        v_types.add("Van - 14 Seater");

        final ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_dropdown_item_1line, v_types);
        vehicle_type.setAdapter(adapter3);

        vehicle_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                vehicle_type.setText(adapter3.getItem(position).toString());

            }
        });

       /* EditText routeDescription = findViewById(R.id.routeDescription);
        EditText fieldNotes = findViewById(R.id.fieldNotes);
        EditText vehicleCapacity = findViewById(R.id.vehicleCapacity);
        EditText vehicleType = findViewById(R.id.vehicleType);*/

        setTitle("Twiga Tatu");
//        applyFontForToolbarTitle(this);

        serviceIntent = new Intent(this, CaptureService.class);
        startService(serviceIntent);

        bindService(serviceIntent, caputreServiceConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        final ProgressDialog pd = new ProgressDialog(NewActivity.this);
        pd.setMessage("loading routes");
        pd.show();
        pd.setCancelable(true);
        GetData getData = new GetData(NewActivity.this);
        getData.online_stops("routes/", new ServerCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    Log.e("routesss", result);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        stopList.add(jsonArray.getJSONObject(i).getString("short_name"));
                        stopIDs.add(jsonArray.getJSONObject(i).getString("id"));
                        routeIDs.add(jsonArray.getJSONObject(i).getString("route_id"));
                        descList.add(jsonArray.getJSONObject(i).getString("desc"));

                    }



                    Log.e("desc",descList.get(0));

                    final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getBaseContext(),
                            android.R.layout.simple_dropdown_item_1line, descList);
                    description.setAdapter(adapter2);

                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
                            android.R.layout.simple_dropdown_item_1line, stopList);
                    route.setAdapter(adapter);






                    route.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                            route.setText(adapter.getItem(position).toString());

                            stopFrom = adapter.getItem(position).toString();
                            int j = 0;
                            Log.e("stop_from is", adapter.getItem(position).toString());
                            if (stopList.size() > 0)
                                 j= stopList.indexOf(route.getText().toString());

                            if (stopList.contains(route.getText().toString()) && stopList.size() > 0) {
                                Log.e("route_id", stopIDs.get(j));

                            }
                        }
                    });
                    description.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                            description.setText(adapter2.getItem(position).toString());

                            stopFrom = adapter2.getItem(position).toString();

                            Log.e("desc is", adapter2.getItem(position).toString());
                        }
                    });



                    pd.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onSuccess(JSONObject response) {

            }
        });

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {

                    case R.id.ContinueButton:


                        if (Utils.checkPermission(getBaseContext())) {
                            if (!captureService.capturing)
                                createNewCapture();
                            else {
                                updateCapture();
                            }

                            if (!route.getText().toString().isEmpty()) {
                                Intent settingsIntent = new Intent(NewActivity.this, CaptureActivity.class);
                                startActivity(settingsIntent);
                            } else {
//                                Toast.makeText(getBaseContext(), "Please fill Route Name Auto!", Toast.LENGTH_LONG);
                                Utils.showToast("Please fill Route Name Auto!", getBaseContext());
                            }
                        } else {
                            Util.showToast("You cannot collect data without accepting these permissions!", getBaseContext());
                            Utils.showPermissionDialog(NewActivity.this);
                        }


                        break;


                    default:
                        break;
                }
            }
        };

        Button wandButton = (Button) findViewById(R.id.ContinueButton);
        wandButton.setOnClickListener(listener);

    }


    private void createNewCapture() {

        synchronized (this) {

//            EditText routeName = (EditText) findViewById(R.id.routeName);
            EditText routeDescription = (EditText) findViewById(R.id.routeDescription);
            EditText fieldNotes = (EditText) findViewById(R.id.fieldNotes);
            EditText vehicleCapacity = (EditText) findViewById(R.id.vehicleCapacity);
            EditText vehicleType = (EditText) findViewById(R.id.vehicleType);
            EditText surveyor = (EditText) findViewById(R.id.surveyor);

            int i = 0;
            if (stopList.size() > 0)
                i = stopList.indexOf(route.getText().toString());

            if (stopList.contains(route.getText().toString()) && stopList.size() > 0) {
                Utils.setDefaults("route_id", stopIDs.get(i), getBaseContext());
                Utils.setDefaults("route_name", route.getText().toString(), getBaseContext());

            } else {
                Utils.setDefaults("route_id", "", getBaseContext());
                Utils.setDefaults("route_name", "", getBaseContext());
            }

//            Utils.setDefaults("rn", routeName.getText().toString(), getBaseContext());
            Utils.setDefaults("route_description", description.getText().toString(), getBaseContext());
            Utils.setDefaults("field_notes", fieldNotes.getText().toString(), getBaseContext());
            Utils.setDefaults("vehicle_capacity", vehicleCapacity.getText().toString(), getBaseContext());
            Utils.setDefaults("vehicle_type", vehicle_type.getText().toString(), getBaseContext());
            Utils.setDefaults("surveyor", surveyor.getText().toString(), getBaseContext());

            long start = new Date().getTime();
            Utils.setDefaults("start_time", start + "", getBaseContext());
            Log.e("route name", route.getText().toString());


            captureService.newCapture("", "", "", "", "", "");

            if (stopList.contains(route.getText().toString())) {
                captureService.newCapture(route.getText().toString(), description.getText().toString(), fieldNotes.getText().toString(), vehicle_type.getText().toString(), vehicleCapacity.getText().toString(), stopIDs.get(i));
            } else {
                captureService.newCapture(route.getText().toString(), description.getText().toString(), fieldNotes.getText().toString(), vehicle_type.getText().toString(), vehicleCapacity.getText().toString(), "");
            }
        }
    }

    private void updateCapture() {

        synchronized (this) {

//            EditText routeName = (EditText) findViewById(R.id.routeName);
//            AutoCompleteTextView routeName = (AutoCompleteTextView) findViewById(R.id.routeName);
            EditText routeDescription = (EditText) findViewById(R.id.routeDescription);
            EditText fieldNotes = (EditText) findViewById(R.id.fieldNotes);
            EditText vehicleCapacity = (EditText) findViewById(R.id.vehicleCapacity);
            EditText vehicleType = (EditText) findViewById(R.id.vehicleType);
            EditText surveyor = (EditText) findViewById(R.id.surveyor);


            captureService.currentCapture.setRouteName("");
            captureService.currentCapture.description = "";
            captureService.currentCapture.notes = "";
            captureService.currentCapture.vehicleCapacity = "";
            captureService.currentCapture.vehicleType = "";

            int j = 0;
            if (stopList.size() > 0) {
                j = stopList.indexOf(route.getText().toString());
                Utils.setDefaults("route_id", stopIDs.get(j), getBaseContext());
            } else {
                Utils.setDefaults("route_id", "", getBaseContext());
            }

            Utils.setDefaults("route_name", route.getText().toString(), getBaseContext());
            Utils.setDefaults("route_description", description.getText().toString(), getBaseContext());
            Utils.setDefaults("field_notes", fieldNotes.getText().toString(), getBaseContext());
            Utils.setDefaults("vehicle_capacity", vehicleCapacity.getText().toString(), getBaseContext());
            Utils.setDefaults("vehicle_type", vehicle_type.getText().toString(), getBaseContext());
            Utils.setDefaults("surveyor", surveyor.getText().toString(), getBaseContext());

        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // granted

                    Looper looper = null;
                    if (CaptureService.checkPermission(getBaseContext())) {

//                        TODO Commented this location code since we start capturing data elsewhere

                        if (Util.hasInternetConnected(getBaseContext())) {
//                            CaptureService.startLocation();
//                            startCapture();
                        }
                    }
                } else {
                    // not granted
                    Util.showToast("You cannot get geo coordinates quickly without accepting this permission!", getBaseContext());
                    Utils.showPermissionDialog(NewActivity.this);
                }
                return;
            }

        }

    }


}
