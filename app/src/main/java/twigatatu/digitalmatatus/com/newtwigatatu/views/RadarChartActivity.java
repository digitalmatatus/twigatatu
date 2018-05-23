
package twigatatu.digitalmatatus.com.newtwigatatu.views;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

import twigatatu.digitalmatatus.com.newtwigatatu.R;
import twigatatu.digitalmatatus.com.newtwigatatu.model.Post;
import twigatatu.digitalmatatus.com.newtwigatatu.utils.AppController;
import twigatatu.digitalmatatus.com.newtwigatatu.utils.Utils;

import static twigatatu.digitalmatatus.com.newtwigatatu.utils.Utils.applyFontForToolbarTitle;

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
        TextView tv =  findViewById(R.id.textView);
        tv.setTypeface(mTfLight);
        tv.setTextColor(Color.BLACK);

//        tv.setTextColor(Color.WHITE);
//        tv.setBackgroundColor(Color.rgb(00, 80, 00));
//        60, 65, 82
        mChart =  findViewById(R.id.chart1);
//        mChart.setBackgroundColor(Color.rgb(00, 80, 00));
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getBaseContext(), LineChartActivity.class);
//                startActivity(intent);
            }
        });
//        floatingActionButton.setBackgroundColor(Color.rgb(60, 65, 82));
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


        setData(); //Uncomment this to set up random data
      /*  getData(); // TODO Gets real data for the user from the server

        getExpenditure(Utils.getDefaults("user_id", getBaseContext())); // //TODO  Gets real data for the user from the server
*/
      Utils.showToast("  Click on a point to view fare amount   ",this);
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
        set1.setColor(Color.rgb(141,	204,	144));
        set1.setFillColor(Color.rgb(141,	204,	144));



        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(false);

        RadarDataSet set2 = new RadarDataSet(entries2, "This Week");
        set2.setColor(Color.rgb( 255	,166,	196));
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


    private void getData() {
        Post.getData("http://41.204.186.47:8000/twiga/fares/fares", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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


                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("fares");
                    Log.e("Fares Array", jsonArray.toString());

//                    TODO .. calculate if we have data for last week the assign the boolean last week
                    boolean noData = false;


                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        Date travel_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jsonObject1.getString("travel_time"));

                        DateFormat df = new SimpleDateFormat("hh:mm aa");
                        //Desired format: 24 hour format: Change the pattern as per the need
                        DateFormat outputformat = new SimpleDateFormat("HH:mm");
                        //Converting the input String to Date
                        Date date = df.parse(jsonObject1.getString("travel_time"));
                        Log.e("date", date + "");
                        //Changing the format of date and storing it in String
                        String output = outputformat.format(date);

//                        Rounding off the time to the nearest hour interval
                        Log.e("time", travel_time + " output is" + output);

                        int hour = toNearestHourInterval(output);
                        Log.e("nearest time", " output is" + hour);

//                        Rounding off the time to the nearest hour interval
//                        int hour = toNearestHourInterval(output);

//                        doublers.add(Double.parseDouble(jsonObject1.getString("stop_name")));
//                            xVal[i] = hour + "";

//                        Getting average of each time
                        if (isDateInCurrentWeek(travel_time)) {

                            if (hour == 6) {
                                six = +1;
                                sixA = +Float.parseFloat(jsonObject1.getString("amount"));
                                fin6 = sixA / six;

                            } else if (hour == 9) {
                                nine = +1;
                                nineA = +Float.parseFloat(jsonObject1.getString("amount"));
                                fin9 = nineA / nine;
                            } else if (hour == 12) {
                                twelve = +1;
                                twelveA = +Float.parseFloat(jsonObject1.getString("amount"));
                                fin12 = twelveA / twelve;
                            } else if (hour == 15) {
                                fifteen = +1;
                                fifteenA = +Float.parseFloat(jsonObject1.getString("amount"));
                                fin15 = fifteenA / fifteen;
                            } else {
                                eighteen = +1;
                                eighteenA = +Float.parseFloat(jsonObject1.getString("amount"));
                                fin18 = eighteenA / eighteen;
                            }

                        } else if (isDateInLastWeek(travel_time)) {
                            if (hour == 6) {
                                sixb = +1;
                                sixB = Float.parseFloat(jsonObject1.getString("amount"));
                                fin6B = sixB / sixb;
                            } else if (hour == 9) {
                                nineb = +1;
                                nineB = Float.parseFloat(jsonObject1.getString("amount"));
                                fin9B = nineB / nineb;
                            } else if (hour == 12) {
                                twelveb = +1;
                                twelveB = Float.parseFloat(jsonObject1.getString("amount"));
                                fin12B = twelveB / twelveb;
                            } else if (hour == 15) {
                                fifteenb = +1;
                                fifteenB = Float.parseFloat(jsonObject1.getString("amount"));
                                fin15B = fifteenB / fifteenb;
                            } else {
                                eighteenb = +1;
                                eighteenB = Float.parseFloat(jsonObject1.getString("amount"));
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
                            float fin = sixA / six;
                            entries1.add(new RadarEntry(fin12));
                        } else {
                            entries1.add(new RadarEntry(0));
                        }
                        if (fifteenA != 0) {
                            float fin = sixA / six;
                            entries1.add(new RadarEntry(fin15));
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
                    sets.add(set1);
                    sets.add(set2);

                    RadarData data = new RadarData(sets);
                    data.setValueTypeface(mTfLight);
                    data.setValueTextSize(8f);
                    data.setDrawValues(false);
                    data.setValueTextColor(Color.WHITE);

                    mChart.setData(data);
                    mChart.invalidate();


//                    drawChart(doublers, xVal);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void getExpenditure(String id) {
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                Utils.baseURL() + "twiga/budgeting/expenditure/user_id/" + id, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("All Data", "response from the server is: " + response.toString());
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


                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("expenditure");
                    Log.e("Expenditure Array", jsonArray.toString());

//                    TODO .. calculate if we have data for last week the assign the boolean last week
                    boolean noData = false;


                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        Date travel_time = new SimpleDateFormat("hh:mm aa").parse(jsonObject1.getString("time"));
                        String week = jsonObject1.getString("week");

                        DateFormat df = new SimpleDateFormat("hh:mm aa");
                        //Desired format: 24 hour format: Change the pattern as per the need
                        DateFormat outputformat = new SimpleDateFormat("HH:mm");
                        //Converting the input String to Date
                        Date date = df.parse(jsonObject1.getString("time"));
                        Log.e("date", date + "");
                        //Changing the format of date and storing it in String
                        String output = outputformat.format(date);

//                        Rounding off the time to the nearest hour interval
                        Log.e("time", travel_time + " output is" + output);

                        int hour = toNearestHourInterval(output);

                        Log.e("hour", hour + " and week is " + week);


//                        doublers.add(Double.parseDouble(jsonObject1.getString("stop_name")));
//                            xVal[i] = hour + "";

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
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Getting data error", "Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", "9");

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    //random data to help me create this function 1 2 3 4 5 6 7 8 8 9 10 11 12 13 14 15 16 17 18
    public static int toNearestHourInterval(String time) {
//        Date d
//        Calendar c = new GregorianCalendar();
//        Calendar c = Calendar.getInstance();

        String[] splitTime = time.split(":");
        String timeHour = splitTime[0];

//        c.setTime(d);

//        int hours = c.get(Calendar.HOUR);
        int hours = Integer.parseInt(timeHour);

        Log.e("casted hour", hours + "");


        if (hours <= 6) {
            int add = 6 - hours;
            hours = hours + add;
        } else if (hours > 6 && hours <= 9) {
            int add = 9 - hours;
            hours = hours + add;
        } else if (hours > 9 && hours <= 12) {
            int add = 12 - hours;
            hours = hours + add;
        } else if (hours > 12 && hours <= 15) {
            int add = 15 - hours;
            hours = hours + add;
        } else if (hours > 15 && hours <= 18) {
            int add = 18 - hours;
            hours = hours + add;
        } else if (hours > 18) {

            hours = 18;
            ;
        }


        return hours;
    }

    public static boolean isDateInCurrentWeek(Date date) {
        Calendar currentCalendar = Calendar.getInstance();
        int week = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int year = currentCalendar.get(Calendar.YEAR);
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(date);
        int targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);
        return week == targetWeek && year == targetYear;
    }

    public static boolean isDateInLastWeek(Date date) {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.add(Calendar.WEEK_OF_YEAR, -1);
        int week = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int year = currentCalendar.get(Calendar.YEAR);
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(date);
        int targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);
        return week == targetWeek && year == targetYear;
    }


//   TODO DELETE UNUSED FUNCTIONS BELOW

    public void drawChart(ArrayList<Double> doublers, final String[] xValues) {


        mChart = (RadarChart) findViewById(R.id.chart1);
        mChart.setBackgroundColor(Color.rgb(60, 65, 82));

        mChart.getDescription().setEnabled(false);

        mChart.setWebLineWidth(1f);
        mChart.setWebColor(Color.LTGRAY);
        mChart.setWebLineWidthInner(1f);
        mChart.setWebColorInner(Color.LTGRAY);
        mChart.setWebAlpha(100);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MarkerView mv = new RadarMarkerView(this, R.layout.radar_markerview);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

//        setData();

        mChart.animateXY(
                1400, 1400,
                Easing.EasingOption.EaseInOutQuad,
                Easing.EasingOption.EaseInOutQuad);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
//        xAxis.setValueFormatter(xValues);
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            //            private String[] mTimes = new String[]{"6am", "9am", "12pm", "3pm", "6pm"};
            private String[] mTimes = xValues;

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Log.e("value", value + "");
                return mTimes[(int) value];

            }
        });
        xAxis.setTextColor(Color.WHITE);

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
        l.setTextColor(Color.WHITE);


        ArrayList<RadarEntry> entries = new ArrayList<RadarEntry>();


        int i = 0;
        for (double value : doublers) {
            entries.add(new RadarEntry(Float.parseFloat(i + "f"), (float) value));
            i++;
        }


        RadarDataSet set2 = new RadarDataSet(entries, "This Week");
        set2.setColor(Color.rgb(121, 162, 175));
        set2.setFillColor(Color.rgb(121, 162, 175));
        set2.setDrawFilled(true);
        set2.setFillAlpha(180);
        set2.setLineWidth(2f);
        set2.setDrawHighlightCircleEnabled(true);
        set2.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        sets.add(set2);

        RadarData data = new RadarData(sets);
        data.setValueTypeface(mTfLight);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);
        mChart.invalidate();


    }


   /* static Date toNearestWholeHour(Date d) {
        Calendar c = new GregorianCalendar();
        c.setTime(d);

        if (c.get(Calendar.MINUTE) >= 30)
            c.add(Calendar.HOUR, 1);

        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);

        return c.getTime();
    }*/

  /* private boolean lastWeek(Date date){

       boolean res =false;
       // Calendar object
       Calendar cal = Calendar.getInstance();

// "move" cal to monday this week (i understand it this way)
       cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

// calculate monday week ago (moves cal 7 days back)
       cal.add(Calendar.DATE, -7);
       Date firstDateOfPreviousWeek = cal.getTime();

// calculate sunday last week (moves cal 6 days fwd)
       cal.add(Calendar.DATE, 6);
       Date lastDateOfPreviousWeek = cal.getTime();

       return res;
   }*/


   /* private boolean lastWeek(Date date){

        boolean res =false;


        return res;
   }

    public static Calendar firstDayOfLastWeek(Calendar c)
    {
        c = (Calendar) c.clone();
        // last week
        c.add(Calendar.WEEK_OF_YEAR, -1);
        // first day
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        return c;
    }

    public static Calendar lastDayOfLastWeek(Calendar c)
    {
        c = (Calendar) c.clone();
        // first day of this week
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        // last day of previous week
        c.add(Calendar.DAY_OF_MONTH, -1);
        return c;
    }*/

    //    TODO Call this function to get real data from the database
