package twigatatu.digitalmatatus.com.newtwigatatu;

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
import twigatatu.digitalmatatus.com.newtwigatatu.controllers.GetData;
import twigatatu.digitalmatatus.com.newtwigatatu.utils.Utils;

public class NewActivity extends Activity {

    private static Intent serviceIntent;
    private twigatatu.digitalmatatus.com.newtwigatatu.CaptureService captureService;
    private AutoCompleteTextView route;
    ArrayList<String> stopList = new ArrayList<>();
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
            captureService = ((twigatatu.digitalmatatus.com.newtwigatatu.CaptureService.CaptureServiceBinder) service).getService();
            //twigatatu.digitalmatatus.com.newtwigatatu.CaptureService.setServiceClient(CaptureActivity.this);

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        route = findViewById(R.id.routeID);

        setTitle("Twiga Tatu");
//        applyFontForToolbarTitle(this);

        serviceIntent = new Intent(this, twigatatu.digitalmatatus.com.newtwigatatu.CaptureService.class);
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

                        stopList.add(jsonArray.getJSONObject(i).getString("short_name"));
                        stopIDs.add(jsonArray.getJSONObject(i).getString("id"));
                        routeIDs.add(jsonArray.getJSONObject(i).getString("route_id"));
                    }

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

                            Log.e("stop_from is", adapter.getItem(position).toString());
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
                                Intent settingsIntent = new Intent(NewActivity.this, twigatatu.digitalmatatus.com.newtwigatatu.CaptureActivity.class);
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

            EditText routeName = (EditText) findViewById(R.id.routeName);
            EditText routeDescription = (EditText) findViewById(R.id.routeDescription);
            EditText fieldNotes = (EditText) findViewById(R.id.fieldNotes);
            EditText vehicleCapacity = (EditText) findViewById(R.id.vehicleCapacity);
            EditText vehicleType = (EditText) findViewById(R.id.vehicleType);
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

            Utils.setDefaults("rn", routeName.getText().toString(), getBaseContext());
            Utils.setDefaults("route_description", routeDescription.getText().toString(), getBaseContext());
            Utils.setDefaults("field_notes", fieldNotes.getText().toString(), getBaseContext());
            Utils.setDefaults("vehicle_capacity", vehicleCapacity.getText().toString(), getBaseContext());
            Utils.setDefaults("vehicle_type", vehicleType.getText().toString(), getBaseContext());
            long start = new Date().getTime();
            Utils.setDefaults("start_time", start + "", getBaseContext());
            Log.e("route name", route.getText().toString());


            captureService.newCapture("", "", "", "", "", "");

            if (stopList.contains(route.getText().toString())) {
                captureService.newCapture(route.getText().toString(), routeDescription.getText().toString(), fieldNotes.getText().toString(), vehicleType.getText().toString(), vehicleCapacity.getText().toString(), stopIDs.get(i));
            } else {
                captureService.newCapture(route.getText().toString(), routeDescription.getText().toString(), fieldNotes.getText().toString(), vehicleType.getText().toString(), vehicleCapacity.getText().toString(), "");
            }
        }
    }

    private void updateCapture() {

        synchronized (this) {

//            EditText routeName = (EditText) findViewById(R.id.routeName);
            AutoCompleteTextView routeName = (AutoCompleteTextView) findViewById(R.id.routeName);
            EditText routeDescription = (EditText) findViewById(R.id.routeDescription);
            EditText fieldNotes = (EditText) findViewById(R.id.fieldNotes);
            EditText vehicleCapacity = (EditText) findViewById(R.id.vehicleCapacity);
            EditText vehicleType = (EditText) findViewById(R.id.vehicleType);


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
            Utils.setDefaults("rn", routeName.getText().toString(), getBaseContext());
            Utils.setDefaults("route_description", routeDescription.getText().toString(), getBaseContext());
            Utils.setDefaults("field_notes", fieldNotes.getText().toString(), getBaseContext());
            Utils.setDefaults("vehicle_capacity", vehicleCapacity.getText().toString(), getBaseContext());
            Utils.setDefaults("vehicle_type", vehicleType.getText().toString(), getBaseContext());

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
