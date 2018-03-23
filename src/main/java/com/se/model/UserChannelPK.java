package com.se.model;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by Jack on 2017/7/1.
 */
@Embeddable
public class UserChannelPK implements Serializable{
    private int userId = 0;
    private int channelId = 0;
    public UserChannelPK(){}
    public UserChannelPK(int userId,int channelId) {
        this.userId = userId;
        this.channelId = channelId;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof UserChannelPK) {
            UserChannelPK ucpk = (UserChannelPK) obj;
            if(this.userId == ucpk.userId &&
                    this.channelId == ucpk.channelId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (this.userId*79+this.channelId+"").hashCode();
    }
}
