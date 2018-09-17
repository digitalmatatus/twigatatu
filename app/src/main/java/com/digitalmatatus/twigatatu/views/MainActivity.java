package com.digitalmatatus.twigatatu.views;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.IntentCompat;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Interface.ServerCallback;
import de.hdodenhof.circleimageview.CircleImageView;

import com.digitalmatatus.twigatatu.R;
import com.digitalmatatus.twigatatu.controllers.GetData;
import com.digitalmatatus.twigatatu.model.Fares;
import com.digitalmatatus.twigatatu.utils.Utils;

import static com.digitalmatatus.twigatatu.utils.Utils.applyFontForToolbarTitle;
import static com.digitalmatatus.twigatatu.utils.Utils.getToken;
import static com.digitalmatatus.twigatatu.utils.Utils.jwtAuthHeaders;
import static com.digitalmatatus.twigatatu.utils.Utils.set;
import static com.digitalmatatus.twigatatu.utils.Utils.showToast;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private AutoCompleteTextView stop_from, stop_to;
    private String stopTo = null, stopFrom = null;
    private int amnt = 0;
    private TextView fare;
    protected Typeface mTfRegular;
    protected Typeface mTfLight;

    ArrayList<String> stopIDs = new ArrayList<>();
    ArrayList<String> stopList = new ArrayList<>();
    ArrayList<String> routeIDs = new ArrayList<>();


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Twiga Tatu");
        applyFontForToolbarTitle(this);
        getBudget();


        if (!Utils.checkDefaults("continuation_dc", getBaseContext())) {

            if (Utils.checkDefaults("data_collection", getBaseContext())) {
                if (Utils.getDefaults("data_collection", getBaseContext()).equals("enabled") && getIntent().getStringExtra("continuation") == null) {
//                finishAffinity();
                    Intent intent = new Intent(getBaseContext(), MainActivity2.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            } else {
                Utils.setDefaults("data_collection", "disabled", getBaseContext());

            }
        } else {
            String d_collection = getIntent().getStringExtra("data_collection");
            if (d_collection != null) {
                if (d_collection.equals("enabled")) {
                    Utils.setDefaults("data_collection", "enabled", getBaseContext());
                } else {
                    Utils.setDefaults("data_collection", "disabled", getBaseContext());
                }
            }
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    Intent i = new Intent(MainActivity.this, MyIntro.class);
                    startActivity(i);

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();


        showStops();

//        Setting the custom font for the wordings
        mTfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");

        stop_from = findViewById(R.id.stop_from);
        stop_from.setTypeface(mTfLight);
        stop_to = findViewById(R.id.stop_to);
        stop_to.setTypeface(mTfLight);
        fare = findViewById(R.id.fare);
        fare.setTypeface(mTfLight);
        TextView title = findViewById(R.id.title);
        title.setTypeface(mTfLight);
        TextView src = findViewById(R.id.src);
        src.setTypeface(mTfLight);
        TextView dest = findViewById(R.id.dest);
        dest.setTypeface(mTfLight);

        DiscreteSeekBar discreteSeekBar = (DiscreteSeekBar) findViewById(R.id.discrete3);
        discreteSeekBar.setNumericTransformer(new DiscreteSeekBar.NumericTransformer() {
            @Override
            public int transform(int value) {
                return value * 10;
            }
        });
        discreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int i, boolean b) {
                amnt = i * 10;
                fare.setText("Ksh. " + i * 10 + " /=");
//                ImageView imageView = (ImageView)findViewById(R.id.image);
                CircleImageView imageView = findViewById(R.id.image);

                if (amnt < 70)
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.giraffe_smiling));
                else
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.giraffetongue));

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {

            }
        });


        Button button = findViewById(R.id.submit_fare);
        button.setTypeface(mTfLight);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), Conditions.class);
