package com.mobilecomputing.mandar.contextawareapp;

/**
 * Created by Mandar on 4/17/2015.
 * Model class to hold item in list view of ViewSchedule class
 */
public class ScheduleModel {
    String content;
    int value;

    public ScheduleModel(String content, int value) {
        this.content = content;
        this.value = value;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
