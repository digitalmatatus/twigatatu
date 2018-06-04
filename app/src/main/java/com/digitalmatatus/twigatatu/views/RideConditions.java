package com.digitalmatatus.twigatatu.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import Interface.ServerCallback;
import co.ceryle.radiorealbutton.library.RadioRealButton;
import co.ceryle.radiorealbutton.library.RadioRealButtonGroup;

import com.digitalmatatus.twigatatu.R;
import com.digitalmatatus.twigatatu.controllers.PostData;
import com.digitalmatatus.twigatatu.utils.Utils;

import static com.digitalmatatus.twigatatu.utils.Utils.applyFontForToolbarTitle;
import static com.digitalmatatus.twigatatu.utils.Utils.font;

public class RideConditions extends AppCompatActivity {

    private RadioRealButton button1, button2, button3, button4, button5, button6, button7, button8, button9, button10;
    private RadioRealButtonGroup group1, group2, group3, group4, group5;
    private String internet = "Internet Connection", music = "Music", drive_safety = "Driving safe", safety = "Personally Safe", air = "Good Air-Quality";
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appbar_ride_conditions);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Twiga Tatu");

        applyFontForToolbarTitle(this);
//        getData2();
//        editText = findViewById(R.id.inputSearchEditText);
//        editText.setTypeface(font(this));

        TextView fare_title = findViewById(R.id.fare_title);
        fare_title.setTypeface(font(this));


        group1 = findViewById(R.id.group1);
        group2 = findViewById(R.id.group2);
        group3 = findViewById(R.id.group3);
        group4 = findViewById(R.id.group4);
        group5 = findViewById(R.id.group5);


        getIntent().getStringExtra("stop_from");
//TODO Put this in a button to show once a person clicks it


        group1.setOnPositionChangedListener(new RadioRealButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(RadioRealButton button, int position) {
//                Utils.showToast(button.getText().toString(), getBaseContext());
                air = button.getText().toString();
                Log.e("internet", button.getText().toString());
//                updateText(position, button1);
            }
        });

        group2.setOnPositionChangedListener(new RadioRealButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(RadioRealButton button, int position) {
//                Utils.showToast(button.getText().toString(), getBaseContext());
                music = button.getText().toString();
                Log.e("music", button.getText().toString());

//                updateText(position, button2);
            }
        });
        group3.setOnPositionChangedListener(new RadioRealButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(RadioRealButton button, int position) {
//                Utils.showToast(button.getText().toString(), getBaseContext());
                drive_safety = button.getText().toString();
//                updateText(position, button1);
            }
        });
        group4.setOnPositionChangedListener(new RadioRealButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(RadioRealButton button, int position) {
//                Utils.showToast(button.getText().toString(), getBaseContext());
                safety = button.getText().toString();
//                updateText(position, button1);
            }
        });


        group5.setOnPositionChangedListener(new RadioRealButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(RadioRealButton button, int position) {
//                Utils.showToast(button.getText().toString(), getBaseContext());
                internet = button.getText().toString();
//                updateText(position, button1);
            }
        });



       /* button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = group1.getPosition();

                position = ++position % group1.getNumberOfButtons();
                group1.setPosition(position);
            }
        });*/

        Button bt = findViewById(R.id.submit_conditions);
        bt.setTypeface(font(this));
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s_fare = getIntent().getStringExtra("fare");
                JSONObject fares;
                try {
                    fares = new JSONObject(s_fare);
                    fares.put("air_quality", air);
                    fares.put("safety", safety);
                    fares.put("drive_safety", drive_safety);
                    fares.put("music", music);
                    fares.put("internet", internet);
                    fares.put("user_id", Utils.getDefaults("user_id", getBaseContext()));

//                   TODO Post fare data here to server
                    Log.e("fares", fares.toString());

                    PostData post = new PostData(getBaseContext());
                    post.post("fare/", null, fares, new ServerCallback() {
                        @Override
                        public void onSuccess(String result) {

                        }

                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.e("response", response.toString());

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }


//                showProceed("Are you a surveyor collecting route information? Click yes and turn on your location/GPS", RideConditions.this, MainActivity2.class, RadarChartActivity.class);

                if (Utils.checkDefaults("data_collection", getBaseContext())) {
                    if (Utils.getDefaults("data_collection", getBaseContext()).equals("enabled")) {
//                finishAffinity();
                        Intent intent = new Intent(getBaseContext(), MainActivity2.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getBaseContext(), RadarChartActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(getBaseContext(), RadarChartActivity.class);
                    startActivity(intent);
                }


            }
        });

    }

    private void updateText(int position, Button button) {
        button.setText("Position: " + position);
    }


