
package com.digitalmatatus.twigatatu.views;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.digitalmatatus.twigatatu.controllers.GetData;
import com.digitalmatatus.twigatatu.model.Fares;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.digitalmatatus.twigatatu.R;
import com.digitalmatatus.twigatatu.model.Post;
import com.digitalmatatus.twigatatu.utils.AppController;
import com.digitalmatatus.twigatatu.utils.Utils;

import Interface.ServerCallback;

import static com.digitalmatatus.twigatatu.utils.Utils.applyFontForToolbarTitle;
import static com.digitalmatatus.twigatatu.utils.Utils.checkDefaults;
import static com.digitalmatatus.twigatatu.utils.Utils.getToken;
import static com.digitalmatatus.twigatatu.utils.Utils.jwtAuthHeaders;
import static com.digitalmatatus.twigatatu.utils.Utils.set;
import static com.digitalmatatus.twigatatu.utils.Utils.showToast;

public class RadarChartActivity extends Base {

    private RadarChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.appbar_radar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Twiga Tatu");
        applyFontForToolbarTitle(this);
        TextView tv = findViewById(R.id.textView);
        tv.setTypeface(mTfLight);
        tv.setTextColor(Color.BLACK);

//        tv.setTextColor(Color.WHITE);
//        tv.setBackgroundColor(Color.rgb(00, 80, 00));
//        60, 65, 82
        mChart = findViewById(R.id.chart1);

        mChart.getDescription().setEnabled(false);
        mChart.setWebLineWidth(1f);
//        mChart.setWebColor(Color.LTGRAY);
        mChart.setWebColor(Color.BLACK);

        mChart.setWebLineWidthInner(1f);
        mChart.setWebColorInner(Color.BLACK);
        mChart.setWebAlpha(100);


        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MarkerView mv = new RadarMarkerView(this, R.layout.radar_markerview);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart


//        setData(); //Uncomment this to set up random data
        getBudget();

      Utils.showToast("  Click on a point to view fare amount   ", this);
        mChart.animateXY(
                1400, 1400,
                Easing.EasingOption.EaseInOutQuad,
                Easing.EasingOption.EaseInOutQuad);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private String[] mTimes = new String[]{"6am", "9am", "12pm", "3pm", "6pm"};

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mTimes[(int) value % mTimes.length];
            }
        });
        xAxis.setTextColor(Color.BLACK);

        YAxis yAxis = mChart.getYAxis();
        yAxis.setTypeface(mTfLight);
        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(80f);
        yAxis.setDrawLabels(false);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setTypeface(mTfLight);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
        l.setTextColor(Color.BLACK);

        Button button = findViewById(R.id.done);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void setData() {

        float mult = 80;
        float min = 20;
        int cnt = 5;

        ArrayList<RadarEntry> entries1 = new ArrayList<RadarEntry>();
        ArrayList<RadarEntry> entries2 = new ArrayList<RadarEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < cnt; i++) {
            float val1 = (float) (Math.random() * mult) + min;
            entries1.add(new RadarEntry(val1));

            float val2 = (float) (Math.random() * mult) + min;
            entries2.add(new RadarEntry(val2));
        }

        RadarDataSet set1 = new RadarDataSet(entries1, "Last Week");
