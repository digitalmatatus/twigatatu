
package com.digitalmatatus.twigatatu.views;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;

import com.digitalmatatus.twigatatu.R;


public class RadarMarkerView extends MarkerView {

    private TextView tvContent;
    private DecimalFormat format = new DecimalFormat("##0");

    public RadarMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = (TextView) findViewById(R.id.tvContent);
        tvContent.setTypeface(Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf"));
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        tvContent.setText(format.format(e.getY()) + " Ksh.");

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight() - 10);
    }
}


   /* private void GetData() {
        Post.getData("http://41.204.186.47:8000/twiga/stops/stops", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ArrayList<Double> doublers = new ArrayList<>();

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("stops");
                    Log.e("Array", jsonArray.toString());
//                    Calling process data to process the actual data
                    String[] xVal = new String[4];
                    String[] yVal = new String[jsonArray.length()];
                   *//* xVal[0] ="Monday";
                    xVal[1] ="Tuesday";
                    xVal[2] ="Wednesday";
                    xVal[3] ="Thursday";
                    xVal[4] ="Friday";
                    xVal[5] ="Saturday";*//*


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        doublers.add(Double.parseDouble(jsonObject1.getString("stop_name")));

                        if (i % 2 == 0) {
                            xVal[i] = "6am";
                        } else if (i % 3 == 0) {
                            xVal[i] = "9am";
                        } else if (i % 5 == 0) {
                            xVal[i] = "12pm";
                        } else {
                            xVal[i] = "6pm";

                        }
//                        yVal[i] =jsonObject1.getString("count");

                    }
                    drawChart(doublers, xVal);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

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

            //            private String[] mActivities = new String[]{"6am", "9am", "12pm", "3pm", "6pm"};
            private String[] mActivities = xValues;

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Log.e("value", value + "");
                return mActivities[(int) value];

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
}
*/