//    private void postFare() {
//
//        String tag_string_req = "req_login";
//       /* Log.e("url",Utils.baseURL() + "twiga/fares/addFare/"+"stop_from/"+getIntent().getStringExtra("stops_from")+"/stop_to/"+getIntent().getStringExtra("stops_to")+"/amount/"+
//                getIntent().getStringExtra("amount")+"/route/"+getIntent().getStringExtra("stops_to")+"/crowd/"+crowd+"/safety_jam/"+safety+"/drive_safety/"+
//                drive_safety+"/music_hour/"+music+"/internet/"+internet+"/user_id/"+Utils.getDefaults("user_id", getBaseContext())+"/travel_time/"+ editText.getText().toString());
//       */ StringRequest strReq = new StringRequest(Request.Method.POST,
//                Utils.baseURL() + "twiga/fares/addFare/", new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                Log.e("All Data", "response from the server is: " + response.toString());
////                hideDialog();
//
////                Log.e("Url is",  Utils.getDefaults("url",getBaseContext()) + "twiga/auth/login?" + "username=" + username + "&password=" + password);
//
//                try {
//                    JSONObject jObj = new JSONObject(response);
//
//                    Intent intent = new Intent(getBaseContext(), RadarChartActivity.class);
//                    startActivity(intent);
//                    /*String success = jObj.getString("success");
//                    String session = "";
//                    if (success.equals("true")) {
//                        session = jObj.getString("user_id");
//
//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        intent.putExtra("user_id", session);
//                        startActivity(intent);
//                    } else {
//                        Utils.showToast("Wrong credentials, please try again", getBaseContext());
//                    }
//*/
//
////                    String success = jObj.getString("success");
//
//
//                } catch (JSONException e) {
//                    Utils.showToast("Wrong credentials, please try again", getBaseContext());
//
//                    // JSON error
////                   loginUser(username,password);
//                }
//
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("Getting data error", "Error: " + error.getMessage());
////                Log.e("Url is", Utils.baseURL() + "cargo_handling/api/login/?" + "username=" + username + "&password=" + password);
//
////                Toast.makeText(getApplicationContext(),
////                        "Check your credentials or internet connectivity!", Toast.LENGTH_LONG).show();
////                loginUser(username,password);
////                hideDialog();
//            }
//        }) {
//           /* @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                setRetryPolicy(new DefaultRetryPolicy(5* DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
//                setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
//                headers.put("Content-Type", "application/json; charset=utf-8");
//                String creds = String.format("%s:%s",username,password);
//                Log.e("pass",password);
//                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
//                headers.put("Authorization", auth);
//                return headers;
//            }*/
//
//            @Override
//            protected Map<String, String> getParams() {
//                // Posting params to register url
//                Map<String, String> params = new HashMap<String, String>();
//
////                try {
//
//
//                /*params.put("stop_from", "1");
//                params.put("stop_to", "2");
//                params.put("amount", "dssd");
//                params.put("route", " 3");
//                params.put("crowd", "fddf");
//                params.put("safety_jam", "dsddf");
//                params.put("drive_safety", "dffd");
//                params.put("music_hour", "fdvs");
//                params.put("internet", "cvccvc");
//                params.put("user_id","11");
//                params.put("travel_time",editText.getText().toString());*/
//
//                    params.put("stop_from", getIntent().getStringExtra("stops_from_id")+"");
//                    params.put("stop_to", getIntent().getStringExtra("stops_to_id")+"");
//                    params.put("amount", getIntent().getStringExtra("amount")+"");
//                    params.put("route", getIntent().getStringExtra("route_id")+"");
//                    params.put("crowd", crowd);
//                    params.put("safety_jam", safety);
//                    params.put("drive_safety", drive_safety);
//                    params.put("music_hour", music);
//                    params.put("internet", internet);
//                    params.put("user_id", Utils.getDefaults("user_id", getBaseContext()));
//                    params.put("travel_time", editText.getText().toString());
//
//                Log.e("data is", getIntent().getStringExtra("stops_from_id") + ",_ " +getIntent().getStringExtra("route_id") + ", " +
//                        getIntent().getStringExtra("stops_to_id") + ", " + getIntent().getStringExtra("amount") + ", " +
//                        crowd + ", " + safety
//                        + ", " + drive_safety + ", " + music + ", " + internet + ", " + editText.getText().toString() + ", " + Utils.getDefaults("user_id", getBaseContext()));
//
//                /*} catch (NullPointerException e) {
//                    Log.e("error", e.toString());
//                }
//                Log.e("data is", getIntent().getStringExtra("stops_from") + ", " +
//                        getIntent().getStringExtra("stops_to") + ", " + getIntent().getStringExtra("amount") + ", " +
//                        crowd + ", " + safety
//                        + ", " + drive_safety + ", " + music + ", " + internet + ", " + editText.getText().toString() + ", " + Utils.getDefaults("user_id", getBaseContext()));
//
//*/
//                return params;
//            }
//
//        };
//
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
//    }


}
/*
"stop_from/"+getIntent().getStringExtra("stops_from")+""+"/stop_to/"+getIntent().getStringExtra("stops_to")+""+"/amount/"+
        getIntent().getStringExtra("amount")+"/route/"+getIntent().getStringExtra("stops_to")+"/crowd/"+crowd+"/safety_jam/"+jam+"/drive_safety/"
        drive_safety+"/music_hour/"+music+"/internet/"+internet+"/user_id/"+Utils.getDefaults("user_id", getBaseContext())+"/travel_time/"+ editText.getText().toString()

*/