//        set1.setColor(Color.rgb(103, 110, 129));
//        set1.setColor(Color.rgb( 255	,166,	196));
//        set1.setFillColor(Color.rgb(255, 166, 196));
//        set1.setColor(Color.rgb(121, 162, 175));
//        set1.setFillColor(Color.rgb(121, 162, 175));
        set1.setColor(Color.rgb(141, 204, 144));
        set1.setFillColor(Color.rgb(141, 204, 144));


        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(false);

        RadarDataSet set2 = new RadarDataSet(entries2, "This Week");
        set2.setColor(Color.rgb(255, 166, 196));
        set2.setFillColor(Color.rgb(255, 166, 196));
        set2.setDrawFilled(true);
        set2.setFillAlpha(180);
        set2.setLineWidth(2f);
        set2.setDrawHighlightCircleEnabled(true);
        set2.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        sets.add(set1);
        sets.add(set2);

        RadarData data = new RadarData(sets);
        data.setValueTypeface(mTfLight);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.BLACK);

        mChart.setData(data);
        mChart.invalidate();
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
                            Log.e("cur week is ", cur_week + "");
                            //                            {"result":[{"amount":"50","travel_time":"18:00:00","week":38}],"success":true}

                            try {

                                ArrayList<Double> doublers = new ArrayList<>();

                                ArrayList<RadarEntry> entries1 = new ArrayList<RadarEntry>();
                                ArrayList<RadarEntry> entries2 = new ArrayList<RadarEntry>();

                                int six = 0, nine = 0, twelve = 0, fifteen = 0, eighteen = 0;
                                float sixA = 0, nineA = 0, twelveA = 0, fifteenA = 0, eighteenA = 0;
                                float fin6 = 0, fin9 = 0, fin12 = 0, fin15 = 0, fin18 = 0;

                                int sixb = 0, nineb = 0, twelveb = 0, fifteenb = 0, eighteenb = 0;
                                float sixB = 0, nineB = 0, twelveB = 0, fifteenB = 0, eighteenB = 0;
                                float fin6B = 0, fin9B = 0, fin12B = 0, fin15B = 0, fin18B = 0;


                                JSONObject jsonObject = new JSONObject(result);
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                Log.e("Fare's Array", jsonArray.toString());

//                    TODO .. calculate if we have data for last week the assign the boolean last week
                                boolean noData = false;


                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    int week_number = 2;

                                    if (cur_week == jsonObject1.getInt("week"))
                                        week_number = 1;
                                    String week = week_number + "";


                                    String[] splitTime = jsonObject1.getString("travel_time").split(":");
                                    String timeHour = splitTime[0];

                                    int hour = Integer.parseInt(timeHour);

                                    Log.e("hour", hour + " and week is " + week);


//                        Getting average of each time
                                    if (week.equals("1")) {

                                        if (hour == 6) {
                                            six += 1;
                                            sixA += Float.parseFloat(jsonObject1.getString("amount"));
                                            fin6 = sixA / six;

                                        } else if (hour == 9) {
                                            nine += 1;
                                            nineA += Float.parseFloat(jsonObject1.getString("amount"));
                                            fin9 = nineA / nine;
                                        } else if (hour == 12) {
                                            twelve += 1;
                                            twelveA += Float.parseFloat(jsonObject1.getString("amount"));
                                            fin12 = twelveA / twelve;

                                        } else if (hour == 15) {
                                            fifteen += 1;
                                            fifteenA += Float.parseFloat(jsonObject1.getString("amount"));
                                            fin15 = fifteenA / fifteen;
                                        } else {
                                            eighteen += 1;
                                            eighteenA += Float.parseFloat(jsonObject1.getString("amount"));
                                            fin18 = eighteenA / eighteen;
                                        }

                                    } else if (week.equals("2")) {
                                        if (hour == 6) {
                                            sixb += 1;
                                            sixB += Float.parseFloat(jsonObject1.getString("amount"));
                                            fin6B = sixB / sixb;
                                        } else if (hour == 9) {
                                            nineb += 1;
                                            nineB += Float.parseFloat(jsonObject1.getString("amount"));
                                            fin9B = nineB / nineb;
                                        } else if (hour == 12) {
                                            twelveb += 1;
                                            twelveB += Float.parseFloat(jsonObject1.getString("amount"));
                                            fin12B = twelveB / twelveb;
                                            Log.e("twelve", fin12B + " " + twelveB);

                                        } else if (hour == 15) {
                                            fifteenb += 1;
                                            fifteenB += Float.parseFloat(jsonObject1.getString("amount"));
                                            fin15B = fifteenB / fifteenb;
                                        } else {
                                            eighteenb += 1;
                                            eighteenB += Float.parseFloat(jsonObject1.getString("amount"));
                                            fin18B = eighteenB / eighteenb;
                                        }

                                    } else {
                                        Utils.showToast("Once you add your fare data, you'll be able to visualize it here", getBaseContext());
                                        noData = true;
                                    }


                                }

