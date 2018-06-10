package com.digitalmatatus.twigatatu.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import Interface.ServerCallback;

import com.digitalmatatus.twigatatu.R;
import com.digitalmatatus.twigatatu.controllers.GetData;
import com.digitalmatatus.twigatatu.utils.Util;
import com.digitalmatatus.twigatatu.utils.Utils;

import static com.digitalmatatus.twigatatu.utils.Utils.applyFontForToolbarTitle;

public class NewActivity extends AppCompatActivity {

    private static Intent serviceIntent;
    private CaptureService captureService;
    private AutoCompleteTextView route, description, vehicle_type;
    EditText notes, capacity, surveyor;
    ArrayList<String> stopList = new ArrayList<>();
    ArrayList<String> stopName = new ArrayList<>();
    ArrayList<String> descList = new ArrayList<>();
    ArrayList<String> stopIDs = new ArrayList<>();
    ArrayList<String> routeIDs = new ArrayList<>();
    private String stopTo = null, stopFrom = null, vehicle_full = "No", new_route = "false", direction = "outbound";
    private boolean mIsBound;
    private static final int REQUEST_FINE_LOCATION = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    protected Typeface mTfLight;


    private final ServiceConnection caputreServiceConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            captureService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            captureService = ((CaptureService.CaptureServiceBinder) service).getService();
            //com.digitalmatatus.twigatatu.views.CaptureService.setServiceClient(CaptureActivity.this);

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Twiga Tatu");
        applyFontForToolbarTitle(this);
        mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");


        TextView textView = findViewById(R.id.text1);
        textView.setTypeface(mTfLight);

        route = findViewById(R.id.routeID);
        route.setTypeface(mTfLight);

        description = findViewById(R.id.routeDescription);
        description.setTypeface(mTfLight);

        vehicle_type = findViewById(R.id.vehicleType);
        vehicle_type.setTypeface(mTfLight);

        notes = findViewById(R.id.fieldNotes);
        notes.setTypeface(mTfLight);

        capacity = findViewById(R.id.vehicleCapacity);
        capacity.setTypeface(mTfLight);

        surveyor = findViewById(R.id.surveyor);
        surveyor.setTypeface(mTfLight);

        ArrayList<String> v_types = new ArrayList<>();
        v_types.add("Bus "); //- 64 seater
        v_types.add("Minibus");// - 45 Seater
        v_types.add("Van");// - 14 Seater

