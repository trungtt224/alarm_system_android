package com.example.smartalarmapplication.model;

public class Alarm {
    private int id;
    private long timeAlarm;
    private String ringtone;
    private int isActive;

    public Alarm() {
    }

    public Alarm(long timeAlarm, String ringtone) {
        this.timeAlarm = timeAlarm;
        this.ringtone = ringtone;
    }

    public Alarm(long timeAlarm, String ringtone, int isActive) {
        this.timeAlarm = timeAlarm;
        this.ringtone = ringtone;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimeAlarm() {
        return timeAlarm;
    }

    public void setTimeAlarm(long timeAlarm) {
        this.timeAlarm = timeAlarm;
    }

    public String getRingtone() {
        return ringtone;
    }

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }
}
