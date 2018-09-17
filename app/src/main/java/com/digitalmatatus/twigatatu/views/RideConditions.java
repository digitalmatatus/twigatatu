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
import com.digitalmatatus.twigatatu.controllers.GetData;
import com.digitalmatatus.twigatatu.controllers.PostData;
import com.digitalmatatus.twigatatu.utils.Utils;

import static com.digitalmatatus.twigatatu.utils.Utils.applyFontForToolbarTitle;
import static com.digitalmatatus.twigatatu.utils.Utils.font;
import static com.digitalmatatus.twigatatu.utils.Utils.getToken;
import static com.digitalmatatus.twigatatu.utils.Utils.jwtAuthHeaders;
import static com.digitalmatatus.twigatatu.utils.Utils.showToast;

public class RideConditions extends AppCompatActivity {

    private RadioRealButton button1, button2, button3, button4, button5, button6, button7, button8, button9, button10;
    private RadioRealButtonGroup group1, group2, group3, group4, group5;
    private String internet = "Internet Connection", music = "Music", drive_safety = "Driving safe", safety = "Personally Safe", air = "Good Air-Quality", token;
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

        token = getIntent().getStringExtra("token");
        TextView fare_title = findViewById(R.id.fare_title);
        fare_title.setTypeface(font(this));

//        Getting token incase it expired
        getToken(getBaseContext(), new ServerCallback() {
            @Override
            public void onSuccess(String result) {
//                    TODO remove this log - insecure
                Log.e("jwt token", result);
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(result);
                    token = jsonObject.getString("token");


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("getting token error", "token error");

                    showToast("Please enter the correct credentials", getBaseContext());

                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.putExtra("error", e.toString());
                    startActivity(intent);
                }

            }

            @Override
            public void onSuccess(JSONObject response) {

            }
        });


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
                    Log.e("fares", fares.toString());//ma3tycoon/fares/add/


                    PostData postData = new PostData(getBaseContext());
                    postData.post2("fares/add/", null, fares, jwtAuthHeaders(token), new ServerCallback() {
                        @Override
                        public void onSuccess(String result) {

                        }

                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.e("response", response.toString());
                        }
                    });

                    /*PostData post = new PostData(getBaseContext());
                    post.post("fare/", null, fares, new ServerCallback() {
                        @Override
                        public void onSuccess(String result) {

                        }

                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.e("response", response.toString());

                        }
                    });*/

                } catch (JSONException e) {
                    e.printStackTrace();
                }


//                showProceed("Are you a surveyor collecting route information? Click yes and turn on your location/GPS", RideConditions.this, MainActivity2.class, RadarChartActivity.class);

                if (Utils.checkDefaults("data_collection", getBaseContext())) {
                    if (Utils.getDefaults("data_collection", getBaseContext()).equals("enabled")) {
//                finishAffinity();
                        Utils.showToast("You have successfully finished collecting your route!", getBaseContext());
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


}