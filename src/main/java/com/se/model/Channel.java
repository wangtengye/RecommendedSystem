package com.se.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Jack on 2017/7/1.
 */
@Entity
public class Channel {
    @Id
    @GeneratedValue
    private int id;
    @Column
    private String channelName;
    @Column
    private String type;
    @Column
    private int clickTime=0;

    public Channel() {}
    public Channel(String channelName,String type) {
        this.channelName = channelName;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getClickTime() {
        return clickTime;
    }

    public void setClickTime(int clickTime) {
        this.clickTime = clickTime;
    }
}
