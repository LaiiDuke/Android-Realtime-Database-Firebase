package com.duke.handwear;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataTracking implements Comparable<DataTracking> {

    private Date dateObj;
    private String time;
    private String date;
    private Integer heartbeat;
    @JsonProperty("SpO2")
    private Integer spO2;
    private Integer message;
    private Integer horn;


    public Date getDateObj() {
        return dateObj;
    }

    public void setDateObj(Date dateObj) {
        this.dateObj = dateObj;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(Integer heartbeat) {
        this.heartbeat = heartbeat;
    }

    public Integer getSpO2() {
        return spO2;
    }

    public void setSpO2(Integer spO2) {
        this.spO2 = spO2;
    }

    public Integer getMessage() {
        return message;
    }

    public void setMessage(Integer message) {
        this.message = message;
    }

    public Integer getHorn() {
        return horn;
    }

    public void setHorn(Integer horn) {
        this.horn = horn;
    }

    @Override
    public int compareTo(DataTracking o) {
        return o.dateObj.compareTo(this.dateObj);
    }
}
