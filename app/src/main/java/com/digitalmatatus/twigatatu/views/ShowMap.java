package com.digitalmatatus.twigatatu.views;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;

import java.util.ArrayList;
import java.util.List;

import com.digitalmatatus.twigatatu.R;

import com.digitalmatatus.twigatatu.utils.Utils;

public class ShowMap extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final GeoPoint NAIROBI = new GeoPoint(-1.279783, 36.822023);
    public static final GeoPoint ADDIS = new GeoPoint(-9.005401, 38.763611);

    private MapView mapView;
    ArrayList<Marker> arrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);

        if (getFilesDir().listFiles().length == 0) {
            Toast.makeText(ShowMap.this, "No data to show on a map.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mapView = findViewById(R.id.mapview);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setUseDataConnection(true);
//        mapView.setTileSource(TileSourceFactory.PUBLIC_TRANSPORT);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
//        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        MapController mapController = (MapController) mapView.getController();
        mapController.setZoom(17);


        IMapController mapViewController = mapView.getController();
        mapViewController.setZoom(17);
        mapViewController.setCenter(NAIROBI);


        if (Utils.checkDefaults("data", getBaseContext())) {
            try {
                JSONArray ja = new JSONArray(Utils.getDefaults("data", getBaseContext()));

                if (ja.length() > 0) {

                    Polyline line = new Polyline(getBaseContext());
                    line.setTitle("Ma3tycoon, Nairobi");
                    line.setSubDescription(Polyline.class.getCanonicalName());
                    line.setWidth(17f);
                    line.setColor(Color.parseColor("#00BD9E"));
                    List<GeoPoint> pts = new ArrayList<>();

                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject route = ja.getJSONObject(i);
                        JSONArray jsonArray = route.getJSONArray("route");

                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            pts.add(new GeoPoint(Float.parseFloat(jsonObject1.getString("latitude")), Float.parseFloat(jsonObject1.getString("longitude"))));

                        }
                    }

                    line.setPoints(pts);
                    line.setGeodesic(true);
                    line.setInfoWindow(new BasicInfoWindow(R.layout.bonuspack_bubble, mapView));
                    //Note, the info window will not show if you set the onclick listener
                    //line can also attach click listeners to the line
                    line.setOnClickListener(new Polyline.OnClickListener() {
                        @Override
                        public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
//                            Toast.makeText(getBaseContext(), eventPos.getLatitude() + ", " + eventPos.getLongitude(), Toast.LENGTH_LONG).show();
                            return false;
                        }
                    });
                    mapView.getOverlayManager().add(line);
                    mapView.invalidate();


                    ArrayList<OverlayItem> items = new ArrayList<>();


                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject route = ja.getJSONObject(i);
                        JSONArray jsonArray = route.getJSONArray("stops");

                        Log.e("stop len",jsonArray.length()+" ");

                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject point = jsonArray.getJSONObject(i);


                            OverlayItem itemMarker = new OverlayItem("Board: " + point.getString("board") + " Alight: " + point.getString("alight"), "", new GeoPoint(Double.parseDouble(point.getString("latitude")), Double.parseDouble(point.getString("longitude"))));
//                            itemMarker.setMarker(ShowMap.this.getResources().getDrawable(R.drawable.stop));
                            items.add(itemMarker);

                            Marker marker = new Marker(mapView);//you can also pass "this" as argument I believe
                            marker.setPosition(new GeoPoint(Double.parseDouble(point.getString("latitude")), Double.parseDouble(point.getString("longitude"))));
                            marker.setTitle("");
                            marker.setDraggable(true);
                            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker, MapView mapView) {
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
                            mapView.getOverlays().add(marker);


                        }
                    }


                    mapView.invalidate();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            
        }

    }

}
