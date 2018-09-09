package com.digitalmatatus.twigatatu.model;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.sql.Timestamp;

public class Fares extends DataSupport {

    @Column(unique = true)
    private long id;
    private String stop_from;
    private String stop_to;
    private int fare;
    private Timestamp timestamp;

    //    TODO add week and time
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStopFrom() {
        return stop_from;
    }

    public void setStopFrom(String stop_from) {
        this.stop_from = stop_from;
    }

    public String getStopTo() {
        return stop_to;
    }

    public void setStopTo(String stop_to) {
        this.stop_to = stop_to;
    }

    public int getFare() {
        return fare;
    }

    public void setFare(int fare) {
        this.fare = fare;
    }

    public int getTimestamp() {
        return fare;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

}
