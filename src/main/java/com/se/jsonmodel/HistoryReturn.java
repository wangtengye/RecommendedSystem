package com.se.jsonmodel;

/**
 * Created by Jack on 2017/7/1.
 */
public class HistoryReturn {
    private int channelId;
    private String channelName;
    private String type;
    private String startTime;
    private String lastTime;


    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getChannelId() {
        return channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getType() {
        return type;
    }

    public String getLastTime() {
        return lastTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
