package com.digitalmatatus.twigatatu;

import android.content.Context;
import android.location.Location;

import com.digitalmatatus.twigatatu.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RouteCapture {

    public Integer id;

    public String name;
    public String description;
    public String notes;

    public String vehicleType;
    public String vehicleCapacity;

    public long startMs;
    public long startTime;
    public long stopTime;

    public Integer totalPassengerCount = 0;
    public Integer alightCount = 0;
    public Integer boardCount = 0;

    public Long distance = 0l;

    List<RoutePoint> points = new ArrayList<RoutePoint>();

    List<RouteStop> stops = new ArrayList<RouteStop>();

    public void setRouteName(String name) {

        if (name.equals(""))
            this.name = "Route " + id;
        else
            this.name = name;
    }

    public static RouteCapture deseralize(TransitWandProtos.Upload.Route r, Context context) {

        RouteCapture route = new RouteCapture();

        route.name = r.getRouteName();
        route.description = r.getRouteDescription();
        route.notes = r.getRouteNotes();
        route.vehicleCapacity = r.getVehicleCapacity();
        route.vehicleType = r.getVehicleType();

        route.startTime = r.getStartTime();

        long lastTimepoint = route.startTime;

        for (TransitWandProtos.Upload.Route.Point p : r.getPointList()) {
            RoutePoint rp = new RoutePoint();
            rp.location = new Location("GPS");
            rp.location.setLatitude(p.getLat());
            rp.location.setLongitude(p.getLon());
            rp.time = (p.getTimeoffset() * 1000) + lastTimepoint;

            lastTimepoint = rp.time;

            route.points.add(rp);
        }

        lastTimepoint = route.startTime;

        for (TransitWandProtos.Upload.Route.Stop s : r.getStopList()) {
            RouteStop rs = new RouteStop();
            rs.location = new Location("GPS");
            rs.location.setLatitude(s.getLat());
            rs.location.setLongitude(s.getLon());
            rs.arrivalTime = (s.getArrivalTimeoffset() * 1000) + lastTimepoint;
            rs.departureTime = (s.getDepartureTimeoffset() * 1000) + lastTimepoint;

            lastTimepoint = rs.arrivalTime;

            rs.board = s.getBoard();
            rs.alight = s.getAlight();

            route.stops.add(rs);
        }

        return route;

    }

    public TransitWandProtos.Upload.Route seralize() {

        TransitWandProtos.Upload.Route.Builder route = TransitWandProtos.Upload.Route.newBuilder();
        route.setRouteName(name);
        route.setRouteDescription(description);
        route.setRouteNotes(notes);
        route.setVehicleCapacity(vehicleCapacity);
        route.setVehicleType(vehicleType);
        route.setStartTime(startTime);

        long lastTimepoint = startMs;

        for (RoutePoint rp : points) {
            TransitWandProtos.Upload.Route.Point.Builder point = TransitWandProtos.Upload.Route.Point.newBuilder();
            point.setLat((float) rp.location.getLatitude());
            point.setLon((float) rp.location.getLongitude());
            point.setTimeoffset((int) ((rp.time - lastTimepoint) / 1000));
            lastTimepoint = rp.time;

            route.addPoint(point);
        }

        lastTimepoint = startMs;

        for (RouteStop rs : stops) {

            TransitWandProtos.Upload.Route.Stop.Builder stop = TransitWandProtos.Upload.Route.Stop.newBuilder();

            stop.setLat((float) rs.location.getLatitude());
            stop.setLon((float) rs.location.getLongitude());
            stop.setArrivalTimeoffset((int) ((rs.arrivalTime - lastTimepoint) / 1000));
            stop.setDepartureTimeoffset((int) ((rs.departureTime - lastTimepoint) / 1000));
            stop.setAlight(rs.alight);
            stop.setBoard(rs.board);
            lastTimepoint = rs.arrivalTime;

            route.addStop(stop);
        }

        return route.build();
    }

    public static JSONObject jsonify(TransitWandProtos.Upload.Route r) {

        JSONObject rt = new JSONObject();

        try {

            rt.put("route_name", r.getRouteName());
            rt.put("description", r.getRouteDescription());
            rt.put("notes", r.getRouteNotes());
            rt.put("vehicle_capacity", r.getVehicleCapacity());
            rt.put("vehicle_type", r.getVehicleType());
            rt.put("startTime", r.getStartTime());
            rt.put("route_id","route_id");
            long lastTimepoint = r.getStartTime();

            JSONArray route = new JSONArray();
            for (TransitWandProtos.Upload.Route.Point p : r.getPointList()) {
                JSONObject point = new JSONObject();
                point.put("latitude", p.getLat());
                point.put("longitude", p.getLon());
                point.put("time", (p.getTimeoffset() * 1000) + lastTimepoint);
                lastTimepoint = (p.getTimeoffset() * 1000) + lastTimepoint;
                route.put(point);
            }
            rt.put("route", route);

            JSONArray stops = new JSONArray();
            for (TransitWandProtos.Upload.Route.Stop s : r.getStopList()) {
                JSONObject stop = new JSONObject();
                stop.put("latitude", s.getLat());
                stop.put("longitude", s.getLon());
                stop.put("arrival_time", (s.getArrivalTimeoffset() * 1000) + lastTimepoint);
                stop.put("departure_time", (s.getDepartureTimeoffset() * 1000) + lastTimepoint);
                lastTimepoint = (s.getArrivalTimeoffset() * 1000) + lastTimepoint;
                stop.put("board", s.getBoard());
                stop.put("alight", s.getAlight());
                stops.put(stop);
            }
            rt.put("stops", stops);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rt;
    }


    public static JSONObject jsonify(TransitWandProtos.Upload.Route r, Context context) {

        JSONObject rt = new JSONObject();

        try {

            rt.put("route_name", Utils.getDefaults("route_name",context));
            rt.put("description", r.getRouteDescription());
            rt.put("notes", r.getRouteNotes());
            rt.put("vehicle_capacity", r.getVehicleCapacity());
            rt.put("vehicle_type", r.getVehicleType());
            rt.put("startTime", r.getStartTime());
//            rt.put("route_id","40200000601");
            rt.put("route_id", Utils.getDefaults("route_id",context));
            long lastTimepoint = r.getStartTime();

            JSONArray route = new JSONArray();
            for (TransitWandProtos.Upload.Route.Point p : r.getPointList()) {
                JSONObject point = new JSONObject();
                point.put("latitude", p.getLat());
                point.put("longitude", p.getLon());
                point.put("time", (p.getTimeoffset() * 1000) + lastTimepoint);
                lastTimepoint = (p.getTimeoffset() * 1000) + lastTimepoint;
                route.put(point);
            }
            rt.put("route", route);

            JSONArray stops = new JSONArray();
            for (TransitWandProtos.Upload.Route.Stop s : r.getStopList()) {
                JSONObject stop = new JSONObject();
                stop.put("latitude", s.getLat());
                stop.put("longitude", s.getLon());
                stop.put("arrival_time", (s.getArrivalTimeoffset() * 1000) + lastTimepoint);
                stop.put("departure_time", (s.getDepartureTimeoffset() * 1000) + lastTimepoint);
                lastTimepoint = (s.getArrivalTimeoffset() * 1000) + lastTimepoint;
                stop.put("board", s.getBoard());
                stop.put("alight", s.getAlight());
                stops.put(stop);
            }
            rt.put("stops", stops);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rt;
    }
}