        final ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_dropdown_item_1line, v_types);
        vehicle_type.setAdapter(adapter3);

        vehicle_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                vehicle_type.setText(adapter3.getItem(position).toString());

            }
        });

        Spinner dropdown = findViewById(R.id.vehicleFull);
        String[] items = new String[]{"Yes", "No"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter2);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vehicle_full = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner newRoute = findViewById(R.id.newRoute);
        String[] new_route_items = new String[]{"No", "Yes"};
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new_route_items);
        newRoute.setAdapter(adapter4);

        newRoute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals("Yes")) {
                    new_route = "true";

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner tripDirection = findViewById(R.id.tripDirection);
        String[] direction_items = new String[]{"outbound", "inbound"};
        ArrayAdapter<String> adapter5 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, direction_items);
        tripDirection.setAdapter(adapter5);

        tripDirection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                direction = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        serviceIntent = new Intent(this, CaptureService.class);
        startService(serviceIntent);

        bindService(serviceIntent, caputreServiceConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        final ProgressDialog pd = new ProgressDialog(NewActivity.this);
        pd.setMessage("loading routes");
        pd.show();
        pd.setCancelable(false);
        GetData getData = new GetData(NewActivity.this);
        getData.online_stops("routes/", new ServerCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    Log.e("routesss", result);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        stopList.add(jsonArray.getJSONObject(i).getString("short_name") + " - " + jsonArray.getJSONObject(i).getString("desc"));
                        stopName.add(jsonArray.getJSONObject(i).getString("short_name"));
                        stopIDs.add(jsonArray.getJSONObject(i).getString("id"));
                        routeIDs.add(jsonArray.getJSONObject(i).getString("route_id"));
                        descList.add(jsonArray.getJSONObject(i).getString("desc"));

                    }


                    Log.e("desc", descList.get(0));

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
                                j = stopList.indexOf(route.getText().toString());

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

                            if (validate()) {
                                if (!captureService.capturing)
                                    createNewCapture();
                                else {
                                    updateCapture();
                                }

                                if (!route.getText().toString().isEmpty() && stopList.contains(route.getText().toString()) && stopList.size() > 0) {
                                    Intent settingsIntent = new Intent(NewActivity.this, CaptureActivity.class);
                                    startActivity(settingsIntent);
                                } else if(new_route.equals("true")){
                                    Intent settingsIntent = new Intent(NewActivity.this, CaptureActivity.class);
                                    startActivity(settingsIntent);
                                }else {
//                                Toast.makeText(getBaseContext(), "Please fill Route Name Auto!", Toast.LENGTH_LONG);
                                    Utils.showToast("Please choose a valid route number!", getBaseContext());
                                }
                            } else {
                                Utils.showToast("Please fill vehicle capacity!", getBaseContext());
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

        Button wandButton = findViewById(R.id.ContinueButton);
        wandButton.setTypeface(mTfLight);
        wandButton.setOnClickListener(listener);

    }


    private void createNewCapture() {

        synchronized (this) {

//            EditText routeName = findViewById(R.id.routeName);
            EditText fieldNotes = findViewById(R.id.fieldNotes);
            EditText vehicleCapacity = findViewById(R.id.vehicleCapacity);
            EditText surveyor = findViewById(R.id.surveyor);


            int i = 0;
            if (stopList.size() > 0)
                i = stopList.indexOf(route.getText().toString());

            if (new_route.equals("false") && stopList.contains(route.getText().toString()) && stopList.size() > 0) {
                Utils.setDefaults("route_id", stopIDs.get(i), getBaseContext());
                Utils.setDefaults("route_name", stopName.get(i), getBaseContext());

            } else if (new_route.equals("false") && stopList.size() > 0) {
                Utils.setDefaults("route_name", stopName.get(i), getBaseContext());
                Utils.setDefaults("route_id", "", getBaseContext());
                Utils.showToast("Please go back and choose a valid route number!", getBaseContext());


            } else {
                Utils.setDefaults("route_name", route.getText().toString(), getBaseContext());
                Utils.setDefaults("route_id", "", getBaseContext());

            }


//            Utils.setDefaults("rn", routeName.getText().toString(), getBaseContext());
            Utils.setDefaults("route_description", description.getText().toString(), getBaseContext());
            Utils.setDefaults("field_notes", fieldNotes.getText().toString(), getBaseContext());
            Utils.setDefaults("vehicle_capacity", vehicleCapacity.getText().toString(), getBaseContext());
            Utils.setDefaults("vehicle_type", vehicle_type.getText().toString(), getBaseContext());
            Utils.setDefaults("surveyor", surveyor.getText().toString(), getBaseContext());
            Utils.setDefaults("vehicle_full", vehicle_full, getBaseContext());
            Utils.setDefaults("new_route", new_route, getBaseContext());
            Utils.setDefaults("direction", direction, getBaseContext());


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

//            EditText routeName =  findViewById(R.id.routeName);
//            AutoCompleteTextView routeName = (AutoCompleteTextView) findViewById(R.id.routeName);
            EditText routeDescription = findViewById(R.id.routeDescription);
            EditText fieldNotes = findViewById(R.id.fieldNotes);
            EditText vehicleCapacity = findViewById(R.id.vehicleCapacity);
            EditText vehicleType = findViewById(R.id.vehicleType);
            EditText surveyor = findViewById(R.id.surveyor);


            captureService.currentCapture.setRouteName(route.getText().toString());
            captureService.currentCapture.description = routeDescription.getText().toString();
            captureService.currentCapture.notes = fieldNotes.getText().toString();
            captureService.currentCapture.vehicleCapacity = vehicleCapacity.getText().toString();
            captureService.currentCapture.vehicleType = vehicleType.getText().toString();

            int j = 0;


            if (new_route.equals("false") && stopList.contains(route.getText().toString()) && stopList.size() > 0) {
                Utils.setDefaults("route_id", stopIDs.get(j), getBaseContext());
                Utils.setDefaults("route_name", stopName.get(j), getBaseContext());

            } else if (new_route.equals("false") && stopList.size() > 0) {
                Utils.setDefaults("route_name", stopName.get(j), getBaseContext());
                Utils.setDefaults("route_id", "", getBaseContext());
                Utils.showToast("Please go back and choose a valid route number!", getBaseContext());


            } else {
                Utils.setDefaults("route_name", route.getText().toString(), getBaseContext());
                Utils.setDefaults("route_id", "", getBaseContext());

            }

            Utils.setDefaults("route_name", route.getText().toString(), getBaseContext());
            Utils.setDefaults("route_description", description.getText().toString(), getBaseContext());
            Utils.setDefaults("field_notes", fieldNotes.getText().toString(), getBaseContext());
            Utils.setDefaults("vehicle_capacity", vehicleCapacity.getText().toString(), getBaseContext());
            Utils.setDefaults("vehicle_type", vehicle_type.getText().toString(), getBaseContext());
            Utils.setDefaults("surveyor", surveyor.getText().toString(), getBaseContext());
            Utils.setDefaults("vehicle_full", vehicle_full, getBaseContext());
            Utils.setDefaults("new_route", new_route, getBaseContext());
            Utils.setDefaults("direction", direction, getBaseContext());

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

    public boolean validate() {
        boolean valid = true;

        String v_capacity = capacity.getText().toString();

        if (v_capacity.isEmpty()) {
            capacity.setError("Please choose/type a valid vehicle capacity");
            valid = false;
        } else {
            capacity.setError(null);
        }

        return valid;
    }


}