//                    Checking if data exist for either last week or this week. Avoid calculations if there's no current data
                                if (!noData) {

                       /* TODO The order of the entries when being added to the entries array determines their position around the center
                          TODO of  the chart.*/


//                    Setting up chart data for this week
                                    if (sixA != 0) {

                                        entries1.add(new RadarEntry(fin6));
                                    } else {
                                        entries1.add(new RadarEntry(0));
                                    }
                                    if (nineA != 0) {

                                        entries1.add(new RadarEntry(fin9));
                                    } else {
                                        entries1.add(new RadarEntry(0));
                                    }
                                    if (twelveA != 0) {
                                        Log.e("twelve", fin12 + "");
                                        entries1.add(new RadarEntry(fin12));

                                    } else {
                                        entries1.add(new RadarEntry(0));
                                    }
                                    if (fifteenA != 0) {
                                        entries1.add(new RadarEntry(fin15));
                                        Log.e("15", fin15 + "");

                                    } else {
                                        entries1.add(new RadarEntry(0));
                                    }
                                    if (eighteenA != 0) {

                                        entries1.add(new RadarEntry(fin18));
                                    }

//                    Setting up chart data for last week

                                    if (sixB != 0) {

                                        entries2.add(new RadarEntry(fin6B));
                                    } else {
                                        entries2.add(new RadarEntry(0));
                                    }
                                    if (nineB != 0) {

                                        entries2.add(new RadarEntry(fin9B));
                                    } else {
                                        entries2.add(new RadarEntry(0));
                                    }
                                    if (twelveB != 0) {

                                        entries2.add(new RadarEntry(fin12B));
                                    } else {
                                        entries2.add(new RadarEntry(0));
                                    }
                                    if (fifteenB != 0) {

                                        entries2.add(new RadarEntry(fin15B));
                                    } else {
                                        entries2.add(new RadarEntry(0));
                                    }
                                    if (eighteenB != 0) {

                                        entries2.add(new RadarEntry(fin18B));
                                    } else {
                                        entries2.add(new RadarEntry(0));
                                    }
                                }

                                RadarDataSet set1 = new RadarDataSet(entries1, "This Week");
                                set1.setColor(Color.rgb(103, 110, 129));
                                set1.setFillColor(Color.rgb(103, 110, 129));
                                set1.setDrawFilled(true);
                                set1.setFillAlpha(180);
                                set1.setLineWidth(2f);
                                set1.setDrawHighlightCircleEnabled(true);
                                set1.setDrawHighlightIndicators(false);


                                RadarDataSet set2 = new RadarDataSet(entries2, "Last Week");
                                set2.setColor(Color.rgb(121, 162, 175));
                                set2.setFillColor(Color.rgb(121, 162, 175));
                                set2.setDrawFilled(true);
                                set2.setFillAlpha(180);
                                set2.setLineWidth(2f);
                                set2.setDrawHighlightCircleEnabled(true);
                                set2.setDrawHighlightIndicators(false);


                                ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();

                                boolean current = false, previous = false;
                                if (!((fin6 == 0) && (fin9 == 0) && (fin12 == 0) && (fin15 == 0) && fin18 == 0)) {
                                    current = true;
                                    sets.add(set1);
                                }
                                if (!((fin6B == 0) && (fin9B == 0) && (fin12B == 0) && (fin15B == 0) && fin18B == 0)) {
                                    previous = true;
                                    sets.add(set2);
                                }

                                if (current || previous) {
                                    RadarData data = new RadarData(sets);
                                    data.setValueTypeface(mTfLight);
                                    data.setValueTextSize(8f);
                                    data.setDrawValues(false);
                                    data.setValueTextColor(Color.WHITE);

                                    mChart.setData(data);
                                    mChart.invalidate();
                                }


//                    drawChart(doublers, xVal);


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

