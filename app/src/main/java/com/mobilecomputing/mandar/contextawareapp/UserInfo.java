package com.mobilecomputing.mandar.contextawareapp;

/**
 * Created by Mandar on 4/14/2015.
 * This is model class to represent a record in database table userInfo
 */
public class UserInfo {

    private int recordID;
    private  String fromTimeStamp;
    private String toTimeStamp;
    private String day;
    private Double workLocationLat;
    private Double workLocationLong;
    private Double homeLocationLat;
    private Double homeLocationLong;

    public String getWorkLocationAddr() {
        return workLocationAddr;
    }

    public void setWorkLocationAddr(String workLocationAddr) {
        this.workLocationAddr = workLocationAddr;
    }

    public String getHomeLocationAddr() {
        return homeLocationAddr;
    }

    public void setHomeLocationAddr(String homeLocationAddr) {
        this.homeLocationAddr = homeLocationAddr;
    }

    private String workLocationAddr;
    private String homeLocationAddr;

    public UserInfo(){

    }
    public UserInfo(int recordID, String fromTimeStamp, String toTimeStamp, String day, Double workLocationLat, Double workLocationLon, Double homeLocationLat, Double homeLocationLong,String workLocationAddr,String homeLocationAddr) {
        this.recordID = recordID;
        this.fromTimeStamp = fromTimeStamp;
        this.toTimeStamp = toTimeStamp;
        this.day = day;
        this.workLocationLat = workLocationLat;
        this.workLocationLong = workLocationLon;
        this.homeLocationLat = homeLocationLat;
        this.homeLocationLong = homeLocationLong;
        this.workLocationAddr = workLocationAddr;
        this.homeLocationAddr = homeLocationAddr;
    }

    public int getRecordID() {
        return recordID;
    }

    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }

    public String getFromTimeStamp() {
        return fromTimeStamp;
    }

    public void setFromTimeStamp(String fromTimeStamp) {
        this.fromTimeStamp = fromTimeStamp;
    }

    public String getToTimeStamp() {
        return toTimeStamp;
    }

    public void setToTimeStamp(String toTimeStamp) {
        this.toTimeStamp = toTimeStamp;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Double getWorkLocationLat() {
        return workLocationLat;
    }

    public void setWorkLocationLat(Double workLocationLat) {
        this.workLocationLat = workLocationLat;
    }

    public Double getWorkLocationLon() {
        return workLocationLong;
    }

    public void setWorkLocationLon(Double workLocationLon) {
        this.workLocationLong = workLocationLon;
    }

    public Double getHomeLocationLat() {
        return homeLocationLat;
    }

    public void setHomeLocationLat(Double homeLocationLat) {
        this.homeLocationLat = homeLocationLat;
    }

    public Double getGetHomeLocationLong() {
        return homeLocationLong;
    }

    public void setGetHomeLocationLong(Double homeLocationLong) {
        this.homeLocationLong = homeLocationLong;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "recordID=" + recordID +
                ", fromTimeStamp='" + fromTimeStamp + '\'' +
                ", toTimeStamp='" + toTimeStamp + '\'' +
                ", day='" + day + '\'' +
                ", workLocationAddress=" + workLocationAddr +
                ", workLocationLat=" + workLocationLat +
                ", workLocationLong=" + workLocationLong +
                ", homeLocationLat=" + homeLocationLat +
                ", homeLocationLong=" + homeLocationLong +
                '}';
    }
}
