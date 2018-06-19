package com.digitalmatatus.twigatatu.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.digitalmatatus.twigatatu.R;
import com.digitalmatatus.twigatatu.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;

import java.util.ArrayList;
import java.util.List;

import static com.digitalmatatus.twigatatu.utils.Utils.applyFontForToolbarTitle;

public class MapActivity extends AppCompatActivity {


    public static final GeoPoint NAIROBI = new GeoPoint(-1.279783, 36.822023);
    MapView map = null;
    ArrayList<Marker> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_map3);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Visualize route collected");
        applyFontForToolbarTitle(this);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);


        IMapController mapController = map.getController();
        mapController.setZoom(20);
        mapController.setCenter(NAIROBI);

//        Marker startMarker = new Marker(map);
//        startMarker.setPosition(NAIROBI);
//        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
//        map.getOverlays().add(startMarker);
//
        map.invalidate();

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setClickable(true);
//        map.setUseDataConnection(true);

        visualize();


    }

    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }


    private void visualize() {
        if (Utils.checkDefaults("data", getBaseContext())) {
            try {
                JSONArray ja = new JSONArray(Utils.getDefaults("data", getBaseContext()));

                if (ja.length() > 0) {

//                  TODO Getting Route and drawing the route line
                    Polyline line = new Polyline(map);
                    line.setTitle("GTFS Route");
                    line.setSubDescription(Polyline.class.getCanonicalName());
                    line.setWidth(17f);
                    line.setColor(Color.parseColor("#00BD9E"));
                    List<GeoPoint> pts = new ArrayList<>();

                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject route = ja.getJSONObject(i);
                        JSONArray jsonArray = route.getJSONArray("route");

                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(j);
                            Log.e("point", jsonObject1.toString());

                            pts.add(new GeoPoint(Float.parseFloat(jsonObject1.getString("latitude")), Float.parseFloat(jsonObject1.getString("longitude"))));

                        }
                    }

                    line.setGeodesic(true);
                    line.setPoints(pts);
                    line.getPaint().setStrokeJoin(Paint.Join.ROUND);
                    line.getPaint().setStrokeCap(Paint.Cap.ROUND);
                    line.setInfoWindow(new BasicInfoWindow(R.layout.bonuspack_bubble, map));
                    //Note, the info window will not show if you set the onclick listener
                    //line can also attach click listeners to the line
                    line.setOnClickListener(new Polyline.OnClickListener() {
                        @Override
                        public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
//                            Toast.makeText(getBaseContext(), eventPos.getLatitude() + ", " + eventPos.getLongitude(), Toast.LENGTH_LONG).show();

                            return false;
                        }
                    });

                    map.getOverlayManager().add(line);

//                    TODO set the center to the center of the route collected
                    IMapController mapViewController = map.getController();
                    GeoPoint Centroid = computeCentroid(pts);
                    mapViewController.setZoom(20);
                    mapViewController.setCenter(Centroid);

                    map.invalidate();


                    ArrayList<OverlayItem> items = new ArrayList<>();


                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject route = ja.getJSONObject(i);
                        JSONArray jsonArray = route.getJSONArray("stops");

                        Log.e("stop len", jsonArray.length() + " ");

                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject point = jsonArray.getJSONObject(j);


                            OverlayItem itemMarker = new OverlayItem("Board: " + point.getString("board") + " Alight: " + point.getString("alight"), "", new GeoPoint(Double.parseDouble(point.getString("latitude")), Double.parseDouble(point.getString("longitude"))));
//                            itemMarker.setMarker(ShowMap.this.getResources().getDrawable(R.drawable.stop));
                            items.add(itemMarker);

                            Marker marker = new Marker(map);//you can also pass "this" as argument I believe
                            marker.setPosition(new GeoPoint(Double.parseDouble(point.getString("latitude")), Double.parseDouble(point.getString("longitude"))));
                            marker.setTitle(point.getString("stop_name") + " (" + point.getString("designation") + ")");
                            marker.setDraggable(true);
                            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker, MapView map) {
                                    marker.showInfoWindow();
                                    return true;

                                }
                            });
                            marker.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
                                @Override
                                public void onMarkerDrag(Marker marker) {

                                }

                                @Override
                                public void onMarkerDragEnd(Marker marker) {

                                }

                                @Override
                                public void onMarkerDragStart(Marker marker) {
                                    marker.getPosition().getLatitude();
                                    marker.getPosition().getLongitude();
//                                marker.setTitle();
                                    marker.getInfoWindow().getView().getTag();


                                }
                            });

                            arrayList.add(marker);
                            map.getOverlays().add(marker);


                        }
                    }


                    map.invalidate();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private GeoPoint computeCentroid(List<GeoPoint> points) {
        double latitude = 0;
        double longitude = 0;
        int n = points.size();

        for (GeoPoint point : points) {
            latitude += point.getLatitude();
            longitude += point.getLongitude();
        }

        return new GeoPoint(latitude / n, longitude / n);
    }

}