//                startActivity(intent);
                JSONObject fare = new JSONObject();
                try {
                    fare.put("stop_to", stop_to.getText().toString());
                    fare.put("stop_from", stop_from.getText().toString());
                    fare.put("amount", amnt + "");

                    if (stopList.size() > 0 && stopList.contains(stop_from.getText().toString()) && stopList.contains(stop_to.getText().toString())) {
                        int i = stopList.indexOf(stop_from.getText().toString());
                        fare.put("stop_from_id", stopIDs.get(i));
                        int j = stopList.indexOf(stop_to.getText().toString());
                        fare.put("stop_to_id", stopIDs.get(j));

                    } else {
                        fare.put("stop_from_id", "");
                        fare.put("stop_to_id", "");
                    }


//                    fare.put("route_id", routeIDs.get(i));
                    if (Utils.checkDefaults("route_id", getBaseContext())) {
                        fare.put("route_id", Utils.getDefaults("route_id", getBaseContext()));
                    } else {
                        fare.put("route_id", "");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (amnt > 0 && !stop_from.getText().toString().equals(null) && !stop_to.getText().toString().equals(null)) {

//                    Saving fares offline for visualization
                    Fares fares = new Fares();
                    fares.setFare(amnt);
                    fares.setStopFrom(stop_from.getText().toString());
                    fares.setStopFrom(stop_to.getText().toString());
                    fares.setTimestamp(new Timestamp(System.currentTimeMillis()));
                    fares.save();

                    intent.putExtra("fare", fare.toString());
                    intent.putExtra("token", getIntent().getStringExtra("token"));
                    startActivity(intent);
                } else {
                    showToast("Please fill all the fields", getBaseContext());
                }

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            setDataCollectionMode();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showStops() {
        GetData stops = new GetData(getBaseContext());
        stops.get("stops", new ServerCallback() {
            @Override
            public void onSuccess(String result) {
                Log.e("result", result.toString());
                try {
//                    JSONObject jsonObject = new JSONObject(result);
//                    JSONArray stops = jsonObject.getJSONArray("stops");

                    JSONArray stops = new JSONArray(result);


                    for (int i = 0; i < stops.length(); i++) {
                        stopList.add(stops.getJSONObject(i).getString("name"));
                        stopIDs.add(stops.getJSONObject(i).getString("id"));
                        routeIDs.add(stops.getJSONObject(i).getString("stop_id"));
                    }

                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
                            android.R.layout.simple_dropdown_item_1line, stopList);
                    stop_from.setAdapter(adapter);
                    stop_to.setAdapter(adapter);

                    stop_from.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (in != null) {
                                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                            }
                            stop_from.setText(adapter.getItem(position));

                            stopFrom = adapter.getItem(position);

                            Log.e("stop_from is", adapter.getItem(position));
                        }
                    });

                    stop_to.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (in != null) {
                                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                            }
                            stop_to.setText(adapter.getItem(position));
                            stopTo = adapter.getItem(position);

                            Log.e("stop_to is", adapter.getItem(position));
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onSuccess(JSONObject response) {

            }
        });
    }

    protected void setDataCollectionMode() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        alertDialogBuilder.setTitle("Data Collection Mode");
        alertDialogBuilder.setMessage("Type the password to enable data collection mode");

        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);


        final EditText et = new EditText(MainActivity.this);
        layout.addView(et);


        alertDialogBuilder.setView(layout);

        alertDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (et.getText().toString().equals("dm2018")) {
                    Utils.setDefaults("data_collection", "enabled", getBaseContext());
                    showToast("Data collection mode enabled!", getBaseContext());
                    Intent intent = new Intent(getBaseContext(), MainActivity2.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                }

            }
        });

        alertDialogBuilder.setNegativeButton("Close", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();


            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();

    }

    private void getBudget() {
        getToken(getBaseContext(), new ServerCallback() {
            @Override
            public void onSuccess(String result) {
//                    TODO remove this log - insecure
                Log.e("jwt token", result);
                final JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(result);
                    String token = jsonObject.getString("token");

                    GetData budget = new GetData(getBaseContext());

                    budget.online_data("fares/budget", null, jwtAuthHeaders(token), new ServerCallback() {
                        @Override
                        public void onSuccess(String result) {
                            Log.e("result string", result);
                            Calendar cal = Calendar.getInstance();
                            int cur_week = cal.get(Calendar.WEEK_OF_YEAR);
                            Log.e(" cur week is ", cur_week + "");

                            try {


                                JSONObject jsonObject = new JSONObject(result);
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                Log.e("Expenditure Array", jsonArray.toString());

//                    TODO .. calculate if we have data for last week the assign the boolean last week
                                boolean noData = false;


                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    Log.e("week SERVER",jsonObject1.getInt("week")+"");
                                    int week = 2;

                                    String[] splitTime = jsonObject1.getString("travel_time").split(":");
                                    String timeHour = splitTime[0];

                                    int hours = Integer.parseInt(timeHour);

                                    Log.e("casted hour", hours + "");

                                    if (week == jsonObject1.getInt("week"))
                                        week = 1;
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.e("result,", response.toString());

                        }
                    });


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
    }


}