package com.digitalmatatus.twigatatu.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import co.ceryle.radiorealbutton.library.RadioRealButton;
import co.ceryle.radiorealbutton.library.RadioRealButtonGroup;

import com.digitalmatatus.twigatatu.R;
import com.digitalmatatus.twigatatu.utils.Utils;

import static com.digitalmatatus.twigatatu.utils.Utils.applyFontForToolbarTitle;
import static com.digitalmatatus.twigatatu.utils.Utils.font;

public class Conditions extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private RadioRealButton button1, button2, button3, button4, button5, button6, button7, button8, button9, button10;
    private RadioRealButtonGroup group1, group2, group3, group4, group5;
    private String peak = "Peak Hour", crowded = "Overcrowded", demand = "High Demand", traffic = "High Traffic", weather = "Good Weather";
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appbar_conditions);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Twiga Tatu");

        applyFontForToolbarTitle(this);
//        getData2();
        editText = findViewById(R.id.inputSearchEditText);
        editText.setTypeface(font(this));

        TextView fare_title = findViewById(R.id.fare_title);
        fare_title.setTypeface(font(this));

        TextView time_title = findViewById(R.id.time_title);
        time_title.setTypeface(font(this));

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        editText.setText(formattedDate);


        group1 = findViewById(R.id.group1);
        group2 = findViewById(R.id.group2);
        group3 = findViewById(R.id.group3);
        group4 = findViewById(R.id.group4);
        group5 = findViewById(R.id.group5);

//TODO Put this in a button to show once a person clicks it
        Button button = findViewById(R.id.pick_time);
        button.setTypeface(font(this));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        Conditions.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );

                dpd.show(getFragmentManager(), "Date Picker");
            }
        });

        group1.setOnPositionChangedListener(new RadioRealButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(RadioRealButton button, int position) {
                peak = button.getText().toString();
                Log.e("peak", button.getText().toString());
            }
        });

        group2.setOnPositionChangedListener(new RadioRealButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(RadioRealButton button, int position) {
                crowded = button.getText().toString();
                Log.e("crowded", button.getText().toString());

            }
        });
        group3.setOnPositionChangedListener(new RadioRealButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(RadioRealButton button, int position) {
                demand = button.getText().toString();
            }
        });
        group4.setOnPositionChangedListener(new RadioRealButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(RadioRealButton button, int position) {
                traffic = button.getText().toString();
            }
        });


        group5.setOnPositionChangedListener(new RadioRealButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(RadioRealButton button, int position) {
                weather = button.getText().toString();
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
                    fares.put("weather", weather);
                    fares.put("traffic_jam", traffic);
                    fares.put("demand", demand);
                    fares.put("crowd", crowded);
                    fares.put("peak", peak);
                    fares.put("user_id", Utils.getDefaults("user_id", getBaseContext()));
                    fares.put("travel_time", editText.getText().toString());
                    Intent intent = new Intent(getBaseContext(), RideConditions.class);
                    intent.putExtra("fare", fares.toString());
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                Intent intent = new Intent(getBaseContext(),RadarChartActivity.class);
//                startActivity(intent);
            }
        });

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Date d = new Date();
        CharSequence s = DateFormat.format("hh:mm:ss", d.getTime());
        editText.setText(year + "-" + monthOfYear + "-" + dayOfMonth + " " + s);

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {

    }

}
/*
"stop_from/"+getIntent().getStringExtra("stops_from")+""+"/stop_to/"+getIntent().getStringExtra("stops_to")+""+"/amount/"+
        getIntent().getStringExtra("amount")+"/route/"+getIntent().getStringExtra("stops_to")+"/weather/"+weather+"/traffic_jam/"+jam+"/demand/"
        demand+"/rush_hour/"+rush+"/peak/"+peak+"/user_id/"+Utils.getDefaults("user_id", getBaseContext())+"/travel_time/"+ editText.getText().toString()

*/