//    private void GetData() {
//        Post.getData("http://41.204.186.47:8000/twiga/stops/stops", new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    ArrayList<Double> doublers = new ArrayList<>();
//
//                    JSONObject jsonObject = new JSONObject(response);
//                    JSONArray jsonArray = jsonObject.getJSONArray("stops");
//                    Log.e("Array", jsonArray.toString());
////                    Calling process data to process the actual data
//                    String[] xVal = new String[4];
//                    String[] yVal = new String[jsonArray.length()];
//                    xVal[0] = "Monday";
//                    xVal[1] = "Tuesday";
//                    xVal[2] = "Wednesday";
//                    xVal[3] = "Thursday";
//                    xVal[4] = "Friday";
//                    xVal[5] = "Saturday";
//
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//
//                        doublers.add(Double.parseDouble(jsonObject1.getString("stop_name")));
//
//                        if (i % 2 == 0) {
//                            xVal[i] = "6am";
//                        } else if (i % 3 == 0) {
//                            xVal[i] = "9am";
//                        } else if (i % 5 == 0) {
//                            xVal[i] = "12pm";
//                        } else {
//                            xVal[i] = "6pm";
//
//                        }
////                        yVal[i] =jsonObject1.getString("count");
//
//                    }
//                    drawChart(doublers, xVal);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//    }

}

