package com.se.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * Created by Jack on 2017/7/1.
 */
@Entity
public class History {
    @EmbeddedId
    private UserChannelPK pk;
    @Column
    private long startTime;
    @Column
    private long lastTime;

    public UserChannelPK getPk() {
        return pk;
    }

    public void setPk(UserChannelPK pk) {
        this.pk = pk;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }


    public long getStartTime() {
        return startTime;
    }

    public long getLastTime() {
        return lastTime;
    }

}
