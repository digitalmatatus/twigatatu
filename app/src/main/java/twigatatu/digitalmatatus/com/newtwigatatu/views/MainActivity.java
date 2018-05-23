package twigatatu.digitalmatatus.com.newtwigatatu.views;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Interface.ServerCallback;
import de.hdodenhof.circleimageview.CircleImageView;
import twigatatu.digitalmatatus.com.newtwigatatu.R;
import twigatatu.digitalmatatus.com.newtwigatatu.controllers.GetData;
import twigatatu.digitalmatatus.com.newtwigatatu.utils.Utils;

import static twigatatu.digitalmatatus.com.newtwigatatu.utils.Utils.applyFontForToolbarTitle;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Twiga Tatu");
        applyFontForToolbarTitle(this);


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

//                    int i = stopList.indexOf(stop_from.getText().toString());
//                    fare.put("stops_from_id", stopIDs.get(i));
                    fare.put("stop_from_id", "");
//                    fare.put("route_id", routeIDs.get(i));
                    fare.put("route_id", "");
//                    int j = stopList.indexOf(stop_to.getText().toString());
//                    fare.put("stops_to_id", stopIDs.get(j));
                    fare.put("stop_to_id", "");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (amnt > 0 && ! stop_from.getText().toString().equals(null) && ! stop_from.getText().toString().equals(null)) {
                    intent.putExtra("fare",fare.toString());
                    startActivity(intent);
                } else {
                    Utils.showToast("Please fill all the fields", getBaseContext());
                }
                /*Intent intent = new Intent(getBaseContext(), Conditions.class);
                intent.putExtra("stops_to",  stop_to.getText().toString());
                intent.putExtra("stops_from",  stop_from.getText().toString());
                intent.putExtra("amount", amnt + "");

                int i=stopList.indexOf(stop_from.getText().toString());
                intent.putExtra("stops_from_id",stopIDs.get(i));
                intent.putExtra("route_id",routeIDs.get(i));
                int j=stopList.indexOf(stop_to.getText().toString());
                intent.putExtra("stops_to_id",stopIDs.get(j));


                Log.e("amount is", amnt + " ," + stop_to.getText().toString() + " ," + stop_from.getText().toString());

                if (amnt > 0 && ! stop_from.getText().toString().equals(null) && ! stop_from.getText().toString().equals(null)) {
                    startActivity(intent);
                } else {
                    Utils.showToast("Please fill all the fields", getBaseContext());
                }*/

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    private void showStops() {
        GetData stops = new GetData(getBaseContext());
        stops.online_stops("routes", new ServerCallback() {
            @Override
            public void onSuccess(String result) {
                Log.e("result", result.toString());
                /*try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray stops = jsonObject.getJSONArray("stops");


                    for (int i = 0; i < stops.length(); i++) {
                        stopList.add(stops.getJSONObject(i).getString("stop_name"));
                        stopIDs.add(stops.getJSONObject(i).getString("id"));
                        routeIDs.add(stops.getJSONObject(i).getString("route_id"));
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
*/
            }

            @Override
            public void onSuccess(JSONObject response) {

            }
        });
    }
}
